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
package com.sc.api.structure.listeners;

import com.sc.api.structure.SCStructureAPI;
import com.sc.api.structure.AsyncBuilder;
import com.sc.api.structure.ConstructionManager;
import com.sc.api.structure.construction.progress.ConstructionException;
import com.sc.api.structure.entity.Structure;
import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.entity.world.SimpleCardinal;
import com.sc.api.structure.persistence.service.StructurePlanService;
import com.sc.api.structure.persistence.service.StructureService;
import com.sc.api.structure.util.WorldUtil;
import com.sc.api.structure.util.plugins.SCWorldEditUtil;
import static com.sc.api.structure.util.plugins.SCWorldEditUtil.getLocalSession;
import com.sc.api.structure.util.plugins.SCWorldGuardUtil;
import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.bukkit.WorldConfiguration;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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

    @EventHandler
    public void onPlayerInteractDebug(PlayerInteractEvent pie) {
        if (pie.getAction() == Action.RIGHT_CLICK_AIR || pie.getAction() == Action.RIGHT_CLICK_BLOCK) {
//            org.bukkit.Location l = pie.getClickedBlock().getLocation();
//            DefaultNmsBlock dmBlock = DefaultNmsBlock.get(l.getWorld(), SCWorldEditUtil.getLocation(l).getPosition(), l.getBlock().getTypeId(), new Byte(l.getBlock().getData()).intValue());
//            if(dmBlock != null && dmBlock.hasNbtData()) {
//                System.out.println(dmBlock.getNbtData());
//            }

        }
//        System.out.println(tags.get("sid").getValue());
//        System.out.println(tags.get("progress").getValue());
    }

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

        boolean defaultFeedBack = SCStructureAPI.getSCStructureAPI().getConfig().contains("default-feedback") ? SCStructureAPI.getSCStructureAPI().getConfig().getBoolean("default-feedback") : true;

        StructurePlanService service = new StructurePlanService();
        StructurePlan plan = service.getPlan(pie.getItem().getItemMeta().getDisplayName());
        Player player = pie.getPlayer();
        if (plan != null
                && pie.getClickedBlock() != null
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
        if (!ConstructionManager.mayClaim(player)) {
            player.sendMessage(ChatColor.RED + " You have no permission to claim regions");
            player.sendMessage(ChatColor.RED + " Therefore your are not able to place structures");
            return false;
        }

        if (!ConstructionManager.canClaim(player)) {
            WorldConfiguration wcfg = SCWorldGuardUtil.getWorldGuard().getGlobalStateManager().get(player.getWorld());
            RegionManager mgr = SCWorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(player.getWorld());
            int plyMaxRegionCount = wcfg.getMaxRegionCount(player);
            int plyCurRegionCount = mgr.getRegionCountOfPlayer(SCWorldGuardUtil.getLocalPlayer(player));
            player.sendMessage(ChatColor.RED + " You have reached your region claim limit (" + plyCurRegionCount + "/" + plyMaxRegionCount + ")");
            player.sendMessage(ChatColor.RED + " Therefore your are not able to place structures");
            return false;
        }

        if (ConstructionManager.overlapsStructure(plan, location, cardinal)) {
            player.sendMessage(ChatColor.RED + " Structure will overlap another structure");
            return false;
        }

        if (ConstructionManager.overlapsUnowned(player, plan, location, cardinal)) {
            player.sendMessage(ChatColor.RED + " Structure overlaps a region you don't own");
            return false;
        }
        return true;
    }

    private boolean handleSimplePlayerSelect(Player player, org.bukkit.Location target, StructurePlan plan, Action action, boolean defaultFeedBack) {
        if (action == Action.LEFT_CLICK_BLOCK) {
            LocalWorld world = SCWorldEditUtil.getLocalWorld(player);
            int x = target.getBlockX();
            int y = target.getBlockY();
            int z = target.getBlockZ();
            Location location = new Location(world, new BlockWorldVector(world, x, y, z));
            SimpleCardinal cardinal = WorldUtil.getCardinal(player);
            Structure structure = new Structure(player.getName(), location, WorldUtil.getCardinal(player), plan);

            if (canPlace(player, location, cardinal, plan)) {
                StructureService service = new StructureService();
                structure = service.save(structure);

                ProtectedRegion region = ConstructionManager.claimGround(player, structure);
                if (region == null) {
                    service.delete(structure);
                    player.sendMessage(ChatColor.RED + " Failed to claim ground for structure");
                    return false;
                }
                try {
                    structure.setStructureRegionId(region.getId());
                    structure = service.save(structure);
                    System.out.println("Clicked: " + location);
                    AsyncBuilder.placeStructure(player, structure);
                } catch (ConstructionException ex) {
                    service.delete(structure);
                    SCWorldGuardUtil.getGlobalRegionManager(target.getWorld()).removeRegion(region.getId());
                    Logger.getLogger(StructurePlanListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return false;
    }

    private boolean handleCUIPlayerSelect(Player player, org.bukkit.Location target, StructurePlan plan, Action action, boolean defaultFeedBack) throws IncompleteRegionException {
        LocalWorld world = SCWorldEditUtil.getLocalWorld(player);
        int x = target.getBlockX();
        int y = target.getBlockY();
        int z = target.getBlockZ();
        Location location = new Location(world, new BlockWorldVector(world, x, y, z));
        LocalSession session = SCWorldEditUtil.getLocalSession(player);
        SimpleCardinal direction = WorldUtil.getCardinal(player);

        Structure structure = new Structure(player.getName(), location, WorldUtil.getCardinal(player), plan);
//         SCConstrucStructureBuilderce().placeSafe(player, structure, true, true);
        if (action == Action.LEFT_CLICK_BLOCK) {
            if (!session.getRegionSelector(world).isDefined()) {
                ConstructionManager.select(player, location, direction, structure.getPlan().getSchematic());
                player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " green " + ChatColor.RESET + "square to " + ChatColor.YELLOW + "confirm");
                player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
            } else {

                CuboidRegion oldRegion = CuboidRegion.makeCuboid(session.getRegionSelector(world).getRegion());
                ConstructionManager.select(player, location, direction, structure.getPlan().getSchematic());
                CuboidRegion newRegion = CuboidRegion.makeCuboid(session.getRegionSelector(world).getRegion());
                if (oldRegion.getPos1().equals(newRegion.getPos1()) && oldRegion.getPos2().equals(newRegion.getPos2())) {
                    if (canPlace(player, location, direction, plan)) {
                        session.getRegionSelector(world).clear();
                        session.dispatchCUISelection(SCWorldEditUtil.getLocalPlayer(player));
                        StructureService service = new StructureService();
                        structure = service.save(structure);
//                        System.out.println("Claiming ground");
                        ProtectedRegion region = ConstructionManager.claimGround(player, structure);
                        if (region == null) {
                            service.delete(structure);
                            player.sendMessage(ChatColor.RED + " Failed to claim ground for structure");
                            return false;
                        }

                        try {
                            structure.setStructureRegionId(region.getId());
                            structure = service.save(structure);
//                            System.out.println("Clicked: " + location);
                            AsyncBuilder.placeStructure(player, structure);
                        } catch (ConstructionException ex) {
                            service.delete(structure);
                            SCWorldGuardUtil.getGlobalRegionManager(target.getWorld()).removeRegion(region.getId());
                            Logger.getLogger(StructurePlanListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return true;
                    }
                } else {
                    ConstructionManager.select(player, location, direction, structure.getPlan().getSchematic());
                    if (ConstructionManager.overlapsStructure(structure)) {
                        player.sendMessage(ChatColor.RED + "Structure overlaps another structure");
                    } else if (ConstructionManager.overlapsUnowned(player, structure)) {
                        player.sendMessage(ChatColor.RED + "Structure overlaps a region u dont own");
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " green " + ChatColor.RESET + "square to " + ChatColor.YELLOW + "confirm");
                        player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
                    }
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
