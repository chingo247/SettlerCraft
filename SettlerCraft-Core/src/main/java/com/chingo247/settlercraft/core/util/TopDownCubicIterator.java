/*
 * Copyright (C) 2015 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.settlercraft.core.util;

import com.sk89q.worldedit.Vector;
import java.util.Iterator;

/**
 *
 * @author Chingo
 */
public class TopDownCubicIterator implements Iterator<Vector>, Iterable<Vector>{
    
    private CuboidIterator iterator;
    private Vector start;

    public TopDownCubicIterator(Vector size, int cubeX, int cubeY, int cubeZ) {
        this.iterator = new CuboidIterator(size, cubeX, cubeY, cubeZ);
        this.start = new Vector(0,size.getBlockY(),0);
    }
    
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
    
    @Override
    public Vector next() {
        Vector next = iterator.next();
        return start.add(next.getBlockX(),-next.getBlockY(),next.getBlockZ()); // If I go up... I GO DOWN!
    }

    @Override
    public Iterator<Vector> iterator() {
        return this;
    }
    
    
    
}
