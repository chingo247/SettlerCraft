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
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.BuildOptions;
import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import com.chingo247.settlercraft.structureapi.exception.ConstructionException;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlanReader;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.DemolishingOptions;
import com.chingo247.settlercraft.structureapi.util.WorldUtil;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
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
    private final boolean hasParent;
    private final Vector position;
    
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
        this.position = new Vector(structureNode.getX(), structureNode.getY(), structureNode.getZ());
        this.hasParent = structureNode.getParent() != null;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Vector getPosition() {
        return position;
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
    public void build(Player player, BuildOptions options, boolean force) throws ConstructionException {
        ConstructionManager.getInstance().build(this, player.getUniqueId(), getSession(player.getUniqueId()), options, force);
    }

    @Override
    public void build(EditSession session, BuildOptions options, boolean force) throws ConstructionException {
        ConstructionManager.getInstance().build(this, PlayerEntry.CONSOLE.getUUID(), session, options, force);
    }

    @Override
    public void demolish(Player player, DemolishingOptions options, boolean force) throws ConstructionException {
        ConstructionManager.getInstance().demolish(this, player.getUniqueId(), getSession(player.getUniqueId()), options, force);
    }

    @Override
    public void demolish(EditSession session, DemolishingOptions options, boolean force) throws ConstructionException {
        ConstructionManager.getInstance().demolish(this, PlayerEntry.CONSOLE.getUUID(), session, options, force);
    }

    @Override
    public void stop(boolean force) throws ConstructionException {
        ConstructionManager.getInstance().stop(this, true, force);
    }
    
    @Override
    public void stop(Player player, boolean useForce) throws ConstructionException {
        ConstructionManager.getInstance().stop(player, this, true, useForce);
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
    public boolean hasParent() {
        return hasParent;
    }

    /**
     * Will add the offset to the structure's origin, which is always the front left corner of a
     * structure.
     *
     * @param offset The offset
     * @return the location
     */
    @Override
    public Vector translateRelativeLocation(Vector offset) {
        Vector p = WorldUtil.translateLocation(getPosition(), direction, offset.getX(), offset.getY(), offset.getZ());
        return new Vector(p.getBlockX(), p.getBlockY(), p.getBlockZ());
    }

    @Override
    public Vector getRelativePosition(Vector worldPosition) {
        switch (direction) {
            case NORTH:
                return new Vector(
                        worldPosition.getBlockX() - this.getPosition().getX(),
                        worldPosition.getBlockY() - this.getPosition().getY(),
                        this.getPosition().getZ() - worldPosition.getBlockZ()
                );
            case SOUTH:
                return new Vector(
                        this.getPosition().getX() - worldPosition.getBlockX(),
                        worldPosition.getBlockY() - this.getPosition().getY(),
                        worldPosition.getBlockZ() - this.getPosition().getZ()
                );
            case EAST:
                return new Vector(
                        worldPosition.getBlockZ() - this.getPosition().getZ(),
                        worldPosition.getBlockY() - this.getPosition().getY(),
                        worldPosition.getBlockX() - this.getPosition().getX()
                );
            case WEST:
                return new Vector(
                        this.getPosition().getZ() - worldPosition.getBlockZ(),
                        worldPosition.getBlockY() - this.getPosition().getY(),
                        this.getPosition().getX() - worldPosition.getBlockX()
                );
            default:
                throw new AssertionError("Unreachable");
        }
    }
    
    
}
