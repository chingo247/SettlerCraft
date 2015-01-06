
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
package com.chingo247.settlercraft.structureapi.structure.plan;

import com.chingo247.settlercraft.structureapi.structure.schematic.Schematic;
import com.chingo247.settlercraft.structureapi.structure.regions.CuboidDimension;
import com.chingo247.settlercraft.structureapi.structure.schematic.SchematicData;
import com.chingo247.settlercraft.structureapi.world.Direction;
import com.chingo247.settlercraft.util.SchematicUtil;
import com.chingo247.settlercraft.util.WorldUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import java.io.File;

/**
 *
 * @author Chingo
 */
public class PlaceableSchematic implements PlaceableDirectional{
    
    private SchematicData schematic;
    private Vector position;
    private Direction direction;
    private int rotation;

    public PlaceableSchematic(File schematic, Direction direction, Vector position) {
        this.schematic = getSchematicData();
        this.position = position;
        this.direction = direction;
    }

    public PlaceableSchematic(File schematic, Vector position) {
        this(schematic,Direction.EAST, position); // Default direction is EAST
    }
    
    public PlaceableSchematic(File schematic) {
        this(schematic, Vector.ZERO); // Default position is (0,0,0) xyz
    }
    
    private SchematicData getSchematicData() {
        // Check if available
        // Not? Then load the schematic and store the result
        throw new UnsupportedOperationException();
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public Vector getRelativePosition() {
        return position;
    }

    @Override
    public CuboidDimension getCuboidDimension() {
         Vector end = getPoint2Right(position, direction, new Vector(
                schematic.getWidth(),
                schematic.getHeight(),
                schematic.getLength())
        );
        return  new CuboidDimension(position, end);
    }
    
     private Vector getPoint2Right(Vector point1, Direction direction, Vector size) {
        switch (direction) {
            case EAST:
                return point1.add(size.subtract(1, 1, 1));
            case SOUTH:
                return point1.add(-(size.getBlockZ() - 1), size.getBlockY() - 1, (size.getBlockX() - 1));
            case WEST:
                return point1.add(-(size.getBlockX() - 1), size.getBlockY() - 1, -(size.getBlockZ() - 1));
            case NORTH:
                return point1.add((size.getBlockZ() - 1), size.getBlockY() - 1, -(size.getBlockX() - 1));
            default:
                throw new AssertionError("unreachable");
        }
    }

     /**
      * Flips the Schematic, note that this method will fake the flip operation and only sets the direction of this Schematic internally as opposed to other flip operations where huge amount of blocks
      * are swapped. This method is therefore a VERY LIGHT operation.
      * @param direction 
      */
    @Override
    public void flip(Direction direction) {
        switch(direction) {
            case EAST: break;
            case SOUTH: rotation += 90; break;
            case WEST: rotation += 180; break;
            case NORTH: rotation += 270; break;
            default:
                throw new AssertionError("unreachable");
        }
        // If the amount is bigger than 360, then remove turn back 360
        if(rotation >= 360) {
            rotation  =- 360;
        }
        this.direction = WorldUtil.getDirection(rotation);
    }
    
}
