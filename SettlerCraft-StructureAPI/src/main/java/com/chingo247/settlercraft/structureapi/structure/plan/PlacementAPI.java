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

import com.chingo247.settlercraft.structureapi.structure.plan.document.PlacementElement;
import com.chingo247.settlercraft.structureapi.structure.plan.exception.PlacementException;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structureapi.structure.plan.xml.parser.SchematicPlacementParser;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.PlacementTypes;
import com.chingo247.settlercraft.structureapi.structure.plan.xml.parser.PlacementParser;
import com.chingo247.settlercraft.structureapi.structure.plan.xml.parser.StructureLotParser;
import com.chingo247.settlercraft.structureapi.structure.plan.xml.handlers.PlacementXMLHandler;
import java.io.File;
import java.util.Map;
import net.minecraft.util.com.google.common.collect.Maps;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 *
 * @author Chingo
 */
public class PlacementAPI {
    
    private static PlacementAPI instance;
    private final Map<String, PlacementParser> parsers;
    private final Map<String, PlacementXMLHandler> writers;
    
    private PlacementAPI() {
        this.parsers = Maps.newHashMap();
        this.writers = Maps.newHashMap();
        // Add Defaults
        this.parsers.put(PlacementTypes.SCHEMATIC, new SchematicPlacementParser());
        this.parsers.put(PlacementTypes.STRUCTURE_LOT, new StructureLotParser());
    }
    
    public static PlacementAPI getInstance() {
        if(instance == null) {
            instance = new PlacementAPI();
        }
        return instance;
    }
    
    public <T extends Placement> T parse(File f, Document d, PlacementElement e) {
        String type = e.getType();
        PlacementParser<T> pp = parsers.get(type.toLowerCase());
        if(pp == null) {
            throw new RuntimeException("Can't handle placement element of type '" + type + "', no parser was registered to handle this type");
        } else {
            return pp.parse(f, d, e);
        }
    }
    
    public Element handle(Placement p) {
        String type = p.getTypeName();
        PlacementXMLHandler handler = writers.get(type);
        if(handler == null) {
            throw new PlacementException("Can't write placement of type '" + type + "', no writer was regisered to handle this type...");
        } else {
            return handler.handle(p);
        }
    }
    
    public void registerHandler(PlacementParser p) {
        String type = p.getType();
        type = type.toLowerCase();
        if(parsers.get(type) != null) {
            throw new RuntimeException("Already registered a handler for type '"+type+"'");
        }
    }
    
    
    
}
