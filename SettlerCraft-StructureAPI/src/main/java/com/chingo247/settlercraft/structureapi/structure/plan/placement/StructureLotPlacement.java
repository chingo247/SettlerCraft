
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
import com.chingo247.settlercraft.structureapi.structure.options.PlaceOptions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;

/**
 *
 * @author Chingo
 */
public class StructureLotPlacement extends AbstractCuboidPlacement<PlaceOptions> {
    
    private int width;
    private int height;
    private int length;
    
    
    public StructureLotPlacement(Vector position, int width, int height, int length) {
        super(Direction.EAST, position);
        this.width = width;
        this.length = length;
        this.height = height;
    }

    @Override
    public void place(EditSession editSession, Vector pos, PlaceOptions options) {
        // Does nothing at the moment... As StructureLot's have a different purpose
    }

    @Override
    public String getTypeName() {
        return PlacementTypes.STRUCTURE_LOT;
    }

    

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getLength() {
        return length;
    }

   


}
