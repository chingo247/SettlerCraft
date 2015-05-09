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
package com.chingo247.settlercraft.structureapi.structure;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.persistence.dao.settler.ISettlerDAO;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import com.chingo247.settlercraft.core.persistence.dao.world.DefaultWorldFactory;
import com.chingo247.settlercraft.core.persistence.dao.world.IWorldDAO;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import com.chingo247.settlercraft.structureapi.exception.StructureException;
import com.chingo247.settlercraft.structureapi.persistence.dao.IStructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureOwnerType;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureWorldNode;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structureapi.util.PlacementUtil;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class StructureHandler extends AbstractStructureHandler {

    private final IStructureDAO structureDAO;
    private final ISettlerDAO settlerDAO;

    public StructureHandler(GraphDatabaseService graph, IWorldDAO worldDAO, IStructureDAO structureDAO, ISettlerDAO settlerDAO, StructureAPI structureAPI) {
        super(graph, worldDAO, structureAPI);
        this.settlerDAO = settlerDAO;
        this.structureDAO = structureDAO;
    }

    @Override
    public Structure handleStructure(StructurePlan plan, World world, Vector position, Direction direction, Player owner) throws StructureException {
        // Check the default restrictions first
        Placement placement = plan.getPlacement();
        checkDefaultRestrictions(placement, world, position, direction);
        WorldNode worldNode = registerWorld(world);

        Structure structure = null;
        try (Transaction tx = getGraph().beginTx()) {

            // Check for overlap with other structures
            Vector min = position;
            Vector max = PlacementUtil.getPoint2Right(min, direction, placement.getCuboidRegion().getMaximumPoint());
            CuboidRegion structureRegion = new CuboidRegion(min, max);
            com.chingo247.settlercraft.core.World scWorld = DefaultWorldFactory.instance().makeWorld(worldNode);
            if (structureDAO.hasStructuresWithin(scWorld, structureRegion)) {
                tx.success(); // End here
                throw new StructureException("Structure overlaps another structure...");
            }

            // Create the StructureNode - Where it all starts...
            StructureNode structureNode = structureDAO.addStructure(plan.getName(), structureRegion, direction, plan.getPrice());
            
            System.out.println("Region: " + structureRegion.getMinimumPoint() + ", " + structureRegion.getMaximumPoint());
            
            System.out.println("WorldNode: " + worldNode);
            
            StructureWorldNode structureWorldNode = new StructureWorldNode(worldNode);
            structureWorldNode.addStructure(structureNode);

           

            // Add owner!
            if (owner != null) {
                SettlerNode settler = settlerDAO.find(owner.getUniqueId());
                if (settler == null) {
                    tx.failure();
                    throw new RuntimeException("Settler was null!"); // SHOULD NEVER HAPPEN AS SETTLERS ARE ADDED AT MOMENT OF FIRST LOGIN
                }
                structureNode.addOwner(settler, StructureOwnerType.MASTER);
            }
            
            try {
                moveResources(worldNode, structureNode, plan);
            } catch (IOException ex) {
                // rollback...
                File structureDir = getDirectoryForStructure(worldNode, structureNode);
                structureDir.delete();
                tx.failure(); 
                Logger.getLogger(StructureHandler.class.getName()).log(Level.SEVERE, "Error occured during structure creation... rolling back changes made", ex);
            }

            tx.success();

            structure = DefaultStructureFactory.instance().makeStructure(structureNode);
        }
        return structure;
    }

}
