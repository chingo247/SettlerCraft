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
package com.chingo247.settlercraft.structureapi.structure.plan.xml.parser;

import com.chingo247.settlercraft.structureapi.structure.plan.document.PlacementElement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import java.io.File;
import org.dom4j.Document;

/**
 *
 * @author Chingo
 * @param <T>
 */
public interface PlacementParser<T extends Placement> {
    
    public String getType();
    
    
    /**
     * Parses a placement from XML
     * @param source The XML file that is used as source
     * @param d The document object that was read from the source
     * @param element The element that contains the placement that should be handled
     * 
     * Note: Only the given element should be handled, the other parameters are there for debugging
     * and to improve handling.
     * 
     * @return The Placement
     */
    public T parse(File source, Document d, PlacementElement element);
    
}
