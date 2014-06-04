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

package com.sc.api.structure.plan;

import com.sc.api.structure.entity.plan.StructurePlan;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Chingo
 */
public class PlanManager {
    
    private final Map<String, StructurePlan> plans;
    private static PlanManager instance;
    
    private PlanManager() {
        this.plans = new HashMap<>();
    }
    
    public static PlanManager getInstance() {
        if(instance == null) {
            instance = new PlanManager();
        }
        return instance;
    }
    
    public List<StructurePlan> getPlans() {
        return new ArrayList<>(plans.values());
    }
    
    public void add(StructurePlan plan) throws StructurePlanException {
        if(plans.containsKey(plan.getId().trim())) {
            throw new StructurePlanException("Plan id: " + plan.getId() + " already in use!");
        } else {
//            System.out.println("PlanId :" + plan.getId().trim() + ":");
            plans.put(plan.getId().trim(), plan);
        }
    }
    
    public StructurePlan getPlan(String planId) {
        return plans.get(planId);
    }
    
    public boolean contains(StructurePlan plan) {
        return plans.containsKey(plan.getId());
    }
    
    
    
    
}
