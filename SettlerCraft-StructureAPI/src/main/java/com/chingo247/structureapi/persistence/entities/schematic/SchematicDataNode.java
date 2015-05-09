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
package com.chingo247.structureapi.persistence.entities.schematic;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 *
 * @author Chingo
 */
public class SchematicDataNode {
    
    public static final String LABEL_NAME = "SchematicData";
    public static final Label LABEL = DynamicLabel.label(LABEL_NAME);
    public static final String WIDTH_PROPERTY = "width";
    public static final String HEIGHT_PROPERTY = "height";
    public static final String LENGTH_PROPERTY = "length";
    public static final String XXHASH_PROPERTY = "xxhash";
    public static final String NAME_PROPERTY = "name";
    public static final String LAST_IMPORT = "lastImport";
    
    
    private final Node underlyingNode;

    public Node getRawNode() {
        return underlyingNode;
    }
    
    public SchematicDataNode(Node node) {
        this.underlyingNode = node;
    }
    
    public int getWidth() {
        return (int) underlyingNode.getProperty(WIDTH_PROPERTY);
    }
    
    public int getHeight() {
        return (int) underlyingNode.getProperty(HEIGHT_PROPERTY);
    }
    
    public int getLength() {
        return (int) underlyingNode.getProperty(LENGTH_PROPERTY);
    }
    
    public long getXXHash64() {
        return (long) underlyingNode.getProperty(XXHASH_PROPERTY);
    }
    
    public String getName() {
        return (String) underlyingNode.getProperty(NAME_PROPERTY);
    }
    
    public long getLastImport() {
        return (long) underlyingNode.getProperty(LAST_IMPORT);
    }
    
    public void setLastImport(long newImportDate) {
        this.underlyingNode.setProperty(LAST_IMPORT, newImportDate);
    }
    
    public void delete() {
        for(Relationship rel : underlyingNode.getRelationships()) {
            rel.delete();
        }
        underlyingNode.delete();
    }
    
}
