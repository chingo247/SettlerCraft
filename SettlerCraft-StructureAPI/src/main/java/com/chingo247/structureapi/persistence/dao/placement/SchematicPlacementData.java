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
package com.chingo247.structureapi.persistence.dao.placement;

/**
 *
 * @author Chingo
 */
public class SchematicPlacementData extends DefaultPlacementData {
    
    public static final String SCHEMATIC_HASH_PROPERTY = "schematicHash";
    public static final String SCHEMATIC_PROPERTY = "schematic";
    
    private final long schematichash;
    private final String schematic;

    public SchematicPlacementData(PlacementDataNode pdn) {
        super(pdn);
        this.schematic = (String) pdn.getRawNode().getProperty(SCHEMATIC_PROPERTY);
        this.schematichash = (long) pdn.getRawNode().getProperty(SCHEMATIC_HASH_PROPERTY);
    }

    public String getSchematic() {
        return schematic;
    }

    public long getSchematichash() {
        return schematichash;
    }
    
}
