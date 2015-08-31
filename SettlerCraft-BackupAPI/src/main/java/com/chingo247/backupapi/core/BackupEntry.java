package com.chingo247.backupapi.core;

/*
 * Copyright (C) 2015 Chingo
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
import com.chingo247.structureapi.construction.backup.event.BackupEntryStateChangeEvent;
import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.chingo247.structureapi.construction.backup.BackupState;
import com.chingo247.structureapi.construction.backup.IBackupEntry;
import com.chingo247.structureapi.construction.backup.IChunkManager;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.util.Iterator;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class BackupEntry implements IBackupEntry {

    private World world;
    private UUID uuid;
    private CuboidRegion region;
    private IChunkManager chunkManager;
    private BackupManager backupManager;
    private boolean isDone;
    private BackupState state;
    private File destinationFile;
    private boolean isCancelled;
    private final Object mutex;

    BackupEntry(BackupManager backupManager, World world, CuboidRegion region, File destinationFile, UUID uuid) {
        this.world = world;
        this.uuid = uuid;
        this.region = region;
        this.backupManager = backupManager;
        this.chunkManager = backupManager.getChunkManager();
        this.isDone = false;
        this.state = BackupState.WAITING;
        this.destinationFile = destinationFile;
        this.isCancelled = false;
        this.mutex = new Object();
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

    void process(BackupManager backupManager) {
        state = BackupState.SAVING_CHUNKS;
        AsyncEventManager.getInstance().post(new BackupEntryStateChangeEvent(this, BackupState.WAITING));
        Iterator<Vector2D> chunkIterator = region.getChunks().iterator();
//        System.out.println("[BackupEntry]: Processing chunks");
        while (chunkIterator.hasNext()) {
            Vector2D nextchunk = chunkIterator.next();
            int x = nextchunk.getBlockX();
            int z = nextchunk.getBlockZ();
            
            
            chunkManager.load(world, x, z);
            chunkManager.save(world, x, z);
            
        }
        state = BackupState.COPYING_DATA;
        AsyncEventManager.getInstance().post(new BackupEntryStateChangeEvent(this, BackupState.SAVING_CHUNKS));
        System.out.println("[BackupEntry]: Adding to write queue");
        backupManager.queueAsyncWrite(this);
    }
    
    

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
        synchronized(mutex) {
            return isCancelled;
        }
    }

    @Override
    public void cancel() {
        synchronized(mutex) {
            isCancelled = true;
        }
        if(destinationFile.exists()) {
            destinationFile.delete();
        }
    }

}
