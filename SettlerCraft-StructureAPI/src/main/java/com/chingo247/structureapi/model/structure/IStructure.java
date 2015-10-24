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
package com.chingo247.structureapi.model.structure;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.structureapi.StructureException;
import com.chingo247.structureapi.model.plot.IPlot;
import com.chingo247.structureapi.plan.IStructurePlan;
import com.sk89q.worldedit.Vector;

/**
 *
 * @author Chingo
 */
public interface IStructure extends IPlot {
    
    public Long getId();
    
    public String getName();
    
    public Vector getOrigin();
    
    public double getPrice();
    
    public Direction getDirection();
    
    /**
     * The construction status
     * @return 
     */
    public ConstructionStatus getStatus();
    
    /**
     * Gets the structure plan
     * @return The structure plan
     * @throws StructureException if structure doesnt have a plan 
     */
    public IStructurePlan getStructurePlan() throws StructureException;
    
}
