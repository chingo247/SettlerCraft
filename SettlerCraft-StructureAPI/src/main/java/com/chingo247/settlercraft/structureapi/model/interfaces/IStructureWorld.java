/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.model.interfaces;

import com.chingo247.settlercraft.structureapi.model.structure.StructureNode;
import com.chingo247.settlercraft.core.model.interfaces.IWorld;
import com.chingo247.settlercraft.structureapi.world.WorldConfig;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;
import java.util.List;

/**
 *
 * @author Chingo
 */
public interface IStructureWorld extends IWorld {
    
    /**
     * Gets the amount of structures in this world
     * @return The amount of structures
     */
    public int getStructureCount();
    
    /**
     * Adds a  structure to this world
     * @param structure The structure to add
     */
    public void addStructure(StructureNode structure);
   
    /**
     * Gets the structures residing within this world
     * @return The structures
     */
    public List<StructureNode> getStructures();
    
    /**
     * Gets the structures that have been deleted after the given date
     * @param date The date
     * @return The structures that have been deleted after a given date
     * This method is used for structure invalidation
     */
    public List<StructureNode> getDeletedAfter(long date);
    
    /**
     * Gets the structures that have been created after the given date
     * @param date The date
     * @return The structures that have been created after a given date
     * This method is used for structure invalidation
     */
    public List<StructureNode> getCreatedAfter(long date);
    
    public boolean deleteStructure(long structureId);
    
    public File getWorldDirectory();
    
    public WorldConfig getConfig();
    
    public List<StructureNode> getStructuresWithin(CuboidRegion region, int limit);
    
    public boolean hasStructuresWithin(CuboidRegion region);
    
    
}
