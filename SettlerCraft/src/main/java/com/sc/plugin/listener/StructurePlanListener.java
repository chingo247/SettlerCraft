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
package com.sc.plugin.listener;

import com.sc.api.structure.construction.StructureManager;
import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.entity.world.SimpleCardinal;
import com.sc.api.structure.entity.world.WorldDimension;
import com.sc.api.structure.plan.PlanManager;
import com.sc.api.structure.util.WorldUtil;
import com.sc.api.structure.util.plugins.SCWorldEditUtil;
import static com.sc.api.structure.util.plugins.SCWorldEditUtil.getLocalSession;
import com.sc.api.structure.util.plugins.SCWorldGuardUtil;
import com.sc.plugin.SettlerCraft;
import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.bukkit.WorldConfiguration;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
public class StructurePlanListener implements Listener {

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

        boolean defaultFeedBack = SettlerCraft.getSettlerCraft().getConfig().contains("default-feedback") ? SettlerCraft.getSettlerCraft().getConfig().getBoolean("default-feedback") : true;

        ItemStack planStack = pie.getItem();
        if (!StructurePlan.isStructurePlan(planStack)) {
            return;
        }
        String structurePlanId = StructurePlan.getPlanID(planStack);
        System.out.println("PlanID: " + structurePlanId);
        StructurePlan plan = PlanManager.getInstance().getPlan(structurePlanId);
        Player player = pie.getPlayer();
        if (plan == null) {
            player.sendMessage(ChatColor.RED + "This plan is not valid anymore, please refund it or throw it away");
            return;
        }

