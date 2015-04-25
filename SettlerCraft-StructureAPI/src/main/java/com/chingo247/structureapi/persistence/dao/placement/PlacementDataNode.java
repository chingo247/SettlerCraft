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

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Chingo
 */
public class PlacementDataNode {
    
    public static final Label LABEL = DynamicLabel.label("PlacementData");
    public static final String TYPE_PROPERTY = "type";
    public static final String WIDTH_PROPERTY = "width";
    public static final String HEIGHT_PROPERTY = "height";
    public static final String LENGTH_PROPERTY = "length";

    private final Node underlyingNode;
    
    public PlacementDataNode(Node node) {
        this.underlyingNode = node;
    }

    public Node getRawNode() {
        return underlyingNode;
    }
    
    
    
    public String getType() {
        return (String) underlyingNode.getProperty(TYPE_PROPERTY);
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
    
}
