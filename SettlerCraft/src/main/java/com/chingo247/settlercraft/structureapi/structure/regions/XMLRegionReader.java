
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
package com.chingo247.settlercraft.structureapi.structure.regions;

import com.google.common.base.Preconditions;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 */
public class XMLRegionReader {
    
    public AbstractStructureRegion read(Element e) {
        Preconditions.checkNotNull(e);
        switch(e.getName()) {
            case "CuboidRegion": return handleCuboid(e);
            case "CylinderRegion": return handleCylinder(e);
            case "EllipsoidRegion": return handleEllipsoid(e);
            case "Polygonal2DRegion" : return handlePolygonal2D(e);
            default: throw new AssertionError("Unsupported region type '"+e.getName()+"'");
        }
        
    }
    
    private StructureCuboidRegion handleCuboid(Element e) {
        throw new UnsupportedOperationException("Not supported yet");
    }
    
    private StructureCylinderRegion handleCylinder(Element e) {
        throw new UnsupportedOperationException("Not supported yet");
    }
    
    private StructureEllipsoidRegion handleEllipsoid(Element e) {
        throw new UnsupportedOperationException("Not supported yet");
    }
    
    private StructurePolygonal2DRegion handlePolygonal2D(Element e) {
        throw new UnsupportedOperationException("Not supported yet");
    }
    
}
