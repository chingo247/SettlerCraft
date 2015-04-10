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
package com.chingo247.structureapi.plan.placement.handlers;

import com.chingo247.structureapi.plan.placement.Placement;
import com.chingo247.structureapi.plan.document.PlacementElement;
import org.dom4j.Element;

/**
 *
 * @author Chingo
 * @param <T>
 */
public abstract class PlacementHandler<T extends Placement> {
    
    private String plugin;
    private String type;

    /**
     * Constructor
     * @param pluginName The name of the plugin that can handle
     * @param typeName The name of the type this handler can handle. 
     * Note that the root element this Handler will handle will have to match: <pluginName.typeName>
     */
    public PlacementHandler(String pluginName, String typeName) {
        this.plugin = pluginName;
        this.type = typeName;
    }
    
    public String getPlugin() {
        return plugin;
    }
    
    public String getType() {
        return type;
    }
    
    
    
    /**
     * Handles a document that should be handled by this handler
     * @param d The document to handle
     * @param file
     * @return The result of this handler as placement
     */
    public abstract T handle(PlacementElement d);
    
    /**
     * Creates a copy of the given placement
     * @param t
     * @return 
     */
    public abstract T copy(T t);
    
    public abstract Element asElement(T t);
    
}
