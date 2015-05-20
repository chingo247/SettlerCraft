/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.platforms.bukkit.services.worldguard;

import com.chingo247.settlercraft.structureapi.event.StructureCreateEvent;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.google.common.eventbus.Subscribe;

/**
 *
 * @author Chingo
 */
public class WorldGuardStructureListener {
    
    private final WorldGuardHelper worldGuardHelper;

    public WorldGuardStructureListener(WorldGuardHelper worldGuardHelper) {
        this.worldGuardHelper = worldGuardHelper;
    }
    
    @Subscribe
    public void onStructureCreate(StructureCreateEvent structureCreateEvent) {
        Structure structure = structureCreateEvent.getStructure();
        worldGuardHelper.protect(structure);
    }
    
}
