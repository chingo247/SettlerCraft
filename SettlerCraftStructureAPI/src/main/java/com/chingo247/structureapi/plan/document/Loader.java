/*
 * Copyright (C) 2014 Chingo247
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

package com.chingo247.structureapi.plan.document;

import com.chingo247.structureapi.exception.StructureDataException;
import java.io.File;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 * @param <T> The type of the objects that will be loaded
 */
public abstract class Loader<T> {
    
    protected final String xPath;
    
    public Loader(String xPath) {
        this.xPath = xPath;
    }
    
    public abstract List<T> load(Element e) throws StructureDataException;
    
    public List<T> load(Document d) throws StructureDataException {
        return load((Element) d.selectSingleNode(xPath));
    }
    
    public List<T> load(File configXML) throws DocumentException, StructureDataException {
        return load(new SAXReader().read(configXML));
    }
    
}
