/*
 * Copyright (C) 2015 ching
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
package com.chingo247.structureapi;

import com.chingo247.xplatform.core.ILocation;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;

/**
 *
 * @author ching
 */
public abstract class AbstractPlotManager {
    
    protected final ConstructionWorld world;

    public AbstractPlotManager(ConstructionWorld world) {
        this.world = world;
    }
    
    /**
     * Checks world restrictions (e.g if the given region is between y=1 and the max build height of the world)
     * @param world The world
     * @param region The affected region
     */
    public void checkWorldRestrictions(CuboidRegion region) throws RestrictionException {

        // Below the world?s
        if (region.getMinimumPoint().getBlockY() <= 1) {
            throw new WorldRestrictionException("Must be placed at a minimum height of 1");
        }

        // Exceeds world height limit?
        if (region.getMaximumPoint().getBlockY() > world.getMaxHeight()) {
            throw new WorldRestrictionException("Will reach above the world's max height (" + world.getMaxHeight()+ ")");
        }

        // Check for overlap on the world's 'SPAWN'
        ILocation l = world.getSpawn();
        Vector spawnPos = new Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        if (region.contains(spawnPos)) {
            throw new WorldRestrictionException("Area overlaps the world's spawn...");
        }
    } 
    
    
    
}
