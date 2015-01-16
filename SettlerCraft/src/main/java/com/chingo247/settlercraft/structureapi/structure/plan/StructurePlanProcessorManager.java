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

import com.chingo247.settlercraft.structureapi.structure.plan.schematic.PlacementSchematicProcessorManager;
import com.chingo247.settlercraft.structureapi.exception.PlanException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 */
public class StructurePlanProcessorManager {

    private final Map<String, StructurePlanProcessor> structurePlanProcessors;

    public StructurePlanProcessorManager() {
        this.structurePlanProcessors = new HashMap<>();
    }

    public StructurePlanProcessor task(File structurePlan, StructurePlan parent, Node n, PlacementSchematicProcessorManager spm) throws DocumentException {
        String path = structurePlan.getAbsolutePath();
        synchronized (structurePlanProcessors) {
            StructurePlanProcessor task = structurePlanProcessors.get(path);
            if (task != null) {
                return task;
            } else {
                File structureFile = handleEmbeddedPlan(structurePlan, n);
                task = new StructurePlanProcessor(structureFile, parent, this, spm);
                structurePlanProcessors.put(path, task);
            }
            return task;
        }
    }

    File handleEmbeddedPlan(File structurePlan, Node n) throws DocumentException {
        String type = handleSubStructureType(n);
        if (!type.equalsIgnoreCase("Embedded")) {
            throw new PlanException("StructurePlan was not of type Embedded"); // Self CHeck!
        }
        Node fileNode = n.selectSingleNode("File");
        if (fileNode == null) {
            throw new PlanException("The 'File' element was not defined for the 'SubStructure' element");
        }
        File f = new File(structurePlan.getParent(), fileNode.getText());
        if (!f.exists()) {
            throw new PlanException("Couldn't resolve relative path '" + fileNode.getText() + "' + from XML element 'Substructure/File'");
        }
        if (!isStructurePlan(f)) {
            throw new PlanException("The 'File element doesn't reference to a 'StructurePlan-File' ");
        }
        return f;
    }

    private String handleSubStructureType(Node n) {
        Node typeNode = n.selectSingleNode("Type");
        if (typeNode == null) {
            throw new PlanException("The 'Type' element was not defined for Element 'SubStructure'");
        }
        return typeNode.getText().trim();

    }

    private boolean isStructurePlan(File file) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document d = reader.read(file);
        return d.getRootElement().getName().equals("StructurePlan");
    }

}
