///*
// * The MIT License
// *
// * Copyright 2015 Chingo.
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// */
//package com.chingo247.settlercraft.bukkit.selection;
//
//import com.chingo247.settlercraft.bukkit.SettlerCraftPlugin;
//import com.chingo247.settlercraft.structureapi.structure.plan.processor.SchematicData;
//import com.chingo247.settlercraft.util.WorldUtil;
//import com.chingo247.settlercraft.structureapi.world.Direction;
//import static com.chingo247.settlercraft.structureapi.world.Direction.EAST;
//import static com.chingo247.settlercraft.structureapi.world.Direction.NORTH;
//import static com.chingo247.settlercraft.structureapi.world.Direction.SOUTH;
//import static com.chingo247.settlercraft.structureapi.world.Direction.WEST;
//import com.gmail.filoghost.holograms.api.Hologram;
//import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
//import com.sk89q.worldedit.Vector;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//import org.apache.log4j.Logger;
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.entity.Player;
//import org.bukkit.plugin.Plugin;
//
///**
// *
// * @author Chingo
// */
//public class SelectionManager {
//
//    private final Logger LOGGER = Logger.getLogger(SelectionManager.class);
//    private final Plugin plugin = SettlerCraftPlugin.getInstance();
//
//    private class HoloSelection {
//
//        private final Vector pos1;
//        private final Vector pos2;
//        private final long checksum;
//
//        private final Hologram[] holos;
//
//        private static final int SELF = 0;
//        private static final int X_AXIS = 1;
//        private static final int Y_AXIS = 2;
//        private static final int Z_AXIS = 3;
//        private static final int END = 4;
//
//        public HoloSelection(final Player whoCanSee, final Vector pos1, final Vector pos2, Direction direction, SchematicData schematic, boolean reverse) {
//            this.pos1 = pos1;
//            this.pos2 = pos2;
//            this.holos = new Hologram[5];
//            this.checksum = schematic.getChecksum();
//
//            final Vector self;
//            switch (direction) {
//                case WEST:
//                    self = WorldUtil.translateLocation(pos1, direction, -0.5, 1, -0.5);
//                    break;
//                case SOUTH:
//                    self = WorldUtil.translateLocation(pos1, direction, -0.5, 1, 0.5);
//                    break;
//                case NORTH:
//                    self = WorldUtil.translateLocation(pos1, direction, 0.5, 1, -0.5);
//                    break;
//                case EAST:
//                    self = WorldUtil.translateLocation(pos1, direction, 0.5, 1, 0.5);
//                    break;
//                default:
//                    return;
//            }
//
//            final Vector xLoc;
//            if (!reverse) {
//                xLoc = WorldUtil.translateLocation(self, direction, 2, 1, 0);
//            } else {
//                xLoc = WorldUtil.translateLocation(self, direction, -2, 1, 0);
//            }
//            final Vector yLoc = WorldUtil.translateLocation(self, direction, 0, 1, 0);
//            final Vector zLoc = WorldUtil.translateLocation(self, direction, 0, 1, 2);
//
//            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//
//                @Override
//                public void run() {
//                    holos[SELF] = HolographicDisplaysAPI.createIndividualHologram(
//                            plugin,
//                            new org.bukkit.Location(whoCanSee.getWorld(), self.getBlockX(), self.getBlockY(), self.getBlockZ()),
//                            whoCanSee, ChatColor.GREEN + "[X]");
//
//                    holos[X_AXIS] = HolographicDisplaysAPI.createIndividualHologram(
//                            plugin,
//                            new org.bukkit.Location(whoCanSee.getWorld(), xLoc.getBlockX(), xLoc.getBlockY(), xLoc.getBlockZ()),
//                            whoCanSee,
//                            ChatColor.YELLOW + "X-AXIS" + ChatColor.RESET + "+" + Math.abs(pos2.subtract(pos1).getBlockX())
//                    );
//
//                    holos[Y_AXIS] = HolographicDisplaysAPI.createIndividualHologram(plugin,
//                            new org.bukkit.Location(whoCanSee.getWorld(), yLoc.getBlockX(), yLoc.getBlockY(), yLoc.getBlockZ()),
//                            whoCanSee,
//                            ChatColor.YELLOW + "Y-AXIS" + ChatColor.RESET + "+" + Math.abs(pos2.subtract(pos1).getBlockY())
//                    );
//
//                    holos[Z_AXIS] = HolographicDisplaysAPI.createIndividualHologram(
//                            plugin,
//                            new org.bukkit.Location(whoCanSee.getWorld(), zLoc.getBlockX(), zLoc.getBlockY(), zLoc.getBlockZ()),
//                            whoCanSee,
//                            ChatColor.YELLOW + "Z-AXIS" + ChatColor.RESET + "+" + Math.abs(pos2.subtract(pos1).getBlockZ())
//                    );
//
//                    org.bukkit.Location eLoc = new org.bukkit.Location(whoCanSee.getWorld(), pos2.getX(), pos2.getY(), pos2.getZ());
//                    holos[END] = HolographicDisplaysAPI.createIndividualHologram(plugin, eLoc, whoCanSee,
//                            ChatColor.RED + "[X]"
//                    );
//                }
//            });
//
//        }
//
//        private void clear() {
//            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
//
//                @Override
//                public void run() {
//                    for (Hologram holo : holos) {
//                        if (!holo.isDeleted()) {
//                            holo.delete();
//                        }
//                    }
//                }
//            });
//
//        }
//
//    }
//
//    private final Map<UUID, HoloSelection> selections = Collections.synchronizedMap(new HashMap<UUID, HoloSelection>());
//    private static SelectionManager instance;
//
//    private SelectionManager() {
//    }
//
//    public static SelectionManager getInstance() {
//        if (instance == null) {
//            instance = new SelectionManager();
//        }
//        return instance;
//    }
//
//    public void select(Player player, SchematicData schematic, Vector target, Vector pos2, boolean reverse) {
//        Direction direction = WorldUtil.getDirection(player);
//        selections.put(player.getUniqueId(), new HoloSelection(player, target, pos2, direction, schematic, reverse));
//    }
//
//    public boolean matchesSelection(Player player, SchematicData schematic, Vector target, Vector pos2) {
//        HoloSelection selection = selections.get(player.getUniqueId());
//        if (selection != null) {
//
//            // is it the same structure and at the same position?
//            return selection.checksum == schematic.getChecksum()
//                    && selection.pos1.equals(target)
//                    && selection.pos2.equals(pos2);
//        }
//        return false;
//    }
//
//    public boolean hasSelection(Player player) {
//        return selections.get(player.getUniqueId()) != null;
//    }
//
//    public void clear(Player player, boolean talk) {
//        if (selections.get(player.getUniqueId()) != null) {
//            selections.get(player.getUniqueId()).clear();
//        }
//    }
//
//    public void clearAll() {
//        for (HoloSelection s : selections.values()) {
//            s.clear();
//        }
////        for(CUIStructureSelection s : cuiSelections.values()) {
////            clearCUISelection(s.player, false);
////        }
//    }
//}
