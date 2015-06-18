/*
 * Copyright (C) 2015 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.settlercraft.structureapi.model.structure;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.model.World;
import com.chingo247.settlercraft.core.model.interfaces.IWorld;
import com.chingo247.settlercraft.structureapi.exception.ConstructionException;
import com.chingo247.settlercraft.structureapi.structure.ConstructionManager;
import com.chingo247.settlercraft.structureapi.structure.StructureAPI;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.BuildOptions;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.DemolishingOptions;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.Date;
import java.util.UUID;
import org.neo4j.graphdb.Node;
import org.primesoft.asyncworldedit.playerManager.PlayerEntry;
import org.primesoft.asyncworldedit.worldedit.ThreadSafeEditSession;

/**
 *
 * @author Chingo
 */
public class Structure extends AbstractStructure {
    
    private Long id;
    private String name;
    private Vector origin;
    private StructureStatus status;
    private double price;
    private Direction direction;
    
    private CuboidRegion cuboidRegion;
    private Date completedAt;
    private Date createdAt;
    private Date deletedAt;
    private IWorld world;
    

    public Structure(Node structureNode) {
        this(new StructureNode(structureNode));
    }
    
    public Structure(StructureNode structure) {
        super(structure.getNode());
        this.id = structure.getId();
        this.name = structure.getName();
        this.origin = structure.getOrigin();
        this.status = structure.getStatus();
        this.price = structure.getPrice();
        this.cuboidRegion = structure.getCuboidRegion();
        this.direction = structure.getDirection();
        this.deletedAt = structure.getDeletedAt();
        this.createdAt = structure.getCreatedAt();
        this.completedAt = structure.getCompletedAt();
        this.world = new World(structure.getWorld());
    }
    
    
    /**
     * Gets the id of the structure
     *
     * @return The id of the structure
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Gets the name of the structure
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the origin of this Structure
     *
     * @return The position
     */
    @Override
    public Vector getOrigin() {
        return origin;
    }

    /**
     * The region this structure overlaps
     * @return The region
     */
    @Override
    public CuboidRegion getCuboidRegion() {
        return cuboidRegion;
    }

    /**
     * Gets the value/price of this structure
     *
     * @return The value/price of this structure
     */
    @Override
    public double getPrice() {
        return price;
    }

    /**
     * Gets the direction in which this structure is oriented
     * @return the direction
     */
    @Override
    public Direction getDirection() {
        return direction;
    }

    /**
     * The current construction status of this structure
     *
     * @return The construction status
     */
    public StructureStatus getConstructionStatus() {
        return status;
    }

    /**
     * Gets when this structure was completed, may return null
     *
     * @return The date of completion
     */
    public Date getCompletedAt() {
        return completedAt;
    }

    /**
     * Gets when this structure was created
     *
     * @return The date this structure was created
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the date when this structure was removed. may return null
     *
     * @return The date of removal
     */
    public Date getDeletedAt() {
        return deletedAt;
    }

    @Override
    public StructureStatus getStatus() {
        return status;
    }

    @Override
    public IWorld getWorld() {
        return world;
    }
    
    public void build(Player player, BuildOptions options, boolean force) throws ConstructionException {
        ConstructionManager.getInstance().build(this, player.getUniqueId(), getSession(player.getUniqueId()), options, force);
    }

    public void build(ThreadSafeEditSession session, BuildOptions options, boolean force) throws ConstructionException {
        ConstructionManager.getInstance().build(this, PlayerEntry.CONSOLE.getUUID(), session, options, force);
    }

    public void demolish(Player player, DemolishingOptions options, boolean force) throws ConstructionException {
        ConstructionManager.getInstance().demolish(this, player.getUniqueId(), getSession(player.getUniqueId()), options, force);
    }

    public void demolish(ThreadSafeEditSession session, DemolishingOptions options, boolean force) throws ConstructionException {
        ConstructionManager.getInstance().demolish(this, PlayerEntry.CONSOLE.getUUID(), session, options, force);
    }

    public void stop(boolean force) throws ConstructionException {
        ConstructionManager.getInstance().stop(this, true, force);
    }
    
    public void stop(Player player, boolean useForce) throws ConstructionException {
        ConstructionManager.getInstance().stop(player, this, true, useForce);
    }
    
    private ThreadSafeEditSession getSession(UUID playerId) {
        Player ply = SettlerCraft.getInstance().getPlayer(playerId);
        com.sk89q.worldedit.world.World w = SettlerCraft.getInstance().getWorld(getWorld().getName());
        ThreadSafeEditSession editSession;
        StructureAPI api = (StructureAPI) StructureAPI.getInstance();
        
        if (ply == null) {
            editSession =  api.getSessionFactory().getThreadSafeEditSession(w, -1);
        } else {
            editSession =  api.getSessionFactory().getThreadSafeEditSession(w, -1, ply);
        }
        return editSession;
    }
    
}
