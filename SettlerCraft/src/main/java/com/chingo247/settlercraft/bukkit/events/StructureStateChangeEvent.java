/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.chingo247.settlercraft.bukkit.events;

import com.chingo247.settlercraft.structure.entities.structure.Structure;
import com.chingo247.settlercraft.structure.entities.structure.Structure.State;

/**
 * Called after the structure has changed state
 * @author Chingo
 */
public class StructureStateChangeEvent extends StructureEvent {
    
    private final State oldState;

    public StructureStateChangeEvent(Structure structure, State oldState) {
        super(structure);
        this.oldState = oldState;
    }

    public State getOldState() {
        return oldState;
    }
    
    
}
