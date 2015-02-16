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

import com.chingo247.settlercraft.commons.util.WorldEditUtil;
import com.chingo247.settlercraft.structure.construction.options.Options;
import com.chingo247.settlercraft.structure.persistence.entities.StructureEntity;
import com.chingo247.settlercraft.structure.persistence.entities.StructurePlayerMemberEntity;
import com.chingo247.settlercraft.structure.persistence.entities.StructurePlayerOwnerEntity;
import com.chingo247.settlercraft.structure.persistence.service.StructureDAO;
import com.chingo247.settlercraft.structure.persistence.service.StructureMemberDAO;
import com.chingo247.settlercraft.structure.persistence.service.StructureOwnerDAO;
import com.chingo247.settlercraft.structure.plan.processing.StructurePlanComplex;
import com.chingo247.settlercraft.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structure.regions.CuboidDimension;
import com.chingo247.settlercraft.structure.util.FireNextQueue;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author Chingo
 */
public class StructureComplex extends Structure {
    
    private FireNextQueue tasks;
    private long id;
    private StructureEntity entity;
    private StructureDAO sdao;
    private StructureMemberDAO memberDAO;
    private StructureOwnerDAO ownerDAO;
    private StructurePlanComplex plan;
    
    private StructureAPI api;
    private boolean isDirty;
    
    StructureComplex(ExecutorService service, StructureEntity entity, StructureAPI api) {
        this.isDirty = false;
        this.tasks = new FireNextQueue(service);
        this.memberDAO = new StructureMemberDAO();
        this.ownerDAO = new StructureOwnerDAO();
        this.api = api;
    }

    public StructureEntity getEntity() {
        return entity;
    }
    
    public void setEntity(StructureEntity entity) {
        this.entity = entity;
    }
    
    private boolean isNew(StructureEntity entity) {
        return entity.getId() == null;
    }
    
    private void setDirty(boolean dirty) {
        this.isDirty = dirty;
    }

    public boolean isDirty() {
        return isDirty;
    }
    
    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getName() {
        return entity.getName();
    }

    @Override
    public void setName(String name) {
        entity.setName(name);
        setDirty(true);
    }

    @Override
    public double getValue() {
        return entity.getValue();
    }

    @Override
    public void setValue(double value) {
        entity.setValue(value);
        setDirty(true);
    }

    @Override
    public World getWorld() {
        return WorldEditUtil.getWorld(entity.getWorld());
    }

    @Override
    public CuboidRegion getCuboidRegion() {
        CuboidDimension dim = entity.getDimension();
        return new CuboidRegion(getWorld(), dim.getMinPosition(), dim.getMaxPosition());
    }

    @Override
    public Structure getParent() {
        return api.getStructure(entity.getParent());
    }

    @Override
    public List<Structure> getSubStructures() {
        
        return api.getSubstructures(id);
    }

    @Override
    public Placement getPlacement() {
        return getStructurePlan().getPlacement();
    }

    @Override
    public StructurePlanComplex getStructurePlan() {
        //TODO Null check and load!
        
        return plan;
    }

    @Override
    public List<StructurePlayerMemberEntity> getMembers() {
        return memberDAO.getOwnersForStructure(id);
    }

    @Override
    public List<StructurePlayerOwnerEntity> getOwners() {
        return ownerDAO.getOwnersForStructure(id);
    }

    @Override
    public boolean isOwner(UUID player) {
        return ownerDAO.isOwner(player, id);
    }

    @Override
    public boolean isMember(UUID player) {
        return memberDAO.isMember(player, id);
    }

    @Override
    public void build(EditSession session, Options options, boolean force) {
        getPlacement().place(session, getCuboidRegion().getMinimumPoint(), options);
    }

    @Override
    public void demolish(EditSession session, Options options, boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void stop(boolean force) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
}
