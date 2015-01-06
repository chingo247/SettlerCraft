
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

import com.chingo247.settlercraft.structureapi.structure.generators.PolygonalGenerator;
import com.chingo247.settlercraft.structureapi.structure.regions.CuboidDimension;
import com.chingo247.settlercraft.structureapi.world.Direction;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import java.util.List;

/**
 *
 * @author Chingo
 */
public class PlaceableGeneratedPolygonal2D extends PlaceableGenerated<PolygonalGenerator> {

    private Polygonal2DRegion region;

    public PlaceableGeneratedPolygonal2D(PolygonalGenerator generator, Polygonal2DRegion region) {
        super(generator);

    }

    @Override
    public CuboidDimension getCuboidDimension() {
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();
        Vector center = region.getCenter();
        
        int width = Math.abs(min.getBlockX() - center.getBlockX()) + Math.abs(center.getBlockX() - max.getBlockX());
        int length = Math.abs(min.getBlockZ() - center.getBlockZ()) + Math.abs(center.getBlockZ() - max.getBlockZ());
        
        Vector minu = new Vector(center.getBlockX() - (width/2), region.getMinimumY(), center.getBlockZ() - (length / 2));
        Vector maxi = new Vector(center.getBlockX() + (width/2), region.getMaximumY(), center.getBlockZ() + (length / 2));
        return new CuboidDimension(minu, maxi);
    }

    @Override
    public void flip(Direction direction) {
        Polygonal2DRegion copy;
        switch (direction) {
            case EAST:
                return;
            case WEST:
                copy = new Polygonal2DRegion();
                for (BlockVector2D vector : region.getPoints()) { 
                    int temp = vector.getBlockX();
                    vector.setX(-1 * vector.getBlockZ());
                    vector.setZ(-1 * temp);
                    copy.addPoint(vector);
                }
                region = copy;
                return;
            case NORTH:
                copy = new Polygonal2DRegion();
                for (BlockVector2D vector : region.getPoints()) { 
                    int temp = vector.getBlockX();
                    vector.setX(vector.getBlockZ());
                    vector.setZ(-1 * temp);
                    copy.addPoint(vector);
                }
                region = copy;
                return;
            case SOUTH:
                copy = new Polygonal2DRegion();
                for (BlockVector2D vector : region.getPoints()) { 
                    int temp = vector.getBlockX();
                    vector.setX(-1 * vector.getBlockZ());
                    vector.setZ(temp);
                    copy.addPoint(vector);
                }
                region = copy;
                return;
            default:
                throw new AssertionError("unreachable");
        }
    }

}
