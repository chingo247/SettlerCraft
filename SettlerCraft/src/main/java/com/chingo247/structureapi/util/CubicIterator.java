package com.chingo247.structureapi.util;


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


import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;

/**
 *
 * @author Chingo
 */
public class CubicIterator {

    private final Vector size, start;
    private final int cubeX, cubeY, cubeZ;
    private int xIndex = 0, yIndex = 0, zIndex = 0;
    private int xCubeIndex = 0, yCubeIndex = 0, zCubeIndex = 0;
    private Vector current;

    public CubicIterator(Vector size, int cubeX, int cubeY, int cubeZ) {
        this(Vector.ZERO, size, cubeX, cubeY, cubeZ);
    }

    
    
    public CubicIterator(Vector start, Vector size, int cubeX, int cubeY, int cubeZ) {
        this.size = size;
        this.cubeX = cubeX;
        this.cubeY = cubeY;
        this.cubeZ = cubeZ;
        this.start = start;
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
    private CubicIterator(Vector start, Vector size, int cubeX, int cubeY, int cubeZ, Vector current, int xIndex, int yIndex, int zIndex, int xCubeIndex, int yCubeIndex, int zCubeIndex) {
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
        this.start = start;
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
    
    public boolean hasNext() {
        return this.copy().next() != null;
    }
    
    public CubicIterator copy() {
        return new CubicIterator(start, size, cubeX, cubeY, cubeZ, current, xIndex, yIndex, zIndex, xCubeIndex, yCubeIndex, zCubeIndex);
    }
    
  
}
