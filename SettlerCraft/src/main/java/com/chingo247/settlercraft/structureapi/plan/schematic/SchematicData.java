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
package com.chingo247.settlercraft.structureapi.plan.schematic;

import com.chingo247.settlercraft.structureapi.plan.schematic.Schematic;
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
    private Integer blocks;

    
    /**
     * JPA Constructor
     */
    protected SchematicData() {
    }

    SchematicData(Long checksum, Integer s_width, Integer s_height, Integer s_length, Integer blocks) {
        this.checksum = checksum;
        this.s_width = s_width;
        this.s_height = s_height;
        this.s_length = s_length;
        this.blocks = blocks;
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

    public int getBlocks() {
        return blocks;
    }
    
    public Vector getSize() {
        return new Vector(s_width, s_height, s_length);
    }
    
    public static SchematicData load(File schematic) throws IOException, DataException {
        CuboidClipboard cc = SchematicFormat.MCEDIT.load(schematic);
        long checksum = FileUtils.checksumCRC32(schematic);
        int blocks = SchematicUtil.count(cc, true);
        return new SchematicData(checksum, cc.getWidth(), cc.getHeight(), cc.getLength(), blocks);
    }
    
    
    
    
}
