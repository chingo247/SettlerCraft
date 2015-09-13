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
package com.chingo247.backupapi.core;

import com.chingo247.backupapi.core.event.BackupEntryStateChangeEvent;
import com.chingo247.backupapi.core.io.region.RegionManager;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo
 */
public class BackupEntry implements IBackupEntry {

    private final World world;
    private final UUID uuid;
    private final CuboidRegion region;
    private final IChunkLoader loader;
    private final IChunkManager manager;

    private boolean isDone;
    private BackupState state;
    private File destinationFile;
    private boolean isCancelled;
    private final Object mutex;

    private Iterator<Vector2D> chunkIt;
    private boolean firstTime = false;
    private int count;
    private int total;
    
    private boolean failedProcess = false;
    private Vector2D currentChunk;

    BackupEntry(IChunkManager chunkManager,  World world, CuboidRegion region, File destinationFile, UUID uuid) {
        this.world = world;
        this.uuid = uuid;
        this.region = region;
        this.isDone = false;
        this.state = BackupState.WAITING;
        this.destinationFile = destinationFile;
        this.isCancelled = false;
        this.mutex = new Object();
        
        Set<Vector2D> chunks = region.getChunks();
        this.total = chunks.size();
        System.out.println("[BackupEntry]: Total number of chunks " + total);
        this.chunkIt = chunks.iterator();
        this.manager = chunkManager;
        this.loader = chunkManager.getLoader(world.getName());
    }

    @Override
    public int getProgress() {
        return (int)((count * 100.0f) / total);
    }
    
    

    void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    void setState(BackupState state) {
        this.state = state;
    }

    @Override
    public File getDestinationFile() {
        return destinationFile;
    }

    @Override
    public BackupState getState() {
        return state;
    }

    @Override
    public void process() {
        if(firstTime) {
            firstTime = false;
            setState(BackupState.SAVING_CHUNKS);
            AsyncEventManager.getInstance().post(new BackupEntryStateChangeEvent(this));
        }
        
        if (chunkIt.hasNext()) {
            if(!failedProcess) {
                currentChunk = chunkIt.next();
            }
            int x = currentChunk.getBlockX();
            int z = currentChunk.getBlockZ();
            try {
                loader.load(x, z);
                loader.unload(x, z);
                failedProcess = false;
                count++;
                if(count % 50 == 0) {
                    manager.writeToDisk(world.getName());
                }
            } catch(Exception ex) {
                count--;
                failedProcess = true;
                Logger.getLogger(BackupEntry.class.getName()).log(Level.WARNING, "[SettlerCraft-BackupAPI]: Error during process", ex);
            }
            
        } else {
            System.out.println("[BackupEntry]: Writing to disk...");
            try {
                manager.writeToDisk(world.getName());
                write();
                finish();
            } catch (Exception ex) {
                Logger.getLogger(BackupEntry.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void finish() {
        setState(BackupState.COMPLETE);
        AsyncEventManager.getInstance().post(new BackupEntryStateChangeEvent(this));
        isDone = true;
    }

    private void write() throws IOException, Exception {
        setState(BackupState.COPYING_DATA);
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
