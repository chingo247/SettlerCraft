/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.platforms.bukkit.services.worldguard;

import com.chingo247.settlercraft.structureapi.event.StructureAddOwnerEvent;
import com.chingo247.settlercraft.structureapi.event.StructureCreateEvent;
import com.chingo247.settlercraft.structureapi.event.StructureRemoveOwnerEvent;
import com.chingo247.settlercraft.structureapi.persistence.dao.StructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureOwnerType;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.google.common.eventbus.Subscribe;
import java.util.UUID;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Chingo
 */
public class WorldGuardStructureListener {
    
    private final WorldGuardHelper worldGuardHelper;
    private final GraphDatabaseService graph;
    private final StructureDAO structureDAO;

    public WorldGuardStructureListener(WorldGuardHelper worldGuardHelper, GraphDatabaseService graph) {
        this.worldGuardHelper = worldGuardHelper;
        this.graph = graph;
        this.structureDAO = new StructureDAO(graph);
    }
    
    @Subscribe
    public void onStructureCreate(StructureCreateEvent structureCreateEvent) {
        Structure structure = structureCreateEvent.getStructure();
        worldGuardHelper.protect(structure);
    }
    
    @Subscribe
    public void onStructureAddOwner(StructureAddOwnerEvent addOwnerEvent) {
        final UUID player = addOwnerEvent.getAddedOwner();
        final StructureOwnerType type = addOwnerEvent.getOwnerType();
        final Structure structure = addOwnerEvent.getStructure();
        if(type == StructureOwnerType.MEMBER) {
            worldGuardHelper.addMember(player, structure);
        } else {
            worldGuardHelper.addOwner(player, structure);
        }
    }
    
    @Subscribe
    public void onStructureRemoveOwner(StructureRemoveOwnerEvent removeOwnerEvent) {
        final UUID player = removeOwnerEvent.getRemovedOwner();
        final StructureOwnerType type = removeOwnerEvent.getOwnerType();
        final Structure structure = removeOwnerEvent.getStructure();
        if(type == StructureOwnerType.MEMBER)  {
            worldGuardHelper.removeMember(player, structure);
        } else {
            worldGuardHelper.removeOwner(player, structure);
        }
    }
    
}
