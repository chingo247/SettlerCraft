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
package com.chingo247.structureapi.plan.placement.node;

import com.chingo247.structureapi.persistence.dao.placement.PlacementTypes;
import com.google.common.collect.Maps;
import java.util.Map;

/**
 *
 * @author Chingo
 */
public class NodePlacementHandlerFactory {
    
    private static NodePlacementHandlerFactory instance;
    private final Map<String, NodePlacementHandler> handlers;
    
    private NodePlacementHandlerFactory() {
        this.handlers = Maps.newHashMap();
        this.handlers.put(PlacementTypes.SCHEMATIC, new NodeSchematicPlacementHandler());
        this.handlers.put(PlacementTypes.STRUCTURE_LOT, new NodeStructureLotHandler());
    }
    
    public static NodePlacementHandlerFactory getInstance() {
        if(instance == null) {
            instance = new NodePlacementHandlerFactory();
        }
        return instance;
    }
    
    public NodePlacementHandler getHandler(String type) {
        return handlers.get(type.toLowerCase());
    }
    
    public void registerNodePlacementHandler(String type, NodePlacementHandler handler) {
        type = type.toLowerCase();
        if(handlers.get(type) != null) {
            throw new RuntimeException("Already registered a handler for type '"+type+"'");
        }
    }
    
    
}
