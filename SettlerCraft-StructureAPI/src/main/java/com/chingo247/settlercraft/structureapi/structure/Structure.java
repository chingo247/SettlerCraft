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

import com.chingo247.settlercraft.structureapi.structure.options.PlaceOptions;
import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.structure.options.DemolishingOptions;
import com.sk89q.worldedit.EditSession;
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
    public void build(Player player, PlaceOptions options, boolean force);
    /**
     * Builds the structure
     * @param session The EditSession
     * @param options The placeOptions
     * @param force whether building should be enforced by ignoring the current construction status
     */
    public void build(EditSession session, PlaceOptions options, boolean force);
    /**
     * Demolishes the structure with the given player to host the EditSession
     * @param player The player
     * @param options The option for demolishing
     * @param force whether to force the demolition by ignoring the current construction status
     */
    public void demolish(Player player, DemolishingOptions options, boolean force);
    /**
     * Demolishes the structure
     * @param session The EditSession
     * @param options The Options
     * @param force Whether to force the demolition by ignoring the current construction status
     */
    public void demolish(EditSession session, DemolishingOptions options, boolean force);
    
    /**
     * Stops the construction, if the structure was being constructed
     * @param useForce Whether to use force
     */
    public void stop(boolean useForce);

    /**
     * Stops the construction, if the structure was being constructed
     * @param player The player
     * @param useForce Whether to use force by ignoring the current construction status
     */
    public void stop(Player player, boolean useForce);
    
    public boolean isRoot();
    
}
