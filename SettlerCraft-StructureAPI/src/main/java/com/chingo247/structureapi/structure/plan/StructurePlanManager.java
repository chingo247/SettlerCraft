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
package com.chingo247.structureapi.structure.plan;

import com.google.common.collect.Maps;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * StructurePlan manager is a storage class for StructurePlans. 
 * All methods within this class are synchronised and can thus be called from multiple threads
 * @author Chingo
 */
public class StructurePlanManager {
    
    //TODO use Guava Cacheloader to reduce RAM-USAGE
    protected final Map<String,StructurePlan> plans;
//    private final Map<String,String> paths;
    private final String planDirectoryPath; 
   
    public StructurePlanManager(File planDirectory) {
        this.plans = Maps.newHashMap();
        this.planDirectoryPath = planDirectory.getAbsolutePath();
    }
   
    public StructurePlan getPlan(String planId) {
        synchronized(plans) {
            return plans.get(planId);
        }
    }
    
    public void putPlan(StructurePlan plan) {
        synchronized(plans) {
            plans.put(plan.getId(), plan);
        }
    }
    
    public void loadPlans() {
        synchronized(plans) {
            plans.clear();
        }
        
        File planDirectory = new File(planDirectoryPath);
        StructurePlanReader reader = new StructurePlanReader();
        List<StructurePlan> plansList = reader.readDirectory(planDirectory);
        for(StructurePlan plan : plansList) {
            StructurePlan existingPlan = getPlan(plan.getId());
            if(existingPlan != null) {
                if(!existingPlan.getFile().getAbsolutePath().equals(plan.getFile().getAbsolutePath())) {
                    throw new AssertionError("Well this is embarrasing... The MD5 Hash has failed us to create an unique String for 2 different paths"
                            + "Paths: [" + existingPlan.getFile().getAbsolutePath() + " , " + plan.getFile().getAbsolutePath() + "]");
                    // Chance is 1 in 2^64, im probably being paranoid...
                }
                continue; // it's exact the same plan...
            }
            putPlan(plan);
        }
    }
    
    protected File getPlanDirectory() {
        return new File(planDirectoryPath);
    }
    
    public List<StructurePlan> getPlans() {
        synchronized(plans) {
            return new ArrayList<>(plans.values());
        }
    }
    
   
    
    
}
