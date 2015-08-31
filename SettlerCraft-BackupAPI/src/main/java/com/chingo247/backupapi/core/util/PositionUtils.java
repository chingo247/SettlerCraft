/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
