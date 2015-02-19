
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
package com.chingo247.settlercraft.structure.plan.placement;

import com.chingo247.settlercraft.structure.regions.CuboidDimension;
import com.chingo247.settlercraft.construction.options.Options;
import com.chingo247.settlercraft.world.Direction;
import static com.chingo247.settlercraft.world.Direction.EAST;
import static com.chingo247.settlercraft.world.Direction.NORTH;
import static com.chingo247.settlercraft.world.Direction.SOUTH;
import static com.chingo247.settlercraft.world.Direction.WEST;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;

/**
 *
 * @author Chingo
 */
public class StructureLot extends Placement{
    
    public final Vector position;
    public int width, height, length;

    public StructureLot(int width, int height, int length) {
        this(Vector.ZERO, width, height, length);
    }

    public StructureLot(Vector position, int width, int height, int length) {
        Preconditions.checkArgument(width > 0);
        Preconditions.checkArgument(height > 0);
        Preconditions.checkArgument(length > 0);
        this.position = position;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public int getHeight() {
        return height;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public Vector getRelativePosition() {
        return position;
    }

    @Override
    public CuboidDimension getCuboidDimension() {
        return new CuboidDimension(position, new Vector(width, height, length));
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void place(EditSession editSession, Vector pos, Options options) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
