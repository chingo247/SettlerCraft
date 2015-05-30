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

import com.chingo247.menuapi.menu.CategoryMenu;
import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.structureapi.platforms.bukkit.IConfigProvider;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlanManager;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.SchematicPlacement;
import com.chingo247.settlercraft.structureapi.structure.restriction.StructureRestriction;
import com.chingo247.xplatform.core.APlatform;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.IOException;

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
    public Structure createStructure(StructurePlan plan, World world, Vector position, Direction direction) throws Exception;
    
    /**
     * Creates a structure with the provided plan
     * @param plan The StructurePlan
     * @param world The world
     * @param position The position
     * @param direction The direction of the player
     * @param owner The player that will be assigned as MASTER owner of this structure
     * @return The Structure that has been created
     */
    public Structure createStructure(StructurePlan plan, World world, Vector position, Direction direction, Player owner) throws Exception;
    
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
    public Structure createSubstructure(Structure structure, StructurePlan plan, World world, Vector position, Direction direction, Player owner) throws Exception;
    
     /**
     * Creates a substructure for given structure with the provided plan
     * @param structure The structure to add the created structure to
     * @param plan The StructurePlan
     * @param world The world
     * @param position The position
     * @param direction The direction of the player
     * @return The Structure that has been created
     */
    public Structure createSubstructure(Structure structure, StructurePlan plan, World world, Vector position, Direction direction) throws Exception;
    
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
    
    /**
     * Gets the ConstructionManager, alternatively {@link ConstructionManager#getInstance() } may be used
     * @return The ConstructionManager 
     */
    public ConstructionManager getConstructionManager();
    
  
    
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
    
    public SchematicPlacement loadSchematic(File schematicFile) throws IOException ;
    
    
    
}
