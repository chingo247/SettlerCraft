
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
package com.chingo247.structureapi.persistence.legacy;

import com.sk89q.worldedit.Vector;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.hibernate.validator.NotNull;

/**
 *
 * @author Chingo
 */
@Embeddable
@Deprecated
public class Dimension implements Serializable {

    @NotNull
    @Column(name = "startX")
    private int minX;

    @NotNull
    @Column(name = "startY")
    private int minY;

    @NotNull
    @Column(name = "startZ")
    private int minZ;

    @NotNull
    @Column(name = "endX")
    private int maxX;

    @NotNull
    @Column(name = "endY")
    private int maxY;

    @NotNull
    @Column(name = "endZ")
    private int maxZ;

    /**
     * JPA Constructor.
     */
    protected Dimension() {
    }       

    /**
     *
     * @deprecated Use {@link com.chingo247.settlercraft.core.regions.CuboidDimension} instead
     */
    @Deprecated
    public Dimension(Vector start, Vector end) {
        this.minX = Math.min(start.getBlockX(), end.getBlockX());
        this.minY = Math.min(start.getBlockY(), end.getBlockY());
        this.minZ = Math.min(start.getBlockZ(), end.getBlockZ());
        this.maxX = Math.max(start.getBlockX(), end.getBlockX());
        this.maxY = Math.max(start.getBlockY(), end.getBlockY());
        this.maxZ = Math.max(start.getBlockZ(), end.getBlockZ());
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public void setMinZ(int minZ) {
        this.minZ = minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(int maxZ) {
        this.maxZ = maxZ;
    }

    public Vector getMinPosition() {
        return new Vector(minX, minY, minZ);
    }
    
    public Vector getMaxPosition() {
        return new Vector(maxX, maxY, maxZ);
    }
    

    @Override
    public String toString() {
        return "minX: " + minX + " minY: " + minY + " minZ: " + minZ + " maxX: " + maxX + " maxY: " + maxY + " maxZ: " + maxZ;    
    }


}
