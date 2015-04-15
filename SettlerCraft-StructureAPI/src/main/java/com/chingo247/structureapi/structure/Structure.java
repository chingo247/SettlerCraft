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

import com.chingo247.structureapi.persistence.repository.IStructure;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.regions.CuboidDimension;
import com.chingo247.structureapi.persistence.repository.StructureNode;
import com.chingo247.structureapi.structure.plan.StructurePlan;
import com.chingo247.structureapi.structure.plan.StructurePlanReader;
import com.chingo247.structureapi.structure.construction.asyncworldedit.AsyncWorldEditUtil;
import com.chingo247.structureapi.structure.plan.placement.PlaceOptions;
import com.chingo247.structureapi.world.Direction;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.util.Date;
import java.util.UUID;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class Structure implements IStructure {
    
    private final GraphDatabaseService graph;
    private final StructureNode structureNode;
    
    Structure(GraphDatabaseService graph, StructureNode structureNode) {
        Preconditions.checkNotNull(graph);
        Preconditions.checkNotNull(structureNode);
        this.graph = graph;
        this.structureNode = structureNode;
    }
    
    @Override
    public Long getId() {
        Long id = null;
        try (Transaction tx = graph.beginTx()) {
            id = structureNode.getId();
            tx.success();
        }
        return id;
    }
    
    @Override
    public String getName() {
        String name = null;
        try (Transaction tx = graph.beginTx()) {
            name = structureNode.getName();
            tx.success();
        }
        return name;
    }
    
    public void setName(String name) {
        try (Transaction tx = graph.beginTx()) {
            structureNode.setName(name);
            tx.success();
        }
    }
    
    @Override
    public Direction getDirection() {
        Direction direction = null;
        try (Transaction tx = graph.beginTx()) {
            direction = structureNode.getDirection();
            tx.success();
        }
        return direction;
    }
    
    @Override
    public String getWorld() {
        String world = null;
        try (Transaction tx = graph.beginTx()) {
            world = structureNode.getWorld().getName();
            tx.success();
        }
        return world;
    }
    
    public Date getCreatedAt() {
        Date createdAt = null;
        try (Transaction tx = graph.beginTx()) {
            createdAt = structureNode.getCreatedAt();
            tx.success();
        }
        return createdAt;
    }
    
    public Date getDeletedAt() {
        Date deletedAt = null;
        try (Transaction tx = graph.beginTx()) {
            deletedAt = structureNode.getDeletedAt();
            tx.success();
        }
        return deletedAt;
    }
    
    public State getState() {
        State state = null;
        try (Transaction tx = graph.beginTx()) {
            state = structureNode.getState();
            tx.success();
        }
        return state;
    }
    
    public void setState(State state) {
        try (Transaction tx = graph.beginTx()) {
            structureNode.setState(state);
            tx.success();
        }
    }
    
    public ConstructionStatus getConstructionStatus() {
        ConstructionStatus status;
        try (Transaction tx = graph.beginTx()) {
            status = structureNode.getConstructionStatus();
            tx.success();
        }
        return status;
    }
    
    public void setConstructionStatus(ConstructionStatus newStatus) {
        try (Transaction tx = graph.beginTx()) {
            structureNode.setConstructionStatus(newStatus);
            tx.success();
        }
    }
    
    public CuboidDimension getDimension() {
        CuboidDimension cuboidDimension;
        try (Transaction tx = graph.beginTx()) {
            cuboidDimension = structureNode.getDimension();
            tx.success();
        }
        return cuboidDimension;
    }
    
    public void build(Player player, PlaceOptions options, boolean force) {
        StructureAPI.getInstance().build(this, player.getUniqueId(), getSession(player.getUniqueId()), options, force);
    }

    public void build(EditSession session, PlaceOptions options, boolean force) {
        StructureAPI.getInstance().build(this, PlayerEntry.CONSOLE.getUUID(), session, options, force);
    }

    public void demolish(Player player, PlaceOptions options, boolean force) {
        StructureAPI.getInstance().demolish(this, player.getUniqueId(), getSession(player.getUniqueId()), options, force);
    }

    public void demolish(EditSession session, PlaceOptions options, boolean force) {
        StructureAPI.getInstance().demolish(this, PlayerEntry.CONSOLE.getUUID(), session, options, force);
    }

    public void stop() {
        StructureAPI.getInstance().stop(this);
    }
    
    public StructurePlan getPlan() {
        StructureAPI structureAPI = StructureAPI.getInstance();
        File planFile = new File(structureAPI.getStructuresDirectory(getWorld()), String.valueOf(getId()));
        StructurePlanReader reader = new StructurePlanReader();
        StructurePlan plan = reader.readFile(planFile);
        return plan;
    }
    
    AsyncEditSession getSession(UUID playerId) {
        Player ply = SettlerCraft.getInstance().getPlayer(playerId);
        World w = SettlerCraft.getInstance().getWorld(getWorld());
        AsyncEditSession editSession;
        if (ply == null) {
            editSession = (AsyncEditSession) AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(w, -1);
        } else {
            editSession = (AsyncEditSession) AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(w, -1, ply);
        }
        return editSession;
    }
    
    
}
