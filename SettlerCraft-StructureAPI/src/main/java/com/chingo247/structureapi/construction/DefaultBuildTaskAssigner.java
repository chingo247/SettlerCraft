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
package com.chingo247.structureapi.construction;

import com.chingo247.structureapi.construction.ConstructionEntry;
import com.chingo247.structureapi.construction.ITaskAssigner;
import com.chingo247.backupapi.core.IBackupAPI;
import com.chingo247.structureapi.exception.ConstructionException;
import com.chingo247.structureapi.model.structure.Structure;
import com.chingo247.structureapi.StructureAPI;
import com.chingo247.structureapi.construction.options.BuildOptions;
import com.chingo247.structureapi.construction.options.Options;
import com.sk89q.worldedit.EditSession;
import java.io.File;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class DefaultBuildTaskAssigner implements ITaskAssigner {
    
    private DefaultStructureTaskFactory taskFactory;

    public DefaultBuildTaskAssigner() {
        this.taskFactory = new DefaultStructureTaskFactory();
    }
    
    
    
    
    @Override
    public void assignTasks(EditSession session, UUID player, ConstructionEntry entry, Options options) throws ConstructionException {
        Structure structure = entry.getStructure();

        // Only create backups when available
        IBackupAPI backupAPI = StructureAPI.getInstance().getBackupAPI();
        if(backupAPI != null) {
            // 1. Create backup if none exists
            File structureDir = structure.getDirectory();
            File backupFile = new File(structureDir, "restore.snapshot");
            if(!backupFile.exists()) {
                entry.addTask(taskFactory.backup(player, structure, "restore"));
            }
        }
        
        // 2. TODO PLACE FENCE
        
        // 3. Build
        entry.addTask(taskFactory.build(session, player, entry.getStructure(), options));
    }
    
}
