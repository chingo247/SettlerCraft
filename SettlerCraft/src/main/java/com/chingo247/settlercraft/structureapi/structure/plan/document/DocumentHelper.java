/*
 * Copyright (C) 2014 Chingo
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

import com.chingo247.settlercraft.structureapi.exception.PlanException;
import com.google.common.base.Preconditions;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author Chingo
 */
public class DocumentHelper {
    
    /**
     * Gets the double value from the element
     * @param e The element
     * @return The double value
     * @throws PlanException on invalid number value
     */
    public static Double getDouble(Element e) {
        String value = e.getText();
        double d;
        try {
            d = Double.parseDouble(value);
        } catch (NumberFormatException nfe) {
            throw new PlanException("Invalid number value for element '"+e.getName()+"'");
        }
        return d;
    }
    
    /**
     * Gets the float value from the element
     * @param e The element
     * @return The float value
     * @throws PlanException on invalid number value
     */
    public static Integer getInt(Element e) {
        String value = e.getText();
        int i;
        try {
            i = Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            throw new PlanException("Invalid number value for element '"+e.getName()+"'");
        }
        return i;
    }
    
    /**
     * Gets the long value from the element
     * @param e The element
     * @return The long value
     * @throws PlanException on invalid number value
     */
    public static Long getLong(Element e) {
        String value = e.getText();
        long l;
        try {
            l = Long.parseLong(value);
        } catch (NumberFormatException nfe) {
            throw new PlanException("Invalid number value for element '"+e.getName()+"'");
        }
        return l;
    }
    
    /**
     * Gets the float value from the element
     * @param e The element
     * @return The float value
     * @throws PlanException on invalid number value
     */
    public static Float getFloat(Element e) {
        String value = e.getText();
        float f;
        try {
            f = Float.parseFloat(value);
        } catch (NumberFormatException nfe) {
            throw new PlanException("Invalid number value for element '"+e.getName()+"'");
        }
        return f;
    }
    
    /**
     * Checks if the given element has an element matching the XPath-Expression
     * @param e The element
     * @param xPath The XPath-Expression
     * @throws PlanException when element was null
     */
    public static void checkNotNull(Element e, String xPath) {
        Preconditions.checkNotNull(e);
        Preconditions.checkNotNull(xPath);
        
        Node n = e.selectSingleNode(xPath);
        
        if(n == null) {
            String element = xPath.substring(xPath.lastIndexOf("/"));
            throw new PlanException("Expected element '"+element+"' within " + e.getName());
        }
    } 
}
