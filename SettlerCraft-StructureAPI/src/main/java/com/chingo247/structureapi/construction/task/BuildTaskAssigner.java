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
package com.chingo247.structureapi.construction.task;

import com.chingo247.structureapi.construction.ConstructionEntry;
import com.chingo247.structureapi.construction.IBuildTaskAssigner;
import com.chingo247.structureapi.construction.backup.IBackupAPI;
import com.chingo247.structureapi.exception.ConstructionException;
import com.chingo247.structureapi.model.structure.Structure;
import com.chingo247.structureapi.structure.StructureAPI;
import com.chingo247.structureapi.structure.plan.placement.options.BuildOptions;
import com.sk89q.worldedit.EditSession;
import java.io.File;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class BuildTaskAssigner implements IBuildTaskAssigner {
    
    private DefaultStructureTaskFactory taskFactory;

    public BuildTaskAssigner() {
        this.taskFactory = new DefaultStructureTaskFactory();
    }
    
    
    
    
    @Override
    public void assignTasks(EditSession session, UUID player, ConstructionEntry entry, BuildOptions buildOptions) throws ConstructionException {
        Structure structure = entry.getStructure();

        // Only create backups when available
        IBackupAPI backupAPI = StructureAPI.getInstance().getBackupAPI();
        if(backupAPI != null) {
            // 1. Create backup if none exists
            File structureDir = structure.getStructureDirectory();
            File backupFile = new File(structureDir, "restore.snapshot");
            if(!backupFile.exists()) {
                entry.addTask(taskFactory.backup(structure, "restore"));
            }
        }
        
        
        
        
        // 2. TODO PLACE FENCE
        
        // 3. Build
        entry.addTask(taskFactory.build(session, player, entry.getStructure(), buildOptions));
    }
    
}
