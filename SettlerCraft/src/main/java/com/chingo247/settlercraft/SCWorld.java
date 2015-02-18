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
package com.chingo247.settlercraft;

import com.chingo247.settlercraft.entities.StructureEntity;
import com.chingo247.settlercraft.entities.WorldData;
import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.structure.persistence.service.StructureDAO;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.plan.placement.Placement;
import com.chingo247.settlercraft.world.Direction;
import com.chingo247.settlercraft.world.World;
import com.sk89q.worldedit.Vector;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.apache.log4j.Logger;

/**
 *
 * @author Chingo
 */
public class SCWorld implements World {
    
    private final Logger LOG = Logger.getLogger(SCWorld.class);
    private ExecutorService threadService;
    private WorldData worldEntity;
    private SCWorldContext worldContext;
    private final Map<Long,Structure> structures;
    private final StructureDAO structureDAO = new StructureDAO();

    SCWorld(ExecutorService threadService, WorldData worldEntity) {
        this.threadService = threadService;
        this.worldEntity = worldEntity;
        this.structures = new HashMap<>();
    }
    
    Structure getStructure(StructureEntity structureEntity) {
        Structure structure = null;
        synchronized(structures) {
            structure = structures.get(structureEntity.getId());
            if(structure == null) {
                structure = new SCStructure(threadService, structureEntity, this);
                structures.put(structure.getId(), structure);
            }
        }
        return structure;
    }
    
    void load() {
        worldContext = getContext();
        List<StructureEntity> entities = structureDAO.getStructureForWorld(worldEntity.getId());
        for(StructureEntity structureEntity : entities) {
            getStructure(structureEntity);
        }
    }
    
    public SCWorldContext getContext() {
        throw new UnsupportedOperationException("Not supported yet...");
    }

    @Override
    public UUID getUniqueId() {
        return worldEntity.getId();
    }

    @Override
    public String getName() {
        return worldEntity.getName();
    }

    @Override
    public void createStructure(StructurePlan plan, Vector position, Direction direction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createStructure(Placement placement, Vector postion, Direction direction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Structure> getStructures() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Structure getStructure(long id) {
        return structures.get(id);
    }
    
    
    
}
