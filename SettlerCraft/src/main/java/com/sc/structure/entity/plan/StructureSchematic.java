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
package com.sc.structure.entity.plan;

import com.google.common.io.Files;
import com.sk89q.worldedit.Countable;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 *
 * @author Chingo
 */
@Entity
public class StructureSchematic implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(updatable = false, unique = true)
    private Long checkSum;

    @Lob
    @Column(updatable = false)
    private File schematic;

    @Column(name = "s_height", updatable = false)
    private int height;

    @Column(name = "s_length", updatable = false)
    private int length;

    @Column(name = "s_width", updatable = false)
    private int width;

    @Column(updatable = false)
    private int blocks;

    protected StructureSchematic() {
    }

    public StructureSchematic(File schematic) throws IOException {
        this.checkSum = Files.getChecksum(schematic, new CRC32());
        this.schematic = schematic;
        CuboidClipboard c;
        try {
            c = SchematicFormat.MCEDIT.load(schematic);
            this.height = c.getHeight();
            this.length = c.getLength();
            this.width = c.getWidth();
            int count = 0;
            for (Countable<BaseBlock> b : c.getBlockDistributionWithData()) {
                count += b.getAmount();
            }
            this.blocks = count;

        } catch (DataException ex) {
            Logger.getLogger(StructureSchematic.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int getBlocks() {
        return blocks;
    }
    
    public int getHeight() {
        return height;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public Long getId() {
        return id;
    }

    public Long getCheckSum() {
        return checkSum;
    }

    public File getSchematic() {
        return schematic;
    }

}
