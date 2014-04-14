/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.event;

import com.settlercraft.core.event.SettlerCraftEvent;
import com.settlercraft.core.model.entity.structure.Structure;

/**
 *
 * @author Chingo
 */
public class StructureStateChangedEvent extends SettlerCraftEvent {
    
    private final Structure.STATE state;
    private final Structure structure;

    public StructureStateChangedEvent(Structure.STATE state, Structure structure) {
        this.state = state;
        this.structure = structure;
    }

    public Structure.STATE getState() {
        return state;
    }

    public Structure getStructure() {
        return structure;
    }
    
    
    
}
