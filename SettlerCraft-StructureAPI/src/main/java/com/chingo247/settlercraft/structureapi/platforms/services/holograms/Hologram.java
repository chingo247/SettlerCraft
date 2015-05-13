/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.platforms.services.holograms;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.World;

/**
 *
 * @author Chingo
 */
public interface Hologram {
    
    public void insertLine(int i, String s);
    
    public void addLine(String s);
    
    public void removeLine(int i);
    
    public Vector getPosition();
    
    public World getWorld();
    
    public void delete();
    
}
