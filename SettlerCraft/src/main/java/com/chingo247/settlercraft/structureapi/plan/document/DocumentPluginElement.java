
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
package com.chingo247.settlercraft.structureapi.plan.document;

import java.util.LinkedList;
import java.util.List;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.tree.BaseElement;

/**
 *
 * @author Chingo
 * @param <T> The root
 */
public class DocumentPluginElement<T extends AbstractDocument> {
    
    protected final String pluginName;
    protected final T root;
    protected final Element pluginElement;

    DocumentPluginElement(String pluginName, T root, Element pluginElement) {
        this.pluginName = pluginName;
        this.root = root;
        this.pluginElement = pluginElement;
    }
    
    public T getRoot() {
        return root;
    }

    /**
     * Saves the pluginElement
     */
    public void save() {
        root.save(this);
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
    
    
    

    public void setList(String listName, List<IStructurePlanElement> elements) {
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
        for (IStructurePlanElement se : elements) {
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
    
    public void setElement(String name, IStructurePlanElement sce) {
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
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.pluginName != null ? this.pluginName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DocumentPluginElement other = (DocumentPluginElement) obj;
        if ((this.pluginName == null) ? (other.pluginName != null) : !this.pluginName.equals(other.pluginName)) {
            return false;
        }
        return true;
    }

    
}
