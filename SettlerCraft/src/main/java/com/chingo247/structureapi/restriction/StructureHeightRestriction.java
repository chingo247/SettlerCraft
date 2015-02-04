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
package com.chingo247.structureapi.restriction;

import com.chingo247.structureapi.entities.StructureType;
import com.chingo247.structureapi.regions.CuboidDimensional;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;

/**
 *
 * @author Chingo
 */
public class StructureHeightRestriction extends StructureRestriction {

    public StructureHeightRestriction() {
        super("Structure exceeds the world height!");
    }

    @Override
    public boolean test(World world, CuboidDimensional region, StructureType type) {
        if(region.getCuboidDimension().getMaxY() < world.getMaxY()) {
            if(region.getCuboidDimension().getMinY() > 0) {
                return true;
            } else {
                reason = "Structure is below world!";
                return false;
            }
        } else {
            return false;
        }
    }
    
}
