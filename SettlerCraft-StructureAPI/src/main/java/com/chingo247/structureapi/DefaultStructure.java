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
package com.chingo247.structureapi;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.structureapi.construction.asyncworldedit.AsyncWorldEditUtil;
import com.chingo247.structureapi.plan.placement.options.PlaceOptions;
import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import com.chingo247.structureapi.persistence.dao.placement.PlacementDataNode;
import com.chingo247.structureapi.persistence.dao.structure.StructureNode;
import com.chingo247.structureapi.plan.placement.Placement;
import com.chingo247.structureapi.plan.placement.node.NodePlacementHandler;
import com.chingo247.structureapi.plan.placement.node.NodePlacementHandlerFactory;
import com.chingo247.structureapi.plan.placement.options.DemolishingOptions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.util.Date;
import java.util.UUID;
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
    private final Placement placement;
    
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
        
        PlacementDataNode node = structureNode.getPlacementData();
        NodePlacementHandler handler = NodePlacementHandlerFactory.getInstance().getHandler(node.getType());
        if(handler == null) {
            throw new RuntimeException("No node handler was registered to handle type '"+node.getType()+"'");
        }
        
        
        this.placement = handler.fromNode(node, getStructureDirectory());
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
        StructureAPI.getInstance().build(this, player.getUniqueId(), getSession(player.getUniqueId()), options, force);
    }

    @Override
    public void build(EditSession session, PlaceOptions options, boolean force) {
        StructureAPI.getInstance().build(this, PlayerEntry.CONSOLE.getUUID(), session, options, force);
    }

    @Override
    public void demolish(Player player, DemolishingOptions options, boolean force) {
        StructureAPI.getInstance().demolish(this, player.getUniqueId(), getSession(player.getUniqueId()), options, force);
    }

    @Override
    public void demolish(EditSession session, DemolishingOptions options, boolean force) {
        StructureAPI.getInstance().demolish(this, PlayerEntry.CONSOLE.getUUID(), session, options, force);
    }

    @Override
    public void stop(boolean force) {
        StructureAPI.getInstance().stop(this, force);
    }
    
    @Override
    public void stop(Player player, boolean useForce) {
        StructureAPI.getInstance().stop(player, this, useForce);
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
    public Placement getPlacement() {
        return placement;
    }

    @Override
    public final File getStructureDirectory() {
        File worldStructureFolder = StructureAPI.getInstance().getStructuresDirectory(world);
        return new File(worldStructureFolder, String.valueOf(id));
    }

    
    
    
}
