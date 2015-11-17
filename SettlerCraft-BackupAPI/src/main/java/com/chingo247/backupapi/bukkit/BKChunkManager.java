/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.backupapi.bukkit;

import com.chingo247.backupapi.core.IChunkManager;
import com.google.common.collect.Maps;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.logging.Level;
import net.minecraft.server.v1_8_R1.Chunk;
import net.minecraft.server.v1_8_R1.ChunkRegionLoader;
import net.minecraft.server.v1_8_R1.ExceptionWorldConflict;
import net.minecraft.server.v1_8_R1.NBTTagCompound;
import net.minecraft.server.v1_8_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Chingo
 */
public class BKChunkManager implements IChunkManager {

    private static Logger LOG = LoggerFactory.getLogger(BKChunkManager.class.getSimpleName());
    private Map<String, WorldData> dataCache;
    

    public BKChunkManager() {
        this.dataCache = Maps.newHashMap();
    }

    @Override
    public void save(String world) {
        WorldData worldData = getWorld(world);
        WorldServer server = worldData.worldServer;
        boolean oldSave = server.savingDisabled;
        server.savingDisabled = false;
        try {
            server.save(true, null);
        } catch (ExceptionWorldConflict ex) {
            LOG.error(ex.getMessage(), ex);
        }
        server.savingDisabled = oldSave;
    }

    @Override
    public void save(String world, int x, int z) {
        WorldData worldData = getWorld(world);
        WorldServer server = worldData.worldServer;
        
        Chunk c = server.chunkProviderServer.getChunkAt(x, z);
        System.out.println("CHUNK_AT: x:" + x + ", z:" + z + " - " + worldData.loader.chunkExists(server, x, z) + " - " + c);
        if(c == null) {
            // This should never return null
            c = server.chunkProviderServer.getOrCreateChunk(x, z);
        }
        worldData.loader.loadChunk(server, x,z);
        Object[] data = worldData.loader.loadChunk(worldData.worldServer, x, z);
        NBTTagCompound nbttagcompound = (NBTTagCompound)data[1];
        
        
        
//        c.mustSave = true;
//        server.chunkProviderServer.saveChunk(c);
//        server.chunkProviderServer.saveChunkNOP(c);
                
                
    }
    
    
    
    public void getData(String world, int x, int z) {
        WorldData worldData = getWorld(world);
        Object[] data = worldData.loader.loadChunk(worldData.worldServer, x, z);
        NBTTagCompound nbttagcompound = (NBTTagCompound)data[1];
        
        System.out.println("");
    }

    private WorldData getWorld(String world) {
        WorldData d = dataCache.get(world);
        if (d == null) {
            try {
                World w = Bukkit.getWorld(world);
                if (w == null) {
                    throw new NullPointerException("No world found for '" + world + "'");
                }
                WorldServer ws = ((CraftWorld) w).getHandle();
                d = new WorldData(ws);
                dataCache.put(world, d);
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
        return d;
    }

    @Override
    public void writeToDisk(String world) {
        // Write to disk
        WorldData wd = getWorld(world);
        WorldServer ws = wd.worldServer;

        boolean oldSave = ws.savingDisabled;
        ws.savingDisabled = false;
        ws.flushSave();
        ws.savingDisabled = oldSave;
    }

    private class WorldData {

        private WorldServer worldServer;
        private ChunkRegionLoader loader;

        public WorldData(WorldServer worldServer) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
            this.worldServer = worldServer;
            System.out.println("CLASS: " + worldServer.chunkProviderServer.getClass().getName());
            Field f = worldServer.chunkProviderServer.getClass().getDeclaredField("chunkLoader");
            f.setAccessible(true);
            this.loader = (ChunkRegionLoader) f.get(worldServer.chunkProviderServer);
        }

    }

}
