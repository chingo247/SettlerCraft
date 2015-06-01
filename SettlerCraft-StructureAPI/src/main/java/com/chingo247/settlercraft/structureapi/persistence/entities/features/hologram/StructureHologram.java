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
package com.chingo247.settlercraft.structureapi.persistence.entities.features.hologram;

import com.chingo247.settlercraft.structureapi.structure.Structure;

/**
 *
 * @author Chingo
 */
public class StructureHologram {
    
    private final int relativeX;
    private final int relativeY;
    private final int relativeZ;
    
    private final Structure structure;
    
    StructureHologram(StructureHologramNode hologramNode, Structure structure) {
        this.relativeX = hologramNode.getRelativeX();
        this.relativeY = hologramNode.getRelativeY();
        this.relativeZ = hologramNode.getRelativeZ();
        this.structure = structure;
    }

    public Structure getStructure() {
        return structure;
    }
    
    public int getRelativeX() {
        return relativeX;
    }

    public int getRelativeY() {
        return relativeY;
    }

    public int getRelativeZ() {
        return relativeZ;
    }
    
    
    
}
