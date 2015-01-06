/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft.bukkit.listener;

import com.chingo247.menu.util.EconomyUtil;
import com.chingo247.settlercraft.bukkit.BukkitStructureAPI;
import com.chingo247.settlercraft.bukkit.PermissionManager;
import com.chingo247.settlercraft.bukkit.SettlerCraftPlugin;
import com.chingo247.settlercraft.bukkit.WorldEditUtil;
import com.chingo247.settlercraft.bukkit.selection.SelectionManager;
import com.chingo247.settlercraft.structureapi.construction.options.BuildOptions;
import com.chingo247.settlercraft.structureapi.exception.StructureException;
import com.chingo247.settlercraft.structureapi.persistence.hibernate.SchematicDataDAO;
import com.chingo247.settlercraft.structureapi.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.selection.CUISelectionManager;
import com.chingo247.settlercraft.structureapi.structure.schematic.SchematicData;
import com.chingo247.settlercraft.structureapi.structure.old.NopeStructure;
import com.chingo247.settlercraft.util.SchematicUtil;
import com.chingo247.settlercraft.util.WorldUtil;
import com.chingo247.settlercraft.structureapi.structure.regions.CuboidDimension;
import com.chingo247.settlercraft.structureapi.world.Direction;
import static com.chingo247.settlercraft.structureapi.world.Direction.EAST;
import static com.chingo247.settlercraft.structureapi.world.Direction.NORTH;
import static com.chingo247.settlercraft.structureapi.world.Direction.SOUTH;
import static com.chingo247.settlercraft.structureapi.world.Direction.WEST;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private final SettlerCraftPlugin settlerCraft;
    private final BukkitStructureAPI structureAPI;
    private final SchematicDataDAO schematicDataDAO;

    public PlanListener(SettlerCraftPlugin settlerCraft) {
        this.settlerCraft = settlerCraft;
        this.structureAPI = settlerCraft.getStructureAPI();
        this.schematicDataDAO = new SchematicDataDAO();
    }

    @EventHandler
    public void onPlayerUsePlan(final PlayerInteractEvent pie) {
        // Check if plan
        final ItemStack planstack = pie.getItem();
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

        if (!settlerCraft.getPlanMenu().isEnabled()) {
            player.sendMessage(ChatColor.RED + "Plans are not loaded yet... please wait...");
            return;
        }

        // Get plan ID
        String structurePlanId = StructurePlan.getPlanID(planstack);
        final StructurePlan plan = settlerCraft.getStructureAPI().getStructurePlanManager().getPlan(structurePlanId);

        // Check if plan is valid
        if (plan == null) {
            double value = StructurePlan.getValue(planstack);
            value *= planstack.getAmount();
            player.getInventory().remove(planstack);
            player.updateInventory();
            player.sendMessage(new String[]{ChatColor.RED + "This plan's data is missing...", SettlerCraftPlugin.MSG_PREFIX + "Refunded: " + ChatColor.GOLD + value});
            EconomyUtil.getInstance().pay(player.getUniqueId(), value);
            return;
        }

        // Check if schematic is valid
        if (!plan.getSchematic().exists()) {
            double value = StructurePlan.getValue(planstack);
            value *= planstack.getAmount();
            player.getInventory().remove(planstack);
            player.updateInventory();
            player.sendMessage(new String[]{ChatColor.RED + "This plan's schematic is missing...", SettlerCraftPlugin.MSG_PREFIX + "Refunded: " + ChatColor.GOLD + value});
            EconomyUtil.getInstance().pay(player.getUniqueId(), value);
            return;
        }

        LocalPlayer lp = WorldEditUtil.wrapPlayer(player);
        final LocalSession ls = WorldEdit.getInstance().getSession(lp);

        if (pie.getClickedBlock() == null || pie.getAction() != Action.LEFT_CLICK_BLOCK /*&& pie.getAction() != Action.RIGHT_CLICK_BLOCK*/) {

            if (ls != null && ls.hasCUISupport() && CUISelectionManager.getInstance().hasSelection(player)) {
                CUISelectionManager.getInstance().clear(player, true);
            } else if (SelectionManager.getInstance().hasSelection(player)) {
                SelectionManager.getInstance().clear(player, true);
            }
            return; // Deselected & Done
        }

//                    if (!structureAPI.getSchematicManager().hasSchematic(plan.getChecksum())) {
//                        player.sendMessage(SettlerCraftPlugin.MSG_PREFIX + "Loading schematic...");
//                    }
        SchematicData schematic = schematicDataDAO.find(plan.getChecksum());
        Location l = pie.getClickedBlock().getLocation();

        // CUI Select
        if (ls.hasCUISupport()) {
            System.out.println("Handle CUI player");
            handleCUIPlayer(player, l, plan, schematic, planstack);
        } else if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null) {
            // Hologram Select
            System.out.println("Handle HOLO player");
            handleHoloPlayer(player, l, plan, schematic, planstack);
        } else {
            // No Selection
            System.out.println("Handle NO-SELECTION player");
            Vector pos = new Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ());

            if (canPlace(player, pos, WorldUtil.getDirection(player), schematic)) {
                NopeStructure structure;
                try {
                    structure = structureAPI.create(player, plan, l, WorldUtil.getDirection(player));
                    ItemStack clone = pie.getItem().clone();
                    clone.setAmount(1);
                    player.getInventory().remove(clone);
                    player.updateInventory();
                    structureAPI.build(player, structure, new BuildOptions(false), true);
                } catch (StructureException ex) {
                    player.sendMessage(ex.getMessage());
                }
            }
        }

    }

    /**
     * Used to to getPlan the secondary position when selecting. So that the green square is always
     * at the same place as the clicked block and the secondary always across.
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
     * Used to to getPlan the secondary position when selecting. So that the green square is always
     * at the same place as the clicked block and the secondary always across.
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

    private boolean canPlace(Player player, Vector pos, Direction direction, SchematicData schematic) {
        org.bukkit.World world = player.getWorld();
        CuboidDimension dimension = SchematicUtil.calculateDimension(schematic, pos, direction);

        if (structureAPI.overlaps(world, dimension)) {
            player.sendMessage(ChatColor.RED + "Structure overlaps another structure");
            return false;
        }

        if (structureAPI.overlaps(player, world, dimension)) {
            player.sendMessage(ChatColor.RED + "Structure overlaps a region you don't own");
            return false;
        }

        return true;
    }

    private void handleCUIPlayer(final Player player, final Location location, StructurePlan plan, final SchematicData schematic, final ItemStack planstack) {
        boolean toLeft = player.isSneaking();
        LocalPlayer localPlayer = WorldEditUtil.wrapPlayer(player);
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
            System.out.println("Can place?");
            if (canPlace(player, pos1, direction, schematic)) {
                
                NopeStructure structure;
                try {
                    System.out.println("Creating structure");
                    structure = structureAPI.create(player, plan, player.getWorld(), pos1, WorldUtil.getDirection(player));
                    System.out.println("Structure: " + structure);
                    if (structure != null) {
                        ItemStack clone = planstack.clone();
                        clone.setAmount(1);
                        player.getInventory().remove(clone);
                        player.updateInventory();
                        System.out.println("Build structure!");
                        structureAPI.build(player, structure, new BuildOptions(false), false);
                    }
                } catch (StructureException ex) {
                    Logger.getLogger(PlanListener.class.getName()).log(Level.SEVERE, null, ex);
                }

                CUISelectionManager.getInstance().clear(player, false);

            }

        } else {
            CUISelectionManager.getInstance().select(player, schematic, pos1, pos2);
            if (canPlace(player, pos1, direction, schematic)) {
                player.sendMessage(ChatColor.YELLOW + "Left-Click " + ChatColor.RESET + " in the " + ChatColor.GREEN + " green " + ChatColor.RESET + "square to " + ChatColor.YELLOW + "confirm");
                player.sendMessage(ChatColor.YELLOW + "Right-Click " + ChatColor.RESET + "to" + ChatColor.YELLOW + " deselect");
            }
        }

    }

    private void handleHoloPlayer(final Player player, Location l, StructurePlan plan, SchematicData schematic, final ItemStack planstack) {
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

                NopeStructure structure;
                try {
                    structure = structureAPI.create(player, plan, player.getWorld(), pos1, WorldUtil.getDirection(player));
                    if (structure != null) {
                        ItemStack clone = planstack.clone();
                        clone.setAmount(1);
                        player.getInventory().remove(clone);
                        structureAPI.build(player, structure, new BuildOptions(false), false);
                    }
                } catch (StructureException ex) {
                    Logger.getLogger(PlanListener.class.getName()).log(Level.SEVERE, null, ex);
                }
                SelectionManager.getInstance().clear(player, false);

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
