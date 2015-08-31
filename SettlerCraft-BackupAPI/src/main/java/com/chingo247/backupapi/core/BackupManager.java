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
import com.chingo247.backupapi.core.io.region.RegionManager;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.chingo247.structureapi.construction.backup.BackupState;
import com.chingo247.structureapi.construction.backup.IChunkManager;
import com.chingo247.structureapi.construction.backup.IBackupEntry;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IScheduler;
import com.google.common.collect.Maps;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Will use region files to create backups of regions
 *
 * @author Chingo
 */
public class BackupManager {
    private static final int MAX_EMPTY_RUNS = 10;
    private int interval = 1000;
    private int emptyRunCount = 0;
    private long maxProcessingTime = 200;

    private Map<UUID, BackupEntry> entries;
    private Iterator<BackupEntry> entryIt;
    private ExecutorService executor;
    private IChunkManager chunkManager;
    private IScheduler scheduler;
    private RegionManager regionManager;

    
    private boolean running = false;

    public BackupManager(IChunkManager chunkManager, IScheduler scheduler, APlatform platform) {
        this.entries = Maps.newHashMap();
        this.chunkManager = chunkManager;
        this.scheduler = scheduler;
        this.executor = SettlerCraft.getInstance().getExecutor();
        this.regionManager = new RegionManager(platform);
    }
    
    /**
     * Set the max time to process a 
     * @param time 
     */
    public void setMaxProcessingTime(long time) {
        this.maxProcessingTime = time;
    }
    
    /**
     * Sets the interval
     * @param interval The interval in milliseconds
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getInterval() {
        return interval;
    }

    public long getMaxProcessingTime() {
        return maxProcessingTime;
    }
    
    public IChunkManager getChunkManager() {
        return chunkManager;
    }
    
    public IBackupEntry createBackup(UUID uuid, World world, CuboidRegion region, File destinationFile) {
        start();
        BackupEntry entry = new BackupEntry(this, world, region, destinationFile, uuid);
        synchronized(entries) {
            entries.put(entry.getUUID(), entry);
        }
        return entry;
    }
    
    public IBackupEntry createBackup(World world, CuboidRegion region, File destinationFile) {
        return createBackup(UUID.randomUUID(), world, region, destinationFile);
    }

    private void start() {
        synchronized(this) {
            if(!running) {
                System.out.println("[BackupManager]: Scheduled first backup in " + interval + " ms");
                scheduler.runSync(new BackupTask());
                running = true;
            }
        }
        
    }
    
    private void stop() {
        synchronized(this) {
            System.out.println("[BackupManager]: Stopping");
            running = false;
        }
    }
    
    
    void queueAsyncWrite(final BackupEntry entry) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("[BackupManager]: Copying data...");
                    regionManager.copy(entry.getWorld().getName(), entry.getRegion(), entry.getDestinationFile());
                    BackupState oldState = entry.getState();
                    entry.setState(BackupState.COMPLETE);
                    System.out.println("[BackupManager]: Backup complete!");
                    AsyncEventManager.getInstance().post(new BackupEntryStateChangeEvent(entry, oldState));
                } catch (Exception ex) {
                    BackupState oldState = entry.getState();
                    entry.setState(BackupState.FAILED);
                    System.out.println("[BackupManager]: Backup failed!");
                    AsyncEventManager.getInstance().post(new BackupEntryStateChangeEvent(entry, oldState));
                    Logger.getLogger(BackupManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    @Subscribe
    @AllowConcurrentEvents
    public void onBackupEntryStateChanged(BackupEntryStateChangeEvent stateChangeEvent) {
        IBackupEntry entry = stateChangeEvent.getBackupEntry();
        
        switch(entry.getState()) {
            case FAILED: 
            case COMPLETE: 
                synchronized(entries) {
                    entries.remove(entry.getUUID());
                }
            default:break;
        }
    }

    private class BackupTask implements Runnable {

        @Override
        public void run() {
            
            try {
            
            // If the iterator is empty and there is nothing left
            // Schedule till MAX empty run count has been reached
            if(entryIt == null && entries.values().isEmpty()) {
                emptyRunCount++;
                if(emptyRunCount >= MAX_EMPTY_RUNS) {
                    System.out.println("[BackupManager]: Max empty run count has been reached");
                    stop();
                } else {
                    System.out.println("[BackupManager]: No backup tasks left, running empty run [" + emptyRunCount + "/" + MAX_EMPTY_RUNS + "]");
                    scheduler.runLater(interval, this);
                }
                return;
            }
            
            if(emptyRunCount > 0) {
                System.out.println("[BackupManager]: Resetting empty run count");
            }
            
            // If we reach this, we can be sure that there is work to do
            // There reset the empty run count
            emptyRunCount = 0;
            
            // Setup iterator if null or no element is next
            if (entryIt == null || !entryIt.hasNext()) {
                entryIt = entries.values().iterator();
            }

            // Process the entries
            long time = System.currentTimeMillis();
            while (entryIt.hasNext() && ((System.currentTimeMillis() - time) < maxProcessingTime)) {
                BackupEntry entry = entryIt.next();
                if(entry.isCancelled()) {
                    synchronized(entries) {
                        entries.remove(entry.getUUID());
                    }
                    continue;
                }
                
                
                if(entry.getState() == BackupState.WAITING) {
                    long start = System.currentTimeMillis();
                    System.out.println("[BackupManager]: Processing " + entry.getUUID().toString());
                    entry.process(BackupManager.this);
                    System.out.println("[BackupManager]: Processed in " + (System.currentTimeMillis() - start) + " ms");
                }
            }
            scheduler.runLater(interval, this);
            } catch (Exception ex) {
                 Logger.getLogger(BackupManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    

}
