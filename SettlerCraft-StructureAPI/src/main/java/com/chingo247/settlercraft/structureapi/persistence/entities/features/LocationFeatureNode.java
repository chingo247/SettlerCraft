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
package com.chingo247.settlercraft.structureapi.persistence.entities.features;

import org.neo4j.graphdb.Node;

/**
 *
 * @author Chingo
 */
public class LocationFeatureNode {
    
    public static final String RELATIVE_X_PROPERTY = "relativeX";
    public static final String RELATIVE_Y_PROPERTY = "relativeY";
    public static final String RELATIVE_Z_PROPERTY = "relativeZ";
    
    private Node underlyingNode;

    public LocationFeatureNode(Node underlyingNode) {
        this.underlyingNode = underlyingNode;
    }

    public Node getRawNode() {
        return underlyingNode;
    }
    
    public int getRelativeX() {
        return (int) underlyingNode.getProperty(RELATIVE_X_PROPERTY);
    }
    
    public int getRelativeY() {
        return (int) underlyingNode.getProperty(RELATIVE_Y_PROPERTY);
    }
    
    public int getRelativeZ() {
        return (int) underlyingNode.getProperty(RELATIVE_Z_PROPERTY);
    }
    
    public void setRelativeX(int relativeX) {
        underlyingNode.setProperty(RELATIVE_X_PROPERTY, relativeX);
    }
    
    public void setRelativeY(int relativeY) {
        underlyingNode.setProperty(RELATIVE_Y_PROPERTY, relativeY);
    }
    
    public void setRelativeZ(int relativeZ) {
        underlyingNode.setProperty(RELATIVE_Z_PROPERTY, relativeZ);
    }
    
    
    
}
