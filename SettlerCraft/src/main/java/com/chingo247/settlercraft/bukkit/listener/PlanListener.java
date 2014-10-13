/*
 * Copyright (C) 2014 Chingo247
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
package com.chingo247.settlercraft.bukkit.listener;

import com.chingo247.settlercraft.plugin.PermissionManager;
import com.chingo247.settlercraft.plugin.SettlerCraft;
import com.chingo247.settlercraft.structure.AsyncStructureAPI;
import com.chingo247.settlercraft.structure.StructureAPI;
import com.chingo247.settlercraft.structure.entities.structure.Structure;
import com.chingo247.settlercraft.structure.entities.world.Dimension;
import com.chingo247.settlercraft.structure.entities.world.Direction;
import static com.chingo247.settlercraft.structure.entities.world.Direction.EAST;
import static com.chingo247.settlercraft.structure.entities.world.Direction.NORTH;
import static com.chingo247.settlercraft.structure.entities.world.Direction.SOUTH;
import static com.chingo247.settlercraft.structure.entities.world.Direction.WEST;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.plan.StructurePlanManager;
import com.chingo247.settlercraft.structure.schematic.Schematic;
import com.chingo247.settlercraft.structure.schematic.SchematicManager;
import com.chingo247.settlercraft.structure.selection.CUISelectionManager;
import com.chingo247.settlercraft.structure.selection.SelectionManager;
import com.chingo247.settlercraft.util.SchematicUtil;
import com.chingo247.settlercraft.util.WorldEditUtil;
import com.chingo247.settlercraft.util.WorldUtil;
import com.sc.module.menuapi.menus.menu.util.EconomyUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.data.DataException;
import java.io.IOException;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

    @EventHandler
    public void onPlayerUsePlan(final PlayerInteractEvent pie) {
        // Check if plan
        ItemStack planstack = pie.getItem();
        if (!StructurePlan.isStructurePlan(planstack)) {
            return;
        }

        // Check permissions
        final Player player = pie.getPlayer();
        if (!PermissionManager.isAllowed(player, PermissionManager.Perms.STRUCTURE_PLACE)) {
            player.sendMessage(ChatColor.RED + "You have no permission to place structures");
            return;
        }

        // Cancel default action as it would damage/remove a block
        pie.setCancelled(true);
        
        if(!SettlerCraft.getInstance().isPlansLoaded()) {
            player.sendMessage(ChatColor.RED + "Plans are not loaded yet... please wait...");
            return;
        }

        // Get plan ID
        String structurePlanId = StructurePlan.getPlanID(planstack);
        final StructurePlan plan = StructurePlanManager.getInstance().getPlan(structurePlanId);

        // Check if plan is valid
        if (plan == null) {
            double value = StructurePlan.getValue(planstack);
            value *= planstack.getAmount();
            player.getInventory().remove(planstack);
            player.updateInventory();
            player.sendMessage(new String[]{ChatColor.RED + "This plan's data is missing...", SettlerCraft.MSG_PREFIX + "Refunded: " + ChatColor.GOLD + value});
            EconomyUtil.getInstance().pay(player.getUniqueId(), value);
            return;
        }

        // Check if schematic is valid
        if (!plan.getSchematic().exists()) {
            double value = StructurePlan.getValue(planstack);
            value *= planstack.getAmount();
            player.getInventory().remove(planstack);
            player.updateInventory();
            player.sendMessage(new String[]{ChatColor.RED + "This plan's schematic is missing...", SettlerCraft.MSG_PREFIX + "Refunded: "+ ChatColor.GOLD + value});
            EconomyUtil.getInstance().pay(player.getUniqueId(), value);
            return;
        }

        
        
        LocalPlayer lp = WorldEditUtil.getLocalPlayer(player);
        LocalSession ls = WorldEdit.getInstance().getSession(lp);

        if (pie.getClickedBlock() == null || pie.getAction() != Action.LEFT_CLICK_BLOCK /*&& pie.getAction() != Action.RIGHT_CLICK_BLOCK*/) {

            if (ls != null && ls.hasCUISupport() && CUISelectionManager.getInstance().hasSelection(player)) {
                CUISelectionManager.getInstance().clear(player, true);
            } else if (SelectionManager.getInstance().hasSelection(player)) {
                SelectionManager.getInstance().clear(player, true);
            }
            return; // Deselected & Done
        }


        try {
            Schematic schematic = SchematicManager.getInstance().getSchematic(plan.getSchematic());
            Location l = pie.getClickedBlock().getLocation();
            
            // CUI Select
            if(ls.hasCUISupport()) {
                handleCUIPlayer(player, l, plan, schematic, planstack);
            } else if(Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null) {
            // Hologram Select
                handleHoloPlayer(player, l, plan, schematic, planstack);
            } else {
            // No Selection
                Vector pos = new Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ());
                
                
                AsyncStructureAPI.getInstance().create(player, plan, player.getWorld(), pos, WorldUtil.getDirection(player), new AsyncStructureAPI.StructureCallback() {

                    @Override
                    public void onComplete(Structure structure) {
                        if(structure != null) {
                            ItemStack clone = pie.getItem().clone();
                            clone.setAmount(1);
                            player.getInventory().remove(clone);
                            
                            player.updateInventory();
                            StructureAPI.build(player, structure);
                        }
                    }
                });
            }

        } catch (IOException | DataException ex) {
            java.util.logging.Logger.getLogger(PlanListener.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Used to to getPlan the secondary position when selecting. So that the green square is always at
     * the same place as the clicked block and the secondary always across.
     *
     * @param point1
     * @param direction
     * @param size
     * @return point2
     */
    private Vector getPoint2Right(Vector point1, Direction direction, Vector size) {
        switch (direction) {
            case EAST:
                return point1.add(size.subtract(1, 1, 1));
            case SOUTH:
                return point1.add(-(size.getBlockZ() - 1), size.getBlockY() - 1, (size.getBlockX() - 1));
            case WEST:
                return point1.add(-(size.getBlockX() - 1), size.getBlockY() - 1, -(size.getBlockZ() - 1));
            case NORTH:
                return point1.add((size.getBlockZ() - 1), size.getBlockY() - 1, -(size.getBlockX() - 1));
            default:
                throw new AssertionError("unreachable");
        }
    }

    /**
     * Used to to getPlan the secondary position when selecting. So that the green square is always at
     * the same place as the clicked block and the secondary always across.
     *
     * @param point1
     * @param direction
     * @param size
     * @return point2
     */
    private Vector getPoint2Left(Vector point1, Direction direction, Vector size) {
        switch (direction) {
            case EAST:
                return point1.add((size.getBlockX() - 1), size.getBlockY() - 1, -(size.getBlockZ() - 1));
            case SOUTH:
                return point1.add((size.getBlockZ() - 1), size.getBlockY() - 1, (size.getBlockX() - 1));
            case WEST:
                return point1.add(-(size.getBlockX() - 1), size.getBlockY() - 1, (size.getBlockZ() - 1));
            case NORTH:
                return point1.add(-(size.getBlockZ() - 1), size.getBlockY() - 1, -(size.getBlockX() - 1));
            default:
                throw new AssertionError("unreachable");
        }
    }

    private boolean canPlace(Player player, Vector pos, Direction direction, Schematic schematic) {
        org.bukkit.World world = player.getWorld();

        Dimension dimension = SchematicUtil.calculateDimension(schematic, pos, direction);

        if (StructureAPI.overlapsStructures(world, dimension)) {
            player.sendMessage(ChatColor.RED + "Structure overlaps another structure");
            return false;
        }

        if (StructureAPI.overlapsRegion(player, world, dimension)) {
            player.sendMessage(ChatColor.RED + "Structure overlaps a region you don't own");
            return false;
        }
        return true;
    }



    private void handleCUIPlayer(final Player player, final Location location, StructurePlan plan, final Schematic schematic, final ItemStack planstack) {
        boolean toLeft = player.isSneaking();
        LocalPlayer localPlayer = WorldEditUtil.getLocalPlayer(player);
        LocalSession session = WorldEdit.getInstance().getSession(localPlayer);

        Direction direction = WorldUtil.getDirection(player);
        Vector pos1 = new BlockVector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Vector pos2;
        if (toLeft) {
            pos2 = getPoint2Left(pos1, direction, schematic.getSize());
        } else {
            pos2 = getPoint2Right(pos1, direction, schematic.getSize());
        }

        /**
         * Haven't selected before
         */
        if (!session.getRegionSelector(localPlayer.getWorld()).isDefined()) {
            CUISelectionManager.getInstance().select(player, schematic, pos1, pos2);
            player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " green " + ChatColor.RESET + "square to " + ChatColor.YELLOW + "confirm");
            player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
        } else if (CUISelectionManager.getInstance().matchesSelection(player, schematic, pos1, pos2)) {
            if (toLeft) {
                // Fix WTF HOW?!!1?
                pos1 = WorldUtil.translateLocation(pos1, direction, (-(schematic.getLength() - 1)), 0, 0);
            }
            
            if(!canPlace(player, pos1, direction, schematic)) {
                return;
            }

            AsyncStructureAPI.getInstance().create(player, plan, player.getWorld(), pos1, direction, new AsyncStructureAPI.StructureCallback() {

                @Override
                public void onComplete(Structure structure) {
                    CUISelectionManager.getInstance().clear(player, false);
                    if (structure != null) {
                        ItemStack clone = planstack.clone();
                        clone.setAmount(1);
                        player.getInventory().remove(clone);
                        player.updateInventory();
                        StructureAPI.build(player, structure);
                    }
                }
            });
        } else {
            CUISelectionManager.getInstance().select(player, schematic, pos1, pos2);
            if (canPlace(player, pos1, direction, schematic)) {
                player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " green " + ChatColor.RESET + "square to " + ChatColor.YELLOW + "confirm");
                player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
            }
        }

    }

    private void handleHoloPlayer(final Player player, Location l, StructurePlan plan, Schematic schematic, final ItemStack planstack) {
        boolean toLeft = player.isSneaking();

        Direction direction = WorldUtil.getDirection(player);
        Vector pos1 = new BlockVector(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        Vector pos2;
        if (toLeft) {
            pos2 = getPoint2Left(pos1, direction, schematic.getSize());
        } else {
            pos2 = getPoint2Right(pos1, direction, schematic.getSize());
        }

        /**
         * Haven't selected before
         */
        if (!SelectionManager.getInstance().hasSelection(player)) {
            SelectionManager.getInstance().select(player, schematic, pos1, pos2, toLeft);
            player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " green " + ChatColor.RESET + "square to " + ChatColor.YELLOW + "confirm");
            player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
        } else if (SelectionManager.getInstance().matchesSelection(player, schematic, pos1, pos2)) {
            if (toLeft) {
                // Fix WTF HOW?!!1?
                pos1 = WorldUtil.translateLocation(pos1, direction, (-(schematic.getLength() - 1)), 0, 0);
            }

            if (canPlace(player, pos1, direction, schematic)) {
                AsyncStructureAPI.getInstance().create(player, plan, player.getWorld(), pos1, direction, new AsyncStructureAPI.StructureCallback() {

                    @Override
                    public void onComplete(Structure structure) {
                        SelectionManager.getInstance().clear(player, false);
                        if (structure != null) {
                            ItemStack clone = planstack.clone();
                            clone.setAmount(1);
                            player.getInventory().remove(clone);
                            StructureAPI.build(player, structure);
                        }
                    }
                });
            }
        } else {
            SelectionManager.getInstance().clear(player, false);
            SelectionManager.getInstance().select(player, schematic, pos1, pos2, toLeft);
            if (canPlace(player, pos1, direction, schematic)) {
                player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " green " + ChatColor.RESET + "square to " + ChatColor.YELLOW + "confirm");
                player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
            }
        }
    }
}
