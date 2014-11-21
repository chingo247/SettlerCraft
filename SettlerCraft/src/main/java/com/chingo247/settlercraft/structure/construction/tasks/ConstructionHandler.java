/*
 * Copyright (C) 2014 Chingo
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
package com.chingo247.settlercraft.structure.construction.tasks;

import com.chingo247.settlercraft.structure.AbstractStructureAPI;
import com.chingo247.settlercraft.structure.util.KeyPool;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author Chingo
 */
public abstract class ConstructionHandler {
    
    protected final KeyPool<Long> executor;
    protected final AbstractStructureAPI structureAPI;
    protected final ExecutorService service;

    public ConstructionHandler(AbstractStructureAPI api, ExecutorService service) {
        this.executor = new KeyPool<>(service);
        this.structureAPI = api;
        this.service = service;
    }

    
    /**
     * Executed when a task fails...
     * @param task The failed task 
     */
    abstract void fail(StructureAPITask task, String reason);
    
    protected class ConstructionEntry {
        protected int jobId;
        protected UUID taskUUID;

        public ConstructionEntry(int jobId, UUID taskUUID) {
            this.jobId = jobId;
            this.taskUUID = taskUUID;
        }
    }
    
}
