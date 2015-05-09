/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.settlercraft.structureapi.persistence.entities.schematic;

/**
 *
 * @author Chingo
 */
public class DefaultSchematicData implements SchematicData{
    
    private final String name;
    private final int width, height, length;
    private final long importDate;
    private final long xxhash64;

    DefaultSchematicData(String name, long xxhash64, int width, int height, int length, long importDate) {
        this.name = name;
        this.xxhash64 = xxhash64;
        this.width = width;
        this.height = height;
        this.length = length;
        this.importDate = importDate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getXXHash64() {
        return xxhash64;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public long getImportDate() {
        return importDate;
    }

    
    
    
    
    
}
