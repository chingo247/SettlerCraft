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
package com.sc.module.structureapi.structure.selection;

import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.sc.module.structureapi.structure.schematic.Schematic;
import com.sc.module.structureapi.util.WorldUtil;
import com.sc.module.structureapi.world.Cardinal;
import static com.sc.module.structureapi.world.Cardinal.EAST;
import static com.sc.module.structureapi.world.Cardinal.NORTH;
import static com.sc.module.structureapi.world.Cardinal.SOUTH;
import static com.sc.module.structureapi.world.Cardinal.WEST;
import com.sk89q.worldedit.Vector;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Chingo
 */
public class SelectionManager {

    private final Logger LOGGER = Logger.getLogger(SelectionManager.class);
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("SettlerCraft");

    private class HoloSelection {

        private final Vector pos1;
        private final Vector pos2;
        private final long checksum;

        private final Hologram[] holos;

        private static final int SELF = 0;
        private static final int X_AXIS = 1;
        private static final int Y_AXIS = 2;
        private static final int Z_AXIS = 3;
        private static final int END = 4;

        public HoloSelection(Player whoCanSee, Vector pos1, Vector pos2, Cardinal cardinal, Schematic schematic, boolean reverse) {
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.holos = new Hologram[5];
            this.checksum = schematic.getCheckSum();

            Vector self;
            switch(cardinal) {
                case WEST: self = WorldUtil.addOffset(pos1, cardinal, -0.5, 0, -0.5); break;
                case SOUTH: self = WorldUtil.addOffset(pos1, cardinal, -0.5, 0, 0.5); break;
                case NORTH: self = WorldUtil.addOffset(pos1, cardinal, 0.5, 0, -0.5); break;
                case EAST: self = WorldUtil.addOffset(pos1, cardinal, 0.5, 0, 0.5); break;
                default:return;
            }
            self = WorldUtil.addOffset(self, cardinal, 0, 1, 0);
            LOGGER.info(self);
            
            
            Vector xLoc;
            if(!reverse){
            xLoc = WorldUtil.addOffset(self, cardinal, 2, 1, 0);
            } else {
            xLoc = WorldUtil.addOffset(self, cardinal, -2, 1, 0);   
            }
            Vector yLoc = WorldUtil.addOffset(self, cardinal, 0, 1, 0);
            Vector zLoc = WorldUtil.addOffset(self, cardinal, 0, 1, 2);

            holos[SELF] = HolographicDisplaysAPI.createIndividualHologram(
                    plugin, 
                    new org.bukkit.Location(whoCanSee.getWorld(), self.getBlockX(), self.getBlockY(), self.getBlockZ()),
                    whoCanSee, ChatColor.GREEN + "[X]");
            
            holos[X_AXIS] = HolographicDisplaysAPI.createIndividualHologram(
                    plugin, 
                    new org.bukkit.Location(whoCanSee.getWorld(), xLoc.getBlockX(), xLoc.getBlockY(), xLoc.getBlockZ()),
                    whoCanSee, 
                    ChatColor.YELLOW + "X-AXIS" + ChatColor.RESET + "+" + Math.abs(pos2.subtract(pos1).getBlockX())
            );

            holos[Y_AXIS] = HolographicDisplaysAPI.createIndividualHologram(plugin, 
                    new org.bukkit.Location(whoCanSee.getWorld(), yLoc.getBlockX(), yLoc.getBlockY(), yLoc.getBlockZ()),
                    whoCanSee, 
                    ChatColor.YELLOW + "Y-AXIS" + ChatColor.RESET + "+" + Math.abs(pos2.subtract(pos1).getBlockY())
            );

            holos[Z_AXIS] = HolographicDisplaysAPI.createIndividualHologram(
                    plugin, 
                    new org.bukkit.Location(whoCanSee.getWorld(), zLoc.getBlockX(), zLoc.getBlockY(), zLoc.getBlockZ()), 
                    whoCanSee, 
                    ChatColor.YELLOW + "Z-AXIS" + ChatColor.RESET + "+" + Math.abs(pos2.subtract(pos1).getBlockZ())
            );

            org.bukkit.Location eLoc = new org.bukkit.Location(whoCanSee.getWorld(), pos2.getX(), pos2.getY(), pos2.getZ());
            holos[END] = HolographicDisplaysAPI.createIndividualHologram(plugin, eLoc, whoCanSee,
                    ChatColor.RED + "[X]"
            );


        }

        private void update() {
            for (Hologram holo : holos) {
                holo.update();
            }
        }

        private void clear() {
            for (Hologram holo : holos) {
                if(!holo.isDeleted()) {
                    holo.delete();
                }
            }
        }

    }

    private final Map<UUID, HoloSelection> simpleSelections = Collections.synchronizedMap(new HashMap<UUID, HoloSelection>());
    private static SelectionManager instance;

    private SelectionManager() {
    }

    public static SelectionManager getInstance() {
        if (instance == null) {
            instance = new SelectionManager();
        }
        return instance;
    }



    public void select(Player player, Schematic schematic, Vector target, Vector pos2, boolean reverse) {
        Cardinal cardinal = WorldUtil.getCardinal(player);
        simpleSelections.put(player.getUniqueId(), new HoloSelection(player, target, pos2, cardinal, schematic, reverse));
    }

    public boolean hasSelection(Player player, Schematic schematic, Vector target, Vector pos2) {
        HoloSelection selection = simpleSelections.get(player.getUniqueId());
        if (selection != null) {
            
            // is it the same structure and at the same position?
            return selection.checksum == schematic.getCheckSum()
                    && selection.pos1.equals(target)
                    && selection.pos2.equals(pos2);
        }
        return false;
    }

    public void clear(Player player, boolean talk) {
        if (simpleSelections.get(player.getUniqueId()) != null) {
            simpleSelections.get(player.getUniqueId()).clear();
        }
    }
    
    public void clearAll() {
        for(HoloSelection s : simpleSelections.values()) {
            s.clear();
        }
//        for(CUIStructureSelection s : cuiSelections.values()) {
//            clearCUISelection(s.player, false);
//        }
    }
}
