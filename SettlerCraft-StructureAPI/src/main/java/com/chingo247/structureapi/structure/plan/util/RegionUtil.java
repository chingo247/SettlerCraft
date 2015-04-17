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
package com.chingo247.structureapi.structure.plan.util;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;

/**
 *
 * @author Chingo
 */
public class RegionUtil {

    private RegionUtil() {
    }

    public static boolean isDimensionWithin(CuboidRegion parent, CuboidRegion child) {
        CuboidRegion p = new CuboidRegion(parent.getMinimumPoint(), parent.getMaximumPoint());
        CuboidRegion c = new CuboidRegion(child.getMinimumPoint(), child.getMaximumPoint());
        return p.getMinimumPoint().getBlockX() > c.getMinimumPoint().getBlockX()
                && p.getMinimumY() > c.getMinimumY()
                && p.getMinimumPoint().getBlockZ() > c.getMinimumPoint().getBlockZ()
                && p.getMaximumPoint().getBlockX() < c.getMaximumPoint().getBlockX()
                && p.getMaximumPoint().getBlockY() < c.getMaximumPoint().getBlockY()
                && p.getMaximumPoint().getBlockZ() < c.getMaximumPoint().getBlockZ();

    }

    /**
     * Checks whether two CuboidDimensionals overlap each other
     *
     * @param dimensionalA The dimensional
     * @param dimensianalB The other dimensional
     * @return True if the two CuboidDimensionals overlap each other
     */
    public static boolean overlaps(CuboidRegion dimensionalA, CuboidRegion dimensianalB) {
        CuboidRegion p = new CuboidRegion(dimensionalA.getMinimumPoint(), dimensionalA.getMaximumPoint());
        CuboidRegion c = new CuboidRegion(dimensianalB.getMinimumPoint(), dimensianalB.getMaximumPoint());

        Vector pMax = p.getMaximumPoint();
        Vector pMin = p.getMinimumPoint();
        
        Vector cMax = c.getMaximumPoint();
        Vector cMin = c.getMinimumPoint();
        
        return pMax.getBlockX() >= cMin.getBlockX() && pMin.getBlockX() <= cMax.getBlockX()
                && pMax.getBlockY() >= cMin.getBlockY() && pMin.getBlockY() <= cMax.getBlockY()
                && pMax.getBlockZ() >= cMin.getBlockZ() && pMin.getBlockZ() <= cMax.getBlockZ();
    }


}
