
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
package com.chingo247.settlercraft.structure.placement;

import com.chingo247.settlercraft.structure.construction.options.Options;
import com.chingo247.settlercraft.structure.generators.CuboidGenerator;
import com.chingo247.settlercraft.persistence.entities.world.CuboidDimension;
import com.chingo247.settlercraft.world.Direction;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;

/**
 *
 * @author Chingo
 */
public class GeneratedCuboid extends GeneratedPlacement<CuboidGenerator>{

    private int length;
    private int width;

    public GeneratedCuboid(CuboidGenerator generator, int width, int length) {
        this(generator, width, length, Vector.ZERO);
    }
    
    
    
    public GeneratedCuboid(CuboidGenerator generator, int width, int length, Vector position) {
        super(generator, position);
        this.length = length;
        this.width = width; 
    }

    @Override
    public CuboidDimension getCuboidDimension() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public void place(EditSession editSession, Vector pos, Options options) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
