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
package com.chingo247.settlercraft.structureapi.structure.restriction;

import com.chingo247.settlercraft.structureapi.exception.StructureException;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.util.Objects;

/**
 * StructureRestriction class is used to determine if a Structure may be build in a certain area
 * @author Chingo
 */
public abstract class StructureRestriction {
    
    private String message;
    private final String plugin;
    private final String restrictionName;

    /**
     * Constructor.
     * 
     * @param plugin The name of plugin to register this restriction
     * @param restriction The name of the restriction
     * @param message What to tell the violater?
     */
    public StructureRestriction(String plugin, String restriction, String message) {
        this.plugin = plugin;
        this.restrictionName = restriction;
        this.message = message;
    }

    public String getPlugin() {
        return plugin;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    
    
    public String getMessage() {
        return message;
    }
    
     /**
     * Used to check if a Structure may be build on a specified location.
     * @param whoPlaces The player who places the structure, value could be null
     * @param world
     * @param affectedArea
     * @return should return true if the action is approved
     */
    public abstract boolean evaluate(Player whoPlaces, World world, CuboidRegion affectedArea);
    
    /**
     * Used to check if a Structure may be build on a specified location.
     * @param whoPlaces
     * @param world
     * @param affectedArea
     * @throws com.chingo247.settlercraft.structureapi.exception.StructureException
     */
    public final void check(Player whoPlaces, World world, CuboidRegion affectedArea) throws StructureException {
        if(!evaluate(whoPlaces, world, affectedArea)) {
            throw new StructureException(message);
        }
    }

   

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 43 * hash + Objects.hashCode(this.plugin);
        hash = 43 * hash + Objects.hashCode(this.restrictionName);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StructureRestriction other = (StructureRestriction) obj;
        if (!Objects.equals(this.plugin, other.plugin)) {
            return false;
        }
        if (!Objects.equals(this.restrictionName, other.restrictionName)) {
            return false;
        }
        return true;
    }
    
    
    
    
}
