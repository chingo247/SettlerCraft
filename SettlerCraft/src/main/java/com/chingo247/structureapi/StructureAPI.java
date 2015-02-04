package com.chingo247.structureapi;

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

import com.chingo247.structureapi.restriction.StructureRestriction;
import com.chingo247.structureapi.plan.StructurePlan;
import com.chingo247.settlercraft.world.Direction;
import com.chingo247.structureapi.exception.StructureException;
import com.chingo247.structureapi.plan.placement.Placement;
import com.chingo247.structureapi.regions.CuboidDimensional;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.World;

/**
 *
 * @author Chingo
 */
public interface StructureAPI {
    
    public Structure getStructure(long id);
    
    public boolean overlaps(CuboidDimensional cuboid);
    
    public Structure create(StructurePlan plan, World world, Vector position, Direction direction) throws StructureException;
    
    public Structure create(Placement placement, World world, Vector position, Direction direction) throws StructureException;
    
    public void addRestriction(StructureRestriction restriction);
    
    public StructurePlan getStructurePlan(String id);
    
    
}
