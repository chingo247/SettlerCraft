
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
package com.chingo247.settlercraft.structure.placement;

import com.chingo247.settlercraft.model.util.WorldUtil;
import com.chingo247.settlercraft.model.persistence.entities.world.CuboidDimension;
import java.util.Objects;
import java.util.UUID;

/**
 *
 * @author Chingo
 */
public abstract class AbstractPlacement implements Placement {
    
    private final UUID id;

    public AbstractPlacement() {
        this.id = UUID.randomUUID();
    }

    /**
     * A unique id generated for this placement. This will change every server startup...
     * @return The unique id
     */
    @Override
    public final UUID getId() {
        return id;
    }
    
    @Override
    public final int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public void rotate(int yaw) {
        if(!(yaw % 90 == 0)) throw new IllegalArgumentException("Value must be a multiple of 90");
        rotate(WorldUtil.getDirection(yaw));
    }
    
    public CuboidDimension getCuboidDimension() {
        return new CuboidDimension(getMinPosition(), getMaxPosition());
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Placement other = (Placement) obj;
        if (!Objects.equals(this.id, other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public CuboidDimension getDimension() {
        return new CuboidDimension(getMinPosition(), getMaxPosition());
    }
    
    
    
}
