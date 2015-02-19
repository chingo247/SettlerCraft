
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
package com.chingo247.settlercraft.construction;

import com.chingo247.settlercraft.persistence.legacy.AbstractStructureAPI;
import com.chingo247.settlercraft.commons.util.KeyPool;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author Chingo
 */
public abstract class ConstructionTaskManager {
    
    protected final KeyPool<Long> executor;
    protected final AbstractStructureAPI structureAPI;
    protected final ExecutorService service;

    public ConstructionTaskManager(AbstractStructureAPI api, ExecutorService service) {
        this.executor = new KeyPool<>(service);
        this.structureAPI = api;
        this.service = service;
    }

    
    /**
     * Executed when a task fails...
     * @param task The failed task 
     */
    abstract void fail(SettlerCraftTask task, String reason);
    
    protected class ConstructionEntry {
        protected int jobId;
        protected UUID taskUUID;

        public ConstructionEntry(int jobId, UUID taskUUID) {
            this.jobId = jobId;
            this.taskUUID = taskUUID;
        }
    }
    
}
