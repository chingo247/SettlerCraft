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
    public void rotate(Direction direction) {
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
    public void move(Vector offset) {
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
