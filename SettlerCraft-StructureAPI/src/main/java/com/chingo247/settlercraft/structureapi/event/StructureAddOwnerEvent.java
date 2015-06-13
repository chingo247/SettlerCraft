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
package com.chingo247.settlercraft.structureapi.event;

import com.chingo247.settlercraft.structureapi.model.owner.StructureOwnerType;
import com.chingo247.settlercraft.structureapi.model.structure.StructureNode;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class StructureAddOwnerEvent {
    
    private final UUID addedOwner;
    private final StructureNode structure;
    private final StructureOwnerType ownerType;

    public StructureAddOwnerEvent(UUID addedMember, StructureNode structure, StructureOwnerType type) {
        this.addedOwner = addedMember;
        this.structure = structure;
        this.ownerType = type;
    }

    public UUID getAddedOwner() {
        return addedOwner;
    }

    public StructureNode getStructure() {
        return structure;
    }

    public StructureOwnerType getOwnerType() {
        return ownerType;
    }
    
    
    
}
