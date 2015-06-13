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
package com.chingo247.settlercraft.structureapi.model.owner;

import com.chingo247.settlercraft.structureapi.model.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureOwner;
import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureOwnership;
import org.neo4j.graphdb.Relationship;

/**
 * Defines a relation between a Structure and an (Structure)Owner. All operations in this class require an active transaction.
 * @author Chingo
 */
public class StructureOwnershipRelation implements IStructureOwnership {
    
    private final StructureNode structure;
    private final IStructureOwner owner;
    private final Relationship rel;

    public StructureOwnershipRelation(StructureNode structure, IStructureOwner owner, Relationship relation) {
        this.structure = structure;
        this.owner = owner;
        this.rel = relation;
    }
    
    

    @Override
    public StructureNode getStructure() {
        return structure;
    }

    @Override
    public IStructureOwner getOwner() {
        return owner;
    }

    @Override
    public StructureOwnerType getOwnerType() {
        Integer typeProp = (Integer) rel.getProperty("Type");
        StructureOwnerType t = StructureOwnerType.match(typeProp);
        return t;
    }

    @Override
    public Relationship getRelation() {
        return rel;
    }
    
}
