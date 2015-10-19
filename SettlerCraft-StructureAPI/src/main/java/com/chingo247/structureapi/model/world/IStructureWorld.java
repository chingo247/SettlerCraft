/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.model.world;

import com.chingo247.structureapi.model.structure.StructureNode;
import com.chingo247.settlercraft.core.model.interfaces.IWorld;
import com.chingo247.structureapi.model.zone.IConstructionZone;

/**
 *
 * @author Chingo
 */
public interface IStructureWorld extends IWorld {
    
    /**
     * Adds a  structure to this world
     * @param structure The structure to add
     */
    void addStructure(StructureNode structure);
    
    /**
     * Adds a  structure to this world
     * @param structure The structure to add
     */
    boolean deleteStructure(long structureId);
    
    void addZone(IConstructionZone zone);

    boolean deleteZone(long id);
    
    
    
}
