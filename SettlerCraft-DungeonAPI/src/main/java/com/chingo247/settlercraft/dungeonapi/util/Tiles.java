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
package com.chingo247.settlercraft.dungeonapi.util;

import com.chingo247.dungeonapi.dungeon.Tile;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector2D;

/**
 *
 * @author Chingo
 */
public class Tiles {
    
    private Tiles() {}
    
   
    
    public static Vector2D[] getBottomBorder(Tile t, int tileSize) {
        int x = t.getX();
        int z = t.getZ();
        
        Vector2D[] vs = new Vector2D[tileSize - 2];
        int count = 0;
        
        for(int relX = 1; relX < tileSize - 1; relX++) {
            int destX = relX + x;
            int destZ = z;
            vs[count] = new Vector2D(destX, destZ);
            count++;
        }
        return vs;
    }
    
    public static Vector2D[] getTopBorder(Tile t, int tileSize) {
        int x = t.getX();
        int z = t.getZ();
        
        Vector2D[] vs = new Vector2D[tileSize - 2];
        int count = 0;
        
        for(int relX = 1; relX < tileSize - 1; relX++) {
            int destX = relX + x;
            int destZ = z + tileSize;
            vs[count] = new Vector2D(destX, destZ);
            count++;
        }
        return vs;
    }
    
    public static Vector2D[] getLeftBorder(Tile t, int tileSize) {
        int x = t.getX();
        int z = t.getZ();
        
        Vector2D[] vs = new Vector2D[tileSize - 2];
        int count = 0;
        for(int relZ = 1; relZ < tileSize - 1; relZ++) {
            int destX = x;
            int destZ = z + relZ;
            vs[count] = new Vector2D(destX, destZ);
            count++;
        }
        return vs;
    
    }
    
    public static  Vector2D[] getRightBorder(Tile t, int tileSize) {
        int x = t.getX();
        int z = t.getZ();
        
        Vector2D[] vs = new Vector2D[tileSize - 2];
        int count = 0;
        for(int relZ = 1; relZ < tileSize - 1; relZ++) {
            int destX = x + tileSize;
            int destZ = z + relZ;
            vs[count] = new Vector2D(destX, destZ);
            count++;
        }
        return vs;
    }
    
    public static Vector2D[] getCorners(Tile t, int cellsize) {
        int x = t.getX();
        int z = t.getZ();
        
        Vector2D[] vs = new Vector2D[4];
        vs[0] = new BlockVector2D(x, z);
        vs[1] = new BlockVector2D(x + cellsize, z);
        vs[2] = new BlockVector2D(x, z + cellsize);
        vs[3] = new BlockVector2D(x + cellsize, z + cellsize);
        
        return vs;
    }
    
}
