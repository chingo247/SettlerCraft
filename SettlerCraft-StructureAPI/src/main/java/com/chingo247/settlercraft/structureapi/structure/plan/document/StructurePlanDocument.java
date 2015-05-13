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
package com.chingo247.settlercraft.structureapi.structure.plan.document;

import com.chingo247.settlercraft.core.util.XXHasher;
import com.google.common.base.Preconditions;
import fiber.core.impl.BuildContextImpl;
import fiber.core.impl.xml.ModelReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import com.chingo247.settlercraft.structureapi.structure.plan.xml.StructurePlanXMLConstants;

/**
 *
 * @author Chingo
 */
public class StructurePlanDocument extends LineElement {
    

    private final File file;
    private final Document doc;
    
    StructurePlanDocument(File f, Document d) {
        super(f, d.getRootElement());
        Preconditions.checkNotNull(f);
        Preconditions.checkNotNull(d);
        
        this.file = f;
        this.doc = d;
    }
    
    public boolean hasSubStructureElements() {
        return le.selectSingleNode(StructurePlanXMLConstants.STRUCTURE_PLAN_SUBSTRUCTURES) != null;
    }
    
    public List<SubStructureElement> getSubStructureElements() {
        List<Node> nodes = le.selectNodes(StructurePlanXMLConstants.STRUCTURE_PLAN_SUBSTRUCTURES + "/" + StructurePlanXMLConstants.STRUCTURE_PLAN_SUBSTRUCTURE);
        List<SubStructureElement> elements = new ArrayList<>(nodes.size());
        for(Node n : nodes) {
            elements.add(new SubStructureElement(getFile(),(Element) n));
        }
        return elements;
    }
    
    public String getReferenceId() {
        Node idNode = doc.selectSingleNode(StructurePlanXMLConstants.STRUCTURE_PLAN_ROOT_ELEMENT + "/" + StructurePlanXMLConstants.STRUCTURE_PLAN_ID_ELEMENT);
        if(idNode == null) {
            String path = file.getAbsolutePath();
            return String.valueOf(new XXHasher().hash32String(path));
        }
        LineElement element = new LineElement(getFile(),(Element) idNode);
        element.checkNotEmpty();
        return element.getTextValue();
    }
    
    public String getDescription() {
        Node descNode = doc.selectSingleNode(StructurePlanXMLConstants.STRUCTURE_PLAN_ROOT_ELEMENT + "/" + StructurePlanXMLConstants.STRUCTURE_PLAN_DESCRIPTION_ELEMENT);
        if(descNode == null) {
            return  "None";
        }
        LineElement element = new LineElement(getFile(),(Element) descNode);
        element.checkNotEmpty();
        return element.getTextValue();
    }
    
    public String getName() {
        Node nameNode = doc.selectSingleNode(StructurePlanXMLConstants.STRUCTURE_PLAN_ROOT_ELEMENT + "/" + StructurePlanXMLConstants.STRUCTURE_PLAN_NAME_ELEMENT);
        if(nameNode == null) {
            return  FilenameUtils.getBaseName(file.getName());
        }
        LineElement element = new LineElement(getFile(),(Element) nameNode);
        element.checkNotEmpty();
        return element.getTextValue();
    }
    
    public double getPrice() {
        Node priceNode = doc.selectSingleNode(StructurePlanXMLConstants.STRUCTURE_PLAN_ROOT_ELEMENT + "/" + StructurePlanXMLConstants.STRUCTURE_PLAN_PRICE_ELEMENT);
        if(priceNode == null) {
            return 0.0;
        } 
        LineElement priceElement = new LineElement(getFile(),(Element) priceNode);
        return priceElement.getDoubleValue();
    }
   
    public PlacementElement getPlacementElement() {
        checkNotNull(StructurePlanXMLConstants.STRUCTURE_PLAN_PLACEMENT);
        Node n = le.selectSingleNode(StructurePlanXMLConstants.STRUCTURE_PLAN_PLACEMENT);
        return new PlacementElement(getFile(),(Element) n);
    }
    
    public static StructurePlanDocument read(File xmlFile) throws DocumentException {
        ModelReader reader = ModelReader.getNonValidatingInstance(new BuildContextImpl());
        Document d = reader.read(xmlFile);
        return new StructurePlanDocument(xmlFile, d);
    }

    public String getCategory() {
        Node categoryNode = doc.selectSingleNode(StructurePlanXMLConstants.STRUCTURE_PLAN_ROOT_ELEMENT + "/" + StructurePlanXMLConstants.STRUCTURE_PLAN_CATEGORY_ELEMENT);
        if(categoryNode == null) {
            return  null;
        }
        LineElement element = new LineElement(getFile(),(Element) categoryNode);
        element.checkNotEmpty();
        return element.getTextValue();
    }
    
    
    
}
