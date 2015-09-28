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


import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.structureapi.exception.StructureException;
import com.chingo247.structureapi.model.plot.PlotRepository;
import com.chingo247.structureapi.model.world.IStructureWorldRepository;
import com.chingo247.structureapi.model.world.StructureWorldRepository;
import com.chingo247.structureapi.plan.placement.Placement;
import com.chingo247.structureapi.world.WorldConfig;
import com.chingo247.xplatform.core.ILocation;
import com.chingo247.xplatform.core.IWorld;
import com.google.common.util.concurrent.Monitor;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.util.UUID;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author ching
 */
public class ConstructionWorld implements IWorld {
    
    private IWorld world;
    private World worldEditWorld;
    private PlotRepository plotRepository;
    private IStructureWorldRepository worldRepository;
    private WorldConfig config;
    private Monitor monitor;
    private StructureCreator structureCreator;
    
    ConstructionWorld(IWorld w, StructureAPI structureAPI) {
        GraphDatabaseService graph = SettlerCraft.getInstance().getNeo4j();
        this.worldEditWorld = SettlerCraft.getInstance().getWorld(w.getUUID());
        this.world = w;
        this.plotRepository = new PlotRepository(graph);
        this.worldRepository = new StructureWorldRepository(graph);
        this.structureCreator = new StructureCreator(this);
    }
    
    World getWorldEditWorld() {
        return worldEditWorld;
    }

    public StructureCreator getStructureCreator() {
        return structureCreator;
    }
    
    /**
     * Checks if the placement is allowed to be placed. Placement should not
     * overlap the world's spawn and should also be placed at a height > 1 and
     * the top may not by higher than the world's max height
     *
     * @param p The placement
     * @param world The world
     * @param position The position of the placement
     * @param direction The direction
     */
    void checkWorldRestrictions(Placement p, World world, Vector position, Direction direction) throws StructureException {
        Vector min = p.getCuboidRegion().getMinimumPoint().add(position);
        Vector max = min.add(p.getCuboidRegion().getMaximumPoint());
        CuboidRegion placementDimension = new CuboidRegion(min, max);

        // Below the world?s
        if (placementDimension.getMinimumPoint().getBlockY() <= 1) {
            throw new StructureException("Structure must be placed at a minimum height of 1");
        }

        // Exceeds world height limit?
        if (placementDimension.getMaximumPoint().getBlockY() > world.getMaxY()) {
            throw new StructureException("Structure will reach above the world's max height (" + world.getMaxY() + ")");
        }

        // Check for overlap on the world's 'SPAWN'
        IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(world.getName());
        ILocation l = w.getSpawn();
        Vector spawnPos = new Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        if (placementDimension.contains(spawnPos)) {
            throw new StructureException("Structure overlaps the world's spawn...");
        }
    }

    public Monitor getMonitor() {
        return monitor;
    }
    
    @Override
    public String getName() {
        return world.getName();
    }

    @Override
    public UUID getUUID() {
        return world.getUUID();
    }

    @Override
    public int getMaxHeight() {
        return world.getMaxHeight();
    }

    @Override
    public ILocation getSpawn() {
        return world.getSpawn();
    }
    
    public void reloadConfig() {
        
    }
    
    
}
