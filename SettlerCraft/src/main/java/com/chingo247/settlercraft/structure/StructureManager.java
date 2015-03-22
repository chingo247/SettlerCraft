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
package com.chingo247.settlercraft.structure;

import com.chingo247.settlercraft.world.WorldConfig;
import com.chingo247.settlercraft.model.world.Direction;
import com.chingo247.settlercraft.model.persistence.entities.structure.StructureEntity;
import com.chingo247.settlercraft.model.persistence.entities.structure.StructureState;
import com.chingo247.settlercraft.model.persistence.dao.StructureDAO;
import com.chingo247.settlercraft.event.EventManager;
import com.chingo247.settlercraft.model.persistence.entities.world.CuboidDimension;
import com.chingo247.settlercraft.structure.event.StructureCreateEvent;
import com.chingo247.settlercraft.structure.placement.Placement;
import com.chingo247.settlercraft.structure.placement.SchematicPlacement;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.plan.SubStructuredPlan;
import com.chingo247.settlercraft.util.PlacementUtil;
import com.google.common.io.Files;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.util.com.google.common.collect.Maps;

/**
 * Manages all the structures of a certain world
 *
 * @author Chingo
 */
public class StructureManager {

    private static final String STRUCTURE_PLAN_FILE = "structureplan.xml";
    private static final StructureDAO STRUCTURE_DAO = new StructureDAO();
    private final UUID worldUUID;
    private final com.sk89q.worldedit.world.World world;
    private final ExecutorService service;
    private final Map<Long, Structure> structures;
    private final StructureAPI structureAPI;

    private WorldConfig config;

    protected StructureManager(StructureAPI structureAPI, com.sk89q.worldedit.world.World world, UUID worldUUID, ExecutorService service) {
        this.worldUUID = worldUUID;
        this.world = world;
        this.service = service;
        this.structures = Maps.newHashMap();
        this.structureAPI = structureAPI;
    }

    protected final ExecutorService getService() {
        return service;
    }

    protected final World getWorld() {
        return world;
    }

    protected Structure getStructure(StructureEntity entity) {
        synchronized (structures) {
            return structures.get(entity.getId());
        }
    }

    public final Structure getStructure(long id) {
        StructureEntity entity = STRUCTURE_DAO.find(id);
        if (entity == null) {
            return null;
        }
        return getStructure(entity);
    }

    public WorldConfig getStructureConfiguration() {
        return config;
    }

    private void putStructure(Structure structure) {
        synchronized (structures) {
            structures.put(structure.getId(), structure);
        }
    }

    public Structure createStructure(StructurePlan plan, Vector position, Direction direction) {
        Structure structure = createUninitizalizedStructure(config, plan, world, direction, position);
        if (structure != null) {
            structure.setState(StructureState.CREATED);

            EventManager.getInstance().getEventBus().post(new StructureCreateEvent(structure));
        }
        return structure;
    }

    public Structure createStructure(Placement placement, Vector position, Direction direction) {
        Structure structure = createUninitizalizedStructure(config, placement, world, direction, position);
        if (structure != null) {
            structure.setState(StructureState.CREATED);
            EventManager.getInstance().getEventBus().post(new StructureCreateEvent(structure));
            putStructure(structure);
        }

        return structure;
    }

    public Structure createUninitizalizedStructure(WorldConfig worldConfig, StructurePlan plan, com.sk89q.worldedit.world.World world, Direction direction, Vector position) {
        System.out.println("Position is " + position);
        if (plan instanceof SubStructuredPlan) {
            System.out.println("Structure is SubstructurePlan!");
            return null;
        } else {
            System.out.println("Structure is StructurePlan!");
            Placement placement = plan.getPlacement();

            Vector min = position;
            Vector max = PlacementUtil.getPoint2Right(min, direction, placement.getMaxPosition());

            System.out.println("Min: " + min);
            System.out.println("Max: " + max);
            System.out.println("Direction: " + direction);

            StructureEntity entity = new StructureEntity(world.getName(), worldUUID, new CuboidDimension(min, max), direction);
            entity = STRUCTURE_DAO.save(entity); // store and get the ID
            Structure structure = new Structure(structureAPI, this, entity, service);
            File structureDir = new File(getStructuresDirectory(), String.valueOf(entity.getId()));
            
            // Overwrite old one if exists...
            
            if(structureDir.exists()) {
                structureDir.delete();
            }
            structureDir.mkdirs();
            
            File planFile = plan.getFile();
            

            try {
                if (placement instanceof SchematicPlacement) {
                    SchematicPlacement schematicPlacement = (SchematicPlacement) placement;
                    File schematicFile = schematicPlacement.getSchematic().getFile();
                    Files.copy(schematicFile, new File(structureDir, schematicFile.getName()));
                }
                Files.copy(planFile, new File(structureDir, STRUCTURE_PLAN_FILE));
                
            } catch (IOException ex) {
                structureDir.delete();
                Logger.getLogger(StructureManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            } 

            return structure;
        }
    }
    
    

    protected final File getStructuresDirectory() {
        File f = new File(structureAPI.getWorkingDirectory(), "worlds//" + world.getName() + "//structures");
        f.mkdirs(); // creates if not exists..
        return f;
    }

    public Structure createUninitizalizedStructure(WorldConfig worldConfig, Placement placement, com.sk89q.worldedit.world.World world, Direction direction, Vector position) {

        //putStructure(null);
        throw new UnsupportedOperationException();
    }

    public void load() {
        synchronized (structures) {
            if (structures.isEmpty()) {
                List<StructureEntity> structureEntities = STRUCTURE_DAO.getStructureForWorld(worldUUID);
                for (StructureEntity entity : structureEntities) {
                    Structure structure = new Structure(structureAPI, this, entity, service);
                    structures.put(structure.getId(), structure);
                }
            }
        }
//        File worldFile = new File(structureAPI.getWorkingDirectory(), "worlds//" + world.getName());
//        config = worldFile.exists() ? WorldConfig.load(worldFile) : WorldConfig.createDefault(worldFile);
    }

}
