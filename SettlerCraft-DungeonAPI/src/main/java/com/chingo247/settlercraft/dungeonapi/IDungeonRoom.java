/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.dungeonapi;

import com.sk89q.worldedit.regions.CuboidRegion;

/**
 *
 * @author Chingo
 */
public interface IDungeonRoom {
    
    String getName();
    
    int getX();
    
    int getZ();
    
    Iterable<IDungeonTile> getTiles();
    
    void addTile(IDungeonTile tile);
    
    CuboidRegion getCuboidRegion();
    
}
