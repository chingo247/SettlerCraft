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
import com.google.common.collect.Maps;
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
