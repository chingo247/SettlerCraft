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
package com.chingo247.settlercraft.structure.restriction;

import com.chingo247.settlercraft.persistence.entities.structure.StructureType;
import com.chingo247.settlercraft.exception.StructureException;
import com.chingo247.settlercraft.structure.regions.CuboidDimensional;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;

/**
 * StructureRestriction class is used to determine if a Structure may be build in a certain area
 * @author Chingo
 */
public abstract class StructureRestriction {
    
    protected String reason;

    /**
     * Constructor.
     * @param reason The reason when to tell the player, why the structure can't be build
     */
    public StructureRestriction(String reason) {
        this.reason = reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public abstract boolean test(World world, CuboidDimensional cuboid, StructureType type);
    
    /**
     * Used to check if a Structure may be build on a specified location.
     * @param world The world of the region
     * @param cuboid
     * @param region The cuboid-region (representing the Structure)
     * @param type The type of the Structure
     * @return True if a Structure is allowed to be build upon target area
     * @throws com.chingo247.settlercraft.exception.StructureException
     */
    public final void allow(World world, CuboidDimensional cuboid, StructureType type) throws StructureException {
        if(!test(world, cuboid, type)) {
            throw new StructureException(reason);
        }
    }
    
    
    
}
