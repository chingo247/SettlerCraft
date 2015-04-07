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
package com.chingo247.settlercraft.structure;

import com.chingo247.settlercraft.persistence.entities.world.CuboidDimension;
import com.chingo247.settlercraft.world.Direction;
import com.chingo247.settlercraft.world.SettlerCraftWorld;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Chingo
 */
public class StructureComplex extends SimpleStructure {

    private List<StructureComplex> substructures;

    public StructureComplex(Long id, String name, SettlerCraftWorld world, Direction direction, CuboidDimension dimension) {
        super(id, name, world, direction, dimension);
        this.substructures = new ArrayList<>();
    }

    public StructureComplex(String name, SettlerCraftWorld world, Direction direction, CuboidDimension dimension) {
        this(null, name, world, direction, dimension);
    }
    
    

    public List<StructureComplex> getSubstructures() {
        return substructures;
    }

    public boolean add(StructureComplex e) {
        return substructures.add(e);
    }

    public boolean remove(StructureComplex e) {
        return substructures.remove(e);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.getId());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SimpleStructure other = (SimpleStructure) obj;
        if (!Objects.equals(this.getId(), other.getId())) {
            return false;
        }
        return true;
    }

}