        if (pie.getClickedBlock() != null
                && pie.getClickedBlock().getType() != Material.AIR) {

            pie.setCancelled(true); // default action would break a block

            LocalSession session = getLocalSession(pie.getPlayer());

            if (session.hasCUISupport()) {
                try {
                    if (handleCUIPlayerSelect(player, pie.getClickedBlock().getLocation(), plan, pie.getAction(), defaultFeedBack)) {
                        ItemStack stack = pie.getItem().clone();
                        stack.setAmount(1);
                        pie.getPlayer().getInventory().removeItem(stack);
                        pie.getPlayer().updateInventory();

                    }
                } catch (IncompleteRegionException ex) {
                    Logger.getLogger(StructurePlanListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                if (handleSimplePlayerSelect(player, pie.getClickedBlock().getLocation(), plan, pie.getAction(), defaultFeedBack)) {
                    ItemStack stack = pie.getItem().clone();
                    stack.setAmount(1);
                    pie.getPlayer().getInventory().removeItem(stack);
                    pie.getPlayer().updateInventory();
                }

            }

        }
    }

    private boolean canPlace(Player player, Location location, SimpleCardinal cardinal, StructurePlan plan) {
        StructureManager sm = StructureManager.getInstance();

        if (!sm.mayClaim(player)) {
            player.sendMessage(ChatColor.RED + " You have no permission to claim regions");
            player.sendMessage(ChatColor.RED + " Therefore your are not able to place structures");
            return false;
        }

        if (!sm.canClaim(player)) {
            WorldConfiguration wcfg = SCWorldGuardUtil.getWorldGuard().getGlobalStateManager().get(player.getWorld());
            RegionManager mgr = SCWorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(player.getWorld());
            int plyMaxRegionCount = wcfg.getMaxRegionCount(player);
            int plyCurRegionCount = mgr.getRegionCountOfPlayer(SCWorldGuardUtil.getLocalPlayer(player));
            player.sendMessage(ChatColor.RED + " You have reached your region claim limit (" + plyCurRegionCount + "/" + plyMaxRegionCount + ")");
            player.sendMessage(ChatColor.RED + " Therefore your are not able to place structures");
            return false;
        }

        if (sm.overlaps(plan, location, cardinal)) {
            player.sendMessage(ChatColor.RED + " Structure will overlap another structure");
            return false;
        }

        if (sm.overlapsUnowned(player, plan, location, cardinal)) {
            player.sendMessage(ChatColor.RED + " Structure overlaps a region you don't own");
            return false;
        }
        return true;
    }

    private boolean handleSimplePlayerSelect(Player player, org.bukkit.Location location, StructurePlan plan, Action action, boolean defaultFeedBack) {
        if (action == Action.LEFT_CLICK_BLOCK) {
            LocalWorld world = SCWorldEditUtil.getLocalWorld(player);
            int x = location.getBlockX();
            int y = location.getBlockY();
            int z = location.getBlockZ();

            SimpleCardinal cardinal = WorldUtil.getCardinal(player);
            Location pos1 = new Location(world, new BlockWorldVector(world, x, y, z));
            CuboidClipboard structureSchematic = PlanManager.getInstance().getClipBoard(plan.getChecksum());
            Location pos2 = WorldUtil.getPos2(pos1, cardinal, structureSchematic);
            WorldDimension dimension = new WorldDimension(pos1, pos2);  // Included sign
            StructureManager sm = StructureManager.getInstance();

            if (canPlace(player, pos1, cardinal, plan)) {
                sm.construct(player, plan, pos1, cardinal);
            }
        }
        return false;
    }

    private boolean handleCUIPlayerSelect(Player player, org.bukkit.Location target, StructurePlan plan, Action action, boolean defaultFeedBack) throws IncompleteRegionException {
        LocalWorld world = SCWorldEditUtil.getLocalWorld(player);
        int x = target.getBlockX();
        int y = target.getBlockY();
        int z = target.getBlockZ();
        LocalSession session = SCWorldEditUtil.getLocalSession(player);
        SimpleCardinal cardinal = WorldUtil.getCardinal(player);
        Location pos1 = new Location(world, new BlockWorldVector(world, x, y, z));
        CuboidClipboard structureSchematic = PlanManager.getInstance().getClipBoard(plan.getChecksum());
        Location pos2 = WorldUtil.getPos2(pos1, cardinal, structureSchematic);
        StructureManager sm = StructureManager.getInstance();

        if (action == Action.LEFT_CLICK_BLOCK) {
            if (!session.getRegionSelector(world).isDefined()) {
                SCWorldEditUtil.select(player, pos1, pos2);
                player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " green " + ChatColor.RESET + "square to " + ChatColor.YELLOW + "confirm");
                player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
            } else {

                CuboidRegion oldRegion = CuboidRegion.makeCuboid(session.getRegionSelector(world).getRegion());
                SCWorldEditUtil.select(player, pos1, pos2);
                CuboidRegion newRegion = CuboidRegion.makeCuboid(session.getRegionSelector(world).getRegion());
                if (oldRegion.getPos1().equals(newRegion.getPos1()) && oldRegion.getPos2().equals(newRegion.getPos2())) {
//                    if (canPlace(player, pos1, cardinal, plan)) {
//                        session.getRegionSelector(world).clear();
//                        session.dispatchCUISelection(SCWorldEditUtil.getLocalPlayer(player));
                    sm.construct(player, plan, pos1, cardinal);
                    session.getRegionSelector(world).clear();
                    session.dispatchCUISelection(SCWorldEditUtil.getLocalPlayer(player));

//                    }
                } else {
                    SCWorldEditUtil.select(player, pos1, pos2);
//                    if (sm.overlaps(plan, pos1, cardinal)) {
//                        player.sendMessage(ChatColor.RED + "Structure overlaps another structure");
//                    } else if (sm.overlapsUnowned(player, plan, pos1, cardinal)) {
//                        player.sendMessage(ChatColor.RED + "Structure overlaps a region u dont own");
//                    } else {
                    player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " green " + ChatColor.RESET + "square to " + ChatColor.YELLOW + "confirm");
                    player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
//                    }
                }
            }
        } else {
            if (session.getRegionSelector(world).isDefined()) {
                session.getRegionSelector(world).clear();
                session.dispatchCUISelection(SCWorldEditUtil.getLocalPlayer(player));
                player.sendMessage("Cleared selection");
            }
        }
        return false;
    }

}
