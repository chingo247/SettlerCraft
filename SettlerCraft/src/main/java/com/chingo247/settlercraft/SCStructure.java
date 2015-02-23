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

import com.chingo247.settlercraft.construction.asyncworldedit.AsyncPlacement;
import com.chingo247.settlercraft.construction.asyncworldedit.AsyncWorldEditUtil;
import com.chingo247.settlercraft.construction.options.Options;
import com.chingo247.settlercraft.persistence.entities.structure.StructureEntity;
import com.chingo247.settlercraft.persistence.entities.structure.StructurePlayerEntity;
import com.chingo247.settlercraft.persistence.entities.structure.StructureState;
import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.persistence.service.StructurePlayerDAO;
import com.chingo247.settlercraft.plan.StructurePlan;
import com.chingo247.settlercraft.plan.processing.StructurePlanReader;
import com.chingo247.settlercraft.regions.CuboidDimension;
import com.chingo247.settlercraft.util.FireNextQueue;
import com.chingo247.settlercraft.util.WorldEditUtil;
import com.chingo247.settlercraft.world.World;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class SCStructure extends Structure {

    private final FireNextQueue tasks;
    private final StructureEntity structureEntity;
    private final StructurePlayerDAO structurePlayerDao;
    private final SCWorld world;
    private StructurePlan plan;
    protected final SettlerCraft settlerCraft;

    protected SCStructure(SettlerCraft settlerCraft, ExecutorService service, StructureEntity entity, SCWorld world, StructurePlan plan) {
        this.tasks = new FireNextQueue(service);
        this.structurePlayerDao = new StructurePlayerDAO();
        this.world = world;
        this.structureEntity = entity;
        this.plan = plan;
        this.settlerCraft = settlerCraft;
    }

    public StructureEntity getEntity() {
        return structureEntity;
    }

    @Override
    public Long getId() {
        return structureEntity.getId();
    }

    @Override
    public StructureState getState() {
        return structureEntity.getState();
    }
    
    

    @Override
    public String getName() {
        return structureEntity.getName();
    }

    @Override
    public void setName(String name) {
        structureEntity.setName(name);
    }

    @Override
    public double getValue() {
        return structureEntity.getValue();
    }

    @Override
    public void setValue(double value) {
        structureEntity.setValue(value);
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public CuboidDimension getCuboidDimension() {
        return structureEntity.getDimension();
    }

    @Override
    public Structure getParent() {
        return world.getStructure(structureEntity.getParent());
    }

    @Override
    public List<Structure> getSubStructures() {
        return world._getSubstructures(getId());
    }

    @Override
    public StructurePlan getStructurePlan() {
        return plan;
    }

    @Override
    public List<StructurePlayerEntity> getOwners() {
        return structurePlayerDao.getOwnersForStructure(getId());
    }

    @Override
    public boolean isOwner(UUID player) {
        return structurePlayerDao.isOwner(player, getId());
    }

    @Override
    public void build(UUID player, Options options, boolean force) {
        PlayerEntry entry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(player);
        AsyncPlacement placement = new AsyncPlacement(entry, plan.getPlacement());
        
        com.sk89q.worldedit.world.World w = WorldEditUtil.getWorld(world.getName());
        
        Player ply = settlerCraft.getPlayer(player);
        
        AsyncEditSession editSession;
        if (player == null) {
            editSession = (AsyncEditSession)AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(w, -1);
        } else {
            editSession = (AsyncEditSession)AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(w, -1, ply);
        }
        
        System.out.println("Build structure: " + getId());
        
        placement.place(editSession, structureEntity.getDimension().getMinPosition(), options);
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
        plan = getStructurePlan();
    }

    @Override
    protected void _load(ForkJoinPool pool) {
        String world = structureEntity.getWorld();
        String uuid = structureEntity.getWorldUUID().toString();
        String path = world + " - " + uuid + "//" + structureEntity.getId();
        File file = new File(settlerCraft.getStructureDirectory(), path + "//StructurePlan.xml");
        StructurePlanReader reader = new StructurePlanReader();
        plan = reader.readFile(file, pool);
    }

}
