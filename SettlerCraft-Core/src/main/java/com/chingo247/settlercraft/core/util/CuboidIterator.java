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

package com.chingo247.settlercraft.core.util;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import java.util.Iterator;

/**
 * Traverse the given area in a cubic-way from bottom to top
 * @author Chingo
 */
public class CuboidIterator implements Iterator<Vector>, Iterable<Vector> {

    private final Vector size, start;
    private final int cubeX, cubeY, cubeZ;
    private int xIndex = 0, yIndex = 0, zIndex = 0;
    private int xCubeIndex = 0, yCubeIndex = 0, zCubeIndex = 0;
    private Vector current;
    

    public CuboidIterator(Vector size, int cubeX, int cubeY, int cubeZ) {
        Preconditions.checkArgument(cubeX > 0, "cubeX has to be greater than 0");
        Preconditions.checkArgument(cubeY > 0, "cubeY has to be greater than 0");
        Preconditions.checkArgument(cubeZ > 0, "cubeZ has to be greater than 0");
        this.size = size;
        this.cubeX = cubeX;
        this.cubeY = cubeY;
        this.cubeZ = cubeZ;
        this.start = Vector.ZERO;
        System.out.println("start: " + size);
    }

    
    

    /**
     * Copy Constructor
     * @param size
     * @param cubeX
     * @param cubeY
     * @param cubeZ
     * @param current
     * @param xIndex
     * @param yIndex
     * @param zIndex
     * @param xCubeIndex
     * @param yCubeIndex
     * @param zCubeIndex 
     */
    private CuboidIterator(Vector start, Vector size, int cubeX, int cubeY, int cubeZ, Vector current, int xIndex, int yIndex, int zIndex, int xCubeIndex, int yCubeIndex, int zCubeIndex) {
        this.size = size;
        this.cubeX = cubeX;
        this.cubeY = cubeY;
        this.cubeZ = cubeZ;
        this.current = current;
        this.xCubeIndex = xCubeIndex;
        this.xIndex = xIndex;
        this.yCubeIndex = yCubeIndex;
        this.yIndex = yIndex;
        this.zCubeIndex = zCubeIndex;
        this.zIndex = zIndex;
        this.start = start;
    }
    
    

    @Override
    public Vector next() {
        Vector v = new BlockVector((xCubeIndex * cubeX) + xIndex, (yCubeIndex * cubeY) + yIndex, (zCubeIndex * cubeZ) + zIndex);

        // Stop condition!
        if(((cubeY * yCubeIndex) + yIndex) >= size.getBlockY()) {
            return null;
        }
        
        xIndex++;
        if (xIndex % cubeX == 0 || ((xCubeIndex * cubeX) + xIndex) >= size.getBlockX()) {
            xIndex = 0;
            zIndex++;
            
            if (zIndex % cubeZ == 0 || ((zCubeIndex * cubeZ) + zIndex) >= size.getBlockZ()) {
                zIndex = 0;
                yIndex++;
                
                if(yIndex % cubeY == 0  || ((yCubeIndex * cubeY) + yIndex) >= size.getBlockY()) {
                    yIndex= 0;
                    xCubeIndex++;
                }
                
            }
        } 

        if (((cubeX * xCubeIndex) + xIndex) >= size.getBlockX()) {
            xCubeIndex = 0;
            zCubeIndex++;
        }
        
        if (((cubeZ * zCubeIndex) + zIndex) >= size.getBlockZ()) {
            zCubeIndex = 0;
            yCubeIndex++;
        }
        
        return start.add(v);

    }
    
    @Override
    public boolean hasNext() {
        return this.copy().next() != null;
    }
    
    public CuboidIterator copy() {
        return new CuboidIterator(start, size, cubeX, cubeY, cubeZ, current, xIndex, yIndex, zIndex, xCubeIndex, yCubeIndex, zCubeIndex);
    }

    @Override
    public Iterator<Vector> iterator() {
        return this;
    }
    
  
}
