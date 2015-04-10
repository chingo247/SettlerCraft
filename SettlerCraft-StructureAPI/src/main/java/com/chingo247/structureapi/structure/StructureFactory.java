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
package com.chingo247.structureapi.structure;

//import com.tinkerpop.blueprints.Vertex;

import com.chingo247.structureapi.SettlerCraft;
import com.chingo247.settlercraft.core.regions.CuboidDimension;
import com.chingo247.structureapi.persistence.graph.documents.StructureDocument;
import com.chingo247.structureapi.world.Direction;
import com.chingo247.structureapi.world.SettlerCraftWorld;
import com.sk89q.worldedit.Vector;


/**
 *
 * @author Chingo
 */
public class StructureFactory {

    StructureFactory() {
    }
    
    public SimpleStructure createSimple(StructureDocument v) {
        Long id = v.getKey() != null ? Long.parseLong(v.getKey()) : null;
        String name = v.getName();
        SettlerCraftWorld world = SettlerCraft.getInstance().getWorld(v.getWorld());
        Direction direction = Direction.match(v.getDirection());
        Vector min = new Vector(v.getMinX(), v.getMinY(), v.getMinZ());
        Vector max = new Vector(v.getMaxX(), v.getMaxY(), v.getMaxZ());
        CuboidDimension dimension = new CuboidDimension(min, max);
        return new SimpleStructure(id, name, world, direction, dimension);
    }
    
    public ComplexStructure createComplex(StructureDocument v) {
        throw new AssertionError("Not supported");
    }
    
    
}
