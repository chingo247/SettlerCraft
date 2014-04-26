/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.manager;

import com.google.common.collect.Maps;
import com.settlercraft.core.exception.DuplicateStructurePlanException;
import com.settlercraft.core.model.plan.StructurePlan;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class StructurePlanManager {

    private static StructurePlanManager instance;
    private HashMap<String, StructurePlan> plans = Maps.newHashMap();

    public static StructurePlanManager getInstance() {
        if (instance == null) {
            instance = new StructurePlanManager();

        }
        return instance;
    }

    public List<StructurePlan> getPlans() {
        return new ArrayList<>(plans.values());
    }

    public void clear() {
        plans.clear();
    }

    /**
     * Adds a plan to the StructurePlanManager
     *
     * @param plan The plan to add
     * @throws DuplicateStructurePlanException When name already in use
     */
    public void addPlan(StructurePlan plan) throws DuplicateStructurePlanException {
        if (plans.containsKey(plan.getName())) {
            throw new DuplicateStructurePlanException("name \"" + plan.getName() + "\" already in use");
        } else {
            plans.put(plan.getName(), plan);
        }
    }

    public List<ItemStack> getPlansToStacks() {
        List<ItemStack> ps = new ArrayList<>();
        for (StructurePlan plan : getPlans()) {
            ps.add(plan.toItemStack(1));
        }
        return ps;
    }

    public StructurePlan getPlan(String plan) {
        return plans.get(plan);
    }

}
