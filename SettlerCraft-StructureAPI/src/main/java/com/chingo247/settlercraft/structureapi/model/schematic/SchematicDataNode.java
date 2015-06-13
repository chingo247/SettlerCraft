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
package com.chingo247.settlercraft.structureapi.model.schematic;

import com.chingo247.settlercraft.structureapi.model.interfaces.ISchematicData;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

/**
 *
 * @author Chingo
 */
public class SchematicDataNode implements ISchematicData {
    
    public static final String LABEL_NAME = "SchematicData";
    public static final Label LABEL = DynamicLabel.label(LABEL_NAME);
    public static final String WIDTH_PROPERTY = "width";
    public static final String HEIGHT_PROPERTY = "height";
    public static final String LENGTH_PROPERTY = "length";
    public static final String XXHASH_PROPERTY = "xxhash";
    public static final String NAME_PROPERTY = "name";
    public static final String LAST_IMPORT = "lastImport";
    
    
    private final Node underlyingNode;

    @Override
    public Node getNode() {
        return underlyingNode;
    }
    
    public SchematicDataNode(Node node) {
        this.underlyingNode = node;
    }
    
    @Override
    public int getWidth() {
        return (int) underlyingNode.getProperty(WIDTH_PROPERTY);
    }
    
    @Override
    public int getHeight() {
        return (int) underlyingNode.getProperty(HEIGHT_PROPERTY);
    }
    
    @Override
    public int getLength() {
        return (int) underlyingNode.getProperty(LENGTH_PROPERTY);
    }
    
    @Override
    public long getXXHash64() {
        return (long) underlyingNode.getProperty(XXHASH_PROPERTY);
    }
    
    @Override
    public String getName() {
        return (String) underlyingNode.getProperty(NAME_PROPERTY);
    }
    
    @Override
    public long getLastImport() {
        return (long) underlyingNode.getProperty(LAST_IMPORT);
    }
    
    @Override
    public void setLastImport(long newImportDate) {
        this.underlyingNode.setProperty(LAST_IMPORT, newImportDate);
    }
    
    @Override
    public void delete() {
        for(Relationship rel : underlyingNode.getRelationships()) {
            rel.delete();
        }
        underlyingNode.delete();
    }
    
}
