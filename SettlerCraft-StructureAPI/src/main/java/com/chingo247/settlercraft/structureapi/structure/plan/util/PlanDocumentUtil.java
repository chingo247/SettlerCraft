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
package com.chingo247.settlercraft.structureapi.structure.plan.util;

import com.chingo247.settlercraft.structureapi.structure.plan.exception.PlanException;
import fiber.core.impl.xml.located.LocatedElement;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 */
public class PlanDocumentUtil {
    
    private PlanDocumentUtil() {
    }
    
    public static final String ROOT_ELEMENT = "StructurePlan";
    public static final String SCHEMATIC_ELEMENT = "Schematic";
    public static final String STRUCTURELOT_ELEMENT = "StructureLot";
    public static final String GENERATED_STRUCTURE_ELEMENT = "Generated";
    public static final String WIDTH_ELEMENT = "Width";
    public static final String HEIGHT_ELEMENT = "Height";
    public static final String LENGTH_ELEMENT = "Length";
    public static final String SUBSTRUCTURES = "SubStructures";
    public static final String SUBSTRUCTURE = "SubStructure";
    public static final String POSITION = "Position";
    public static final String DIRECTION = "Direction";
    
    private static void checkLocated(Element d) {
        if(!(d instanceof LocatedElement)) {
            throw new IllegalArgumentException("Element was not of type LocatedElement, use "+DocumentUtil.class.getSimpleName()+"#read() to retrieve an allowed instance of document");
        }
    }
    
    public void checkHasNumber(Element e) {
        checkLocated(e);
        if(!NumberUtils.isNumber(e.getText())) {
           LocatedElement le = (LocatedElement) e;
           throw new IllegalArgumentException("Value of element <"+e.getName()+"> on line " + le.getLine() + " is not a number");
        }
    }
    
    public void checkNotNull(Element e, String xpath) {
        checkLocated(e);
        Node notNull = e.selectSingleNode(xpath);
        
        if(notNull == null) {
            String element = xpath.substring(xpath.lastIndexOf("/"));
            LocatedElement le = (LocatedElement) e;
            throw new PlanException("Expected element <"+element+"> within <" + e.getName() +"> on line " +le.getLine());
        }
    }
    
    public void checkNullOrNotEmpty(Element e, String xpath) {
        checkLocated(e);
        Node notNull = e.selectSingleNode(xpath);
        
        if(notNull != null && notNull.getText().trim().isEmpty()) {
            String element = xpath.substring(xpath.lastIndexOf("/"));
            LocatedElement le = (LocatedElement) e;
            throw new PlanException("No value for element <"+element+"> within <" + e.getName() +"> on line " +le.getLine());
        }
    }
    
    

    

    public static boolean isStructurePlan(Document d) {
        return d.getRootElement().getName().equals(ROOT_ELEMENT);
    }

    public static boolean isStructurePlan(File structurePlanFile) {
        SAXReader reader = new SAXReader();
        try {
            Document d = reader.read(structurePlanFile);
            return isStructurePlan(d);
        } catch (DocumentException ex) {
            Logger.getLogger(PlanDocumentUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

  
    

    
}
