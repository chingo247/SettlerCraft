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
package com.chingo247.backupapi.core.io;

import com.chingo247.backupapi.core.util.PositionUtils;
import com.google.common.collect.Maps;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.util.Map;

/**
 *
 * @author Chingo
 */
public class WorldPartSnapshot implements IWorldPartSnapshot {
    
    private Map<String, Tag> snapshotMap;
    private int minX, minZ, maxX, maxZ;
    private Map<String, ChunkSnapshot> chunks;

    WorldPartSnapshot(Map<String, Tag> snapshotMap, int minX, int minZ, int maxX, int maxZ) {
       this.snapshotMap = snapshotMap;
       this.minX = minX;
       this.minZ = minZ;
       this.maxX = maxX;
       this.maxZ = maxZ;
       this.chunks = Maps.newHashMap();
    }
    
    private IChunkSnapshot getChunk(int x , int z) {
        ChunkSnapshot cs = chunks.get(x + "," + z);
        if(cs == null) {
            Tag t = snapshotMap.get("Chunk-[" + x + "," + z + "]");
            if(t == null) {
                return null;
            }
            Map<String, Tag> chunkMap = (Map) t.getValue();
            
           
            
            cs = new ChunkSnapshot(chunkMap, x, z);
            chunks.put(x + "," + z, cs);
        }
        return cs;
    }

    @Override
    public BaseBlock getWorldBlockAt(int x, int y, int z) {
        Vector2D pos = PositionUtils.getChunkCoordinate(x, z);
        IChunkSnapshot cs = getChunk(pos.getBlockX(), pos.getBlockZ());
        
        
        
        if(cs == null) {
            return null;
        }
        
        int chunkX = (x >> 4) * 16;
        int chunkZ = (z >> 4) * 16;
        
        BaseBlock b = cs.getBlockAt(x - chunkX, y, z - chunkZ);
        if(b == null) {
            System.out.println("NULL BLOCK");
            return new BaseBlock(0);
        }
        
        return b;
    }


    @Override
    public Vector2D getMinPosition() {
        return new Vector2D(minX, minZ);
    }

    @Override
    public Vector2D getMaxPosition() {
        return new Vector2D(maxX, maxZ);
    }

    
}
