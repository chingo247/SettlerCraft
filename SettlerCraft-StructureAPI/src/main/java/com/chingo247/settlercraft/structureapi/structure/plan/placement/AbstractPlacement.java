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
package com.chingo247.settlercraft.structureapi.structure.plan.placement;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.PlacementOptions;
import com.chingo247.settlercraft.structureapi.util.WorldUtil;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;

/**
 *
 * @author Chingo
 * @param <T>
 */
public abstract class AbstractPlacement<T extends PlacementOptions> implements Placement<T>, RotationalPlacement<T> {
    
    protected final Vector position;
    private Direction direction;
    private int rotation = -90;
    private int width;
    private int height;
    private int length;

    public AbstractPlacement(int width, int height, int length) {
        this(Direction.EAST, Vector.ZERO, width, height, length);
    }

    
    public AbstractPlacement(Direction direction, Vector relativePosition, int width, int height, int length) {
        this.direction = direction;
        this.rotation = WorldUtil.getYaw(direction);
        this.position = relativePosition;
        this.length = length;
        this.height = height;
        this.width = width;
    }
    
    @Override
    public CuboidRegion getCuboidRegion() {
        return new CuboidRegion(Vector.ZERO, new Vector(width, height, length));
    }

    @Override
    public Vector getPosition() {
        return position;
    }

    @Override
    public synchronized void rotate(Direction direction) {
//        if(((direction == Direction.EAST || direction == Direction.WEST) && (this.direction == Direction.NORTH || this.direction == Direction.NORTH))
//                || ((direction == Direction.NORTH || direction == Direction.SOUTH) && (this.direction == Direction.WEST || this.direction == Direction.EAST))) {
//            int temp = width;
//            width = length;
//            length = temp;
//        }
        
        
        switch(direction) {
            case EAST:
                break;
            case WEST: 
                rotation += 180;
                break;
            case NORTH:
                rotation -= 90;
                break;
            case SOUTH:
                rotation += 90;
            break;
            default:break;
        }
        // If the amount is bigger than 360, then remove turn back 360
        if (rotation >= 360) {
            rotation = - 360;
        }
        this.direction = WorldUtil.getDirection(rotation);
    }
    
     @Override
    public void rotate(int rotation) {
        rotate(WorldUtil.getDirection(rotation));
    }


    @Override
    public synchronized  void move(Vector offset) {
        this.position.add(offset);
    }

    @Override
    public Vector getSize() {
        return new Vector(width, height, length);
    }
    
    
    @Override
    public Direction getDirection() {
        return direction;
    }
    
    @Override
    public int getRotation() {
        return WorldUtil.getYaw(direction);
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public int getWidth() {
        return width;
    }
    
}
