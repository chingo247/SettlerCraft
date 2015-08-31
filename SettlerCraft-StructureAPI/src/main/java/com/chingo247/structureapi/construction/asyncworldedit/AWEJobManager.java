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
package com.chingo247.structureapi.construction.asyncworldedit;

import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.chingo247.structureapi.construction.event.StructureTaskCancelledEvent;
import com.chingo247.structureapi.construction.event.StructureTaskCompleteEvent;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.api.blockPlacer.IBlockPlacerListener;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;

/**
 *
 * @author Chingo
 */
class AWEJobManager {
    
    private static AWEJobManager instance;
    
    private final Map<UUID, AWEPlacementTask> tasks;
    private final Lock lock;

    private AWEJobManager() {
        this.tasks = Maps.newHashMap();
        this.lock = new ReentrantLock();
        
        AsyncWorldEditMain.getInstance().getBlockPlacer().addListener(new IBlockPlacerListener() {

            @Override
            public void jobAdded(JobEntry je) {
                // DO NOTHING
            }

            @Override
            public void jobRemoved(JobEntry je) {
                System.out.println("On job removed!");
                if (je instanceof AWEJobEntry) {
                    // I FIRED THIS JOB!
                    System.out.println("It appears we fired this!");
                    AWEJobEntry jobEntry = (AWEJobEntry) je;
                    

                    boolean isCanceled = false;
                    lock.lock();
                    AWEPlacementTask task = null;
                    try {

                        task = tasks.get(jobEntry.getTaskUUID());
                        if (task != null) {
                            isCanceled = task.isCanceled();
                            if (isCanceled) {
                                if (task.isChecked()) { // Fixes duplicate state 
                                    System.out.println("Task was already cancelled...");
                                    isCanceled = false; // dont fire it again...
                                } else {
                                    task.setChecked(true);
                                }
                            }
                        }
                    } finally {
                        lock.unlock();
                    }

                    if (isCanceled) {
                        AsyncEventManager.getInstance().post(new StructureTaskCancelledEvent(task));
                    } else {
                        AsyncEventManager.getInstance().post(new StructureTaskCompleteEvent(task));
                    }
                    
                    if(task != null) {
                        System.out.println("Finishing: " + task.getUUID().toString());
                        task.finish();
                        tasks.remove(task.getUUID());
                    }
                    
                }
            }
        });
        
        
    }
    
    public static AWEJobManager getInstance() {
        if(instance == null) {
            instance = new AWEJobManager();
        }
        return instance;
    }
    
    void register(AWEPlacementTask task) {
        System.out.println("Registering task: " + task.getUUID().toString());
        synchronized(tasks) {
            this.tasks.put(task.getUUID(), task);
        }
    }
    
    void handleCancelled(AWEPlacementTask task) {
        synchronized(tasks) {
            task.setCanceled(true);
            task.finish();
            //TODO FIRE EVENT
            
            tasks.remove(task.getUUID());
        }
    }
    
}
