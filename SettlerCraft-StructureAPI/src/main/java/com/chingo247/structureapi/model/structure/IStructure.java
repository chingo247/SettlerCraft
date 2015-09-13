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
import com.chingo247.settlercraft.core.model.interfaces.IWorld;
import com.chingo247.structureapi.model.structure.ConstructionStatus;
import com.chingo247.structureapi.plan.IStructurePlan;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Chingo
 */
public interface IStructure {
    
    public Long getId();
    
    public Node getNode();
    
    public String getName();
    
    public Vector getOrigin();
    
    public double getPrice();
    
    public CuboidRegion getCuboidRegion();
    
    public Direction getDirection();
    
    public ConstructionStatus getStatus();
    
    public <T extends IWorld> T getWorld();
    
    public <T extends IStructurePlan> T getStructurePlan();
    
}
