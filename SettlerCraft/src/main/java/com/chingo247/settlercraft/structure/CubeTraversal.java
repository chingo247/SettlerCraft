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
package com.chingo247.settlercraft.structure;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;

/**
 *
 * @author Chingo
 */
public class CubeTraversal {

    private Vector size;
    private final int cubeX, cubeY, cubeZ;

    private int xIndex = 0, yIndex = 0, zIndex = 0;

    private int xCubeIndex = 0, yCubeIndex = 0, zCubeIndex = 0;

    private Vector current;

    public CubeTraversal(Vector size, int cubeX, int cubeY, int cubeZ) {
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
    private CubeTraversal(Vector size, int cubeX, int cubeY, int cubeZ, Vector current, int xIndex, int yIndex, int zIndex, int xCubeIndex, int yCubeIndex, int zCubeIndex) {
        this.size = new BlockVector(size);
        this.cubeX = cubeX;
        this.cubeY = cubeY;
        this.cubeZ = cubeZ;
        this.current = new BlockVector(current);
        this.xCubeIndex = xCubeIndex;
        this.xIndex = xIndex;
        this.yCubeIndex = yCubeIndex;
        this.yIndex = yIndex;
        this.zCubeIndex = zCubeIndex;
        this.zIndex = zIndex;
    }
    
    

    public Vector next() {
        Vector v = new BlockVector((xCubeIndex * cubeX) + xIndex, yIndex, (zCubeIndex * cubeZ) + zIndex);

        xIndex++;
        
        if (xIndex % cubeX == 0 || ((xCubeIndex * cubeX) + xIndex) >= size.getBlockX()) {
            xIndex = 0;
            zIndex++;

            if (zIndex % cubeZ == 0 || zIndex == size.getBlockZ() || ((zCubeIndex * cubeZ) + zIndex) >= size.getBlockZ()) {
                zIndex = 0;
                xCubeIndex++;
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
        
//        if(((cubeY * yCubeIndex) + yIndex) == size.getBlockY()) {
//            return null;
//        }

        return v;

    }
    
    public boolean hasNext() {
        return this.clone().next() != null;
    }
    
    public CubeTraversal clone() {
        return new CubeTraversal(size, cubeX, cubeY, cubeZ, current, xIndex, yIndex, zIndex, xCubeIndex, yCubeIndex, zCubeIndex);
    }

    public static void main(String[] args) {
        CubeTraversal ct = new CubeTraversal(new Vector(9, 0, 9), 2, 2, 2);
        Vector v;
        int count = 0;
        while (count < 81) {
            v = ct.next();
            System.out.println("x: " + (v.getBlockX()) + " z: " + v.getBlockZ());
            count++;
        }
        System.out.println("Count: " + count);
    }
    
    

}
