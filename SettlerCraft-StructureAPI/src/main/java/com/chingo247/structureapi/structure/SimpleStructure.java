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
package com.chingo247.structureapi.structure;

import com.chingo247.proxyplatform.core.IWorld;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.regions.CuboidDimension;
import com.chingo247.structureapi.structure.construction.asyncworldedit.AsyncWorldEditUtil;
import com.chingo247.structureapi.structure.construction.options.Options;
import com.chingo247.structureapi.plan.StructurePlan;
import com.chingo247.structureapi.plan.StructurePlanReader;
import com.chingo247.structureapi.util.WorldEditUtil;
import com.chingo247.structureapi.world.Direction;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.util.Objects;
import java.util.UUID;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class SimpleStructure implements Structure {

    private Long id;
    private String name;
    private IWorld world;
    private Direction direction;
    private CuboidDimension dimension;
    private State state;
    private ConstructionStatus constructionStatus;

    SimpleStructure(Long id, String name, IWorld world, Direction direction, CuboidDimension dimension) {
        this.id = id;
        this.name = name;
        this.world = world;
        this.direction = direction;
        this.dimension = dimension;
        this.state = State.INITIALIZING;
        this.constructionStatus = ConstructionStatus.WAITING;
    }
    
    

    SimpleStructure(String name, IWorld world, Direction direction, CuboidDimension dimension) {
        this(null, name, world, direction, dimension);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public World getWorld() {
        return WorldEditUtil.getWorld(world.getName());
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public CuboidDimension getDimension() {
        return dimension;
    }

    @Override
    public void build(Player player, Options options, boolean force) {
        ConstructionManager.getInstance().build(this, player.getUniqueId(), getSession(player.getUniqueId()), options, force);
    }

    @Override
    public void build(EditSession session, Options options, boolean force) {
        ConstructionManager.getInstance().build(this, PlayerEntry.CONSOLE.getUUID(), session, options, force);
    }

    @Override
    public void demolish(Player player, Options options, boolean force) {
        ConstructionManager.getInstance().demolish(this, player.getUniqueId(), getSession(player.getUniqueId()), options, force);
    }

    @Override
    public void demolish(EditSession session, Options options, boolean force) {
        ConstructionManager.getInstance().demolish(this, PlayerEntry.CONSOLE.getUUID(), session, options, force);
    }

    @Override
    public void stop(boolean force) {
        ConstructionManager.getInstance().stop(this, force);
    }

    @Override
    public StructurePlan getPlan() {
        StructureAPI structureAPI = StructureAPI.getInstance();
        File planFile = new File(structureAPI.getStructuresDirectory(world.getName()), String.valueOf(id));
        StructurePlanReader reader = new StructurePlanReader();
        StructurePlan plan = reader.readFile(planFile);
        return plan;
    }

    @Override
    public void save() {
        StructureRepository repository = new StructureRepository();
        Structure structure = repository.save(this);
        if(this.getId() == null) {
            this.id = structure.getId();
        }
    }

    @Override
    public ConstructionStatus getConstructionStatus() {
        return constructionStatus;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SimpleStructure other = (SimpleStructure) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    AsyncEditSession getSession(UUID playerId) {
        Player ply = SettlerCraft.getInstance().getPlayer(playerId);
        World w = SettlerCraft.getInstance().getWorld(world.getUUID());
        
        AsyncEditSession editSession;
        if (ply == null) {
            editSession = (AsyncEditSession) AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(w, -1);
        } else {
            editSession = (AsyncEditSession) AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(w, -1, ply);
        }
        return editSession;
    }

}
