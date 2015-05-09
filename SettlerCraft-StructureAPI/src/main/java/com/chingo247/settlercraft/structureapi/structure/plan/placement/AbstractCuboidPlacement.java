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
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;

/**
 *
 * @author Chingo
 * @param <T>
 */
public abstract class AbstractCuboidPlacement<T> extends Placement<T> {
    
    public final Vector position;
    public int width, height, length;

    public AbstractCuboidPlacement(int width, int height, int length) {
        this(Vector.ZERO, width, height, length);
    }

    public AbstractCuboidPlacement(Vector position, int width, int height, int length) {
        Preconditions.checkArgument(width > 0);
        Preconditions.checkArgument(height > 0);
        Preconditions.checkArgument(length > 0);
        this.position = position;
        this.width = width;
        this.height = height;
        this.length = length;
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

    @Override
    public Vector getPosition() {
        return position;
    }

    @Override
    public void rotate(Direction direction) {
        switch(direction) {
            case EAST:
            case WEST: break;
            case NORTH:
            case SOUTH:
            int temp = width;
            width = length;
            length = temp;
        }
    }

    @Override
    public void move(Vector offset) {
        this.position.add(offset);
    }

    @Override
    public CuboidRegion getCuboidRegion() {
        return new CuboidRegion(position, new Vector(width, height, length));
    }
    
}
