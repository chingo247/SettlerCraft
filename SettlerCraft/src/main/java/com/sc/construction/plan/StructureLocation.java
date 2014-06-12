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

package com.sc.construction.plan;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

/**
 *
 * @author Chingo
 */
@Embeddable
public class StructureLocation implements Serializable {
    
    private final Integer relativeLocX;
    private final Integer relativeLocY;
    private final Integer relativeLocZ;

    public StructureLocation() {
        this.relativeLocX = 0;
        this.relativeLocY = 0;
        this.relativeLocZ = 0;
    }
    
    public StructureLocation(int x, int y, int z) {
        this.relativeLocX = x;
        this.relativeLocY = y;
        this.relativeLocZ = z;
    }

    public int getX() {
        return relativeLocX;
    }

    public int getY() {
        return relativeLocY;
    }

    public int getZ() {
        return relativeLocZ;
    }

   

  

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StructureLocation other = (StructureLocation) obj;
        if (!Objects.equals(this.relativeLocX, other.relativeLocX)) {
            return false;
        }
        if (!Objects.equals(this.relativeLocY, other.relativeLocY)) {
            return false;
        }
        if (!Objects.equals(this.relativeLocZ, other.relativeLocZ)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.relativeLocX);
        hash = 79 * hash + Objects.hashCode(this.relativeLocY);
        hash = 79 * hash + Objects.hashCode(this.relativeLocZ);
        return hash;
    }

    
    
    
    
}
