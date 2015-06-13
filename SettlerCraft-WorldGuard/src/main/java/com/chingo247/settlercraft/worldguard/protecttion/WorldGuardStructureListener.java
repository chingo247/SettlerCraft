/*
 * Copyright (C) 2015 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.settlercraft.worldguard.protecttion;

import com.chingo247.settlercraft.structureapi.event.StructureAddOwnerEvent;
import com.chingo247.settlercraft.structureapi.event.StructureCreateEvent;
import com.chingo247.settlercraft.structureapi.event.StructureRemoveEvent;
import com.chingo247.settlercraft.structureapi.event.StructureRemoveOwnerEvent;
import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureRepository;
import com.chingo247.settlercraft.structureapi.model.owner.StructureOwnerType;
import com.chingo247.settlercraft.structureapi.model.structure.Structure;
import com.chingo247.settlercraft.structureapi.model.structure.StructureRepository;
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
    private final IStructureRepository structureDAO;

    public WorldGuardStructureListener(WorldGuardHelper worldGuardHelper, GraphDatabaseService graph) {
        this.worldGuardHelper = worldGuardHelper;
        this.graph = graph;
        this.structureDAO = new StructureRepository(graph);
    }
    
    @Subscribe
    public void onStructureCreate(StructureCreateEvent structureCreateEvent) {
        Structure structure = structureCreateEvent.getStructure();
        worldGuardHelper.protect(structure);
    }
    
    @Subscribe
    public void onStructureRemove(StructureRemoveEvent structureRemoveEvent) {
        Structure structure = structureRemoveEvent.getStructure();
        worldGuardHelper.removeProtection(structure);
    }
    
    @Subscribe
    public void onStructureAddOwner(StructureAddOwnerEvent addOwnerEvent) {
        final UUID player = addOwnerEvent.getAddedOwner();
        final StructureOwnerType type = addOwnerEvent.getOwnerType();
        final Structure structure = addOwnerEvent.getStructure();
        if(type == StructureOwnerType.MEMBER) {
            worldGuardHelper.addMember(player, structure);
        } else {
            worldGuardHelper.removeMember(player, structure);
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
