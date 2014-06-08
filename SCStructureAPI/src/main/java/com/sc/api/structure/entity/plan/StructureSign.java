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

package com.sc.api.structure.entity.plan;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author Chingo
 */
@Embeddable
public class StructureSign implements Serializable {
    
    private final Integer sign_x;
    private final Integer sign_y;
    private final Integer sign_z;

    public StructureSign() {
        this.sign_x = 0;
        this.sign_y = 0;
        this.sign_z = 0;
    }
    
    public StructureSign(int x, int y, int z) {
        this.sign_x = x;
        this.sign_y = y;
        this.sign_z = z;
    }

    public int getX() {
        return sign_x;
    }

    public int getY() {
        return sign_y;
    }

    public int getZ() {
        return sign_z;
    }

    
    
    
}
