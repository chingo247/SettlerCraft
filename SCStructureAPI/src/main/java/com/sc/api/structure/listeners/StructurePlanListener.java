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
import com.sc.api.structure.construction.builder.SCStructureBuilder;
import com.sc.api.structure.model.structure.Structure;
import com.sc.api.structure.model.structure.plan.StructurePlan;
import com.sc.api.structure.model.structure.world.SimpleCardinal;
import com.sc.api.structure.persistence.StructurePlanService;
import com.sc.api.structure.util.WorldUtil;
import com.sc.api.structure.util.plugins.WorldEditUtil;
import static com.sc.api.structure.util.plugins.WorldEditUtil.getLocalSession;
import com.sk89q.worldedit.BlockWorldVector;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.regions.CuboidRegion;
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
                }
                catch (IncompleteRegionException ex) {
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

    private boolean handleSimplePlayerSelect(Player player, org.bukkit.Location target, StructurePlan plan, Action action, boolean defaultFeedBack) {
        if (action == Action.LEFT_CLICK_BLOCK) {
            LocalWorld world = WorldEditUtil.getLocalWorld(player);
            int x = target.getBlockX();
            int y = target.getBlockY();
            int z = target.getBlockZ();
            Location location = new Location(world, new BlockWorldVector(world, x, y, z));
            
            Structure structure = new Structure(player.getName(), location, WorldUtil.getDirection(player), plan);
            if (SCStructureBuilder.overlaps(location, WorldUtil.getDirection(player), plan)) {
                player.sendMessage(ChatColor.RED + "Structure overlaps another structure");
            } else {
                player.sendMessage(ChatColor.YELLOW + "Placing structure");
                SCStructureBuilder.placeStructure(player, location, WorldUtil.getDirection(player), plan, defaultFeedBack);
                return true;
            }
        }
        return false;
    }

    private boolean handleCUIPlayerSelect(Player player, org.bukkit.Location target, StructurePlan plan, Action action, boolean defaultFeedBack) throws IncompleteRegionException {
        LocalWorld world = WorldEditUtil.getLocalWorld(player);
        int x = target.getBlockX();
        int y = target.getBlockY();
        int z = target.getBlockZ();
        Location location = new Location(world, new BlockWorldVector(world, x, y, z));
        LocalSession session = WorldEditUtil.getLocalSession(player);
        SimpleCardinal direction = WorldUtil.getDirection(player);
        
        Structure structure = new Structure(player.getName(), location, WorldUtil.getDirection(player), plan);
        if (action == Action.LEFT_CLICK_BLOCK) {
            if (!session.getRegionSelector(world).isDefined()) {
                SCStructureBuilder.select(player, structure);
                player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " green " + ChatColor.RESET + "square to " + ChatColor.YELLOW + "confirm");
                player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
            } else {

                CuboidRegion oldRegion = CuboidRegion.makeCuboid(session.getRegionSelector(world).getRegion());
                SCStructureBuilder.select(player, structure);
                CuboidRegion newRegion = CuboidRegion.makeCuboid(session.getRegionSelector(world).getRegion());
                if (oldRegion.getPos1().equals(newRegion.getPos1()) && oldRegion.getPos2().equals(newRegion.getPos2())) {
                    if (SCStructureBuilder.placeStructure(player, location, direction, plan, defaultFeedBack)) {
                        session.getRegionSelector(world).clear();
                        session.dispatchCUISelection(WorldEditUtil.getLocalPlayer(player));
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "Structure overlaps another structure");
                    }
                } else {
                    SCStructureBuilder.select(player, structure);
                    if (SCStructureBuilder.overlaps(location, direction, plan)) {
                        player.sendMessage(ChatColor.RED + "Structure overlaps another structure");
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " green " + ChatColor.RESET + "square to " + ChatColor.YELLOW + "confirm");
                        player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
                    }
                }
            }
        } else {
            if (session.getRegionSelector(world).isDefined()) {
                session.getRegionSelector(world).clear();
                session.dispatchCUISelection(WorldEditUtil.getLocalPlayer(player));
                player.sendMessage("Cleared selection");
            }
        }
        return false;
    }
}
