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

import com.chingo247.backupapi.core.IBackupAPI;
import com.chingo247.backupapi.core.IBackupEntry;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.chingo247.structureapi.construction.ConstructionEntry;
import com.chingo247.structureapi.construction.event.StructureTaskStartEvent;
import com.chingo247.structureapi.construction.task.StructureTask;
import com.chingo247.structureapi.exception.StructureTaskException;
import com.chingo247.structureapi.model.structure.ConstructionStatus;
import com.chingo247.structureapi.model.structure.Structure;
import com.chingo247.structureapi.structure.StructureAPI;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo
 */
public class BackupTask extends StructureTask {
    
    private File target;
    private IBackupEntry backupEntry;
    

    public BackupTask(ConstructionEntry constructionEntry, String backupName) throws StructureTaskException {
        super(ConstructionStatus.CREATING_BACKUP.name(), constructionEntry);
        
        ConstructionEntry entry = getConstructionEntry();
        Structure structure = entry.getStructure();
        File structureDir = structure.getStructureDirectory();
        
        File backupDir = new File(structureDir, "//backups");
        backupDir.mkdirs();
        this.target = new File(backupDir, backupName + ".snapshot");
        
        System.out.println("[BackupTask]: Creating backup '" + target.getAbsolutePath() + "'");
        
        if(target.exists()) {
            throw new StructureTaskException("File '" + backupName + "' already exists!");
        }
    }
    
    @Override
    protected void _start() {
        IBackupAPI backupAPI = StructureAPI.getInstance().getBackupAPI();
        ConstructionEntry entry = getConstructionEntry();
        Structure structure = entry.getStructure();

        AsyncEventManager.getInstance().post(new StructureTaskStartEvent(this));
        
        try {
            BackupTaskManager.getInstance().registerTask(this);
            backupEntry = backupAPI.createBackup(getUUID(), SettlerCraft.getInstance().getWorld(structure.getWorld().getName()), structure.getCuboidRegion(), target);
            
        } catch (Exception ex) {
            Logger.getLogger(BackupTask.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            setFailed(true);
            finish();
        }
        
    }

    @Override
    protected void _cancel() {
        if(backupEntry != null) {
            IBackupAPI backupAPI = StructureAPI.getInstance().getBackupAPI();
            backupAPI.cancel(backupEntry);
        }
    }
    
}
