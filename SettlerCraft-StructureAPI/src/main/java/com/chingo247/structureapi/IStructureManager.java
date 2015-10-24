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

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.structureapi.model.structure.Structure;
import com.chingo247.structureapi.plan.IStructurePlan;
import com.chingo247.structureapi.plan.placement.Placement;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public interface IStructureManager {
    
    void checkWorldRestrictions(CuboidRegion region) throws RestrictionException;
    
    /* Creates a structure with the provided plan
     * @param plan The StructurePlan
     * @param world The world
     * @param position The position
     * @param direction The direction of the player
     * @return The Structure that has been created
     */
    public Structure createStructure(IStructurePlan plan, Vector position, Direction direction) throws Exception;
    
    /**
     * Creates a structure with the provided plan
     * @param plan The StructurePlan
     * @param world The world
     * @param position The position
     * @param direction The direction of the player
     * @param owner The player that will be assigned as MASTER owner of this structure
     * @return The Structure that has been created
     */
    public Structure createStructure(IStructurePlan plan, Vector position, Direction direction, UUID owner) throws Exception;
    
     /**
     * Creates a structure with the provided placement
     * @param placement The Placement
     * @param world The world
     * @param position The position
     * @param direction The direction of the player
     * @return The Structure that has been created
     */
    public Structure createStructure(Placement placement, Vector position, Direction direction) throws Exception;
    
    /**
     * Creates a structure with the provided plan
     * @param placement The Placement
     * @param world The world
     * @param position The position
     * @param direction The direction of the player
     * @param owner The player that will be assigned as MASTER owner of this structure
     * @return The Structure that has been created
     */
    public Structure createStructure(Placement placement, Vector position, Direction direction, UUID owner) throws Exception;
    
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
    public Structure createSubstructure(Structure structure, IStructurePlan plan, Vector position, Direction direction, UUID owner) throws Exception;
    
     /**
     * Creates a substructure for given structure with the provided plan
     * @param structure The structure to add the created structure to
     * @param plan The StructurePlan
     * @param world The world
     * @param position The position
     * @param direction The direction of the player
     * @return The Structure that has been created
     */
    public Structure createSubstructure(Structure structure, IStructurePlan plan, Vector position, Direction direction) throws Exception;
    
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
    public Structure createSubstructure(Structure structure, Placement placement, Vector position, Direction direction, UUID owner) throws Exception;
    
     /**
     * Creates a substructure for given structure with the provided placement
     * @param structure The structure to add the created structure to
     * @param placement The Placement
     * @param world The world
     * @param position The position
     * @param direction The direction of the player
     * @return The Structure that has been created
     */
    public Structure createSubstructure(Structure structure, Placement placement, Vector position, Direction direction) throws Exception;
    
    void checkStructureRestrictions(CuboidRegion region) throws StructureRestrictionException;
    
    void checkStructureRestrictions(Player player, CuboidRegion region) throws StructureRestrictionException;
    
    void checkStructurePlacingRestrictions(Player player, CuboidRegion region) throws RestrictionException;
    
    void checkStructurePlacingRestrictions(CuboidRegion region) throws RestrictionException;
    
    void checkStructurePlacingRestrictions(Player player, CuboidRegion affectArea, Vector placingPoint) throws RestrictionException;
    
    void checkStructurePlacingRestrictions(CuboidRegion affectArea, Vector placingPoint) throws RestrictionException;
    
}
