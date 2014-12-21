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

import com.chingo247.settlercraft.structureapi.world.Dimension;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author Chingo
 */
@Entity
public class Plot implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    @Embedded
    private Dimension dimension;
    
    private String world;
    
    private Timestamp createdAt;

    /**
     * JPA Constructor.
     */
    protected Plot() {}

    /**
     * Constructor
     * @param dimension 
     */
    Plot(String world, Dimension dimension) {
        this.dimension = dimension;
        this.createdAt = new Timestamp(new Date().getTime());
    }

    public Long getId() {
        return id;
    }
    
    public Dimension getDimension() {
        return dimension;
    }

    public String getWorld() {
        return world;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

}
