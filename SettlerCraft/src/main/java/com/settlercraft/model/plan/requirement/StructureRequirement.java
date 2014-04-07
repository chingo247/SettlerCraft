/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.plan.requirement;

import com.settlercraft.model.plan.requirement.material.MaterialRequirement;
import com.settlercraft.model.plan.StructurePlan;

/**
 * All requirements for this structure will be initialized once for each building at server start
 * @author Chingo
 */
public final class StructureRequirement {
    
    
    private MaterialRequirement materialRQ;
    
    public StructureRequirement(StructurePlan sp) {
        this.materialRQ = new MaterialRequirement(sp.getSchematic());
    }

    public MaterialRequirement getMaterialRQ() {
        return materialRQ;
    }
    
    

    
    





}
