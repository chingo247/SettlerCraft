/*
 * Copyright (C) 2014 Chingo
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
package com.chingo247.blocklogger.orientdb;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Chingo
 */
public class BlockLog implements Serializable {

    // Generating a random UUID is faster than generating a long value
    int x;
    int y;
    int z;
    int newMaterial;
    int oldMaterial;
    byte newData;
    byte oldData;
    long date;
    String world;

    /**
     * JPA Constructor
     */
    protected BlockLog() {
    }

    public BlockLog(int x, int y, int z, int oldMaterial, byte oldData, int newMaterial, byte newData, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.oldMaterial = oldMaterial;
        this.oldData = oldData;
        this.newMaterial = newMaterial;
        this.newData = newData;
        this.date = new Date().getTime();
        this.world = world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getNewMaterial() {
        return newMaterial;
    }

    public int getOldMaterial() {
        return oldMaterial;
    }

    public byte getNewData() {
        return newData;
    }

    public byte getOldData() {
        return oldData;
    }

    public long getDate() {
        return date;
    }
    
    
}
