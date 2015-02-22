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

import com.chingo247.settlercraft.construction.options.Options;
import com.chingo247.settlercraft.persistence.entities.structure.StructureEntity;
import com.chingo247.settlercraft.persistence.entities.structure.StructurePlayerEntity;
import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.persistence.service.StructurePlayerDAO;
import com.chingo247.settlercraft.plan.StructurePlan;
import com.chingo247.settlercraft.plan.processing.StructurePlanReader;
import com.chingo247.settlercraft.regions.CuboidDimension;
import com.chingo247.settlercraft.util.FireNextQueue;
import com.chingo247.settlercraft.world.World;
import com.sk89q.worldedit.EditSession;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

/**
 *
 * @author Chingo
 */
public class SCStructure extends Structure {

    private final FireNextQueue TASKS;
    private final StructureEntity STRUCTURE_ENTITY;
    private final StructurePlayerDAO STRUCTURE_PLAYER_DAO;
    private final SCWorld WORLD;
    private final SCGlobalContext CONTEXT;
    private StructurePlan PLAN;

    protected SCStructure(ExecutorService service, StructureEntity entity, SCWorld world) {
        this.TASKS = new FireNextQueue(service);
        this.STRUCTURE_PLAYER_DAO = new StructurePlayerDAO();
        this.WORLD = world;
        this.STRUCTURE_ENTITY = entity;
        this.CONTEXT = SCGlobalContext.getContext();
    }

    public StructureEntity getEntity() {
        return STRUCTURE_ENTITY;
    }

    @Override
    public Long getId() {
        return STRUCTURE_ENTITY.getId();
    }

    @Override
    public String getName() {
        return STRUCTURE_ENTITY.getName();
    }

    @Override
    public void setName(String name) {
        STRUCTURE_ENTITY.setName(name);
    }

    @Override
    public double getValue() {
        return STRUCTURE_ENTITY.getValue();
    }

    @Override
    public void setValue(double value) {
        STRUCTURE_ENTITY.setValue(value);
    }

    @Override
    public World getWorld() {
        return WORLD;
    }

    @Override
    public CuboidDimension getCuboidDimension() {
        return STRUCTURE_ENTITY.getDimension();
    }

    @Override
    public Structure getParent() {
        return WORLD.getStructure(STRUCTURE_ENTITY.getParent());
    }

    @Override
    public List<Structure> getSubStructures() {
        return WORLD._getSubstructures(getId());
    }

    @Override
    public StructurePlan getStructurePlan() {
        return PLAN;
    }

    @Override
    public List<StructurePlayerEntity> getOwners() {
        return STRUCTURE_PLAYER_DAO.getOwnersForStructure(getId());
    }

    @Override
    public boolean isOwner(UUID player) {
        return STRUCTURE_PLAYER_DAO.isOwner(player, getId());
    }

    @Override
    public void build(EditSession session, Options options, boolean force) {
    }

    @Override
    public void demolish(EditSession session, Options options, boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stop(boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void _load() {
        PLAN = getStructurePlan();
    }

    @Override
    protected void _load(ForkJoinPool pool) {
        String world = STRUCTURE_ENTITY.getWorld();
        String uuid = STRUCTURE_ENTITY.getWorldUUID().toString();
        String path = world + " - " + uuid + "//" + STRUCTURE_ENTITY.getId();
        File file = new File(CONTEXT.getStructureDirectory(), path + "//StructurePlan.xml");
        StructurePlanReader reader = new StructurePlanReader();
        PLAN = reader.readFile(file, pool);
    }

}
