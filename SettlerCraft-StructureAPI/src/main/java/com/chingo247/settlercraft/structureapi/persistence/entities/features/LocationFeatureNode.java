/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
