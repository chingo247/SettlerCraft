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
package com.chingo247.structureapi.structure;

import com.chingo247.menuapi.menu.CategoryMenu;
import com.chingo247.settlercraft.core.Direction;
import com.chingo247.structureapi.construction.IBuildTaskAssigner;
import com.chingo247.structureapi.construction.IConstructionManager;
import com.chingo247.structureapi.construction.IDemolitionTaskAssigner;
import com.chingo247.structureapi.construction.asyncworldedit.AsyncPlacement;
import com.chingo247.backupapi.core.IBackupAPI;
import com.chingo247.backupapi.core.IChunkManager;
import com.chingo247.structureapi.exception.StructureRestrictionException;
import com.chingo247.structureapi.model.structure.Structure;
import com.chingo247.structureapi.platform.IConfigProvider;
import com.chingo247.structureapi.exception.ConstructionException;
import com.chingo247.structureapi.structure.plan.IStructurePlan;
import com.chingo247.structureapi.structure.plan.StructurePlanManager;
import com.chingo247.structureapi.structure.plan.placement.Placement;
import com.chingo247.structureapi.structure.plan.placement.SchematicPlacement;
import com.chingo247.structureapi.structure.plan.placement.options.BuildOptions;
import com.chingo247.structureapi.structure.plan.placement.options.DemolitionOptions;
import com.chingo247.structureapi.structure.restriction.StructureRestriction;
import com.chingo247.xplatform.core.APlatform;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
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
    
    /**
     * Creates a structure with the provided plan
     * @param plan The StructurePlan
     * @param world The world
     * @param position The position
     * @param direction The direction of the player
     * @return The Structure that has been created
     */
    public Structure createStructure(IStructurePlan plan, World world, Vector position, Direction direction) throws Exception;
    
    /**
     * Creates a structure with the provided plan
     * @param plan The StructurePlan
     * @param world The world
     * @param position The position
     * @param direction The direction of the player
     * @param owner The player that will be assigned as MASTER owner of this structure
     * @return The Structure that has been created
     */
    public Structure createStructure(IStructurePlan plan, World world, Vector position, Direction direction, Player owner) throws Exception;
    
     /**
     * Creates a structure with the provided placement
     * @param placement The Placement
     * @param world The world
     * @param position The position
     * @param direction The direction of the player
     * @return The Structure that has been created
     */
    public Structure createStructure(Placement placement, World world, Vector position, Direction direction) throws Exception;
    
    /**
     * Creates a structure with the provided plan
     * @param placement The Placement
     * @param world The world
     * @param position The position
     * @param direction The direction of the player
     * @param owner The player that will be assigned as MASTER owner of this structure
     * @return The Structure that has been created
     */
    public Structure createStructure(Placement placement, World world, Vector position, Direction direction, Player owner) throws Exception;
    
     /**
     * Creates a substructure for given structure with the provided plan
     * @param structure The structure to add the created structure to
     * @param plan The StructurePlan
     * @param world The world
     * @param position The position
     * @param direction The direction of the player
     * @param owner The player that will be assigned as MASTER owner of this structure
     * @return The Structure that has been created
     */
    public Structure createSubstructure(Structure structure, IStructurePlan plan, World world, Vector position, Direction direction, Player owner) throws Exception;
    
     /**
     * Creates a substructure for given structure with the provided plan
     * @param structure The structure to add the created structure to
     * @param plan The StructurePlan
     * @param world The world
     * @param position The position
     * @param direction The direction of the player
     * @return The Structure that has been created
     */
    public Structure createSubstructure(Structure structure, IStructurePlan plan, World world, Vector position, Direction direction) throws Exception;
    
     /**
     * Creates a substructure for given structure with the provided placement
     * @param structure The structure to add the created structure to
     * @param placement The Placement
     * @param world The world
     * @param position The position
     * @param direction The direction of the player
     * @param owner The player that will be assigned as MASTER owner of this structure
     * @return The Structure that has been created
     */
    public Structure createSubstructure(Structure structure, Placement placement, World world, Vector position, Direction direction, Player owner) throws Exception;
    
     /**
     * Creates a substructure for given structure with the provided placement
     * @param structure The structure to add the created structure to
     * @param placement The Placement
     * @param world The world
     * @param position The position
     * @param direction The direction of the player
     * @return The Structure that has been created
     */
    public Structure createSubstructure(Structure structure, Placement placement, World world, Vector position, Direction direction) throws Exception;
    
    public void build(AsyncEditSession editSession, UUID player, Structure structure, BuildOptions options, IBuildTaskAssigner taskAssigner) throws ConstructionException;
    
    public void build(AsyncEditSession editSession, UUID player, Structure structure, BuildOptions options) throws ConstructionException;
    
    public void build(UUID player, Structure structure, BuildOptions options) throws ConstructionException;
    
    public void build(Structure structure, BuildOptions options) throws ConstructionException;
    
    public void build(Structure structure) throws ConstructionException;
    
    public void demolish(AsyncEditSession editSession, UUID player, Structure structure, DemolitionOptions options, IDemolitionTaskAssigner taskAssigner) throws ConstructionException;
    
    public void demolish(AsyncEditSession editSession, UUID player, Structure structure, DemolitionOptions options) throws ConstructionException;
    
    public void demolish(UUID player, Structure structure, DemolitionOptions options) throws ConstructionException;
    
    public void demolish(Structure structure, DemolitionOptions options) throws ConstructionException;
    
    public void demolish(Structure structure) throws ConstructionException;
    
    public void stop(Structure structure, boolean force) throws ConstructionException;
    
    /**
     * Gets the ConstructionManager
     * @return The ConstructionManager 
     */
    public IConstructionManager getConstructionManager();
    
  
    
    /**
     * Gets the StructurePlanManager, alternatively {@link StructurePlanManager#getInstance() } may be used
     * @return The StructurePlanManager 
     */
    public StructurePlanManager getStructurePlanManager();
    
    /**
     * Reloads the StructureAPI
     */
    public void reload();
    
    /**
     * Checks if the AWE queue is locked for a given UUID
     * @param player The player UUID or PlayerEntry UUID
     * @return True if the queue was locked
     */
    public boolean isQueueLocked(UUID player);
    
    
    /**
     * Checks if StructureAPI is loading (plans, schematics, etc)
     * @return True if StructureAPI is loadings
     */
    public boolean isLoading();
    
    /**
     * Gets the ConfigProvider
     * @return The ConfigProvider
     */
    public IConfigProvider getConfig();
    
    /**
     * Gets the platform
     * @return The platform
     */
    public APlatform getPlatform();
    
    /**
     * Gets the plan directory
     * @return The plan directory
     */
    public File getPlanDirectory();
    
    /**
     * The directory where plans are generated from schematics
     * @return The directory
     */
    public File getGenerationDirectory();
    
    /**
     * The directory where plans are generated from schematics
     * @return The directory
     */
    public File getWorkingDirectory();
    
    
    /**
     * Gets the Structures directory for a world
     * @param world The world 
     * @return The structures directory
     */
    public File getStructuresDirectory(String world);
    
    /**
     * Creates a new PlanMenu, this PlanMenu is loaded with all the plans available. 
     * The Plan Menu can be used by ONE player. Each player requires it's own plan menu, therefore 
     * new instances need to be created
     * @return The PlanMenu
     */
    public CategoryMenu createPlanMenu();
    
    /**
     * Adds a restriction that will be added as check when placing structures
     * @param structureRestriction The restriction
     */
    public void addRestriction(StructureRestriction structureRestriction);
    
    /**
     * Removes a restriction for placing structures
     * @param structureRestriction 
     */
    public void removeRestriction(StructureRestriction structureRestriction);
    
    /**
     * Checks all StructureRestrictions. Each restriction determines if something is allowed to be placed
     * in a certain area by a certain player.
     * @param player The player, may be null
     * @param world The world
     * @param region The region
     * @throws com.chingo247.structureapi.exception.StructureRestrictionException Thrown when a restriction was violated
     */
    public void checkRestrictions(Player player, World world, CuboidRegion region) throws StructureRestrictionException;
    
    /**
     * Loads a schematic file
     * @param schematicFile The schematic file to load
     * @return The schematicPlacement
     * @throws IOException 
     */
    public SchematicPlacement loadSchematic(File schematicFile) throws IOException ;
    
    /**
     * Makes a placement Async
     * @param player The player UUID or null, The UUID specifies the Queue for AsyncWorldEdit
     * A null value means that the UUID was issued by PlayerEntry.CONSOLE. 
     * Note that the UUID does not have to be of a real player.
     * @param placement The placement
     * @return The AsyncPlacement
     */
    public AsyncPlacement makeAsync(UUID player, Placement placement);
    
    /**
     * Gets the registered BackupAPI. May return null if none was registered
     * @return The BackupAPI
     */
    public IBackupAPI getBackupAPI();
    
    /**
     * Gets an AsyncEditSessionFactory
     * @return The AsyncEditSessionFactor
     */
    public AsyncEditSessionFactory getSessionFactory();
    
    public IChunkManager getChunkManager();
    
}
