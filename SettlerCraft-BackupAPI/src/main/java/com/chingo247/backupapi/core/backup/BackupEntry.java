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
package com.chingo247.backupapi.core.backup;

import com.chingo247.backupapi.core.BackupState;
import com.chingo247.backupapi.core.IBackupEntry;
import com.chingo247.backupapi.core.BackupMaker;
import com.chingo247.backupapi.core.IBackupMaker;
import com.chingo247.backupapi.core.IChunkManager;
import com.chingo247.backupapi.core.event.BackupEntryStateChangeEvent;
import com.chingo247.backupapi.core.io.region.RegionManager;
import com.chingo247.backupapi.core.world.IChunk;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_8_R1.Chunk;
import net.minecraft.server.v1_8_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;

/**
 *
 * @author Chingo
 */
public class BackupEntry implements IBackupEntry{
    
    private final World world;
    private final UUID uuid;
    private final CuboidRegion region;
    private final IBackupMaker backupMaker;
    private final IChunkManager chunkManager;
    
    private boolean isDone;
    private BackupState state;
    private File destinationFile;
    private boolean isCancelled;
    private final Object mutex;
    
    private Iterator<Vector2D> chunkIt;
    
    

    BackupEntry(IChunkManager chunkManager, IBackupMaker backupMaker, World world, CuboidRegion region, File destinationFile, UUID uuid) {
        this.world = world;
        this.uuid = uuid;
        this.region = region;
        this.backupMaker = backupMaker;
        this.isDone = false;
        this.state = BackupState.WAITING;
        this.destinationFile = destinationFile;
        this.isCancelled = false;
        this.mutex = new Object();
        this.chunkIt = region.getChunks().iterator();
        this.chunkManager = chunkManager;
    }

    void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    void setState(BackupState state) {
        this.state = state;
    }

    public File getDestinationFile() {
        return destinationFile;
    }

    @Override
    public BackupState getState() {
        return state;
    }

    @Override
    public void process(IBackupMaker backupManager) {
        try {
            long start = System.currentTimeMillis();
            
            WorldServer server = ((CraftWorld)Bukkit.getWorld(world.getName())).getHandle();
            org.bukkit.World w = Bukkit.getWorld(world.getName());
            
            int i = 0;
            while(chunkIt.hasNext()) {
                Vector2D v = chunkIt.next();
                
                int x = v.getBlockX();
                int z = v.getBlockZ();
                
                w.loadChunk(x, z, true);
                Chunk c = server.chunkProviderServer.getOrCreateChunk(v.getBlockX(), v.getBlockZ());
                server.chunkProviderServer.saveChunk(c);
                w.unloadChunk(x,z);
                i++;
                if(i % 10 == 0) {
                    server.chunkProviderServer.unloadChunks();
                    server.flushSave();
                }
            }
            server.flushSave();
            
            System.out.println("Saved in " + (System.currentTimeMillis() - start));
            write();
            System.out.println("Total time is " + (System.currentTimeMillis() - start));
            finish();
        } catch (IOException ex) {
            Logger.getLogger(BackupEntry.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(BackupEntry.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void finish() {
        BackupState oldState = getState();
        setState(BackupState.COMPLETE);
        AsyncEventManager.getInstance().post(new BackupEntryStateChangeEvent(this, oldState));
        isDone = true;
    }

    private void write() throws IOException, Exception {
        RegionManager regionManager = new RegionManager(SettlerCraft.getInstance().getPlatform());
        regionManager.copy(world.getName(), region, destinationFile);
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    public CuboidRegion getRegion() {
        return region;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public boolean isCancelled() {
        synchronized (mutex) {
            return isCancelled;
        }
    }

    @Override
    public void cancel() {
        synchronized (mutex) {
            isCancelled = true;
        }
        if (destinationFile.exists()) {
            destinationFile.delete();
        }
    }
    
    
    
}
