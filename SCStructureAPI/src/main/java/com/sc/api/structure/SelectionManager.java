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
package com.sc.api.structure;

import com.sc.plugin.SettlerCraft;
import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.entity.plan.StructureSchematic;
import com.sc.api.structure.entity.world.SimpleCardinal;
import com.sc.util.WorldUtil;
import com.sc.util.SCWorldEditUtil;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.Vector;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Chingo
 */
public class SelectionManager {

    private final StructurePlanManager pm = StructurePlanManager.getInstance();
    private final Logger LOGGER = Logger.getLogger(SelectionManager.class);
    private final Plugin plugin = SettlerCraft.getSettlerCraft();

    private class CUIStructureSelection {

        private StructurePlan plan;
        private Location pos1;
        private Location pos2;

        public CUIStructureSelection(StructurePlan plan, Location target, Location pos2) {
            this.plan = plan;
            this.pos1 = target;
            this.pos2 = pos2;
        }

    }

    private class HoloStructureSelection {

        private final Vector pos1;
        private final Vector pos2;
        private final Long planChecksum;

        private final Hologram[] holos;

        private static final int SELF = 0;
        private static final int X_AXIS = 1;
        private static final int Y_AXIS = 2;
        private static final int Z_AXIS = 3;
        private static final int END = 4;

        public HoloStructureSelection(Player whoCanSee, Vector pos1, Vector pos2, SimpleCardinal cardinal, StructurePlan plan) {
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.holos = new Hologram[5];
            this.planChecksum = plan.getChecksum();

            org.bukkit.Location self = new org.bukkit.Location(whoCanSee.getWorld(), pos1.getX(), pos1.getY(), pos1.getZ());
            switch(cardinal) {
                case WEST: self = WorldUtil.addOffset(self, cardinal, -0.5, 0, -0.5); break;
                case SOUTH: self = WorldUtil.addOffset(self, cardinal, -0.5, 0, 0.5); break;
                case NORTH: self = WorldUtil.addOffset(self, cardinal, 0.5, 0, -0.5); break;
                case EAST: self = WorldUtil.addOffset(self, cardinal, 0.5, 0, 0.5); break;
                default:break;
            }
            self = WorldUtil.addOffset(self, cardinal, 0, 1, 0);
            LOGGER.info(self);
            
            StructureSchematic schematic = pm.getSchematic(planChecksum);
            
            org.bukkit.Location xLoc = WorldUtil.addOffset(self.clone(), cardinal, 1, 1, 0);
            org.bukkit.Location yLoc = WorldUtil.addOffset(self.clone(), cardinal, 0, 1, 0);
            org.bukkit.Location zLoc = WorldUtil.addOffset(self.clone(), cardinal, 0, 1, 1);

            holos[SELF] = HolographicDisplaysAPI.createIndividualHologram(plugin, self, whoCanSee, ChatColor.GREEN + "[X]");
            

            holos[X_AXIS] = HolographicDisplaysAPI.createIndividualHologram(plugin, xLoc, whoCanSee, 
                    ChatColor.YELLOW + "X-AXIS" + ChatColor.RESET + "+" +schematic.getWidth()
            );

            holos[Y_AXIS] = HolographicDisplaysAPI.createIndividualHologram(plugin, yLoc,whoCanSee, 
                    ChatColor.YELLOW + "Y-AXIS" + ChatColor.RESET + "+" +schematic.getHeight()
            );

            holos[Z_AXIS] = HolographicDisplaysAPI.createIndividualHologram(plugin, zLoc, whoCanSee, 
                    ChatColor.YELLOW + "Z-AXIS" + ChatColor.RESET + "+" +schematic.getLength()
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

    private final Map<UUID, CUIStructureSelection> cuiSelections;
    private final Map<UUID, HoloStructureSelection> simpleSelections;
    private static SelectionManager instance;

    private SelectionManager() {
        cuiSelections = Collections.synchronizedMap(new HashMap<UUID, CUIStructureSelection>());
        simpleSelections = Collections.synchronizedMap(new HashMap<UUID, HoloStructureSelection>());
    }

    public static SelectionManager getInstance() {
        if (instance == null) {
            instance = new SelectionManager();
        }
        return instance;
    }

    public void CUISelect(Player player, StructurePlan plan, Location target, Location pos2) {
        SCWorldEditUtil.select(player, target.getPosition(), pos2.getPosition());
        cuiSelections.put(player.getUniqueId(), new CUIStructureSelection(plan, target, pos2));
    }

    public boolean matchesCUISelection(Player player, StructurePlan plan, Location target, Location pos2) {
        if (cuiSelections.get(player.getUniqueId()) == null) {
            return false;
        } else {
            CUIStructureSelection selection = cuiSelections.get(player.getUniqueId());
            return selection.plan.getChecksum().equals(plan.getChecksum())
                    && target.equals(selection.pos1)
                    && pos2.equals(selection.pos2);
        }
    }

    public void clearCUISelection(Player player, boolean talk) {
        LocalSession session = SCWorldEditUtil.getLocalSession(player);
        LocalWorld world = SCWorldEditUtil.getLocalWorld(player);
        if (session.getRegionSelector(world).isDefined()) {
            session.getRegionSelector(world).clear();
            session.dispatchCUISelection(SCWorldEditUtil.getLocalPlayer(player));
            if (talk) {
                player.sendMessage("Cleared selection");
            }
        }
    }

    public void simpleSelect(Player player, StructurePlan plan, Location target, Location pos2) {
        SimpleCardinal cardinal = WorldUtil.getCardinal(player);
        simpleSelections.put(player.getUniqueId(), new HoloStructureSelection(player, target.getPosition(), pos2.getPosition(), cardinal, plan));
    }

    public boolean matchesSimple(Player player, StructurePlan plan, Location target) {
        if (simpleSelections.get(player.getUniqueId()) != null) {
            HoloStructureSelection selection = simpleSelections.get(player.getUniqueId());
            return selection.planChecksum.equals(plan.getChecksum()) && selection.pos1.equals(target.getPosition());
        }
        return false;
    }

    public void clearsSimple(Player player, boolean talk) {
        if (simpleSelections.get(player.getUniqueId()) != null) {
            simpleSelections.get(player.getUniqueId()).clear();
            if (talk) {
                player.sendMessage("Cleared selection");
            }
        }
    }

    public boolean hasSimpleSelection(Player player) {
        return simpleSelections.get(player.getUniqueId()) != null;
    }
}
