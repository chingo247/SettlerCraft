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

import com.chingo247.settlercraft.structureapi.structure.Structure.State;
import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 *
 * @author Chingo
 */
@Embeddable
public class StructureProperties implements Serializable {
    
    private State state;
    private String name;
    private Double value;

    /**
     * JPA constructor
     */
    protected StructureProperties() {}

    StructureProperties(State state, String name, Double value) {
        this.state = state;
        this.name = name;
        this.value = value;
    }
    
    
    
    
    
}
