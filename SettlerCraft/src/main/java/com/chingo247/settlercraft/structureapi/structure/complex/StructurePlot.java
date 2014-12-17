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

import com.chingo247.settlercraft.structureapi.structure.Structure;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author Chingo
 */
@Entity
public class StructurePlot implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Embedded
    private Plot plot;
    
    @OneToMany
    private Set<StructureRoomPlot> roomPlots;
    
    @OneToOne
    private Structure structure;

    /**
     * JPA Constructor
     */
    protected StructurePlot() {
    }

    StructurePlot(Structure structure, Plot plot) {
        this.plot = plot;
        this.roomPlots = new LinkedHashSet<>();
        this.structure = structure;
    }
    
    
    
}
