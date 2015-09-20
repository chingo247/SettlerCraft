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
package com.chingo247.structureapi.model.structure;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.model.World;
import com.chingo247.settlercraft.core.model.interfaces.IWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.Date;
import org.neo4j.graphdb.Node;

/**
 * As opposed to the {@link StructureNode} this unmodifable structure has all it's properties loaded. 
 * None of the opertions of this class have to be executed within a transaction
 * @author Chingo
 */
public class Structure extends AStructure {
    
    private Long id;
    private String name;
    private Vector origin;
    private ConstructionStatus status;
    private double price;
    private Direction direction;
    
    private CuboidRegion cuboidRegion;
    private Date completedAt;
    private Date createdAt;
    private Date deletedAt;
    private IWorld world;
    

    public Structure(Node structureNode) {
        this(new StructureNode(structureNode));
    }
    
    public Structure(StructureNode structure) {
        super(structure.getNode());
        this.id = structure.getId();
        this.name = structure.getName();
        this.origin = structure.getOrigin();
        this.status = structure.getStatus();
        this.price = structure.getPrice();
        this.cuboidRegion = structure.getCuboidRegion();
        this.direction = structure.getDirection();
        this.deletedAt = structure.getDeletedAt();
        this.createdAt = structure.getCreatedAt();
        this.completedAt = structure.getCompletedAt();
        this.world = new World(structure.getWorld());
    }
    
    
    /**
     * Gets the id of the structure
     *
     * @return The id of the structure
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Gets the name of the structure
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the origin of this Structure
     *
     * @return The position
     */
    @Override
    public Vector getOrigin() {
        return origin;
    }

    /**
     * The region this structure overlaps
     * @return The region
     */
    @Override
    public CuboidRegion getCuboidRegion() {
        return cuboidRegion;
    }

    /**
     * Gets the value/price of this structure
     *
     * @return The value/price of this structure
     */
    @Override
    public double getPrice() {
        return price;
    }

    /**
     * Gets the direction in which this structure is oriented
     * @return the direction
     */
    @Override
    public Direction getDirection() {
        return direction;
    }

    /**
     * The current construction status of this structure
     *
     * @return The construction status
     */
    public ConstructionStatus getConstructionStatus() {
        return status;
    }

    /**
     * Gets when this structure was completed, may return null
     *
     * @return The date of completion
     */
    public Date getCompletedAt() {
        return completedAt;
    }

    /**
     * Gets when this structure was created
     *
     * @return The date this structure was created
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the date when this structure was removed. may return null
     *
     * @return The date of removal
     */
    public Date getDeletedAt() {
        return deletedAt;
    }

    @Override
    public ConstructionStatus getStatus() {
        return status;
    }

    @Override
    public IWorld getWorld() {
        return world;
    }
  
    
}
