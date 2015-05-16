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
import com.chingo247.settlercraft.structureapi.util.WorldUtil;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;

/**
 *
 * @author Chingo
 * @param <T>
 */
public abstract class AbstractCuboidPlacement<T> implements Placement<T>, RotationalPlacement<T> {
    
    public final Vector position;
    private Direction direction;
    private int rotation = -90;

    public AbstractCuboidPlacement() {
        this(Direction.EAST, Vector.ZERO);
    }

    public AbstractCuboidPlacement(Direction direction, Vector position) {
        this.direction = direction;
        this.rotation = WorldUtil.getYaw(direction);
        
        System.out.println("Rotation in constructor: " + rotation);
        
        this.position = position;
    }

    
    @Override
    public Vector getPosition() {
        return position;
    }

    @Override
    public void rotate(Direction direction) {
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
        
        System.out.println("Current rotation: " + rotation);
        
        // If the amount is bigger than 360, then remove turn back 360
        if (rotation >= 360) {
            rotation = - 360;
        }
        this.direction = WorldUtil.getDirection(rotation);
        System.out.println("Current direction: " + direction);
        
        
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
    public CuboidRegion getCuboidRegion() {
        return new CuboidRegion(position, new Vector(getWidth(), getHeight(), getLength()));
    }
    
    @Override
    public Direction getDirection() {
        return direction;
    }
    
    @Override
    public int getRotation() {
        return WorldUtil.getYaw(direction);
    }
    
}
