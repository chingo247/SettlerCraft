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
package com.chingo247.structureapi.persistence.repository;

import com.chingo247.settlercraft.core.regions.CuboidDimension;
import com.chingo247.structureapi.structure.ConstructionStatus;
import com.chingo247.structureapi.structure.State;
import com.chingo247.structureapi.world.Direction;
import java.util.Date;

/**
 *
 * @author Chingo
 */
public interface IStructure {
    
    public Long getId();
    public String getName();
    public String getWorld();
    public Direction getDirection();
    public CuboidDimension getDimension();
    public ConstructionStatus getConstructionStatus();
    public State getState();
    public Date getCreatedAt();
    public Date getDeletedAt();
    
}
