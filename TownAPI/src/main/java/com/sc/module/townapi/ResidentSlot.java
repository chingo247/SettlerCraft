/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.townapi;

import com.sc.module.structureapi.structure.Structure;

/**
 *
 * @author Chingo
 */
public class ResidentSlot extends StructureSlot {
    
    private Peasant peasant;

    public ResidentSlot(Structure structure) {
        super(structure);
    }
    
    public boolean inUse() {
        return peasant != null;
    }

    void setPeasant(Peasant peasant) {
        this.peasant = peasant;
    }
    
    
    
}
