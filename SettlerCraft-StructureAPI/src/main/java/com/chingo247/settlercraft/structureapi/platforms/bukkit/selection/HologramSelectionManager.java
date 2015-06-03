/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.settlercraft.structureapi.platforms.bukkit.selection;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.structureapi.platforms.services.holograms.Hologram;
import com.chingo247.settlercraft.structureapi.platforms.services.holograms.HologramsProvider;
import com.chingo247.settlercraft.structureapi.selection.ISelectionManager;
import com.chingo247.settlercraft.structureapi.util.WorldUtil;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IColors;
import com.chingo247.xplatform.core.IPlugin;
import com.chingo247.xplatform.core.IScheduler;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class HologramSelectionManager implements ISelectionManager{
    
    private HologramsProvider hologramsProvider;
    private static HologramSelectionManager instance;
    private final Map<UUID, HoloSelection> selections = Collections.synchronizedMap(new HashMap<UUID, HoloSelection>());
    
    private HologramSelectionManager() {
    }
    
    public static HologramSelectionManager getInstance() {
        if(instance == null) {
            instance = new HologramSelectionManager();
        }
        return instance;
    }

    public HologramsProvider getHologramsProvider() {
        return hologramsProvider;
    }
    
    public void setHologramsProvider(HologramsProvider hologramsProvider) {
        this.hologramsProvider = hologramsProvider;
    }
    
   
    
    public boolean hasHologramsProvider() {
        return hologramsProvider != null;
    }
    

    @Override
    public void select(Player player, Vector pos1, Vector pos2) {
        Direction direction = WorldUtil.getDirection(player.getLocation().getYaw());
        
        Vector pos = WorldUtil.translateLocation(pos1, direction, pos2.getBlockX(), pos2.getBlockY(), pos2.getBlockZ());
        System.out.println("Pos: " + pos);
        
        selections.put(player.getUniqueId(), new HoloSelection(player, pos1, pos2, direction, pos1.getBlockX() < 0));
    }

    @Override
    public void deselect(Player player) {
        UUID uuid = player.getUniqueId();
        if (selections.get(uuid) != null) {
            selections.get(uuid).clear();
            selections.remove(uuid);
        }
    }

    @Override
    public boolean hasSelection(Player player) {
        return selections.get(player.getUniqueId()) != null;
    }

    @Override
    public boolean matchesCurrentSelection(Player player, Vector pos1, Vector pos2) {
        HoloSelection selection = selections.get(player.getUniqueId());
        if (selection != null) {

            // is it the same structure and at the same position?
            return  selection.pos1.equals(pos1)
                    && selection.pos2.equals(pos2);
        }
        return false;
    }

    private class HoloSelection {

        private final Vector pos1;
        private final Vector pos2;

        private final Hologram[] holos;

        private static final int SELF = 0;
        private static final int X_AXIS = 1;
        private static final int Y_AXIS = 2;
        private static final int Z_AXIS = 3;
        private static final int END = 4;

        public HoloSelection(final Player whoCanSee, final Vector pos1, final Vector pos2, Direction direction, boolean reverse) {
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.holos = new Hologram[5];

            final Vector self;
            switch (direction) {
                case WEST:
                    self = WorldUtil.translateLocation(pos1, direction, -0.5, 1, -0.5);
                    break;
                case SOUTH:
                    self = WorldUtil.translateLocation(pos1, direction, -0.5, 1, 0.5);
                    break;
                case NORTH:
                    self = WorldUtil.translateLocation(pos1, direction, 0.5, 1, -0.5);
                    break;
                case EAST:
                    self = WorldUtil.translateLocation(pos1, direction, 0.5, 1, 0.5);
                    break;
                default:
                    return;
            }

            final Vector xLoc;
            if (!reverse) {
                xLoc = WorldUtil.translateLocation(self, direction, 2, 1, 0);
            } else {
                xLoc = WorldUtil.translateLocation(self, direction, -2, 1, 0);
            }
            final Vector yLoc = WorldUtil.translateLocation(self, direction, 0, 1, 0);
            final Vector zLoc = WorldUtil.translateLocation(self, direction, 0, 1, 2);

            final APlatform platform = SettlerCraft.getInstance().getPlatform();
            final IPlugin plugin = SettlerCraft.getInstance().getPlugin();
            final IScheduler scheduler = platform.getServer().getScheduler(plugin);
            final IColors colors = platform.getChatColors();
            
            scheduler.runSync(new Runnable() {

                @Override
                public void run() {
                    // BoxHolo
                    Hologram boxHolo = hologramsProvider.createHologram(
                            plugin.getName(), 
                            whoCanSee.getWorld(), 
                            new Vector(self.getBlockX(), self.getBlockY(), self.getBlockZ())
                    );
                    boxHolo.addLine(colors.green() + "[X]");
                    holos[SELF] = boxHolo;
                    
                    // X-AXIS Holo
                    Hologram xAxisHolo = hologramsProvider.createHologram(
                            plugin.getName(), 
                            whoCanSee.getWorld(), 
                            new Vector(xLoc.getBlockX(), xLoc.getBlockY(), xLoc.getBlockZ())
                    );
                    boxHolo.addLine(colors.yellow() + "X-AXIS" + colors.reset() + "+" + Math.abs(pos2.subtract(pos1).getBlockX()));
                    holos[X_AXIS] = xAxisHolo;
//                           
                    // Y-AXIS Holo
                    Hologram yAxisHolo = hologramsProvider.createHologram(
                            plugin.getName(), 
                            whoCanSee.getWorld(), 
                            new Vector(yLoc.getBlockX(), yLoc.getBlockY(), yLoc.getBlockZ())
                    );
                    boxHolo.addLine(colors.yellow() + "Y-AXIS" + colors.reset() + "+" + Math.abs(pos2.subtract(pos1).getBlockY()));
                    holos[Y_AXIS] = yAxisHolo;
                    
                    // Z-AXIS Holo
                    Hologram zAxisHolo = hologramsProvider.createHologram(
                            plugin.getName(), 
                            whoCanSee.getWorld(), 
                            new Vector(zLoc.getBlockX(), zLoc.getBlockY(), zLoc.getBlockZ())
                    );
                    boxHolo.addLine(colors.yellow() + "Z-AXIS" + colors.reset() + "+" + Math.abs(pos2.subtract(pos1).getBlockZ()));
                    holos[Z_AXIS] = zAxisHolo;
                }
            });
            
            

        }

        private void clear() {
            final APlatform platform = SettlerCraft.getInstance().getPlatform();
            final IPlugin plugin = SettlerCraft.getInstance().getPlugin();
            final IScheduler scheduler = platform.getServer().getScheduler(plugin);
            
            scheduler.runSync(new Runnable() {

                @Override
                public void run() {
                    for (Hologram holo : holos) {
                        holo.delete();
                    }
                }
            });
        }

    }
    
    public void clearAll() {
        for(HoloSelection s : selections.values()) {
            s.clear();
        }
    }
    
}
