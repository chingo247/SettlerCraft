/*
 * Copyright (C) 2014 Chingo247
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
package com.chingo247.structureapi.main.plan.schematic;

import com.chingo247.structureapi.main.util.SchematicUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Chingo
 */
public class Schematic implements Serializable {

    private final long checkSum;
    private final int s_length;
    private final int s_height;
    private final int s_width;
    private final int blocks;
    private final File schematic;

    private Schematic(File schematic, int width, int height, int length, long checksum, int blocks) throws IOException {
        this.checkSum = checksum;
        this.schematic = schematic;
        this.s_width = width;
        this.s_height = height;
        this.s_length = length;
        this.blocks = blocks;
    }

    public long getCheckSum() {
        return checkSum;
    }

    public int getBlocks() {
        return blocks;
    }
    
    public Vector getSize() {
        return new Vector(s_width, s_height, s_length);
    }

    public CuboidClipboard getClipboard() throws IOException, DataException {
        return SchematicFormat.MCEDIT.load(schematic);
    }

    public Integer getWidth() {
        return s_width;
    }

    public Integer getHeight() {
        return s_height;
    }

    public Integer getLength() {
        return s_length;
    }

    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (this.checkSum ^ (this.checkSum >>> 32));
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
        final Schematic other = (Schematic) obj;
        if (this.checkSum != other.checkSum) {
            return false;
        }
        return true;
    }

    

    public static Schematic load(File schematic) throws IOException, DataException {
        CuboidClipboard cc = SchematicFormat.MCEDIT.load(schematic);
        long checksum = FileUtils.checksumCRC32(schematic);
        int blocks = SchematicUtil.count(cc, true);
        return new Schematic(schematic, cc.getWidth(), cc.getHeight(), cc.getLength(), checksum, blocks);
    }
    
   

}
