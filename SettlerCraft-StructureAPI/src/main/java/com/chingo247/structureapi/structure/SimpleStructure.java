/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.structureapi.structure;

import com.chingo247.structureapi.persistence.repository.IStructure;
import com.chingo247.settlercraft.core.regions.CuboidDimension;
import com.chingo247.structureapi.world.Direction;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public class SimpleStructure implements IStructure {
    
    private final Long id;
    private final String name;
    private final Direction direction;
    private final CuboidDimension dimension;
    private final String world;
    private final UUID worldUUID;
    private final ConstructionStatus status;
    private final State state;

    private final Date createdAt;
    private final Date removedAt;

    SimpleStructure(Long id, String name, CuboidDimension dimension, String world, UUID worldUUID, Direction direction, ConstructionStatus status, State state, Date createdAt, Date removedAt) {
        this.id = id;
        this.name = name;
        this.dimension = dimension;
        this.world = world;
        this.worldUUID = worldUUID;
        this.status = status;
        this.state = state;
        this.createdAt = createdAt;
        this.removedAt = removedAt;
        this.direction = direction;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Direction getDirection() {
        return direction;
    }

    public CuboidDimension getDimension() {
        return dimension;
    }

    public String getWorld() {
        return world;
    }

    public UUID getWorldUUID() {
        return worldUUID;
    }

    public ConstructionStatus getConstructionStatus() {
        return status;
    }

    public State getState() {
        return state;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getDeletedAt() {
        return removedAt;
    }
    
}
