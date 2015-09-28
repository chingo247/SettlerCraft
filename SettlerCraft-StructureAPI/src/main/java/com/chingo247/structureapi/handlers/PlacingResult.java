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
package com.chingo247.structureapi.handlers;

import com.chingo247.structureapi.model.structure.Structure;

/**
 *
 * @author Chingo
 */
class PlacingResult {
    
    private Structure parentStructure;
    private boolean canPlace;

    public PlacingResult(boolean canPlace) {
        this.canPlace = canPlace;
    }

    public void setParentStructure(Structure parentStructure) {
        this.parentStructure = parentStructure;
    }
    
    public Structure getParentStructure() {
        return parentStructure;
    }
    
    public boolean hasParentStructure() {
        return parentStructure != null;
    }

    public boolean canPlace() {
        return canPlace;
    }
    
    
    
    
    
}
