/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.core.manager;

import com.settlercraft.core.exception.DuplicateStructurePlanException;
import com.settlercraft.core.model.plan.StructurePlan;
import java.util.HashMap;

/**
 *
 * @author Chingo
 */
public class StructurePlanManager {
    
    private static StructurePlanManager instance;
    private final HashMap<String,StructurePlan> plans;
    
    private StructurePlanManager() {
        plans = new HashMap<>();
    }
    
    public static StructurePlanManager getInstance() {
        if(instance == null) {
            instance = new StructurePlanManager();
        }
        return instance;
    }
    
    /**
     * Adds a plan to the StructurePlanManager
     * @param plan The plan to add
     * @throws DuplicateStructurePlanException When name already in use
     */
    public void addPlan(StructurePlan plan) throws DuplicateStructurePlanException {
        if(plans.containsKey(plan.getConfig().getName()))  {
            throw new DuplicateStructurePlanException("name \""+plan.getConfig().getName()+"\" already in use");
        } else {
            plans.put(plan.getConfig().getName(), plan);
        }
    }
    
    public StructurePlan getPlan(String plan) {
        return plans.get(plan);
    }
    
    
    
}
