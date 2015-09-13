/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.model.structure;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.structureapi.model.structure.StructureNode;
import com.chingo247.structureapi.model.world.StructureWorldNode;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;

/**
 *
 * @author Chingo
 */
public interface IStructureRepository {
    
    public StructureNode findById(Long id);
    
    public StructureNode addStructure(StructureWorldNode world, String name, Vector position, CuboidRegion affectedArea, Direction direction, double price);
    
    
    
}
