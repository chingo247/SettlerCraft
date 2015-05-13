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
     * @param whoPlaces
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
    public final void allow(Player whoPlaces, World world, CuboidRegion affectedArea) throws StructureException {
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
