
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
package com.chingo247.settlercraft.structureapi.structure.plan.schematic;

import com.chingo247.settlercraft.util.SchematicUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.apache.commons.io.FileUtils;


/**
 * Contains data about the schematic, however it doesn't contain data for construction. 
 * See {@link Schematic} to obtain the CuboidClipboard
 * @author Chingo
 */
@Entity
public class SchematicData implements Serializable {
    
    @Id
    private Long checksum;
    
    private Integer s_width;
    private Integer s_height;
    private Integer s_length;

    
    /**
     * JPA Constructor
     */
    protected SchematicData() {
    }

    SchematicData(Long checksum, Integer s_width, Integer s_height, Integer s_length) {
        this.checksum = checksum;
        this.s_width = s_width;
        this.s_height = s_height;
        this.s_length = s_length;
    }

    public long getChecksum() {
        return checksum;
    }

    public int getLength() {
        return s_length;
    }

    public int getHeight() {
        return s_height;
    }

    public int getWidth() {
        return s_width;
    }

    public Vector getSize() {
        return new Vector(s_width, s_height, s_length);
    }
    
    public static SchematicData load(File schematic) throws IOException, DataException {
        CuboidClipboard cc = SchematicFormat.MCEDIT.load(schematic);
        long checksum = FileUtils.checksumCRC32(schematic);
//        int blocks = SchematicUtil.count(cc, true); Removed because too expensive
        return new SchematicData(checksum, cc.getWidth(), cc.getHeight(), cc.getLength());
    }
    
    
    
    
    
}
