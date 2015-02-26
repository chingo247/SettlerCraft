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
import com.chingo247.settlercraft.structure.construction.asyncworldedit.AsyncPlacement;
import com.chingo247.settlercraft.structure.construction.asyncworldedit.AsyncWorldEditUtil;
import com.chingo247.settlercraft.structure.construction.options.Options;
import com.chingo247.settlercraft.util.WorldEditUtil;
import com.chingo247.settlercraft.world.World;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.entity.Player;
import java.util.List;
import java.util.UUID;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public abstract class Structure {
    
    protected final StructureEntity structureEntity;
    
    protected Structure(StructureEntity structureEntity) {
        this.structureEntity = structureEntity;
    }
    
    protected StructureEntity getStructureEntity() {
        return structureEntity;
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
    
    public Double getValue() {
        return structureEntity.getValue();
    }
    
    public void setValue(double value) {
        structureEntity.setValue(value);
    }
    
    public void demolish(UUID anUUID, Options options, boolean force) {
        demolish(getSession(anUUID), options, force);
    }
    
    public void demolish(Player player, Options options, boolean force) {
        demolish(getSession(player.getUniqueId()), options, force);
    }
    
    public void demolish(AsyncEditSession editSession, Options options, boolean force) {
       //TODO: implement demolish structure
       //TODO: Check if there are substructures...
    }
    
    public void build(UUID anUUID, Options options, boolean force) {
        build(getSession(anUUID), options, force);
    }
    
    public void build(Player player, Options options, boolean force) {
        build(getSession(player.getUniqueId()), options, force);
    }
    
    public void build(AsyncEditSession editSession, Options options, boolean force) {
        //TODO: implement build structure
        //TODO: ignore substructure areas using masks (Ignore Mask)
    }
    
    private AsyncEditSession getSession(UUID anUUID) {
        com.sk89q.worldedit.world.World w = WorldEditUtil.getWorld(getWorld().getName());
        Player ply = getWorldEditPlayer(anUUID);
        AsyncEditSession editSession;
        if (ply == null) {
            editSession = (AsyncEditSession)AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(w, -1);
        } else {
            editSession = (AsyncEditSession)AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(w, -1, ply);
        }
        return editSession;
    }
    
    public abstract Structure getParentStructure();
    
    public abstract List<Structure> getSubstructures();
    
    public abstract World getWorld();
    
    public abstract List<Player> getOwners();
    
    protected abstract Player getWorldEditPlayer(UUID player);
    
}
