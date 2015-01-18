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
package com.chingo247.structureapi;

import com.chingo247.structureapi.plan.StructurePlan;
import com.chingo247.structureapi.plan.schematic.Schematic;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.util.List;

/**
 *
 * @author Chingo
 */
public interface Structure {
    
    public long getId();
    public String getName();
    public void setName(String name);
    public double getValue();
    public void setValue(double value);
    public World getWorld();
    public CuboidRegion getCuboidRegion();
    public Structure getParent();
    public List<Structure> getSubStructures();
    public Schematic getSchematic();
    public StructurePlan getStructurePlan();
    public List<StructureMember> getMembers();
    public List<StructureOwner> getOwners();
    
    public void build(boolean force);
    public void stop(boolean force);
    public void demolish(boolean force);
    
    
}
