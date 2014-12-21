/*
 * Copyright (C) 2014 Chingo
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
package com.chingo247.settlercraft.structureapi.structure.complex;

import com.chingo247.settlercraft.structureapi.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.settlercraft.structureapi.world.Dimension;
import com.chingo247.settlercraft.structureapi.world.Direction;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;

/**
 *
 * @author Chingo
 */
public interface StructureAPI {
    
    public StructureComplex create(Player player, StructurePlan plan, World world, Vector position, Direction direction);
    
    public boolean overlaps(World world, Dimension dimension);
    
    public void build(Player player, StructureComplex structure, boolean force);
    
    public void demolish(Player player, StructureComplex structure, boolean force);
    
    public void stop(Player player, StructureComplex structure, boolean force);
}
