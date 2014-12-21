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
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author Chingo
 */
@Entity(name = "StructureComplex")
public class StructureComplex implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    private State state;
    private String name;
    private Double value;
    private Set<SubStructure> subStructures;
    
    
    protected StructureComplex() {}

    StructureComplex(String name, Double value) {
        this.state = State.INITIALIZING;
        this.name = name;
        this.value = value;
        this.subStructures = new HashSet<>();
    }
    
   
    public boolean isOnStructure(World world, Vector pos) {
        throw new UnsupportedOperationException(); // ALSO CHECK SUBSTRUCTURES!
    }
    
    public boolean isOwner(Player player) {
        throw new UnsupportedOperationException();
    }
    
    public boolean isMember(Player player) {
        throw new UnsupportedOperationException();
    }

    
}
