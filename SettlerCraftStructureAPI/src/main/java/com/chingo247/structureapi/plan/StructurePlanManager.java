/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.plan;

import com.chingo247.structureapi.Structure;
import com.chingo247.structureapi.StructureAPI;
import com.chingo247.structureapi.exception.StructureDataException;
import com.chingo247.structureapi.plan.document.PlanDocument;
import com.chingo247.structureapi.plan.document.StructureDocument;
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
    private final StructureAPI structureAPI;
    
    public StructurePlanManager(StructureAPI structureAPI, ExecutorService executor) {
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
        System.out.println("Retrieving plan");
        StructurePlan plan = structures.get(structure.getId());
        System.out.println("Plan: " + plan);
        
        if(plan == null) {
            StructureDocument d = structureAPI.getStructureDocumentManager().getDocument(structure.getId());
            plan = new StructurePlan();
            plan.load(d);
            structures.put(structure.getId(), plan);
        }
        return plan;
    }
    
    public void updatePlan(StructureDocument d) {
        System.out.println("Updating plan...");
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
