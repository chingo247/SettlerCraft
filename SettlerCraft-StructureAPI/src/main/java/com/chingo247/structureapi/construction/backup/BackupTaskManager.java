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
package com.chingo247.structureapi.construction.backup;

import com.chingo247.backupapi.core.IBackupEntry;
import com.chingo247.backupapi.core.event.BackupEntryProgressUpdateEvent;
import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.chingo247.backupapi.core.event.BackupEntryStateChangeEvent;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.structureapi.model.structure.Structure;
import com.chingo247.xplatform.core.IColors;
import com.chingo247.xplatform.core.IPlayer;
import com.google.common.collect.Maps;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.sk89q.worldedit.entity.Player;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
class BackupTaskManager {
    
    private Map<UUID,BackupTask> tasks;
    private static BackupTaskManager instance;

    private BackupTaskManager() {
        this.tasks = Maps.newHashMap();
    }
    
    public static BackupTaskManager getInstance() {
        if(instance == null) {
            instance = new BackupTaskManager();
            AsyncEventManager.getInstance().register(instance);
        }
        return instance;
    }
    
    @Subscribe
    @AllowConcurrentEvents
    public void onBackupStateChanged(BackupEntryStateChangeEvent stateChangeEvent) {
        IBackupEntry entry = stateChangeEvent.getBackupEntry();
        UUID uuid = entry.getUUID();
        
        switch(entry.getState()) {
            case FAILED:
                System.out.println("[BackupTaskManager]: Task '" + uuid + "' has failed");
                synchronized(this) {
                    handleTask(uuid, true);
                }
                break;
            case COMPLETE:
                System.out.println("[BackupTaskManager]: Task '" + uuid + "' has completed");
                synchronized(this) {
                    handleTask(uuid, false);
                }
                break;
        }
    }
    
    @Subscribe
    @AllowConcurrentEvents
    public void onBackupEntryProgressUpdate(BackupEntryProgressUpdateEvent event) {
        
        
        if(event.getBackupEntry().getProgress() < 100) {
            BackupTask t = null;
            synchronized(this) {
                t = tasks.get(event.getBackupEntry().getUUID());
            }
            if(t != null) {
                UUID submitter = t.getSubmitter();
                Structure structure = t.getConstructionEntry().getStructure();
                if(submitter != null && structure != null) {
                    IColors colors = SettlerCraft.getInstance().getPlatform().getChatColors();
                    IPlayer p = SettlerCraft.getInstance().getPlatform().getPlayer(submitter);
                    if(p != null) {
                        p.sendMessage(colors.yellow() + "BACKUP PROGRESS" + colors.reset() + ": #"
                                + colors.gold() + t.getConstructionEntry().getStructure().getId() 
                                + colors.reset() + " at " + event.getBackupEntry().getProgress() + "%");
                    }
                }
            } 
        }
        
        
    }
    
    void registerTask(BackupTask task) {
        System.out.println("[BackupTaskManager]: RegisteredTask '" + task.getUUID() + "'");
        synchronized(this) {
            tasks.put(task.getUUID(), task);
        }
    }
    
    private void handleTask(UUID id, boolean failed) {
        BackupTask task = tasks.get(id);
        if(task != null) {
            task.setFailed(failed);
            task.finish();
        }
        tasks.remove(id);
    }
    
   
    
    
    
    
    
}
