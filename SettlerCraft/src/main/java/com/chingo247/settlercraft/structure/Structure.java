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
import com.chingo247.settlercraft.construction.asyncworldedit.AsyncWorldEditUtil;
import com.chingo247.settlercraft.construction.options.Options;
import com.chingo247.settlercraft.persistence.entities.structure.StructurePlayerEntity;
import com.chingo247.settlercraft.plan.StructurePlan;
import com.chingo247.settlercraft.regions.CuboidDimension;
import com.chingo247.settlercraft.world.World;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

/**
 *
 * @author Chingo
 */
public abstract class Structure {
    
    public abstract Long getId();
    public abstract String getName();
    public abstract void setName(String name);
    public abstract double getValue();
    public abstract void setValue(double value);
    public abstract World getWorld();
    public abstract CuboidDimension getCuboidDimension();
    public abstract Structure getParent();
    public abstract List<Structure> getSubStructures();
    public abstract StructurePlan getStructurePlan();
    public abstract List<StructurePlayerEntity> getOwners();
    
    public abstract boolean isOwner(UUID player);
    public boolean isOwner(Player player) {
        return isOwner(player.getUniqueId());
    }
    
    public void build(Player player, Options options, boolean force) {
        com.sk89q.worldedit.world.World world = WorldEditUtil.getWorld(getWorld().getName());
        EditSession session = AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(world, -1, player);
        build(session, options, force);
    }
    
    /**
     * Builds the structure
     * @param session The editSession to use
     * @param force Whether to skip checks to the structure's state
     * @throws IllegalArgumentException if editsession's world  isn't equal to that of the structure
     */
    public void build(EditSession session, boolean force) {
        checkWorldSession(session);
        build(session, Options.defaultOptions(), force);
    }
    public abstract void build(EditSession session, Options options, boolean force);
    
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
