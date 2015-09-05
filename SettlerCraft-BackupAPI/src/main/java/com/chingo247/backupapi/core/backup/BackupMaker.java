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
import com.chingo247.backupapi.core.IBackupMaker;
import com.chingo247.backupapi.core.IChunkManager;
import com.chingo247.backupapi.core.event.BackupEntryStateChangeEvent;
import com.chingo247.backupapi.core.exception.BackupException;
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
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo
 */
public class BackupMaker implements IBackupMaker {
    
    private static final int MAX_EMPTY_RUNS = 5;
    private int interval;
    private int emptyRunCount;
    
    private Map<UUID, IBackupEntry> entries;
    private Iterator<IBackupEntry> entryIt;
    private boolean running;
    private Lock lock;
    private BackupTask backupTask;
    private IChunkManager chunkManager;
    private IScheduler scheduler;
    private APlatform platform;
    
    private int chunks;
    private long maxProcessingTime;
    

    public BackupMaker(IScheduler scheduler, IChunkManager chunkManager, APlatform platform) {
        this.entries = Maps.newHashMap();
        this.platform = platform;
        this.backupTask = new BackupTask(this);
    }
    
    @Override
    public void setChunks(int chunks) {
        this.chunks = chunks;
    }

    @Override
    public int getChunks() {
        return chunks;
    }

    @Override
    public long getTime() {
        return maxProcessingTime;
    }
    
    /**
     * Set the max time to process a
     * @param time
     */
    @Override
    public void setTime(long time) {
        this.maxProcessingTime = time;
    }

    @Override
    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public int getInterval() {
        return interval;
    }

    public long getMaxProcessingTime() {
        return maxProcessingTime;
    }
    
    @Override
    public IBackupEntry createBackup(World world, CuboidRegion region, File destinationFile) throws BackupException {
        return createBackup(UUID.randomUUID(), world, region, destinationFile);
    }
    
    @Override
    public IBackupEntry createBackup(UUID uuid, World world, CuboidRegion region, File destinationFile) throws BackupException {
        IBackupEntry entry = null;

        lock.lock();
        try {
            entry = new BackupEntry(chunkManager, this, world, region, destinationFile, uuid);
            if (entries.get(uuid) != null) {
                throw new BackupException("Entry with UUID '" + uuid.toString() + "' already exists!");
            }
            entries.put(uuid, entry);
            
           
            if (!running) {
                System.out.println("start!");
                System.out.println("[BackupManager]: Scheduled first backup in " + interval + " ms");
                scheduler.runAsync(backupTask);
                running = true;
            }
        } finally {
            lock.unlock();
        }
        return entry;
    }
    
    private void stop() {
        lock.lock();
        try {
            System.out.println("[BackupManager]: Stopping");
            running = false;
        } finally {
            lock.unlock();
        }
    }
    
    @Subscribe
    @AllowConcurrentEvents
    public void onBackupEntryStateChanged(BackupEntryStateChangeEvent stateChangeEvent) {
        IBackupEntry entry = stateChangeEvent.getBackupEntry();

        switch (entry.getState()) {
            case FAILED:
            case COMPLETE:
                remove(entry);
            default:
                break;
        }
    }
    
    private class BackupTask implements Runnable {
        
        private IBackupMaker backupMaker;

        public BackupTask(IBackupMaker BackupMaker) {
            this.backupMaker = BackupMaker;
        }
        

        @Override
        public void run() {
            long runTime = System.currentTimeMillis();
            System.out.println("RUN ");
            try {

                // If the iterator is empty and there is nothing left
                // Schedule till MAX empty run count has been reached
                if (entryIt != null && !entryIt.hasNext() && entries.values().isEmpty()) {
                    emptyRunCount++;
                    if (emptyRunCount >= MAX_EMPTY_RUNS) {
                        System.out.println("[BackupManager]: Max empty run count has been reached");
                        stop();
                    } else {
                        System.out.println("[BackupManager]: No backup tasks left, running empty run [" + emptyRunCount + "/" + MAX_EMPTY_RUNS + "]");
                        scheduler.runLaterAsync(interval, this);
                    }
                    return;
                }

                if (emptyRunCount > 0) {
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
                    IBackupEntry entry = entryIt.next();
                    if (entry.isCancelled() || entry.getState() == BackupState.FAILED) {
                        remove(entry);
                        continue;
                    }

                    if (entry.getState() == BackupState.WAITING) {
                        long start = System.currentTimeMillis();
                        System.out.println("[BackupManager]: Processing " + entry.getUUID().toString());
                        entry.process(backupMaker);
                        System.out.println("[BackupManager]: Processed in " + (System.currentTimeMillis() - start) + " ms");
                    }
                }

                System.out.println("RUN Done in " + (System.currentTimeMillis() - runTime));
                scheduler.runLaterAsync(interval, this);
            } catch (Exception ex) {
                Logger.getLogger(BackupMaker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    
    private void remove(IBackupEntry entry) {
        lock.lock();
        try {
            entries.remove(entry.getUUID());
        } finally {
            lock.unlock();
        }
    }
    
    
    
}
