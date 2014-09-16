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
public abstract class StructureSlot {
    
    private Structure structure;

    public StructureSlot(Structure structure) {
        this.structure = structure;
    }

    public Structure getStructure() {
        return structure;
    }
    
    
    
}
