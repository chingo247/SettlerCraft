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
package com.chingo247.settlercraft.structure;

import com.chingo247.settlercraft.regions.CuboidDimensional;
import com.chingo247.settlercraft.structure.construction.options.Options;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.world.Direction;
import com.chingo247.settlercraft.world.SettlerCraftWorld;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.entity.Player;

/**
 *
 * @author Chingo
 */
public interface Structure extends CuboidDimensional {
    
    public Long getId();
    
    public SettlerCraftWorld getWorld();
    
    public String getName();
    
    public Direction getDirection();
    
    public StructurePlan getPlan();
    
    public void setState(State state);
    
    public void build(Player player, Options options, boolean force);
    
    public void build(EditSession session, Options options, boolean force);
    
    public void demolish(EditSession session, Options options, boolean force);
    
    public void demolish(Player player, Options options, boolean force);
    
    public void stop(boolean force);
    
    public void save();
    
    public ConstructionStatus getConstructionStatus();
    
    public State getState();
    
}
