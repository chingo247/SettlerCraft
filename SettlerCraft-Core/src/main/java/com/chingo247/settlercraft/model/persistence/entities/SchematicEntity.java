
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
package com.chingo247.settlercraft.model.persistence.entities;

import com.sk89q.worldedit.Vector;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.annotations.Index;


/**
 * Contains data about the schematic, however it doesn't contain data for construction. 
 * See {@link Placement} to obtain the CuboidClipboard
 * @author Chingo
 */
@Entity
public class SchematicEntity implements Serializable {
    
    @Id
    @Index(name = "checksumIndex")
    private Long checksum;
    
    private Integer s_width;
    private Integer s_height;
    private Integer s_length;

    
    /**
     * JPA Constructor
     */
    protected SchematicEntity() {
    }

    public SchematicEntity(Long checksum, Integer s_width, Integer s_height, Integer s_length) {
        this.checksum = checksum;
        this.s_width = s_width;
        this.s_height = s_height;
        this.s_length = s_length;
    }

    public long getId() {
        return checksum;
    }

    public Vector getSize() {
        return new Vector(s_width, s_height, s_length);
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
    
    
    
    
    
}
