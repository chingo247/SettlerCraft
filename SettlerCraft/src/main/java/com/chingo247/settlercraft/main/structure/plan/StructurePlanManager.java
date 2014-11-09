/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.main.structure.plan;

import com.chingo247.settlercraft.main.exception.StructureDataException;
import com.chingo247.settlercraft.main.structure.AbstractStructureAPI;
import com.chingo247.settlercraft.main.structure.Structure;
import com.chingo247.settlercraft.main.structure.plan.document.PlanDocument;
import com.chingo247.settlercraft.main.structure.plan.document.StructureDocument;
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
