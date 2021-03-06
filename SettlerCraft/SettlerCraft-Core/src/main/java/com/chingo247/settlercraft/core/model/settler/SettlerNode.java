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
package com.chingo247.settlercraft.core.model.settler;

import java.util.UUID;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

/**
 * Represents a Node containing data for a Settler. All operations within this class require to be executed within a Transaction
 * @author Chingo
 */
public class SettlerNode  {
    
    public static final String LABEL = "Settler";
    public static final String UUID_PROPERTY = "uuid";
    public static final String NAME_PROPERTY = "name";
    public static final String ID_PROPERTY = "settlerId";
    
    public static Label label() {
        return DynamicLabel.label(LABEL);
    }
    
    protected final Node underlyingNode;

    // Second level cache
    private UUID uuid;
    private Long id;
    
    public SettlerNode(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    public Node getNode() {
        return underlyingNode;
    }
    
    public void setName(String name) {
        underlyingNode.setProperty(NAME_PROPERTY, name);
    }
    
    public UUID getUniqueId() {
        if(uuid != null) {
            return uuid;
        }
        String uuidString = (String)underlyingNode.getProperty(UUID_PROPERTY);
        uuid =  UUID.fromString(uuidString);
        return uuid;
    }
    
    public String getName() {
        String name = (String) underlyingNode.getProperty(NAME_PROPERTY);
        return name;
    }
    
    public Long getId() {
        if(id != null) {
            return id;
        }
        id = (Long) underlyingNode.getProperty(ID_PROPERTY);
        return id;
    }
    
    
    
}
