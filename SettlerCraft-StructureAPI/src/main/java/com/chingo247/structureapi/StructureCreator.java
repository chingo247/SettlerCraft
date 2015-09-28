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
package com.chingo247.structureapi;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.settlercraft.core.model.WorldNode;
import com.chingo247.structureapi.event.StructureCreateEvent;
import com.chingo247.structureapi.exception.StructureException;
import com.chingo247.structureapi.model.settler.Settler;
import com.chingo247.structureapi.model.settler.SettlerRepositiory;
import com.chingo247.structureapi.model.owner.OwnerType;
import com.chingo247.structureapi.model.owner.Ownership;
import com.chingo247.structureapi.model.structure.Structure;
import com.chingo247.structureapi.model.structure.StructureNode;
import com.chingo247.structureapi.model.structure.StructureRepository;
import com.chingo247.structureapi.model.world.StructureWorld;
import com.chingo247.structureapi.model.world.StructureWorldRepository;
import com.chingo247.structureapi.plan.DefaultStructurePlan;
import com.chingo247.structureapi.plan.IStructurePlan;
import com.chingo247.structureapi.plan.placement.FilePlacement;
import com.chingo247.structureapi.plan.placement.Placement;
import com.chingo247.structureapi.plan.xml.export.PlacementExporter;
import com.chingo247.structureapi.util.PlacementUtil;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IWorld;
import com.google.common.io.Files;
import com.google.common.util.concurrent.Monitor;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 * Handles the creation of 'plot-like objects' in a world. Example of plots are:
 * - ConstructionZone
 * - Structure
 * - SelectionStructure
 * - StructureLot
 * 
 * The monitor of this class will assure that all checks are applied correctly, even when plots created over multiple threads.
 * This monitor can be retrieved by calling the {@link WorldCreationHandler#getMonitor() } method. 
 * 
 * @since 2.3.0
 *
 * @author Chingo
 */
public class StructureCreator implements IStructureCreator {

    private final ConstructionWorld world;
    private final IStructureAPI structureAPI;
    private final APlatform platform;
    private final StructureWorldRepository structureWorldRepository;
    private final StructureRepository structureRepository;
    private final SettlerRepositiory structureOwnerRepository;
    private final GraphDatabaseService graph;
    private final Monitor monitor;

    /**
     * Constructor
     *
     * @param world The world this StructureManager will 'Manage'
     */
    StructureCreator(ConstructionWorld world) {
        this.graph = SettlerCraft.getInstance().getNeo4j();
        this.structureAPI = (StructureAPI) StructureAPI.getInstance();
        this.world = world;
        this.platform = structureAPI.getPlatform();
        this.structureWorldRepository = new StructureWorldRepository(graph);
        this.structureRepository = new StructureRepository(graph);
        this.structureOwnerRepository = new SettlerRepositiory(graph);
        this.monitor = world.getMonitor();
    }
    
    @Override
    public Structure createStructure(IStructurePlan plan, Vector position, Direction direction, Player owner) throws StructureException {
        return createStructure(null, plan, position, direction, owner);
    }

    @Override
    public Structure createStructure(IStructurePlan plan,  Vector position, Direction direction) throws StructureException {
        return createStructure(null, plan, position, direction, null);
    }

    @Override
    public Structure createStructure(Placement placement, Vector position, Direction direction) throws StructureException {
        return createStructure(null, placement, position, direction, null);
    }

    @Override
    public Structure createStructure(Placement placement, Vector position, Direction direction, Player owner) throws StructureException {
        return createStructure(null, placement, position, direction, owner);
    }

    @Override
    public Structure createSubstructure(Structure parent, IStructurePlan plan, Vector position, Direction direction, Player owner) throws StructureException {
        return createStructure(parent, plan, position, direction, owner);
    }

    @Override
    public Structure createSubstructure(Structure parent, IStructurePlan plan, Vector position, Direction direction) throws StructureException {
        return createStructure(parent, plan, position, direction, null);
    }

    @Override
    public Structure createSubstructure(Structure parent, Placement placement, Vector position, Direction direction, Player owner) throws StructureException {
        return createStructure(parent, placement, position, direction, owner);
    }

    @Override
    public Structure createSubstructure(Structure parent, Placement placement, Vector position, Direction direction) throws StructureException {
        return createStructure(parent, placement, position, direction, null);
    }
    
    private Structure createStructure(Structure parentStructure, Placement placement, Vector position, Direction direction, Player owner) throws StructureException {
        IWorld w = platform.getServer().getWorld(world.getName());
        Structure structure = null;
        Transaction tx = null;
        File structureDirectory = null;
        try {
            monitor.enter();
            tx = graph.beginTx();
            
            StructureWorld worldNode = structureWorldRepository.addOrGet(w.getName(), w.getUUID());
            structure = create(worldNode, parentStructure, placement, placement.getClass().getSimpleName(), position, direction, owner);

            if (structure != null) {
                
                structureDirectory = structure.getDirectory();
                if(structureDirectory.exists()) {
                    structureDirectory.delete();
                }
                structureDirectory.mkdirs();
                
                File structurePlanFile = new File(structureDirectory, "structureplan.xml");
                PlacementExporter exporter = new PlacementExporter();
                exporter.export(placement, structurePlanFile, "structureplan.xml", true);
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
            monitor.leave();
        }
       
        if(structure != null) {
            EventManager.getInstance().getEventBus().post(new StructureCreateEvent(structure));
        }
        return structure;
    }
    
    private Structure createStructure(Structure parentStructure, IStructurePlan structurePlan, Vector position, Direction direction, Player owner) throws StructureException {
        IWorld w = platform.getServer().getWorld(world.getName());
        Structure structure = null;
        Transaction tx = null;
        try {
            tx = graph.beginTx();
            monitor.enter();
            StructureWorld worldNode = structureWorldRepository.addOrGet(w.getName(), w.getUUID());
            structure = create(worldNode, parentStructure, structurePlan.getPlacement(), structurePlan.getName(), position, direction, owner);
             
            File structureDirectory = structure.getDirectory();
            if(structureDirectory.exists()) {
                structureDirectory.delete();
            }
            structureDirectory.mkdirs();
            try {
                moveResources(worldNode, structure, structurePlan);
            } catch (IOException ex) {
                structureDirectory.delete();
                Logger.getLogger(StructureCreator.class.getName()).log(Level.SEVERE, null, ex);
            }
            

       
        
        tx.success();
        } finally {
           if(tx != null) {
               tx.close();
           }
           monitor.leave();
        }
        
        if(structure != null) {
            EventManager.getInstance().getEventBus().post(new StructureCreateEvent(structure));
        }
        
        
        return structure;
    }
    
    private Structure create(StructureWorld structureWorld, Structure parent, Placement placement, String name, Vector position, Direction direction, Player owner) throws StructureException {
        world.checkWorldRestrictions(placement, world.getWorldEditWorld(), position, direction);

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

        // Create the StructureNode - Where it all starts...
        structureNode = structureRepository.addStructure(name, position, structureRegion, direction, 0.0);
        structureWorld.addStructure(structureNode);
        
        if (parentNode != null && structureNode != null) {
            boolean hasWithin = parentNode.hasSubstructuresWithin(structureRegion);
            if (hasWithin) {
                throw new StructureException("Structure overlaps another structure...");
            }
            parentNode.addSubstructure(structureNode);
        }

        // Add owner!
        if (owner != null  && structureNode != null) {
            Settler settler = structureOwnerRepository.findByUUID(owner.getUniqueId());
            if (settler == null) {
                throw new RuntimeException("Settler was null!"); // SHOULD NEVER HAPPEN AS SETTLERS ARE ADDED AT MOMENT OF FIRST LOGIN
            }
            structureNode.getOwnerDomain().updateOwnership(settler, OwnerType.MASTER);
        }

        // Inherit ownership if there is a parent
        if (parentNode != null  && structureNode != null) {
            for (Ownership ownership : parentNode.getOwnerDomain().getOwnerships()) {
                structureNode.getOwnerDomain().updateOwnership(ownership.getOwner(), ownership.getOwnerType());
            }
        }
        
        Structure structure = null;
        if(structureNode != null) {
            structure = new Structure(structureNode);
        }

        return structure;

    }

    private void moveResources(WorldNode worldNode, Structure structure, IStructurePlan plan) throws IOException {
        // Give this structure a directory!
        File structureDir = structure.getDirectory();
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
