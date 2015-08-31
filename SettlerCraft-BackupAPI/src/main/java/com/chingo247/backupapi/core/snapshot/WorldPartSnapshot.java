/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.backupapi.core.snapshot;

import com.chingo247.backupapi.core.util.PositionUtils;
import com.chingo247.structureapi.construction.backup.IChunkSnapshot;
import com.chingo247.structureapi.construction.backup.IWorldPartSnapshot;
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
