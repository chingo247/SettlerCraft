
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
package com.chingo247.settlercraft.structureapi.structure.plan;

import com.chingo247.settlercraft.structureapi.exception.PlanException;
import com.chingo247.settlercraft.structureapi.world.Direction;
import com.sk89q.worldedit.Vector;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author Chingo
 */
public class XMLUtils {
    
    public static String getXPathNodeValue(Element e, String xPath) {
        Node n = e.selectSingleNode(xPath);
        if(n != null) {
            return n.getStringValue();
        }
        return null;
    }
    
    public static String getXPathNodeValue(Node node, String xPath) {
        Node n = node.selectSingleNode(xPath);
        if(n != null) {
            return n.getStringValue();
        }
        return null;
    }
    
    
    
    public static int getXPathIntValue(Element e, String xPath) {
        Node n = e.selectSingleNode(xPath);
        if(n != null) {
            return Integer.parseInt(n.getStringValue());
        }
        throw new RuntimeException("Element '"+xPath+"' was not found within '<" + e.getName() +">'");
    } 
    
    public static double getXPathDoubleValue(Element e, String xPath) {
        Node n = e.selectSingleNode(xPath);
        if(n != null) {
            return Double.parseDouble(n.getStringValue());
        }
        throw new RuntimeException("Element '"+e.getName()+"' was not found");
    } 
    
    public static Vector getXYZFrom(Element e) {
        double x = getXPathDoubleValue(e, "X");
        double y = getXPathDoubleValue(e, "Y");
        double z = getXPathDoubleValue(e, "Z");
        return new Vector(x, y, z);
    }
    
    /**
     * Gets the direction from the given element. The check is case-insensitive
     * @param e The element
     * @return Direction.NORTH if value was [0|360|north}. Direction.EAST if value was [90|-270|east].
     * Direction.SOUTH if value was [180|-180|south]. Direction.WEST if value was [270|-90|west]
     */
    public static Direction getDirectionFrom(Element e) {
        Node directionNode = e.selectSingleNode("Direction");
        
        String direction = directionNode.getText().trim().toLowerCase();
        switch(direction) {
            case "0":
            case "360":
            case "north": return Direction.NORTH;
            case "90":
            case "-270":
            case "east": return Direction.EAST;
            case "180":
            case "-180":
            case "south": return Direction.SOUTH;
            case "-90": 
            case "270":
            case "west": return Direction.WEST;
            default: throw new PlanException("Invalid direction value in element '<"+e.getName()+">'");
        }
    }
    
    public static boolean hasDirection(Element e) {
        Node directionNode = e.selectSingleNode("Direction");
        return directionNode != null;
    }
    
    public static boolean hasPosition(Element e) {
        Node posNode = e.selectSingleNode("Position");
        return posNode != null;
    }
}
