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
package com.chingo247.backupapi.core.util;

import com.chingo247.backupapi.core.io.region.RegionFileFormat;
import com.sk89q.worldedit.Vector2D;

/**
 * 
 * @author Chingo
 */
public class PositionUtils {
    
    private PositionUtils () {}

    /**
     * Gets the region coordinate as region-coordinate meaning value is divided by 512
     * @param x The x coordinate
     * @param z The z coordinate
     * @return The coordinate of this region
     */
    public static Vector2D getRegionCoordinate(int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        return new Vector2D(chunkX >> 5, chunkZ >> 5);
    }

    /**
     * Gets the chunk coordinate as chunk-coordinate meaning value is divided by the size of the chunk
     * @param x The x coordinate
     * @param z The z coordinate
     * @return The coordinate of the chunk
     */
    public static Vector2D getChunkCoordinate(int x, int z) {
        return new Vector2D(x >> 4, z >> 4);
    }
    
    /**
     * Gets the position of this chunk, this method divers from {@link #getChunkCoordinate(int, int)} as it doesn't divide
     * the value by the size of the chunk
     * @param x The x
     * @param z The z
     * @return The postion of this chunk
     */
    public static Vector2D getChunkPosition(int x, int z) {
        return new Vector2D(RegionFileFormat.CHUNK_SIZE * (x >> 4), RegionFileFormat.CHUNK_SIZE * (z >> 4));
    }
    
    /**
     * Gets the position of this region, this method divers from {@link #getRegionCoordinate(int, int)} as it doesn't divide
     * the value by the size of the region
     * @param x The x
     * @param z The z
     * @return The postion of this region
     */
    public static Vector2D getRegionPosition(int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        return new Vector2D(RegionFileFormat.REGION_SIZE * (chunkX >> 5), RegionFileFormat.REGION_SIZE * (chunkZ >> 5));
    }
}
