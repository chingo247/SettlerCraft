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
package com.chingo247.settlercraft.structure.plan.document;

import com.chingo247.settlercraft.structure.plan.data.StructurePlanElement;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.BaseElement;

/**
 *
 * @author Chingo
 */
public final class PluginElement {

    protected final String pluginName;
    protected final PlanDocument root;
    protected final Element pluginElement;

    PluginElement(String pluginName, PlanDocument root, Element pluginElement) {
        this.pluginName = pluginName;
        this.root = root;
        this.pluginElement = pluginElement;
    }

    /**
     * Saves the pluginElement
     */
    public void save() {
        root.savePluginElement(this);
    }

    public Element getAsElement() {
        return pluginElement;
    }
    
    public String getStringValue(String xPath) {
        Node n = pluginElement.selectSingleNode(xPath);
        if (n == null || n.getText().trim().isEmpty()) {
            return null;
        }
        return n.getText();
    }
    
    /**
     * Gets the double value from the xPath expression, may return null if xPath expression returned null
     * @param xPath The xPath
     * @return The value
     * @throws NumberFormatException if string wasn't a number value
     */
    public Double getDoubleValue(String xPath) {
        Node n = pluginElement.selectSingleNode(xPath);
        if(n == null) {
            return null;
        } else {
           return Double.parseDouble(n.getText());
        }
    }
    
    /**
     * Gets the float value from the xPath expression, may return null if xPath expression returned null
     * @param xPath The xPath
     * @return The value
     * @throws NumberFormatException if string wasn't a number value
     */
    public Float getFloatValue(String xPath) {
        Node n = pluginElement.selectSingleNode(xPath);
        if(n == null) {
            return null;
        } else {
           return Float.parseFloat(n.getText());
        }
    }
    
    /**
     * Gets the int value from the xPath expression, may return null if xPath expression returned null
     * @param xPath The xPath
     * @return The value
     * @throws NumberFormatException if string wasn't a number value
     */
    public Integer getIntegerValue(String xPath) {
        Node n = pluginElement.selectSingleNode(xPath);
        if(n == null) {
            return null;
        } else {
           return Integer.parseInt(n.getText());
        }
    }
    
    public Boolean getBooleanValue(String xPath) {
        Node n = pluginElement.selectSingleNode(xPath);
        if(n == null) {
            return null;
        } else {
            return Boolean.parseBoolean(n.getText());
        }
    }
    
    
    

    public void setList(String listName, List<StructurePlanElement> elements) {
        // Get the list Element
        Element e = (Element) pluginElement.selectSingleNode(listName);
        
        // If we found the element and the new value was null or an empty list.
        // Delete the element
        if (e != null && (elements == null || elements.isEmpty())) {
            e.detach();
            return;
        }
        
        if (e == null) {
            pluginElement.add(new BaseElement(listName));
            e = (Element) pluginElement.selectSingleNode(listName);
        }

        e.clearContent();

        //
        List<Element> toElements = new LinkedList<>();
        for (StructurePlanElement se : elements) {
            toElements.add(se.asElement());
        }
        e.setContent(toElements);
    }
           
    public void setValue(String name, Object value) {
        // Get the list Element
        Element e = (Element) pluginElement.selectSingleNode(name);
        
        // Remove the element if value is null and the element exists
        if(value == null) {
            if(e != null) { 
                e.detach();
            }
            return;
        }
        
        // If element doesnt exist, add it
        if(e == null) {
            pluginElement.add(new BaseElement(name));
            e = (Element) pluginElement.selectSingleNode(name);
        } 
        e.setText(String.valueOf(value));
    }
    
    public void setElement(String name, StructurePlanElement sce) {
        // Get the list Element
        Element e = (Element) pluginElement.selectSingleNode(name);
        
        // If new value is null, remove it if the element exists
        if(sce == null) {
            if(e != null) {
            e.detach();
            }
            return;
        }
        
        // If doesnt exist add it
        if(e == null) {
            pluginElement.add(sce.asElement());
        // Otherwise replace it
        } else {
            e.detach();
            sce.asElement().setParent(e);
        }
    }

    @Override
    public final int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.pluginName);
        return hash;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PluginElement other = (PluginElement) obj;
        if (!Objects.equals(this.pluginName, other.pluginName)) {
            return false;
        }
        return true;
    }

}
