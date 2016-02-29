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
package com.chingo247.settlercraft.core.model.world;

import java.util.UUID;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Chingo
 */
public class WorldNode {
    
    
    public static final String LABEL = "WORLD";
    public static final String NAME_PROPERTY = "name";
    public static final String UUID_PROPERTY = "uuid";
    
    public static Label label() {
        return DynamicLabel.label(LABEL);
    }
    
    protected final Node underlyingNode;
    
    public WorldNode(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    public Node getNode() {
        return underlyingNode;
    }
    
    public String getName() {
        return (String) underlyingNode.getProperty(NAME_PROPERTY);
    }
    
    public UUID getUUID() {
        return UUID.fromString((String) underlyingNode.getProperty(UUID_PROPERTY));
    }
    
    
    
    
}
