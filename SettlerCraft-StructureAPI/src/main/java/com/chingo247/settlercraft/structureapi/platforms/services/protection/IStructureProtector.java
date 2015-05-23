/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.platforms.services.protection;

import com.chingo247.settlercraft.structureapi.platforms.services.Service;
import com.chingo247.settlercraft.structureapi.structure.Structure;

/**
 * An interface for plugins that protect regions (e.g. worldguard)
 * @author Chingo
 */
public interface IStructureProtector extends Service {
    
    /**
     * Name of the plugin/service that will protect the structure
     * @return The name
     */
    public String getName();
    
    /**
     * Protects a Structure
     * @param structure 
     */
    public void protect(Structure structure);
    
    /**
     * Removes protection from a structure
     * @param structure 
     */
    public void removeProtection(Structure structure);
    
    /**
     * Checks whether a structure is protected
     * @param structure
     * @return True if Structure was protected
     */
    public boolean hasProtection(Structure structure);
    
}
