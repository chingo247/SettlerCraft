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
package com.chingo247.settlercraft.structureapi.structure.complex;

import com.chingo247.settlercraft.structureapi.world.Dimension;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.World;
import java.util.List;

/**
 *
 * @author Chingo
 */
public interface Structure {

    /**
     * Gets the id of this structure, may return null if instance is not saved
     * @return The id
     */
    public Long getId();
    /**
     * Gets the name of this structure
     * @return The name of this structure
     */
    public String getName();
    
    /**
     * Gets the world of this structure
     * @return The world of this structure
     */
    public World getWorld();
    
    /**
     * Gets the dimension of this structure.
     * See {@link Dimension} for more info about dimensions
     * @return The dimension of this structure.
     */
    public Dimension getDimension();
    /**
     * Gets all the members of this structure.
     * See {@link StructureMembership} for more info about membership and permissions
     * @return The members of this structure
     */
    public List<StructureMembership> getMembers();
    
    /**
     * Gets all the owners of this structure
     * See {@link StructureOwnership} for more info about ownership and permissions
     * @return The owners of this structure
     */
    public List<StructureOwnership> getOwners();
    
    /**
     * Checks if the given dimension is within the structure
     * @param world The world of the given dimension
     * @param dimension The dimension
     * @return True if the dimension fits entirely in the structure
     */
    public boolean isWithin(World world, Dimension dimension);
    
    /**
     * Checks if the given position is within the structure
     * @param world The world of the given vector
     * @param position The position
     * @return True is the given position is within the structure
     */
    public boolean isWithin(World world, Vector position);
    
    
    
}
