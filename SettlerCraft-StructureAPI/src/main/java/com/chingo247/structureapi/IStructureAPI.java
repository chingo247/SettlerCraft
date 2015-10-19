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
package com.chingo247.structureapi;

import com.chingo247.menuapi.menu.CategoryMenu;
import com.chingo247.structureapi.construction.ITaskAssigner;
import com.chingo247.structureapi.construction.IConstructionManager;
import com.chingo247.structureapi.construction.asyncworldedit.AsyncPlacement;
import com.chingo247.backupapi.core.IBackupAPI;
import com.chingo247.backupapi.core.IChunkManager;
import com.chingo247.settlercraft.core.concurrent.KeyPool;
import com.chingo247.structureapi.model.structure.Structure;
import com.chingo247.structureapi.platform.IConfigProvider;
import com.chingo247.structureapi.construction.ConstructionException;
import com.chingo247.structureapi.plan.StructurePlanManager;
import com.chingo247.structureapi.plan.placement.Placement;
import com.chingo247.structureapi.plan.placement.SchematicPlacement;
import com.chingo247.structureapi.construction.options.BuildOptions;
import com.chingo247.structureapi.construction.options.DemolitionOptions;
import com.chingo247.xplatform.core.APlatform;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSessionFactory;

/**
 *
 * @author Chingo
 */
public interface IStructureAPI {

    ConstructionWorld getConstructionWorld(String world);

    ConstructionWorld getConstructionWorld(World world);

    void build(AsyncEditSession editSession, UUID player, Structure structure, BuildOptions options, ITaskAssigner taskAssigner) throws ConstructionException;

    void build(AsyncEditSession editSession, UUID player, Structure structure, BuildOptions options) throws ConstructionException;

    void build(UUID player, Structure structure, BuildOptions options) throws ConstructionException;

    void build(Structure structure, BuildOptions options) throws ConstructionException;

    void build(Structure structure) throws ConstructionException;

    void demolish(AsyncEditSession editSession, UUID player, Structure structure, DemolitionOptions options, ITaskAssigner taskAssigner) throws ConstructionException;

    void demolish(AsyncEditSession editSession, UUID player, Structure structure, DemolitionOptions options) throws ConstructionException;

    void demolish(UUID player, Structure structure, DemolitionOptions options) throws ConstructionException;

    void demolish(Structure structure, DemolitionOptions options) throws ConstructionException;

    void demolish(Structure structure) throws ConstructionException;

    void stop(Structure structure, boolean force) throws ConstructionException;

    /**
     * Gets the ConstructionManager
     *
     * @return The ConstructionManager
     */
    IConstructionManager getConstructionManager();

    /**
     * Gets the StructurePlanManager, alternatively {@link StructurePlanManager#getInstance()
     * } may be used
     *
     * @return The StructurePlanManager
     */
    StructurePlanManager getStructurePlanManager();

    /**
     * Reloads the StructureAPI
     */
    void reload();

    /**
     * Checks if the AWE queue is locked for a given UUID
     *
     * @param player The player UUID or PlayerEntry UUID
     * @return True if the queue was locked
     */
    boolean isQueueLocked(UUID player);

    /**
     * Checks if StructureAPI is loading (plans, schematics, etc)
     *
     * @return True if StructureAPI is loadings
     */
    boolean isLoading();

    /**
     * Gets the ConfigProvider
     *
     * @return The ConfigProvider
     */
    IConfigProvider getConfig();

    /**
     * Gets the platform
     *
     * @return The platform
     */
    APlatform getPlatform();

    /**
     * Gets the plan directory
     *
     * @return The plan directory
     */
    File getPlanDirectory();

    /**
     * The directory where plans are generated from schematics
     *
     * @return The directory
     */
    File getGenerationDirectory();

    /**
     * The directory where plans are generated from schematics
     *
     * @return The directory
     */
    File getWorkingDirectory();

    /**
     * Gets the directory where world data is stored (within StructureAPI
     * directory)
     *
     * @param world The world
     * @return The directory for given world
     */
    File getWorldDirectory(String world);

    /**
     * Gets the Structures directory for a world
     *
     * @param world The world
     * @return The structures directory
     */
    File getStructuresDirectory(String world);

    /**
     * Creates a new PlanMenu, this PlanMenu is loaded with all the plans
     * available. The Plan Menu can be used by ONE player. Each player requires
     * it's own plan menu, therefore new instances need to be created
     *
     * @return The PlanMenu
     */
    CategoryMenu createPlanMenu();


    /**
     * Checks all StructureRestrictions. Each restriction determines if
     * something is allowed to be placed in a certain area by a certain player.
     *
     * @param player The player, may be null
     * @param world The world
     * @param region The region
     * @throws
     * com.chingo247.structureapi.exception.StructureRestrictionException Thrown
     * when a restriction was violated
     */
    void checkRestrictions(Player player, World world, CuboidRegion region) throws StructureRestrictionException;

    void checkRestrictions(Player player, ConstructionWorld constructionWorld, CuboidRegion region) throws StructureRestrictionException;

    void addRestriction(StructureRestriction structureRestriction);
    
    void removeRestriction(StructureRestriction structureRestriction);
    
    /**
     * Loads a schematic file
     *
     * @param schematicFile The schematic file to load
     * @return The schematicPlacement
     * @throws IOException
     */
    SchematicPlacement loadSchematic(File schematicFile) throws IOException;

    /**
     * Makes a placement Async
     *
     * @param player The player UUID or null, The UUID specifies the Queue for
     * AsyncWorldEdit A null value means that the UUID was issued by
     * PlayerEntry.CONSOLE. Note that the UUID does not have to be of a real
     * player.
     * @param placement The placement
     * @return The AsyncPlacement
     */
    AsyncPlacement makeAsync(UUID player, Placement placement);

    /**
     * Gets the registered BackupAPI. May return null if none was registered
     *
     * @return The BackupAPI
     */
    IBackupAPI getBackupAPI();

    /**
     * Gets an AsyncEditSessionFactory
     *
     * @return The AsyncEditSessionFactor
     */
    AsyncEditSessionFactory getSessionFactory();

    IChunkManager getChunkManager();

}
