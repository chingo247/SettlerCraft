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

import com.chingo247.structureapi.structure.exception.StructureException;
import com.chingo247.structureapi.structure.construction.asyncworldedit.AsyncPlacement;
import com.chingo247.structureapi.structure.construction.asyncworldedit.AsyncWorldEditUtil;
import com.chingo247.structureapi.structure.construction.options.Options;
import com.chingo247.settlercraft.model.service.StructurePlayerDAO;
import com.chingo247.structureapi.structure.plan.StructurePlan;
import com.chingo247.structureapi.structure.plan.processing.StructurePlanReader;
import com.chingo247.settlercraft.model.entities.structure.StructureEntity;
import com.chingo247.settlercraft.model.entities.structure.StructurePlayerEntity;
import com.chingo247.settlercraft.model.entities.structure.StructureState;
import com.chingo247.settlercraft.model.entities.world.CuboidDimension;
import com.chingo247.settlercraft.common.util.FireNextQueue;
import com.chingo247.settlercraft.common.util.WorldEditUtil;
import com.chingo247.settlercraft.common.world.Direction;
import com.sk89q.worldedit.EditSession;
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
public class SettlerCraftStructure {

    private final FireNextQueue tasks;
    private final StructureEntity structureEntity;
    private final StructurePlayerDAO structurePlayerDao;
    private final SettlerCraftWorld world;
    private StructurePlan plan;
    protected final SettlerCraft settlerCraft;

    protected SettlerCraftStructure(SettlerCraft settlerCraft, ExecutorService service, StructureEntity entity, SettlerCraftWorld world, StructurePlan plan) {
        this.tasks = new FireNextQueue(service);
        this.structurePlayerDao = new StructurePlayerDAO();
        this.world = world;
        this.structureEntity = entity;
        this.plan = plan;
        this.settlerCraft = settlerCraft;
    }

    public Long getId() {
        return structureEntity.getId();
    }
    
    public String getName() {
        return structureEntity.getName();
    }
    
    public void setName(String name) {
        structureEntity.setName(name);
    }
    public double getValue() {
        return structureEntity.getValue();
    }
    public void setValue(double value) {
        structureEntity.setValue(value);
    }
    
    public SettlerCraftWorld getWorld() {
        return settlerCraft.getWorld(structureEntity.getWorld());
    }
    
    public CuboidDimension getCuboidDimension() {
        return structureEntity.getDimension();
    }

    public void setState(StructureState state) {
        structureEntity.setState(state);
    }
    
    public StructureState getState() {
        return structureEntity.getState();
    }
    
    public Direction getDirection() {
        return structureEntity.getDirection();
    }
    
    protected StructureEntity getEntity() {
        return structureEntity;
    }

    public SettlerCraftStructure getParent() {
        return world.getStructure(structureEntity.getParent());
    }

    public List<SettlerCraftStructure> getSubStructures() {
        return world._getSubstructures(getId());
    }

    public StructurePlan getStructurePlan() {
        return plan;
    }

    public List<StructurePlayerEntity> getOwners() {
        return structurePlayerDao.getOwnersForStructure(getId());
    }

    public boolean isOwner(UUID player) {
        return structurePlayerDao.isOwner(player, getId());
    }

    public void build(UUID player, Options options, boolean force) throws StructureException {
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
//        placement.rotate(getDirection());
        placement.place(editSession, structureEntity.getDimension().getMinPosition(), options);
    }

    public void demolish(EditSession session, Options options, boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void stop(boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    void _load() {
        plan = getStructurePlan();
    }

    protected void _load(ForkJoinPool pool) {
        String worldName = structureEntity.getWorld();
        String worldUuid = structureEntity.getWorldUUID().toString();
        String path = worldName + " - " + worldUuid + "//" + structureEntity.getId();
        File file = new File(settlerCraft.getStructureDirectory(), path + "//StructurePlan.xml");
        StructurePlanReader reader = new StructurePlanReader();
        plan = reader.readFile(file, pool);
    }

}
