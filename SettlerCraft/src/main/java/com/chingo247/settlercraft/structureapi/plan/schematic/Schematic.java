
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
package com.chingo247.settlercraft.structureapi.plan.schematic;

import com.chingo247.settlercraft.util.SchematicUtil;
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
