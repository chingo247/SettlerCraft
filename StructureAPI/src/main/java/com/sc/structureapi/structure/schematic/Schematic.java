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
package com.sc.structureapi.structure.schematic;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
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
public class Schematic implements Serializable, Cloneable {

    private final long checkSum;
    private final int s_length;
    private final int s_height;
    private final int s_width;
    private final CuboidClipboard clipboard;

    private Schematic(CuboidClipboard clipboard, int width, int height, int length, long checksum) throws IOException {
        this.checkSum = checksum;
        this.clipboard = clipboard;
        this.s_width = width;
        this.s_height = height;
        this.s_length = length;
    }

    public long getCheckSum() {
        return checkSum;
    }
    
    public Vector getSize() {
        return clipboard.getSize();
    }

    public CuboidClipboard getClipboard() {
        CuboidClipboard copy = new CuboidClipboard(clipboard.getSize());
        for (int x = 0; x < copy.getWidth(); x++) {
            for (int y = 0; y < copy.getHeight(); y++) {
                for (int z = 0; z < copy.getLength(); z++) {
                    copy.setBlock(new Vector(x, y, z), clipboard.getBlock(new Vector(x, y, z)));
                }
            }
        }
        return copy;
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

    public static Schematic load(File schematic) throws IOException, DataException {
        CuboidClipboard cc = SchematicFormat.MCEDIT.load(schematic);
        long checksum = FileUtils.checksumCRC32(schematic);
        return new Schematic(cc, cc.getWidth(), cc.getHeight(), cc.getLength(), checksum);
    }

}
