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
package com.chingo247.settlercraft.structure.rollback;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author Chingo
 */
@Entity(name = "BlockLog")
public class BlockLog implements Serializable {
    
    @Id
    // Generating a random UUID is faster than generating a long value
    private UUID id;
    
    private int x;
    private int y;
    private int z;
    private int newMaterial;
    private int oldMaterial;
    private byte newData;
    private byte oldData;
    private long date;

    /**
     * JPA Constructor
     */
    protected BlockLog() {
    }

    public BlockLog(int x, int y, int z, int oldMaterial, byte oldData, int newMaterial, byte newData) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.oldMaterial = oldMaterial;
        this.oldData = oldData;
        this.newMaterial = newMaterial;
        this.newData = newData;
        this.date = new Date().getTime();
        this.id = UUID.randomUUID();
    }

    
    
    
}
