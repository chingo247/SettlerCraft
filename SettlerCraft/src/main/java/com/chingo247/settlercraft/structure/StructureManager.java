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

import com.chingo247.settlercraft.model.world.Direction;
import com.chingo247.settlercraft.model.persistence.entities.structure.StructureEntity;
import com.chingo247.settlercraft.model.persistence.entities.structure.StructureState;
import com.chingo247.settlercraft.model.persistence.dao.StructureDAO;
import com.chingo247.settlercraft.event.EventManager;
import com.chingo247.settlercraft.structure.event.StructureCreateEvent;
import com.chingo247.settlercraft.structure.placement.Placement;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import net.minecraft.util.com.google.common.collect.Maps;

/**
 * Manages all the structures of a certain world
 *
 * @author Chingo
 */
public class StructureManager {

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

   

    public WorldConfig getConfig() {
        return config;
    }

    protected final ExecutorService getService() {
        return service;
    }

    public final UUID getWorldUUID() {
        return worldUUID;
    }

    public final World getWorld() {
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

        //putStructure(null);
        throw new UnsupportedOperationException();
    }

    public Structure createUninitizalizedStructure(WorldConfig worldConfig, Placement plan, com.sk89q.worldedit.world.World world, Direction direction, Vector position) {

        //putStructure(null);
        throw new UnsupportedOperationException();
    }

    public void load() {
        synchronized (structures) {
            if (structures.isEmpty()) {
                List<StructureEntity> structureEntities = STRUCTURE_DAO.getStructureForWorld(worldUUID);
                for (StructureEntity entity : structureEntities) {
                    Structure structure = new Structure(structureAPI, this, entity);
                    structures.put(structure.getId(), structure);
                }
            }
        }
        File worldFile = new File(structureAPI.getWorkingDirectory(), "worlds//" + world.getName());
        config = worldFile.exists() ? WorldConfig.load(worldFile) : WorldConfig.createDefault(worldFile);
    }

}
