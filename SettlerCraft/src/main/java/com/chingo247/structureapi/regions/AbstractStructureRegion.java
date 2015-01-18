
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
package com.chingo247.structureapi.regions;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import java.util.Set;


/**
 *
 * @author Chingo
 * @param <T> WorldEdit Region Type
 */
public abstract class AbstractStructureRegion<T extends Region> implements CuboidDimensional {
    
    private final T region;

    public AbstractStructureRegion(T region) {
        Preconditions.checkNotNull(region);
        this.region = region;
    }

    public Vector getCenter() {
        return region.getCenter();
    }
    
    public int getArea() {
        return region.getArea();
    }
    
    public Vector getMin() {
        return region.getMinimumPoint();
    }
    
    public Vector getMax() {
        return region.getMaximumPoint();
    }
    
    public Set<Vector2D> getChunks() {
        return region.getChunks();
    }
    
    public World getWorld() {
       
        return region.getWorld();
    }

    
}
