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
package com.chingo247.settlercraft.structureapi.structure;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.structureapi.structure.construction.asyncworldedit.AsyncWorldEditUtil;
import com.chingo247.settlercraft.structureapi.structure.options.PlaceOptions;
import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureRelTypes;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlanReader;
import com.chingo247.settlercraft.structureapi.structure.options.DemolishingOptions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.util.Date;
import java.util.UUID;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 * The default Structure, containing all default properties and methods
 * @author Chingo
 */
public class DefaultStructure implements Structure {
    
    private final Long id;
    private final String name;
    private final Direction direction;
    private final CuboidRegion region;
    private final String world;
    private final UUID worldUUID;
    private final ConstructionStatus status;
    private final Date createdAt;
    private final Date removedAt;
    private final Date completedAt;
    private final double price;
    private final boolean isRoot;
    
    DefaultStructure(StructureNode structureNode) {
        this.id = structureNode.getId();
        this.name = structureNode.getName();
        this.region = structureNode.getCuboidRegion();
        WorldNode worldNode = structureNode.getWorld();
        this.world = worldNode.getName();
        this.worldUUID = worldNode.getUUID();
        this.status = structureNode.getConstructionStatus();
        this.createdAt = structureNode.getCreatedAt();
        this.removedAt = structureNode.getDeletedAt();
        this.direction = structureNode.getDirection();
        this.price = structureNode.getPrice();
        this.completedAt = structureNode.getCompletedAt();
        this.isRoot = structureNode.getRawNode().hasRelationship(org.neo4j.graphdb.Direction.INCOMING, DynamicRelationshipType.withName(StructureRelTypes.RELATION_SUBSTRUCTURE)) == false;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public double getPrice() {
        return price;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public CuboidRegion getCuboidRegion() {
        return region;
    }

    @Override
    public String getWorld() {
        return world;
    }

    @Override
    public UUID getWorldUUID() {
        return worldUUID;
    }
    
    

    @Override
    public ConstructionStatus getConstructionStatus() {
        return status;
    }

   
    
    @Override
    public Date getCompletedAt() {
        return completedAt;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public Date getDeletedAt() {
        return removedAt;
    }

    @Override
    public void build(Player player, PlaceOptions options, boolean force) {
        ConstructionManager.getInstance().build(this, player.getUniqueId(), getSession(player.getUniqueId()), options, force);
    }

    @Override
    public void build(EditSession session, PlaceOptions options, boolean force) {
        ConstructionManager.getInstance().build(this, PlayerEntry.CONSOLE.getUUID(), session, options, force);
    }

    @Override
    public void demolish(Player player, DemolishingOptions options, boolean force) {
        ConstructionManager.getInstance().demolish(this, player.getUniqueId(), getSession(player.getUniqueId()), options, force);
    }

    @Override
    public void demolish(EditSession session, DemolishingOptions options, boolean force) {
        ConstructionManager.getInstance().demolish(this, PlayerEntry.CONSOLE.getUUID(), session, options, force);
    }

    @Override
    public void stop(boolean force) {
        ConstructionManager.getInstance().stop(this, force);
    }
    
    @Override
    public void stop(Player player, boolean useForce) {
        ConstructionManager.getInstance().stop(player, this, useForce);
    }

    
    private AsyncEditSession getSession(UUID playerId) {
        Player ply = SettlerCraft.getInstance().getPlayer(playerId);
        World w = SettlerCraft.getInstance().getWorld(world);
        AsyncEditSession editSession;
        if (ply == null) {
            editSession = (AsyncEditSession) AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(w, -1);
        } else {
            editSession = (AsyncEditSession) AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(w, -1, ply);
        }
        return editSession;
    }

    @Override
    public StructurePlan getStructurePlan() {
        File planFile = new File(getStructureDirectory(), "structureplan.xml");
        
        StructurePlanReader reader = new StructurePlanReader();
        StructurePlan plan = reader.readFile(planFile);
        
        return plan;
    }

    @Override
    public final File getStructureDirectory() {
        File worldStructureFolder = StructureAPI.getInstance().getStructuresDirectory(world);
        return new File(worldStructureFolder, String.valueOf(id));
    }

    @Override
    public boolean isRoot() {
        return isRoot;
    }

    
    
    
}
