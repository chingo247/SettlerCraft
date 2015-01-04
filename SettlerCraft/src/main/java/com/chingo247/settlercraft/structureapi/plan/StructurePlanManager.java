
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
package com.chingo247.settlercraft.structureapi.plan;

import com.chingo247.settlercraft.structureapi.exception.StructureDataException;
import com.chingo247.settlercraft.structureapi.structure.old.AbstractStructureAPI;
import com.chingo247.settlercraft.structureapi.structure.old.Structure;
import com.chingo247.settlercraft.structureapi.plan.document.PlanDocument;
import com.chingo247.settlercraft.structureapi.plan.document.StructureDocument;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo
 */
public class StructurePlanManager {
    
    private final Map<String, StructurePlan> plans = Collections.synchronizedMap(new HashMap<String, StructurePlan>());
//    private final Map<Long, StructurePlan> structures = Collections.synchronizedMap(new HashMap<Long, StructurePlan>());
    private final Map<Long, StructurePlan> structures = Collections.synchronizedMap(new HashMap<Long, StructurePlan>());
    
    private final ExecutorService executor;
    private final AbstractStructureAPI structureAPI;
    
    public StructurePlanManager(AbstractStructureAPI structureAPI, ExecutorService executor) {
        this.executor = executor;
        this.structureAPI = structureAPI;
    }
    
    /**
     * Loads all plans in the target folder
     * @param planDocuments
     */
    public void load(List<PlanDocument> planDocuments) {
        
        
        List<Future> tasks = new LinkedList<>();
        for(final PlanDocument pd : planDocuments) {
            tasks.add(executor.submit(new Runnable() {

                @Override
                public void run() {
                    StructurePlan plan = new StructurePlan();
                    try {
                        plan.load(pd);
                        plans.put(plan.getRelativePath(), plan);
                    } catch (IOException | StructureDataException ex) {
                        Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }));
        }
        
        // Blocks until all tasks are done
        for(Future task : tasks) {
            try {
                task.get();
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
                for(Future f : tasks) {
                    f.cancel(true);
                }
            }
        }
       
    }
    
    public void clear() {
        plans.clear();
    }
    
    public StructurePlan getPlan(Structure structure) throws StructureDataException, IOException {
        StructurePlan plan = structures.get(structure.getId());
        
        if(plan == null) {
            StructureDocument d = structureAPI.getStructureDocumentManager().getDocument(structure.getId());
            plan = new StructurePlan();
            plan.load(d);
            structures.put(structure.getId(), plan);
        }
        return plan;
    }
    
    public void updatePlan(StructureDocument d) {
        StructurePlan plan = new StructurePlan();
        try {
            plan.load(d);
            structures.put(d.getStructure().getId(), plan);
        } catch (StructureDataException | IOException ex) {
            Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public StructurePlan getPlan(String id) {
        return plans.get(id);
    }
    
    public List<StructurePlan> getPlans() {
        return new LinkedList<>(plans.values());
    }
    
}
