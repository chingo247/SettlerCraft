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

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public interface IPlacingValidator {
    
    /**
     * Checks world restrictions such as if the region is within the 1 and the max build height of the world
     * @param world The world
     * @param region The affected region
     */
    void checkWorldRestrictions(World world, CuboidRegion region) throws RestrictionException;
    
    /**
     * Checks if the player is allowed to place something that will affect the given area.
     * Protection plugins such as worldguard and any other plugin that can submit a {@link StructureRestriction} to {@link StructureAPI#addRestriction(com.chingo247.structureapi.StructureRestriction) 
     * will be checked.
     * 
     * @param world The world
     * @param region The affected region
     * @param player The player
     */
    void checkStructureRestrictions(World world, CuboidRegion region, Player player) throws RestrictionException;
    
    /**
     * Checks if affected area will violate any structure restrictions that are applied on the given area
     * Protection plugins such as worldguard and any other plugin that can submit a {@link StructureRestriction} to {@link StructureAPI#addRestriction(com.chingo247.structureapi.StructureRestriction) 
     * will be checked.
     * 
     * @param world The world
     * @param region The affected region
     * @param player The player
     */
    void checkStructureRestrictions(World world, CuboidRegion region) throws RestrictionException;
    
//    /**
//     * Checks if the given region will overlap any 'plots' (e.g. structures, constructionzones, etc), if so it will check the policy
//     * for those plots are violated. Structure for instance should only allow overlap if the structure completely fits within another structure.
//     * Also substructuring is only allowed when the structure is owned by the given player.
//     * 
//     * <b>NOTE: REQUIRES AN ACTIVE {@link Transaction}<b/> 
//     * @param world
//     * @param region
//     * @param player 
//     */
//    void checkStructureOverlapRestrictions(World world, CuboidRegion region, Player player) throws RestrictionException;
//    
    /**
     * This method will enforce restrictions from the player point of view. This method will first get the smallest structure on the give position.
     * If the affected area fits within this structure and it does not overlap any other structures than the affected area is allowed to be edited
     * 
     * <b>NOTE: REQUIRES AN ACTIVE {@link Transaction}<b/> 
     * @param world
     * @param region
     * @param player 
     */
    void checkStructurePlacingRestrictions(World world, CuboidRegion affectArea, Vector placingPoint, Player player) throws RestrictionException;
    
    /**
     * This method will enforce restrictions from the player point of view. This method will first get the smallest structure on the give position.
     * If the affected area fits within this structure and it does not overlap any other structures than the affected area is allowed to be edited
     * 
     * <b>NOTE: REQUIRES AN ACTIVE {@link Transaction}<b/> 
     * @param world
     * @param region
     * @param player 
     */
    void checkStructurePlacingRestrictions(World world, CuboidRegion affectArea, Vector placingPoint) throws RestrictionException;
    
//    /**
//     * Checks if the given region will overlap any 'plots' (e.g. structures, constructionzones, etc), if so it will check the policy
//     * for those plots are violated. Structure for instance should only allow overlap if the structure completely fits within another structure.
//     * 
//     * <b>NOTE: REQUIRES AN ACTIVE {@link Transaction}<b/> 
//     * @param world
//     * @param region
//     */
//    void checkStructureOverlapRestrictions(World world, CuboidRegion region) throws RestrictionException;
    
}
