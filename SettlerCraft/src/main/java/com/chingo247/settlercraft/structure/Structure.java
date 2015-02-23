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

import com.chingo247.settlercraft.persistence.entities.structure.StructureEntity;
import com.chingo247.settlercraft.structure.construction.options.Options;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.persistence.entities.structure.StructurePlayerEntity;
import com.chingo247.settlercraft.persistence.entities.structure.StructureState;
import com.chingo247.settlercraft.persistence.entities.world.CuboidDimension;
import com.chingo247.settlercraft.world.Direction;
import com.chingo247.settlercraft.world.World;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.entity.Player;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

/**
 *
 * @author Chingo
 */
public abstract class Structure {
    
    private StructureEntity structureEntity;
    
    protected Structure(StructureEntity entity) {
        this.structureEntity = entity;
    }
    
    public Long getId() {
        return structureEntity.getId();
    }
    
    public Direction getDirection() {
        return structureEntity.getDirection();
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
    
    public CuboidDimension getCuboidDimension() {
        return structureEntity.getDimension();
    }

    public void setState(StructureState state) {
        structureEntity.setState(state);
    }
    
    public StructureState getState() {
        return structureEntity.getState();
    }
    
    public abstract World getWorld();
    public abstract Structure getParent();
    public abstract List<Structure> getSubStructures();
    public abstract StructurePlan getStructurePlan();
    public abstract List<StructurePlayerEntity> getOwners();
    
    public abstract boolean isOwner(UUID player);
    public boolean isOwner(Player player) {
        return isOwner(player.getUniqueId());
    }
    
    public void build(Player player, Options options, boolean force) {
        build(player.getUniqueId(), options, force);
    }
    
    public abstract void build(UUID uuid, Options options, boolean force);
    
    public abstract void demolish(EditSession session, Options options, boolean force);
    public void demolish(EditSession session, boolean force) {
        demolish(session, Options.defaultOptions(), force);
    }
    
    public abstract void stop(boolean force);

    protected abstract void _load(ForkJoinPool pool);
    
    protected void checkWorldSession(EditSession session) {
        if(!session.getWorld().getName().equals(getWorld().getName())) {
            throw new IllegalArgumentException("EditSession's world doesn't equal the world of the structure...");
        }
    }
    
}
