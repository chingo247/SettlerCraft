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
package com.chingo247.structureapi.plan.placement.xml;

import com.chingo247.structureapi.persistence.dao.placement.PlacementTypes;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Chingo
 */
public class XMLPlacementHandlerFactory {
    
    private static XMLPlacementHandlerFactory instance;
    private final Map<String, XMLPlacementHandler> handlers;
    
    private XMLPlacementHandlerFactory() {
        this.handlers = new HashMap<>();
        // Add Defaults
        this.handlers.put(PlacementTypes.SCHEMATIC, new XMLSchematicPlacementHandler());
        this.handlers.put(PlacementTypes.STRUCTURE_LOT, new XMLStructureLotHandler());
    }
    
    public static XMLPlacementHandlerFactory getInstance() {
        if(instance == null) {
            instance = new XMLPlacementHandlerFactory();
        }
        return instance;
    }
    
    public XMLPlacementHandler<?> getHandler(String type) {
        return handlers.get(type.toLowerCase());
    }
    
    public void registerHandler(String type) {
        type = type.toLowerCase();
        if(handlers.get(type) != null) {
            throw new RuntimeException("Already registered a handler for type '"+type+"'");
        }
    }
    
    
    
}
