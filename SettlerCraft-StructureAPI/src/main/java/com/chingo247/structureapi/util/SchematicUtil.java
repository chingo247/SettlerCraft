
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
package com.chingo247.structureapi.util;


import com.chingo247.settlercraft.core.Direction;
import static com.chingo247.settlercraft.core.Direction.EAST;
import static com.chingo247.settlercraft.core.Direction.NORTH;
import static com.chingo247.settlercraft.core.Direction.SOUTH;
import static com.chingo247.settlercraft.core.Direction.WEST;
import com.chingo247.structureapi.plan.schematic.Schematic;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;


/**
 *
 * @author Chingo
 */
public class SchematicUtil {

    private SchematicUtil() {
    }

    public static CuboidRegion calculateDimension(Schematic schematic, Vector pos, Direction direction) {
        Vector size = schematic.getSize();
        Vector end = getPoint2Right(pos, direction, new BlockVector(
                size.getBlockX(),
                size.getBlockY(),
                size.getBlockZ())
        );
        CuboidRegion dimension = new CuboidRegion(pos, end);
        return dimension;
    }
    
    private static Vector getPoint2Right(Vector point1, Direction direction, Vector size) {
        switch (direction) {
            case EAST:
                return point1.add(size.subtract(1, 1, 1));
            case SOUTH:
                return point1.add(-(size.getBlockZ() - 1), size.getBlockY() - 1, (size.getBlockX() - 1));
            case WEST:
//                clipboard.rotate2D(180);
                return point1.add(-(size.getBlockX() - 1), size.getBlockY() - 1, -(size.getBlockZ() - 1));
            case NORTH:
                return point1.add((size.getBlockZ() - 1), size.getBlockY() - 1, -(size.getBlockX() - 1));
//                clipboard.rotate2D(270);

            default:
                throw new AssertionError("unreachable");
        }
    }


   

    /**
     * Creates a string from a value e.g. value > 1E3 = value/1E3 + "K" e.g. value > 1E6 = value/1E6
     * + "M"
     *
     * @param value
     * @return
     */
    public static String valueString(double value) {
        if (value < 1000) {
            return String.valueOf(value);
        } else if (value < 1E6) {
            return String.valueOf(Math.round(value / 1E3)) + "K";
        } else {
            return String.valueOf(Math.round(value / 1E6)) + "M";
        }
    }

}
