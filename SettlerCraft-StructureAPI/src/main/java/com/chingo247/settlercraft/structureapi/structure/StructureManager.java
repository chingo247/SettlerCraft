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
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.settlercraft.core.model.WorldNode;
import com.chingo247.settlercraft.structureapi.event.StructureCreateEvent;
import com.chingo247.settlercraft.structureapi.exception.StructureException;
import com.chingo247.settlercraft.structureapi.model.owner.StructureOwnerNode;
import com.chingo247.settlercraft.structureapi.model.owner.StructureOwnerRepository;
import com.chingo247.settlercraft.structureapi.model.owner.StructureOwnerType;
import com.chingo247.settlercraft.structureapi.model.owner.StructureOwnershipRelation;
import com.chingo247.settlercraft.structureapi.model.structure.Structure;
import com.chingo247.settlercraft.structureapi.model.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.model.structure.StructureRepository;
import com.chingo247.settlercraft.structureapi.model.world.StructureWorldNode;
import com.chingo247.settlercraft.structureapi.model.world.StructureWorldRepository;
import com.chingo247.settlercraft.structureapi.structure.plan.DefaultStructurePlan;
import com.chingo247.settlercraft.structureapi.structure.plan.IStructurePlan;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.FilePlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.SchematicPlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.xml.export.PlacementExporter;
import com.chingo247.settlercraft.structureapi.util.PlacementUtil;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.ILocation;
import com.chingo247.xplatform.core.IWorld;
import com.google.common.io.Files;
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
 * Manages the creation of Structures. As of version 2.1.0 structures of
 * different world's no long have to wait on each other. Each world has it's own
 * StructureManager and serves as monitor for placing structure.
 *
 * @since 2.1.0
 *
 * @author Chingo
 */
public class StructureManager {

    private final World world;
    private final StructureAPI structureAPI;
    private final APlatform platform;
    private final StructureWorldRepository structureWorldRepository;
    private final StructureRepository structureRepository;
    private final StructureOwnerRepository structureOwnerRepository;
    private final GraphDatabaseService graph;

