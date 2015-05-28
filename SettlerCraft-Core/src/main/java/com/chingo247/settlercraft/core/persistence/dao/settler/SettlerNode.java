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
package com.chingo247.settlercraft.core.persistence.dao.settler;

import java.util.UUID;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

/**
 * Represents a Node containing data for a Settler. All operations within this class require to be executed within a Transaction
 * @author Chingo
 */
public class SettlerNode {
    
    public static final Label LABEL = DynamicLabel.label("Settler");
    public static final String UUID_PROPERTY = "uuid";
    public static final String NAME_PROPERTY = "name";
    public static final String ID_PROPERTY = "settlerId";
    
    private final Node underlyingNode;

    public Node getRawNode() {
        return underlyingNode;
    }
    
    public SettlerNode(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }
    
    public UUID getUUID() {
        String uuidString = (String)underlyingNode.getProperty(UUID_PROPERTY);
        return UUID.fromString(uuidString);
    }
    
    public String getName() {
        String name = (String) underlyingNode.getProperty(NAME_PROPERTY);
        return name;
    }
    
    public Long getId() {
        return underlyingNode.hasProperty(ID_PROPERTY) ? (Long) underlyingNode.getProperty(ID_PROPERTY) : null;
    }
    
    
    
}
