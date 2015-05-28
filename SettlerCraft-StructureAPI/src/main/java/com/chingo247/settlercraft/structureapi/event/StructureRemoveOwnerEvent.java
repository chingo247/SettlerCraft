/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.event;

import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureOwnerType;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class StructureRemoveOwnerEvent {
    
    private final UUID removedOwner;
    private final Structure structure;
    private final StructureOwnerType ownerType;

    public StructureRemoveOwnerEvent(UUID removedOwner, Structure structure, StructureOwnerType type) {
        this.removedOwner = removedOwner;
        this.structure = structure;
        this.ownerType = type;
    }

    public UUID getRemovedOwner() {
        return removedOwner;
    }

    public Structure getStructure() {
        return structure;
    }

    public StructureOwnerType getOwnerType() {
        return ownerType;
    }
    
    
    
}
