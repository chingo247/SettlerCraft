
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
package com.chingo247.settlercraft.structureapi.persistence.legacy;

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
