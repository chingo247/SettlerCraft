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
package com.chingo247.settlercraft.structureapi.structure.plan.document;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.structureapi.structure.plan.xml.PlacementXMLConstants;
import com.chingo247.settlercraft.structureapi.structure.plan.xml.StructurePlanXMLConstants;
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
        checkNotNull(PlacementXMLConstants.TYPE_ELEMENT);
        Node n = le.selectSingleNode(PlacementXMLConstants.TYPE_ELEMENT);
        LineElement simpleElement = new LineElement(getFile(), (Element) n);
        simpleElement.checkNotEmpty();
        return simpleElement.getTextValue().trim();
    }

    public Direction getDirection() {
        Node dirNode = le.selectSingleNode(PlacementXMLConstants.DIRECTION_ELEMENT);
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
        Node posNode = le.selectSingleNode(PlacementXMLConstants.POSITION_ELEMENT);
        if (posNode != null) {
            PositionElement posElement = new PositionElement(getFile(), (Element) posNode);
            return posElement.getPosition();
        }
        return Vector.ZERO;
    }

    public String getSchematic() {
        Node schematicNode = le.selectSingleNode(PlacementXMLConstants.SCHEMATIC_ELEMENT);
        if (schematicNode == null) {
            return null;
        }
        LineElement simpleElement = new LineElement(getFile(), (Element) schematicNode);
        simpleElement.checkNotEmpty();
        return simpleElement.getTextValue().trim();
    }

}
