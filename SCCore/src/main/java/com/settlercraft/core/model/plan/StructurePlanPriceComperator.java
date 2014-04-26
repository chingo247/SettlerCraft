/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.core.model.plan;

import java.util.Comparator;

/**
 *
 * @author Chingo
 */
public class StructurePlanPriceComperator implements Comparator<StructurePlan>{

    @Override
    public int compare(StructurePlan planA, StructurePlan planB) {
        if(planA.getCost() > planB.getCost()) {
            return 1;
        } else {
            return -1;
        }
    }
    
}
