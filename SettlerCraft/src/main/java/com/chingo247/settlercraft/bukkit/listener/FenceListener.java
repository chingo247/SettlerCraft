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

import com.chingo247.settlercraft.structureapi.persistence.hibernate.StructureDAO;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.settlercraft.structureapi.world.Dimension;
import com.sk89q.worldedit.Vector;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * @author Chingo
 */
public class FenceListener implements Listener {

    private final StructureDAO structureDAO = new StructureDAO();

    @EventHandler
    public void onFenceBreak(BlockBreakEvent bbe) {
        if (bbe.getBlock().getType() == Material.IRON_FENCE) {
            Location l = bbe.getBlock().getLocation();
            Structure structure = structureDAO.getStructure(l.getWorld().getName(), l.getBlockX(), l.getBlockY(), l.getBlockZ());
            if (structure != null && structure.getState() != Structure.State.COMPLETE) {
                Vector v = structure.getRelativePosition(new Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
                Dimension dim = structure.getDimension();

                if (v.getBlockY() == 1 && (l.getBlockX() == dim.getMinX() || l.getBlockX() == dim.getMaxX() || l.getBlockZ() == dim.getMinZ() || l.getBlockZ() == dim.getMaxZ())) {
                    if (bbe.getPlayer() != null) {
                        bbe.getPlayer().sendMessage(ChatColor.RED + "Can't destroy fence....");
                    }
                    bbe.setCancelled(true);
                }
            }
        }

    }

}
