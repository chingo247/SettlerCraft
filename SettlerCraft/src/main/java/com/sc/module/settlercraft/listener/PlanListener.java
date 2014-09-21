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
package com.sc.module.settlercraft.listener;

import com.sc.module.settlercraft.plugin.SettlerCraft;
import com.sc.module.structureapi.structure.AsyncStructureAPI;
import com.sc.module.structureapi.structure.Structure;
import com.sc.module.structureapi.structure.StructureAPI;
import com.sc.module.structureapi.structure.plan.StructurePlan;
import com.sc.module.structureapi.structure.plan.StructurePlanManager;
import com.sc.module.structureapi.structure.schematic.Schematic;
import com.sc.module.structureapi.structure.schematic.SchematicManager;
import com.sc.module.structureapi.util.WorldEditUtil;
import com.sc.module.structureapi.util.WorldUtil;
import com.sc.module.structureapi.world.Cardinal;
import com.sc.plugin.PermissionManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.data.DataException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
public class PlanListener implements Listener {

    private final Logger LOGGER = Logger.getLogger(PlanListener.class);
    private Set<UUID> structureProcesses = Collections.synchronizedSet(new HashSet());

    @EventHandler
    public void onPlayerUsePlan(PlayerInteractEvent pie) {
        // Check if plan
        ItemStack planstack = pie.getItem();
        if (!StructurePlan.isStructurePlan(planstack)) {
            return;
        }

        // Check permissions
        Player player = pie.getPlayer();
        if (!PermissionManager.isAllowed(player, PermissionManager.Perms.STRUCTURE_PLACE)) {
            player.sendMessage(ChatColor.RED + "You have no permission to place structures");
            return;
        }

        // Cancel default action as it would damage/remove a block
        pie.setCancelled(true);

        // Get plan ID
        String structurePlanId = StructurePlan.getPlanID(planstack);
        final StructurePlan plan = StructurePlanManager.getInstance().getPlan(structurePlanId);

        // Check if plan is valid
        if (plan == null) {
            if (!SettlerCraft.getInstance().isPlansLoaded()) {
                player.sendMessage(ChatColor.RED + "This plan is invalid, please refund it or throw it away");
            } else {
                player.sendMessage(ChatColor.RED + "Plans aren't loaded yet please wait...");
            }
            return;
        }

        try {
            // Loads schematic
            build(player, pie.getClickedBlock(), SchematicManager.getInstance().getSchematic(plan.getSchematic()), plan, pie.getAction(), planstack);
        } catch (IOException | DataException ex) {
            java.util.logging.Logger.getLogger(PlanListener.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private boolean canPlace(Player player, Vector pos, Cardinal cardinal, Schematic schematic) {
        org.bukkit.World world = player.getWorld();

        if (StructureAPI.overlaps(schematic, world, pos, cardinal)) {
            player.sendMessage(ChatColor.RED + "Structure overlaps another structure");
            return false;
        }

//        if (!WorldGuardUtil.mayClaim(player)) {
//            player.sendMessage(ChatColor.RED + " You have no permission to claim regions");
//            player.sendMessage(ChatColor.RED + " Therefore your are not able to place structures");
//            return false;
//        }
//        if (!WorldGuardUtil.canClaim(player)) {
//            WorldConfiguration wcfg = WorldGuardUtil.getWorldGuard().getGlobalStateManager().get(player.getWorld());
//            RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(player.getWorld());
//            int plyMaxRegionCount = wcfg.getMaxRegionCount(player);
//            int plyCurRegionCount = mgr.getRegionCountOfPlayer(WorldGuardUtil.getLocalPlayer(player));
//            player.sendMessage(ChatColor.RED + " You have reached your region claim limit (" + plyCurRegionCount + "/" + plyMaxRegionCount + ")");
//            player.sendMessage(ChatColor.RED + " Therefore your are not able to place structures");
//            return false;
//        }
        if (StructureAPI.overlapsRegion(player, schematic, world, pos, cardinal)) {
            player.sendMessage(ChatColor.RED + "Structure overlaps a region you don't own");
            return false;
        }
        return true;
    }

    private void build(final Player player, final Block block, final Schematic s, final StructurePlan plan, final Action action, final ItemStack item) {
        // Schematics are preloaded and therefore always available
        // Unless the plan really refers to an schematic file that doesnt exist anymore
        if (s == null) {
            player.sendMessage(ChatColor.RED + "Schematic for for this plan doesn't exist!");
            return;
        }
        com.sk89q.worldedit.world.World world = WorldEditUtil.getWorld(player.getWorld().getName());
        Location l = block.getLocation();
        EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);
        
        
        AsyncStructureAPI.getInstance().create(
                player, 
                plan, 
                player.getWorld(), 
                new Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ()), 
                WorldUtil.getCardinal(player), 
                new AsyncStructureAPI.StructureCallback() {

            @Override
            public void onComplete(Structure structure) {
                if(structure != null) {
                StructureAPI.build(player, structure);
                }
            }
        });
//        SelectionManager slm = SelectionManager.getInstance();
//        // Clicked in air
//        if (block == null
//                || block.getType() == Material.AIR) {
//            LOGGER.info("Clearing simple");
//            // Clear current selection
//            slm.clear(player, true);
//
//            return;
//        }
//
//        // Player Left-Clicked on a block!
//        if (action == Action.LEFT_CLICK_BLOCK) {
//            World world = player.getWorld();
//            org.bukkit.Location location = block.getLocation();
//            int x = location.getBlockX();
//            int y = location.getBlockY();
//            int z = location.getBlockZ();
//
//            Cardinal cardinal = WorldUtil.getCardinal(player);
//            Vector pos1 = new Vector(x, y, z);
//
//            // Retrieve schematic
//            Vector pos2;
//            if (player.isSneaking()) {
//                pos2 = WorldUtil.getPoint2Left(pos1, cardinal, new BlockVector(s.getWidth(), s.getHeight(), s.getLength()));
//            } else {
//                pos2 = WorldUtil.getPoint2Right(pos1, cardinal, new BlockVector(s.getWidth(), s.getHeight(), s.getLength()));
//            }
//
//            if (slm.hasSelection(player, s, pos1, pos2)) {
//                if (canPlace(player, pos1, cardinal, s)) {
//
//                    if (player.isSneaking()) {
//                        // Fix? WTF HOW?
//                        pos1 = WorldUtil.addOffset(pos1, cardinal, (-(s.getLength() - 1)), 0, 0);
//                    }
//
//                    // Remove 1 from player's inventory
//                    if (structureProcesses.contains(player.getUniqueId())) {
//                        return;
//                    }
//                    structureProcesses.add(player.getUniqueId());
//                    AsyncStructureAPI.getInstance().create(player, plan, world, pos1, cardinal, new AsyncStructureAPI.StructureCallback() {
//
//                        @Override
//                        public void onComplete(Structure structure) {
//                            if (structure != null) {
//                                ItemStack stack = item.clone();
//                                stack.setAmount(1);
//                                player.getInventory().removeItem(stack);
//                                player.updateInventory();
//                                structureProcesses.remove(player.getUniqueId());
////                                        StructureAPI.build(SettlerCraft.getInstance(), player.getUniqueId(), structure);
//                            }
//                        }
//                    });
//
//                    return;
//
//                }
//
//                slm.clear(player, false);
//            }
//
//            slm.select(player, s, pos1, pos2, player.isSneaking());
//            if (canPlace(player, pos1, cardinal, s)) {
//                player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " [X] " + ChatColor.RESET + " to " + ChatColor.YELLOW + "confirm");
//                player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
//            } else {
//                slm.clear(player, false);
//            }
//
//        }
    }

//    private boolean handleCUIPlayerSelect(Player player, Block block, StructurePlanV2 plan, Action action) throws IncompleteRegionException {
//        LocalWorld world = SCWorldEditUtil.getWorld(player);
//        StructureManager sm = StructureManager.getInstance();
//        SelectionManager slm = SelectionManager.getInstance();
//        if (block == null || block.getType() == Material.AIR) {
//            slm.clearCUISelection(player, true);
//            return false;
//        }
//        final boolean isSneaking = player.isSneaking();
//
//        org.bukkit.Location target = block.getLocation();
//        int x = target.getBlockX();
//        int y = target.getBlockY();
//        int z = target.getBlockZ();
//        LocalSession session = SCWorldEditUtil.getLocalSession(player);
//        SimpleCardinal cardinal = WorldUtil.getCardinal(player);
//        //                        session.dispatchCUISelection(SCWorldEditUtil.getLocalPlayer(player));
//
//        Location pos1 = new Location(world, new BlockVector(x, y, z));
//        SchematicService service = new SchematicService();
//        StructureSchematic s = service.getSchematicFile(plan.getSchematicChecksum());
//
//        if (s == null) {
//            player.sendMessage(ChatColor.RED + "Schematic for for this plan doesn't exist!");
//            return false;
//        }
////        Location pos2 = WorldUtil.addOffset(location, cardinal, s.getWidth(), s.getHeight(), s.getLength());
//
//        Location pos2;
//        if (isSneaking) {
//            pos2 = WorldUtil.getPoint2Left(pos1, cardinal, new BlockVector(s.getWidth(), s.getHeight(), s.getLength()));
//        } else {
//            pos2 = WorldUtil.getPoint2Right(pos1, cardinal, new BlockVector(s.getWidth(), s.getHeight(), s.getLength()));
//        }
//
//        if (action == Action.LEFT_CLICK_BLOCK) {
//            if (!session.getRegionSelector(world).isDefined()) {
//                slm.CUISelect(player, plan, pos1, pos2);
//                player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " green " + ChatColor.RESET + "square to " + ChatColor.YELLOW + "confirm");
//                player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
//            } else {
//
//                if (slm.matchesCUISelection(player, plan, pos1, pos2)) {
//                    slm.CUISelect(player, plan, pos1, pos2);
//                    if (isSneaking) {
//                        // Fix? WTF How?
//                        pos1 = WorldUtil.addOffset(pos1, cardinal, (-(s.getLength() - 1)), 0, 0);
//                    }
//                    if (sm.place(player, plan, pos1, cardinal) != null) {
//                        slm.clearCUISelection(player, false);
//                        return true;
//                    }
//
////                    }
//                } else {
//                    slm.CUISelect(player, plan, pos1, pos2);
//                    if (canPlace(player, pos1, cardinal, plan, s)) {
//                        player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " green " + ChatColor.RESET + "square to " + ChatColor.YELLOW + "confirm");
//                        player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
//                    }
//                }
//            }
//        } else {
//            slm.clearCUISelection(player, true);
//        }
//        return false;
//    }
}
