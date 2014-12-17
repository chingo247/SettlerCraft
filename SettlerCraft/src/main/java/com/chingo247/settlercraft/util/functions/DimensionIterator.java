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
package com.chingo247.settlercraft.util.functions;

import com.chingo247.settlercraft.structureapi.world.Dimension;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.Vector;

/**
 *
 * @author Chingo
 */
public class DimensionIterator {

    private final Dimension dimension;
    private Vector currentStart, currentEnd;
    private int minX, minY, minZ;
    private final int xRange, yRange, zRange;

    public DimensionIterator(Dimension dimension, int xRange, int yRange, int zRange) {
//        Preconditions.checkNotNull(dimension);
        this.dimension = dimension;
        this.minX = dimension.getMinX();
        this.minY = dimension.getMinY();
        this.minZ = dimension.getMinZ();
        this.xRange = xRange;
        this.yRange = yRange;
        this.zRange = zRange;
    }
    
    /**
     * Copy constructor
     * @param dimension
     * @param currentStart
     * @param currentEnd
     * @param xRange
     * @param yRange
     * @param zRange 
     */
    private DimensionIterator(Dimension dimension, Vector currentStart, Vector currentEnd, int xRange, int yRange, int zRange, int minX, int minY, int minZ) {
        this.dimension = dimension;
        this.currentStart = currentStart;
        this.currentEnd = currentEnd;
        this.xRange = xRange;
        this.yRange = yRange;
        this.zRange = zRange;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
    }

    public Dimension getDimension() {
        return dimension;
    }
    
    public Dimension next() {
        currentStart = new Vector(minX, minY, minZ);
        currentEnd = new Vector(minX + xRange, minY + yRange, minZ + zRange);
        
        if(minY > dimension.getMaxY()) {
            return null;
        }

        fitEnd();
        minX += xRange + 1;
        
        if(minX > dimension.getMaxX()) {
            minX = dimension.getMinX();
            minZ+= zRange + 1;
            
            if(minZ > dimension.getMaxZ()) {
                minZ = dimension.getMinZ();
                minY += yRange + 1;
            }
        }
        

        return new Dimension(currentStart, currentEnd);
    }

    /**
     * Makes the end fit
     */
    private void fitEnd() {
        if(isInside(currentEnd)) return;
        int x = currentEnd.getBlockX(), y = currentEnd.getBlockY(), z = currentEnd.getBlockZ();
        if (currentEnd.getBlockX() > dimension.getMaxX()) {
            x = dimension.getMaxX();
        }
        if (currentEnd.getBlockY() > dimension.getMaxY()) {
            y = dimension.getMaxY();
        }
        if (currentEnd.getBlockZ() > dimension.getMaxZ()) {
            z = dimension.getMaxZ();
        }
        currentEnd = new Vector(x, y, z);
    }

    private boolean isInside(Vector v) {
        return v.getBlockX() < dimension.getMaxX() && v.getBlockX() > dimension.getMinX()
                && v.getBlockY() < dimension.getMaxY() && v.getBlockY() > dimension.getMinY()
                && dimension.getMaxZ() < dimension.getMaxZ() && v.getBlockZ() > dimension.getMinZ();
    }
    
    public DimensionIterator copy() {
        return new DimensionIterator(dimension, currentStart, currentEnd, xRange, yRange, zRange, minX, minY, minZ);
    }
    
    public boolean hasNext() {
        return this.copy().next() != null;
    }

}
