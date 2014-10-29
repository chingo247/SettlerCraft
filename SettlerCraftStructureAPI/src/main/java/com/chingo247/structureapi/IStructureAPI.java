/*
 * Copyright (C) 2014 Chingo
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
package com.chingo247.structureapi;

import com.chingo247.structureapi.construction.BuildOptions;
import com.chingo247.structureapi.construction.DemolitionOptions;
import com.chingo247.structureapi.exception.StructureException;
import com.chingo247.structureapi.persistence.service.StructureService;
import com.chingo247.structureapi.plan.StructurePlan;
import com.chingo247.structureapi.plan.StructurePlanManager;
import com.chingo247.structureapi.plan.document.PlanDocumentGenerator;
import com.chingo247.structureapi.plan.document.PlanDocumentManager;
import com.chingo247.structureapi.plan.document.StructureDocumentManager;
import com.chingo247.structureapi.plan.schematic.Schematic;
import com.chingo247.structureapi.plan.schematic.SchematicManager;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.flags.Flag;
import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public interface IStructureAPI {
    
    public PlanDocumentGenerator getPlanDocumentGenerator();

    public StructurePlanManager getStructurePlanManager();

    public PlanDocumentManager getPlanDocumentManager();

    public StructureDocumentManager getStructureDocumentManager();

    public SchematicManager getSchematicManager();
    
    public File getFolder(Structure structure);
    
    public File getStructurePlanFile(Structure structure);

    public Schematic getSchematic(Structure structure) throws Exception;

    public StructureService getStructureService();

    /**
     * Creates a structure.
     *
     * @param plan The StructurePlan
     * @param world The world
     * @param pos The position
     * @param direction The direction / direction
     * @return The structure or null if failed to claim the ground
     */
    public Structure create(StructurePlan plan, World world, Vector pos, Direction direction) throws StructureException;

    /**
     * Creates a structure
     *
     * @param player The player, which will also be added as an owner of the structure
     * @param plan The StructurePlan
     * @param world The world
     * @param pos The position
     * @param direction The direction / direction
     * @return The structure or null if failed to claim the ground
     */
    public Structure create(Player player, StructurePlan plan, World world, Vector pos, Direction direction) throws StructureException;
    
    /**
     * Starts construction of a structure
     * @param uuid The UUID to backtrack this construction process
     * @param structure The structure
     * @param options
     * @param force
     * @return true if successfully started to build
     */
    public boolean build(UUID uuid, Structure structure, BuildOptions options, boolean force);

    /**
     * Starts construction of a structure
     * @param uuid The UUID to backtrack this construction process
     * @param structure The structure
     * @param force
     * @return true if successfully started to build
     */
    public boolean build(UUID uuid, Structure structure, boolean force);

    /**
     * Starts construction of a structure
     *
     * @param player The player that issues the build order
     * @param structure The structure to build
     * @param force
     * @return true if successfully started to build
     */
    public boolean build(Player player, Structure structure, boolean force);

    /**
     * Starts construction of a structure
     * @param uuid The UUID to backtrack this construction process
     * @param structure The structure
     * @param options
     * @param force
     * @return true if successfully started to build
     */
    public boolean demolish(UUID uuid, Structure structure, DemolitionOptions options, boolean force);
    
    /**
     * Starts demolishment of a structure
     *
     * @param uuid The UUID to register the construction order (for AsyncWorldEdit's API calls)
     * @param structure The structure
     * @param force
     * @return True if successfully started to demolish the structure
     */
    public boolean demolish(UUID uuid, Structure structure, boolean force);

    /**
     * Starts demolishment of a structure
     *
     * @param player The player to register the demolition order (for AsyncWorldEdit API calls) and
     * authorise the order
     * @param structure The structure
     * @param force
     * @return True if successfully started to build
     */
    public boolean demolish(Player player, Structure structure, boolean force);

    /**
     * Stops construction/demolishment of this structure
     *
     * @param player The player to authorise the stop order
     * @param structure The structure
     * @return True if successfully stopped
     */
    public boolean stop(Player player, Structure structure);
    

    /**
     * Adds the player as owner to this structure
     *
     * @param player The player
     * @param type The owner type
     * @param structure The structure to add the player to
     * @throws StructureException if player already owns this structure or structure doesn't have a
     * region
     */
    public void makeOwner(Player player, PlayerOwnership.Type type, Structure structure) throws StructureException;

    /**
     * Adds the player as member to this structure
     *
     * @param player The player to add
     * @param structure The structure to add the player to
     * @throws StructureException if player already is a member or Structure doesn't have a region
     */
    public void makeMember(Player player, Structure structure) throws StructureException;

    /**
     * Removes an owner of this structure
     *
     * @param player The player to remove
     * @param structure The structure
     * @return if player was successfully removed
     * @throws StructureException When structure doesn't have a region
     */
    public boolean removeOwner(Player player, Structure structure) throws StructureException;

    /**
     * Removes a member of this structure
     *
     * @param player The player to remove
     * @param structure The structure
     * @return if player was successfully removed
     * @throws StructureException When structure doesn't have a region
     */
    public boolean removeMember(Player player, Structure structure) throws StructureException;

    /**
     * Checks if the given dimension overlaps any structures.
     *
     * @param world The world
     * @param dimension The dimension
     * @return True if dimension overlaps any structure
     */
    public boolean overlapsStructures(World world, Dimension dimension);

    /**
     * Checks if the dimension overlaps any (WorldGuard) region
     *
     * @param world The world
     * @param dimension The dimension
     * @return True if dimension overlaps any region
     */
    public boolean overlapsRegion(World world, Dimension dimension);

    /**
     * Checks if the dimension overlaps any region which the target player does is not an own
     *
     * @param player The player
     * @param world The world
     * @param dimension The dimension
     * @return True if dimension overlaps any region the player is not an owner of.
     */
    public boolean overlapsRegion(Player player, World world, Dimension dimension);

    public abstract HashMap<Flag, Object> getDefaultFlags();

    public abstract int getBuildMode();

    public abstract int getDemolisionMode();

    public abstract boolean useHolograms();

    public abstract double getRefundPercentage();

    public abstract File getStructureDataFolder();

    public abstract File getPlanDataFolder();
    
    public abstract File getSchematicToPlanFolder();
    
}
