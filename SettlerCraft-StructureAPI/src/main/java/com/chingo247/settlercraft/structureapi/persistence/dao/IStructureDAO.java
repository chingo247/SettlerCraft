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
package com.chingo247.settlercraft.structureapi.persistence.dao;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.World;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public interface IStructureDAO {
    
    public StructureNode find(long id);
    
    public StructureNode findRoot(long id);

    public StructureNode addStructure(String name, CuboidRegion dimension, Direction direction, double price);

    public List<StructureNode> getStructuresWithin(World world, CuboidRegion region, int limit);

    public boolean hasStructuresWithin(World world, CuboidRegion region);

    public List<StructureNode> getStructuresForSettler(UUID settler, int skip, int limit);

    public long getStructureCountForSettler(UUID settler);
    
    public void delete(Long id);
    
//    public List<StructureNode> findSiblingsWithConstructionStatus(UUID world, long id, ConstructionStatus status);
//    
//    public List<StructureNode> findSubstructuresConstructionStatus(UUID world, long id, ConstructionStatus status);
    
}