    /**
     * Constructor
     *
     * @param world The world this StructureManager will 'Manage'
     */
    StructureManager(World world, StructureAPI structureAPI) {
        this.graph = SettlerCraft.getInstance().getNeo4j();
        this.world = world;
        this.structureAPI = structureAPI;
        this.platform = structureAPI.getPlatform();
        this.structureWorldRepository = new StructureWorldRepository(graph);
        this.structureRepository = new StructureRepository(graph);
        this.structureOwnerRepository = new StructureOwnerRepository(graph);
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
    private void checkWorldRestrictions(Placement p, World world, Vector position, Direction direction) throws StructureException {
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

    public Structure createStructure(Structure parentStructure, Placement placement, Vector position, Direction direction, Player owner) throws StructureException {
        IWorld w = platform.getServer().getWorld(world.getName());
        Structure structure = null;
        Transaction tx = null;
        File structureDirectory = null;
        try {
            tx = graph.beginTx();
            
            StructureWorldNode worldNode = structureWorldRepository.registerWorld(w.getName(), w.getUUID());
            StructureNode structureNode = create(worldNode, structure, placement, placement.getClass().getSimpleName(), position, direction, owner);

            if (structureNode != null) {
                
                structureDirectory = structureAPI.getDirectoryForStructure(worldNode, structureNode);
                if(structureDirectory.exists()) {
                    structureDirectory.delete();
                }
                structureDirectory.mkdirs();
                
                File structurePlanFile = new File(structureDirectory, "structureplan.xml");
                PlacementExporter exporter = new PlacementExporter();
                exporter.export(placement, structurePlanFile, "structureplan.xml", true);
                structure = new Structure(structureNode);
            }
            tx.success();
        } catch (StructureException ex) {
            if (tx != null) {
                tx.failure();
            }
            if(structureDirectory != null) {
                structureDirectory.delete();
            }
            throw ex;
        } catch (Exception ex) {
            if (tx != null) {
                tx.failure();
            }
            if(structureDirectory != null) {
                structureDirectory.delete();
            }
            throw new RuntimeException(ex);
        } finally {
            if(tx != null) {
                tx.close();
            }
        }
       
        if(structure != null) {
            EventManager.getInstance().getEventBus().post(new StructureCreateEvent(structure));
        }
        return structure;
    }

    public Structure createStructure(Structure parentStructure, IStructurePlan structurePlan, Vector position, Direction direction, Player owner) throws StructureException {
        IWorld w = platform.getServer().getWorld(world.getName());
        Structure structure = null;
        try (Transaction tx = graph.beginTx()){
            StructureWorldNode worldNode = structureWorldRepository.registerWorld(w.getName(), w.getUUID());
            StructureNode structureNode = create(worldNode, structure, structurePlan.getPlacement(), structurePlan.getName(), position, direction, owner);
             
            File structureDirectory = structureAPI.getDirectoryForStructure(worldNode, structureNode);
            if(structureDirectory.exists()) {
                structureDirectory.delete();
            }
            structureDirectory.mkdirs();
            try {
                moveResources(worldNode, structureNode, structurePlan);
            } catch (IOException ex) {
                structureDirectory.delete();
                Logger.getLogger(StructureManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            

        if (structureNode != null) {
            structure = new Structure(structureNode);
        }
        
        tx.success();
        }  
//         if(structure != null) {
//            EventManager.getInstance().getEventBus().post(new StructureCreateEvent(structure));
//        }
        
        
        return structure;
    }

    synchronized StructureNode create(StructureWorldNode worldNode, Structure parent, Placement placement, String name, Vector position, Direction direction, Player owner) throws StructureException {
        checkWorldRestrictions(placement, world, position, direction);

        Vector min = position;
        Vector max = PlacementUtil.getPoint2Right(min, direction, placement.getCuboidRegion().getMaximumPoint());
        CuboidRegion structureRegion = new CuboidRegion(min, max);
        StructureNode structureNode = null;

        StructureNode parentNode = null;
        if (parent != null) {
            parentNode = new StructureNode(parent.getNode());
            CuboidRegion parentRegion = parent.getCuboidRegion();
            if (!(parentRegion.contains(min) && parentRegion.contains(max))) {
                throw new StructureException("Structure overlaps structure #" + parent.getId() + ", but does not fit within it's boundaries");
            }
        }

        if (parentNode != null) {
            
            boolean hasWithin = parentNode.hasSubstructuresWithin(structureRegion);
            if (hasWithin) {
                throw new StructureException("Structure overlaps another structure...");
            }
            parentNode.addSubstructure(structureNode);
        }


        // Create the StructureNode - Where it all starts...
        structureNode = structureRepository.addStructure(worldNode, name, position, structureRegion, direction, 0.0);

        // Add owner!
        if (owner != null) {
            StructureOwnerNode settler = structureOwnerRepository.findByUUID(owner.getUniqueId());
            if (settler == null) {
                throw new RuntimeException("Settler was null!"); // SHOULD NEVER HAPPEN AS SETTLERS ARE ADDED AT MOMENT OF FIRST LOGIN
            }
            structureNode.addOwner(settler, StructureOwnerType.MASTER);
        }

        // Inherit ownership if there is a parent
        if (parentNode != null) {
            for (StructureOwnershipRelation rel : parentNode.getOwnerships()) {
                structureNode.addOwner(rel.getOwner(), rel.getOwnerType());
            }
        }

        return structureNode;

    }

    final void moveResources(WorldNode worldNode, StructureNode structureNode, IStructurePlan plan) throws IOException {
        // Give this structure a directory!
        File structureDir = structureAPI.getDirectoryForStructure(worldNode, structureNode);
        structureDir.mkdirs();

        Files.copy(plan.getFile(), new File(structureDir, "structureplan.xml"));
        Placement placement = plan.getPlacement();
        
        System.out.println("Moving resources, Direction: " + ((SchematicPlacement)placement).getRotation());

        // Move the resources if applicable!
        if (placement instanceof FilePlacement) {
            FilePlacement filePlacement = (FilePlacement) placement;
            File[] files = filePlacement.getFiles();
            for (File f : files) {
                Files.copy(f, new File(structureDir, f.getName()));
            }
        }
    }

    class PlacementPlan extends DefaultStructurePlan {

        PlacementPlan(String id, File planFile, Placement placement) {
            super(id, planFile, placement);
            setCategory("Other");
            setDescription("None");
            setPrice(0.0);
            setName(placement.getClass().getSimpleName());
        }

    }

}
