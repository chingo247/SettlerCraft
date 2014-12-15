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
package com.chingo247.settlercraft.util;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;

/**
 *
 * @author Chingo
 */
public class CubicIterator {

    private final Vector size;
    private final int cubeX, cubeY, cubeZ;
    private int xIndex = 0, yIndex = 0, zIndex = 0;
    private int xCubeIndex = 0, yCubeIndex = 0, zCubeIndex = 0;
    private Vector current;

    public CubicIterator(Vector size, int cubeX, int cubeY, int cubeZ) {
        this.size = size;
        this.cubeX = cubeX;
        this.cubeY = cubeY;
        this.cubeZ = cubeZ;
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
    private CubicIterator(Vector size, int cubeX, int cubeY, int cubeZ, Vector current, int xIndex, int yIndex, int zIndex, int xCubeIndex, int yCubeIndex, int zCubeIndex) {
        this.size = new BlockVector(size);
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
    }
    
    

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
//                xCubeIndex++;
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
        
        return v;

    }
    
    public boolean hasNext() {
        return this.copy().next() != null;
    }
    
    public CubicIterator copy() {
        return new CubicIterator(size, cubeX, cubeY, cubeZ, current, xIndex, yIndex, zIndex, xCubeIndex, yCubeIndex, zCubeIndex);
    }
    
  
}
