/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.model.structure;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.structureapi.model.world.StructureWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public interface IStructureRepository {
    
    public StructureNode findById(Long id);
    
    public StructureNode addStructure(StructureWorld world, String name, Vector position, CuboidRegion affectedArea, Direction direction, double price);
    
    public Iterable<StructureNode> findByWorld(UUID worldUUID);
    
    public Iterable<StructureNode> findBySettler(UUID settlerUUID);
    
    public Iterable<StructureNode> findWorldDeletedAfter(UUID worldUUID, long date);
    
    public Iterable<StructureNode> findCreatedAfter(UUID worldUUID, long date);
    
    public Iterable<StructureNode> findStructuresWithin(UUID worldUUID, CuboidRegion region, int limit);
    
    public int countStructuresOfSettler(UUID settlerUUID);
    
    public int countStructuresWithinWorld(UUID worldUUID);
    
    public boolean hasStructuresWithin(UUID worldUUID, CuboidRegion region);
    
}
