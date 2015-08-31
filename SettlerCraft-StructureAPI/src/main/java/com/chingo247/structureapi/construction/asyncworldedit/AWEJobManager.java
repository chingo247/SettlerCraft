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
import com.chingo247.structureapi.exception.ConstructionException;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.api.blockPlacer.IBlockPlacerListener;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;

/**
 *
 * @author Chingo
 */
class AWEJobManager {

    private static AWEJobManager instance;

    private Map<UUID, AWEPlacementTask> tasks;
    private Lock lock;

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
                if (je instanceof AWEJobEntry) {

                    lock.lock();
                    try {
                        // I FIRED THIS JOB!
                        AWEJobEntry jobEntry = (AWEJobEntry) je;
                        AWEPlacementTask task = tasks.get(jobEntry.getTaskUUID());
                        if (task != null) {
                            if (task.isChecked()) {
                                return;
                            }
                            task.setChecked(true);
                        }
                        if (task != null) {
                            task.finish();
                            tasks.remove(task.getUUID());
                        }
                    } finally {
                        lock.unlock();
                    }

                }

            }
        });

    }

    public static AWEJobManager getInstance() {
        if (instance == null) {
            instance = new AWEJobManager();
        }
        return instance;
    }

    void register(AWEPlacementTask task) {
        lock.lock();
        try {
            this.tasks.put(task.getUUID(), task);
        } finally {
            lock.unlock();
        }
    }

    void remove(AWEPlacementTask task) {
        lock.lock();
        try {
            tasks.remove(task.getUUID());
        } finally {
            lock.unlock();
        }

    }

}
