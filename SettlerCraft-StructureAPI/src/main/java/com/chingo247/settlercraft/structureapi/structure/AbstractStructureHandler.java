/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft.structureapi.structure;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.persistence.dao.world.IWorldDAO;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import com.chingo247.settlercraft.structureapi.exception.StructureAPIException;
import com.chingo247.settlercraft.structureapi.exception.StructureException;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.FilePlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.xplatform.core.ILocation;
import com.chingo247.xplatform.core.IWorld;
import com.google.common.io.Files;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.IOException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
abstract class AbstractStructureHandler<T extends StructurePlan> {

    private final IWorldDAO worldDAO;
    private final GraphDatabaseService graph;
    private final StructureAPI structureAPI;

    public AbstractStructureHandler(GraphDatabaseService graph, IWorldDAO worldDAO, StructureAPI structureAPI) {
        this.worldDAO = worldDAO;
        this.graph = graph;
        this.structureAPI = structureAPI;
    }

    protected WorldNode registerWorld(World world) {
        IWorld w = structureAPI.getPlatform().getServer().getWorld(world.getName());
        if (w == null) {
            throw new RuntimeException("World was null");
        }

        try (Transaction tx = graph.beginTx()) {
            WorldNode worldNode = worldDAO.find(w.getUUID());
            if (worldNode == null) {
                worldDAO.addWorld(w.getName(), w.getUUID());
                worldNode = worldDAO.find(w.getUUID());
                if (worldNode == null) {
                    throw new StructureAPIException("Something went wrong during creation of the 'WorldNode'"); // SHOULD NEVER HAPPEN
                }
            }
            tx.success();
            return worldNode;
        }
    }

    protected StructureAPI getStructureAPI() {
        return structureAPI;
    }

    protected GraphDatabaseService getGraph() {
        return graph;
    }

    protected IWorldDAO getWorldDAO() {
        return worldDAO;
    }

    public abstract Structure handleStructure(T structurePlan, World world, Vector vector, Direction direction, Player owner) throws Exception;

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
    protected void checkDefaultRestrictions(Placement p, World world, Vector position, Direction direction) throws StructureException {
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

    protected void moveResources(WorldNode worldNode, StructureNode structureNode, StructurePlan plan) throws IOException {
        // Give this structure a directory!
        File structureDir = getDirectoryForStructure(worldNode, structureNode);
        structureDir.mkdirs();
        
        Files.copy(plan.getFile(), new File(structureDir, "structureplan.xml"));
        Placement placement = plan.getPlacement();
        

        // Move the resources if applicable!
        if (placement instanceof FilePlacement) {
            FilePlacement filePlacement = (FilePlacement) placement;
            File[] files = filePlacement.getFiles();
            for (File f : files) {
                Files.copy(f, new File(structureDir, f.getName()));
            }
        }
    }
    
    protected File getDirectoryForStructure(WorldNode worldNode, StructureNode structureNode) {
        File structuresDirectory = structureAPI.getStructuresDirectory(worldNode.getName());
        File structureDir = new File(structuresDirectory, String.valueOf(structureNode.getId()));
        return structureDir;
    }

}
