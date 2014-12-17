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

import java.io.Serializable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author Chingo
 */
@Entity
public class StructureRoomPlot implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne
    private StructurePlot structurePlot;
    
    @Embedded
    private Plot plot;

    /**
     * JPA Constructor
     */
    protected StructureRoomPlot() {
    }

    StructureRoomPlot(Plot plot) {
        this.plot = plot;
    }
    
    
    
}
