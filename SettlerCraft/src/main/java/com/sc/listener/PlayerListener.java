/*
 * Copyright (C) 2014 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sc.listener;

import com.sc.SimpleCardinal;
import com.sc.construction.SelectionManager;
import com.sc.construction.plan.StructurePlan;
import com.sc.construction.plan.StructurePlanManager;
import com.sc.construction.plan.StructureSchematic;
import com.sc.construction.structure.StructureManager;
import com.sc.persistence.SchematicService;
import com.sc.plugin.PermissionManager;
import com.sc.plugin.SettlerCraft;
import com.sc.util.SCWorldEditUtil;
import static com.sc.util.SCWorldEditUtil.getLocalSession;
import com.sc.util.SCWorldGuardUtil;
import com.sc.util.WorldUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Location;
import com.sk89q.worldguard.bukkit.WorldConfiguration;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.apache.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class PlayerListener implements Listener {

    private final Logger LOGGER = Logger.getLogger(PlayerListener.class);

    /**
     * Places a structure on player's target location
     *
     * @param pie The playerInteractEvent
     */
    @EventHandler
    public void onPlayerPlaceStructure(PlayerInteractEvent pie) {
        if (pie.getItem() == null || pie.getItem().getType() != Material.PAPER) {
            return;
        }

        ItemStack planStack = pie.getItem();
        if (!StructurePlan.isStructurePlan(planStack)) {
            return;
        }
        pie.setCancelled(true); // default action would break a block
        String structurePlanId = StructurePlan.getPlanID(planStack);
        StructurePlan plan = StructurePlanManager.getInstance().getPlan(structurePlanId);
        Player player = pie.getPlayer();
        if (!PermissionManager.isAllowed(player, PermissionManager.Perms.STRUCTURE_PLACE)) {
            player.sendMessage(ChatColor.RED + "You have no permission to place structures");
            return;
        }

        if (plan == null) {
            if (!SettlerCraft.getSettlerCraft().isPlansLoaded()) {
                player.sendMessage(ChatColor.RED + "This plan is invalid, please refund it or throw it away");
            } else {
                player.sendMessage(ChatColor.RED + "Plans aren't loaded yet please wait...");
            }

            return;
        }

        if (!PermissionManager.isAllowed(player, PermissionManager.Perms.STRUCTURE_PLACE)) {
            player.sendMessage(ChatColor.RED + "You don't have permission to place structures");
            return;
        }

        LocalSession session = getLocalSession(pie.getPlayer());

        if (session.hasCUISupport()) {
            try {
                if (handleCUIPlayerSelect(player, pie.getClickedBlock(), plan, pie.getAction())) {
                    ItemStack stack = pie.getItem().clone();
                    stack.setAmount(1);
                    pie.getPlayer().getInventory().removeItem(stack);
                    pie.getPlayer().updateInventory();

                }
            } catch (IncompleteRegionException ex) {
                LOGGER.error(ex);
            }
        } else {
            if (handleSimplePlayerSelect(player, pie.getClickedBlock(), plan, pie.getAction())) {
                ItemStack stack = pie.getItem().clone();
                stack.setAmount(1);
                pie.getPlayer().getInventory().removeItem(stack);
                pie.getPlayer().updateInventory();
            }

        }
    }

    private boolean canPlace(Player player, Location location, SimpleCardinal cardinal, StructurePlan plan, StructureSchematic schematic) {
        StructureManager sm = StructureManager.getInstance();

//        if (sm.overlaps(plan, location, cardinal)) {
//            player.sendMessage(ChatColor.RED + " Structure overlaps another structure");
//            return false;
//        }
//
//        if (!sm.mayClaim(player)) {
//            player.sendMessage(ChatColor.RED + " You have no permission to claim regions");
//            player.sendMessage(ChatColor.RED + " Therefore your are not able to place structures");
//            return false;
//        }
        if (!sm.canClaim(player)) {
            WorldConfiguration wcfg = SCWorldGuardUtil.getWorldGuard().getGlobalStateManager().get(player.getWorld());
            RegionManager mgr = SCWorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(player.getWorld());
            int plyMaxRegionCount = wcfg.getMaxRegionCount(player);
            int plyCurRegionCount = mgr.getRegionCountOfPlayer(SCWorldGuardUtil.getLocalPlayer(player));
            player.sendMessage(ChatColor.RED + " You have reached your region claim limit (" + plyCurRegionCount + "/" + plyMaxRegionCount + ")");
            player.sendMessage(ChatColor.RED + " Therefore your are not able to place structures");
            return false;
        }

        if (sm.overlaps(plan, location, cardinal, schematic)) {
            player.sendMessage(ChatColor.RED + " Structure overlaps another structure");
            return false;
        }

        if (sm.overlapsRegion(player, plan, location, cardinal, schematic)) {
            player.sendMessage(ChatColor.RED + " Structure overlaps a region you don't own");
            return false;
        }
        return true;
    }

    private boolean handleSimplePlayerSelect(Player player, Block block, StructurePlan plan, Action action) {
        SelectionManager slm = SelectionManager.getInstance();
        if (block == null
                || block.getType() == Material.AIR) {
            LOGGER.info("Clearing simple");
            slm.clearsSimple(player, true);

            return false;
        }
        StructureManager sm = StructureManager.getInstance();
        org.bukkit.Location location = block.getLocation();

        if (action == Action.LEFT_CLICK_BLOCK) {
            LocalWorld world = SCWorldEditUtil.getLocalWorld(player);
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();

            SimpleCardinal cardinal = WorldUtil.getCardinal(player);
            Location pos1 = new Location(world, new BlockWorldVector(world, x, y, z));

            SchematicService service = new SchematicService();
            StructureSchematic s = service.getSchematic(plan.getSchematicChecksum());

            if (s == null) {
                player.sendMessage(ChatColor.RED + "Schematic for for this plan doesn't exist!"); // Should never happen!
                return false;
            }

             Location pos2 = WorldUtil.getPoint2(pos1, cardinal, new BlockVector(s.getWidth(), s.getHeight(), s.getLength()));

            if (slm.hasSimpleSelection(player)) {
                if (slm.matchesSimple(player, plan, pos1)) {
                    if (canPlace(player, pos1, cardinal, plan, s)) {
                        if (sm.construct(player, plan, pos1, cardinal) != null) {
                            slm.clearsSimple(player, false);
                            return true;
                        }
                    }

                }
                slm.clearsSimple(player, false);
            }

            slm.simpleSelect(player, plan, pos1, pos2);
            if (canPlace(player, pos1, cardinal, plan, s)) {
                player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " [X] " + ChatColor.RESET + " to " + ChatColor.YELLOW + "confirm");
                player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
            }
        } else {
            slm.clearsSimple(player, false);
        }
        return false;
    }

    private boolean handleCUIPlayerSelect(Player player, Block block, StructurePlan plan, Action action) throws IncompleteRegionException {
        LocalWorld world = SCWorldEditUtil.getLocalWorld(player);
        StructureManager sm = StructureManager.getInstance();
        SelectionManager slm = SelectionManager.getInstance();
        if (block == null || block.getType() == Material.AIR) {
            slm.clearCUISelection(player, true);
            return false;
        }

        org.bukkit.Location target = block.getLocation();
        int x = target.getBlockX();
        int y = target.getBlockY();
        int z = target.getBlockZ();
        LocalSession session = SCWorldEditUtil.getLocalSession(player);
        SimpleCardinal cardinal = WorldUtil.getCardinal(player);
        //                        session.dispatchCUISelection(SCWorldEditUtil.getLocalPlayer(player));

        Location pos1 = new Location(world, new BlockVector(x, y, z));
        SchematicService service = new SchematicService();
        StructureSchematic s = service.getSchematic(plan.getSchematicChecksum());

            if (s == null) {
                player.sendMessage(ChatColor.RED + "Schematic for for this plan doesn't exist!");
                return false;
            }
//        Location pos2 = WorldUtil.addOffset(location, cardinal, s.getWidth(), s.getHeight(), s.getLength());
        Location pos2 = WorldUtil.getPoint2(pos1, cardinal, new BlockVector(s.getWidth(), s.getHeight(), s.getLength()));
        

        if (action == Action.LEFT_CLICK_BLOCK) {
            if (!session.getRegionSelector(world).isDefined()) {

                slm.CUISelect(player, plan, pos1, pos2);
                player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " green " + ChatColor.RESET + "square to " + ChatColor.YELLOW + "confirm");
                player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
            } else {

                if (slm.matchesCUISelection(player, plan, pos1, pos2)) {
                    slm.CUISelect(player, plan, pos1, pos2);

                    if (sm.construct(player, plan, pos1, cardinal) != null) {
                        slm.clearCUISelection(player, false);
                        return true;
                    }

//                    }
                } else {
                    slm.CUISelect(player, plan, pos1, pos2);
                    if (canPlace(player, pos1, cardinal, plan, s)) {
                        player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " green " + ChatColor.RESET + "square to " + ChatColor.YELLOW + "confirm");
                        player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
                    }
                }
            }
        } else {
            slm.clearCUISelection(player, true);
        }
        return false;
    }

}
