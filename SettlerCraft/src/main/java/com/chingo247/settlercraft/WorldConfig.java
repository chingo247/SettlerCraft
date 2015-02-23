package com.chingo247.settlercraft;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.sk89q.worldedit.world.World;
import java.util.Properties;

/**
 *
 * @author Chingo
 */
public class WorldConfig {
    
    private final World world;
    private boolean allowStructures;

    protected WorldConfig(World world) {
        this.world = world;
        this.allowStructures = false;
        
    }

    public boolean isAllowStructures() {
        return allowStructures;
    }

    public World getWorld() {
        return world;
    }
    
    void _load(SettlerCraft settlerCraft) {
        
    }
    
    
    
    
}
