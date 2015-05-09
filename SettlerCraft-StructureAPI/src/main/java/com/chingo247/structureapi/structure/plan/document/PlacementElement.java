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
package com.chingo247.structureapi.structure.plan.document;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.structureapi.structure.plan.xml.PlacementXMLConstants;
import com.chingo247.structureapi.structure.plan.xml.StructurePlanXMLConstants;
import com.sk89q.worldedit.Vector;
import java.io.File;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * An element that contains attributes of a placement. Note that this class is 
 * a helper class for retrieving placement attributes, therefore some of these methods may return null
 * @author Chingo
 */
public class PlacementElement extends LineElement {

    
    
    PlacementElement(File file, Element element) {
        super(file, element);
    }
    
    public String getPlacementReferenceId() {
        Node n = le.selectSingleNode(StructurePlanXMLConstants.STRUCTURE_PLAN_ID_ELEMENT);
        if(n == null) return null;
        LineElement simpleElement = new LineElement(getFile(), (Element) n);
        simpleElement.checkNotEmpty();
        return simpleElement.getTextValue().trim();
    }
    
    public String getPlacementReferencePath() {
        Node n = le.selectSingleNode(StructurePlanXMLConstants.STRUCTURE_PLAN_RELATIVE_PATH_ELEMENT);
        if(n == null) return null;
        LineElement simpleElement = new LineElement(getFile(), (Element) n);
        simpleElement.checkNotEmpty();
        return simpleElement.getTextValue().trim();
    }

    public String getType() {
        checkNotNull(PlacementXMLConstants.PLACEMENT_TYPE_ELEMENT);
        Node n = le.selectSingleNode(PlacementXMLConstants.PLACEMENT_TYPE_ELEMENT);
        LineElement simpleElement = new LineElement(getFile(), (Element) n);
        simpleElement.checkNotEmpty();
        return simpleElement.getTextValue().trim();
    }

    public Direction getDirection() {
        Node dirNode = le.selectSingleNode(PlacementXMLConstants.PLACEMENT_DIRECTION_ELEMENT);
        if (dirNode != null) {
            LineElement dirElement = new LineElement(getFile(), (Element) dirNode);
            dirElement.checkNotEmpty();
            String direction = dirElement.getTextValue();
            switch (direction.toLowerCase().trim()) {
                case "east":
                    return Direction.EAST;
                case "west":
                    return Direction.WEST;
                case "north":
                    return Direction.NORTH;
                case "south":
                    return Direction.SOUTH;
                default:
                    throw new AssertionError("Unreachable");
            }
        }
        return Direction.EAST; // Default
    }

    public Vector getPosition() {
        Node posNode = le.selectSingleNode(PlacementXMLConstants.PLACEMENT_POSITION_ELEMENT);
        if (posNode != null) {
            PositionElement posElement = new PositionElement(getFile(), (Element) posNode);
            return posElement.getPosition();
        }
        return Vector.ZERO;
    }

    public String getSchematic() {
        Node schematicNode = le.selectSingleNode(PlacementXMLConstants.PLACEMENT_SCHEMATIC_ELEMENT);
        if (schematicNode == null) {
            return null;
        }
        LineElement simpleElement = new LineElement(getFile(), (Element) schematicNode);
        simpleElement.checkNotEmpty();
        return simpleElement.getTextValue().trim();
    }

}
