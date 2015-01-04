
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
package com.chingo247.settlercraft.util.functions;

import com.chingo247.settlercraft.structureapi.structure.regions.CuboidDimension;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.Vector;

/**
 *
 * @author Chingo
 */
public class DimensionIterator {

    private final CuboidDimension dimension;
    private Vector currentStart, currentEnd;
    private int minX, minY, minZ;
    private final int xRange, yRange, zRange;

    public DimensionIterator(CuboidDimension dimension, int xRange, int yRange, int zRange) {
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
    private DimensionIterator(CuboidDimension dimension, Vector currentStart, Vector currentEnd, int xRange, int yRange, int zRange, int minX, int minY, int minZ) {
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

    public CuboidDimension getDimension() {
        return dimension;
    }
    
    public CuboidDimension next() {
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
        

        return new CuboidDimension(currentStart, currentEnd);
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
