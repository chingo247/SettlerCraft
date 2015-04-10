
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
package com.chingo247.structureapi.plan.placement;

import com.chingo247.structureapi.world.Direction;
import static com.chingo247.structureapi.world.Direction.EAST;
import static com.chingo247.structureapi.world.Direction.NORTH;
import static com.chingo247.structureapi.world.Direction.SOUTH;
import static com.chingo247.structureapi.world.Direction.WEST;
import com.chingo247.structureapi.structure.generators.PolygonalGenerator;
import com.chingo247.structureapi.structure.construction.options.Options;
import com.chingo247.settlercraft.core.regions.CuboidDimension;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.Polygonal2DRegion;

/**
 *
 * @author Chingo
 */
public class GeneratedPolygonal2D extends GeneratedPlacement<PolygonalGenerator> {

    private Polygonal2DRegion region;

    public GeneratedPolygonal2D(PolygonalGenerator generator, Polygonal2DRegion region) {
        this(generator, region, Vector.ZERO);
    }
    
    public GeneratedPolygonal2D(PolygonalGenerator generator, Polygonal2DRegion region, Vector position) {
        super(generator, position);
    }

    @Override
    public void rotate(Direction direction) {
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

    @Override
    public void place(EditSession editSession, Vector pos, Options options) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Vector getMinPosition() {
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();
        Vector center = region.getCenter();
        
        int width = Math.abs(min.getBlockX() - center.getBlockX()) + Math.abs(center.getBlockX() - max.getBlockX());
        int length = Math.abs(min.getBlockZ() - center.getBlockZ()) + Math.abs(center.getBlockZ() - max.getBlockZ());
        
        Vector minu = new Vector(center.getBlockX() - (width/2), region.getMinimumY(), center.getBlockZ() - (length / 2));
        return minu;
    }

    public Vector getMaxPosition() {
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();
        Vector center = region.getCenter();
        
        int width = Math.abs(min.getBlockX() - center.getBlockX()) + Math.abs(center.getBlockX() - max.getBlockX());
        int length = Math.abs(min.getBlockZ() - center.getBlockZ()) + Math.abs(center.getBlockZ() - max.getBlockZ());
        
        Vector maxi = new Vector(center.getBlockX() + (width/2), region.getMaximumY(), center.getBlockZ() + (length / 2));
        return maxi;
    }
    
    @Override
    public CuboidDimension getDimension() {
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();
        Vector center = region.getCenter();
        
        int width = Math.abs(min.getBlockX() - center.getBlockX()) + Math.abs(center.getBlockX() - max.getBlockX());
        int length = Math.abs(min.getBlockZ() - center.getBlockZ()) + Math.abs(center.getBlockZ() - max.getBlockZ());
        
        Vector minu = new Vector(center.getBlockX() - (width/2), region.getMinimumY(), center.getBlockZ() - (length / 2));
        Vector maxi = new Vector(center.getBlockX() + (width/2), region.getMaximumY(), center.getBlockZ() + (length / 2));
        return new CuboidDimension(minu, maxi);
    }

}
