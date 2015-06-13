/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.model.interfaces;

import com.chingo247.settlercraft.structureapi.model.structure.StructureNode;
import com.sk89q.worldedit.Vector;

/**
 * An object that is related to the structure
 * @author Chingo
 */
public interface IStructureObject {
    
    public StructureNode getStructure();
    
    public String getName();
    
    public void setName(String name);
    
    public double getX();
    
    public double getY();
    
    public double getZ();
    
    public int getBlockX();
    
    public int getBlockY();
    
    public int getBlockZ();
    
    public int getRelativeX();
    
    public int getRelativeY();
    
    public int getRelativeZ();
    
    public Vector getPosition();
    
    public Vector getRelativePosition();
    
}
