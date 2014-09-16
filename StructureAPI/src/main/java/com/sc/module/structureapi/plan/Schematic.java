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
package com.sc.module.structureapi.plan;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Chingo
 */
public class Schematic implements Serializable {

    private final long checkSum;

    private final CuboidClipboard schematic;
    
    private final int s_length;
    private final int s_height;
    private final int s_width;


    private Schematic(CuboidClipboard schematic, int width, int height, int length, long checksum) throws IOException {
        this.checkSum = checksum;
        this.schematic = schematic;
        this.s_width = width;
        this.s_height = height;
        this.s_length = length;
    }



    public Long getCheckSum() {
        return checkSum;
    }

    public CuboidClipboard getClipboard() {
        return schematic;
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
    
    public static Schematic load(File file) throws IOException, DataException {
        CuboidClipboard c = SchematicFormat.MCEDIT.load(file);
        long checksum = FileUtils.checksumCRC32(file);
        return new Schematic(c, c.getWidth(), c.getHeight(), c.getLength(), checksum);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.checkSum);
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
        if (!Objects.equals(this.checkSum, other.checkSum)) {
            return false;
        }
        return true;
    }
    
    

}
