/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.persistence.entities.features.hologram;

import com.chingo247.settlercraft.structureapi.structure.Structure;

/**
 *
 * @author Chingo
 */
public class StructureHologram {
    
    private final int relativeX;
    private final int relativeY;
    private final int relativeZ;
    
    private final Structure structure;
    
    StructureHologram(StructureHologramNode hologramNode, Structure structure) {
        this.relativeX = hologramNode.getRelativeX();
        this.relativeY = hologramNode.getRelativeY();
        this.relativeZ = hologramNode.getRelativeZ();
        this.structure = structure;
    }

    public Structure getStructure() {
        return structure;
    }
    
    public int getRelativeX() {
        return relativeX;
    }

    public int getRelativeY() {
        return relativeY;
    }

    public int getRelativeZ() {
        return relativeZ;
    }
    
    
    
}
