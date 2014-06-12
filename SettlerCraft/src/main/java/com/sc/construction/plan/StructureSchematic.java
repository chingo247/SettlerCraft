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
package com.sc.construction.plan;

import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
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
    
    private final Integer length;
    private final Integer height;
    private final Integer width;

    /**
     * JPA Constructor
     */
    protected StructureSchematic() {
        this.length = null;
        this.height = null;
        this.width = null;
    }

    StructureSchematic(File schematic, int width, int height, int length) throws IOException {
        this.checkSum = Files.getChecksum(schematic, new CRC32());
        this.schematic = schematic;
        this.width = width;
        this.height = height;
        this.length = length;
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

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getLength() {
        return length;
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
        final StructureSchematic other = (StructureSchematic) obj;
        if (!Objects.equals(this.checkSum, other.checkSum)) {
            return false;
        }
        return true;
    }
    
    

}
