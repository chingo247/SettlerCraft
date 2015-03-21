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
package com.chingo247.settlercraft.structure.plan.document;

import com.google.common.base.Preconditions;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
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

/**
 *
 * @author Chingo
 */
public class StructurePlanDocument extends SimpleElement {
    
    private static final HashFunction hashFunction = Hashing.md5();

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
        return le.selectSingleNode("Substructures") != null;
    }
    
    public List<SubStructureElement> getSubStructureElements() {
        List<Node> nodes = le.selectNodes("SubStructures/SubStructure");
        List<SubStructureElement> elements = new ArrayList<>(nodes.size());
        for(Node n : nodes) {
            elements.add(new SubStructureElement(getFile(),(Element) n));
        }
        return elements;
    }
    
    public String getReferenceId() {
        Node idNode = doc.selectSingleNode("Id");
        if(idNode == null) {
            String path = file.getAbsolutePath();
            return hashFunction.hashString(path).toString();
        }
        SimpleElement element = new SimpleElement(getFile(),(Element) idNode);
        element.checkNotEmpty();
        return element.getTextValue();
    }
    
    public String getDescription() {
        Node descNode = doc.selectSingleNode("Description");
        if(descNode == null) {
            return  "None";
        }
        SimpleElement element = new SimpleElement(getFile(),(Element) descNode);
        element.checkNotEmpty();
        return element.getTextValue();
    }
    
    public String getName() {
        Node nameNode = doc.selectSingleNode("Name");
        if(nameNode == null) {
            return  FilenameUtils.getBaseName(file.getName());
        }
        SimpleElement element = new SimpleElement(getFile(),(Element) nameNode);
        element.checkNotEmpty();
        return element.getTextValue();
    }
    
    public double getPrice() {
        Node priceNode = doc.selectSingleNode("Price");
        if(priceNode == null) {
            return 0.0;
        } 
        SimpleElement priceElement = new SimpleElement(getFile(),(Element) priceNode);
        return priceElement.getDoubleValue();
    }
   
    public PlacementElement getPlacementElement() {
        checkNotNull("Placement");
        Node n = le.selectSingleNode("Placement");
        return new PlacementElement(getFile(),(Element) n);
    }
    
    public static StructurePlanDocument read(File xmlFile) throws DocumentException {
        ModelReader reader = ModelReader.getNonValidatingInstance(new BuildContextImpl());
        Document d = reader.read(xmlFile);
        return new StructurePlanDocument(xmlFile, d);
    }
    
    
    
}
