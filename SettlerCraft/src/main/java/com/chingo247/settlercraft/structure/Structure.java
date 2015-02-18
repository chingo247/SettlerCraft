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

import com.chingo247.settlercraft.structure.construction.options.Options;
import com.chingo247.settlercraft.entities.StructurePlayerMemberEntity;
import com.chingo247.settlercraft.entities.StructurePlayerOwnerEntity;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structure.plan.processing.StructurePlanComplex;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public abstract class Structure {
    
    public abstract long getId();
    public abstract String getName();
    public abstract void setName(String name);
    public abstract double getValue();
    public abstract void setValue(double value);
    public abstract World getWorld();
    public abstract CuboidRegion getCuboidRegion();
    public abstract Structure getParent();
    public abstract List<Structure> getSubStructures();
    public abstract StructurePlan getStructurePlan();
    public abstract List<StructurePlayerMemberEntity> getMembers();
    public abstract List<StructurePlayerOwnerEntity> getOwners();
    
    public abstract boolean isOwner(UUID player);
    public boolean isOwner(Player player) {
        return isOwner(player.getUniqueId());
    }
    
    public abstract boolean isMember(UUID player);
    public boolean isMember(Player player) {
        return isMember(player.getUniqueId());
    }
    
    public void build(EditSession session, boolean force) {
        build(session, Options.defaultOptions(), force);
    }
    public abstract void build(EditSession session, Options options, boolean force);
    
    public abstract void demolish(EditSession session, Options options, boolean force);
    public void demolish(EditSession session, boolean force) {
        demolish(session, Options.defaultOptions(), force);
    }
    
    public abstract void stop(boolean force);
    
}
