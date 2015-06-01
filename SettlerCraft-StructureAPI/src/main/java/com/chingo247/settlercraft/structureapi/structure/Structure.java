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
package com.chingo247.settlercraft.structureapi.structure;

import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.BuildOptions;
import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.structureapi.exception.ConstructionException;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.DemolishingOptions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public interface Structure {
    
    /**
     * Gets the id of the structure
     * @return The id of the structure
     */
    public Long getId();
    /**
     * Gets the name of the structure
     * @return The name of the structure
     */
    public String getName();
    /**
     * Gets the name of the world of this structure
     * @return The world
     */
    public String getWorld();

    /**
     * Gets the position of this Structure
     * @return The position
     */
    public Vector getPosition();
    
    /**
     * Gets the world id of this structure
     * @return The world id
     */
    public UUID getWorldUUID();
    /**
     * Gets the direction in which this structure is oriented
     * @return the direction
     */
    public Direction getDirection();
    /**
     * The region this structure overlaps
     * @return The region
     */
    public CuboidRegion getCuboidRegion();
    /**
     * The current construction status of this structure
     * @return The construction status
     */
    public ConstructionStatus getConstructionStatus();
    /**
     * Gets the plan for this structure
     * @return The plan
     */
    public StructurePlan getStructurePlan();
    
    /**
     * Returns the directory for this structure
     * @return The directory
     */
    public File getStructureDirectory();
    
    
    /**
     * Gets the value/price of this structure 
     * @return The value/price of this structure
     */
    public double getPrice();
    
    /**
     * Gets when this structure was completed, may return null
     * @return The date of completion
     */
    public Date getCompletedAt();
    /**
     * Gets when this structure was created
     * @return The date this structure was created
     */
    public Date getCreatedAt();
    /**
     * Gets the date when this structure was removed. may return null
     * @return The date of removal
     */
    public Date getDeletedAt();
    /**
     * Builds the structure using the given player to create the editsession
     * @param player The player
     * @param options The placeOptions
     * @param force whether building should be enforced by ignoring the current construction status
     */
    public void build(Player player, BuildOptions options, boolean force) throws ConstructionException;
    /**
     * Builds the structure
     * @param session The EditSession
     * @param options The placeOptions
     * @param force whether building should be enforced by ignoring the current construction status
     */
    public void build(EditSession session, BuildOptions options, boolean force) throws ConstructionException;
    /**
     * Demolishes the structure with the given player to host the EditSession
     * @param player The player
     * @param options The option for demolishing
     * @param force whether to force the demolition by ignoring the current construction status
     */
    public void demolish(Player player, DemolishingOptions options, boolean force) throws ConstructionException;
    /**
     * Demolishes the structure
     * @param session The EditSession
     * @param options The Options
     * @param force Whether to force the demolition by ignoring the current construction status
     */
    public void demolish(EditSession session, DemolishingOptions options, boolean force) throws ConstructionException;
    
    /**
     * Stops the construction, if the structure was being constructed
     * @param useForce Whether to use force
     */
    public void stop(boolean useForce) throws ConstructionException;

    /**
     * Stops the construction, if the structure was being constructed
     * @param player The player
     * @param useForce Whether to use force by ignoring the current construction status
     */
    public void stop(Player player, boolean useForce) throws ConstructionException;
    
    /**
     * Checks if this Structure is the most upper parent
     * @return True if this Structure has no parent
     */
    public boolean hasParent();
    
    /**
     * Will add the offset to the structure's origin, which is always the front left corner of a
     * structure.
     *
     * @param offset The offset
     * @return the location
     */
    public Vector translateRelativeLocation(Vector offset);
    
    public Vector getRelativePosition(Vector worldPosition);
}
