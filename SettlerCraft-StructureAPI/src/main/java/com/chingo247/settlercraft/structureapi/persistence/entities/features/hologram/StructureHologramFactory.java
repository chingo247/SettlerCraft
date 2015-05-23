/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.persistence.entities.features.hologram;

import com.chingo247.settlercraft.structureapi.structure.DefaultStructureFactory;
import com.chingo247.settlercraft.structureapi.structure.Structure;

/**
 *
 * @author Chingo
 */
public class StructureHologramFactory {
    
    public StructureHologram makeStructureHologram(StructureHologramNode structureHologramNode) {
        Structure structure = DefaultStructureFactory.getInstance().makeStructure(structureHologramNode.getStructure());
        StructureHologram hologram = new StructureHologram(structureHologramNode, structure);
        return hologram;
    }
    
}
