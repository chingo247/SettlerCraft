/*
 * Copyright (C) 2015 ching
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
package com.chingo247.structureapi;

import com.chingo247.structureapi.model.owner.OwnerDomain;
import com.chingo247.structureapi.model.owner.OwnerType;
import com.chingo247.structureapi.model.owner.Ownership;
import com.chingo247.structureapi.model.plot.IPlot;
import com.chingo247.structureapi.model.plot.Plot;
import com.chingo247.structureapi.model.settler.ISettlerRepository;
import com.chingo247.structureapi.model.settler.Settler;
import com.chingo247.structureapi.model.settler.SettlerRepositiory;
import com.chingo247.xplatform.core.ILocation;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.Iterator;
import java.util.UUID;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

/**
 *
 * @author ching
 */
public abstract class AbstractPlotManager<T extends IPlot> implements IPlotManager<T>{
    
    protected final ConstructionWorld world;
    protected final ISettlerRepository settlerRepository;
    protected final GraphDatabaseService graph;

    public AbstractPlotManager(ConstructionWorld world, GraphDatabaseService graph) {
        this.graph = graph;
        this.world = world;
        this.settlerRepository = new SettlerRepositiory(graph);
    }
    
    /**
     * Checks world restrictions (e.g if the given region is between y=1 and the max build height of the world)
     * @param world The world
     * @param region The affected region
     */
    @Override
    public void checkWorldRestrictions(CuboidRegion region) throws RestrictionException {

        // Below the world?s
        if (region.getMinimumPoint().getBlockY() <= 1) {
            throw new WorldRestrictionException("Must be placed at a minimum height of 1");
        }

        // Exceeds world height limit?
        if (region.getMaximumPoint().getBlockY() > world.getMaxHeight()) {
            throw new WorldRestrictionException("Will reach above the world's max height (" + world.getMaxHeight()+ ")");
        }
        

        // Check for overlap on the world's 'SPAWN'
        ILocation l = world.getSpawn();
        Vector spawnPos = new Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        if (region.contains(spawnPos)) {
            throw new WorldRestrictionException("Area overlaps the world's spawn...");
        }
    } 

    @Override
    public void removeOwnership(T plot, Iterable<UUID> players) {
        for(Iterator<UUID> it = players.iterator(); it.hasNext();) {
            UUID next = it.next();
            removeOwnership(plot, next);
        }
    }
    
    @Override
    public void removeOwnership(T plot, UUID player) {
        updateOwnership(plot, player, null);
    }

    @Override
    public void updateOwnership(T plot, UUID player, OwnerType type) {
        Node node = plot.getNode();
        OwnerDomain ownerDomain = new OwnerDomain(node);
        if(type == null) {
            Ownership ownership = ownerDomain.getOwnership(player);
            if(ownership != null) {
                OwnerType ownerType = ownership.getOwnerType();
                ownership.getRelation().delete();
                onRemoveOwnership(plot, player, ownerType);
            }
        } else {
            Settler settler = settlerRepository.findByUUID(player);
            boolean update = ownerDomain.setOwnership(settler, type);
            if(update) {
                onUpdateOwnership(plot, player, type);
            }
        }
    }

    @Override
    public void updateOwnership(T plot, Iterable<UUID> players, OwnerType type) {
        for(Iterator<UUID> it = players.iterator(); it.hasNext();) {
             UUID next = it.next();
             updateOwnership(plot, next, type);
        }
    }

    protected abstract void onRemoveOwnership(T plot, UUID player, OwnerType type);
    
    protected abstract void onUpdateOwnership(T plot, UUID player, OwnerType type);
    
}
