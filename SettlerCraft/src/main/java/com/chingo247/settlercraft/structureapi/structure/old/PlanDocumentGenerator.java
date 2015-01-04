
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
package com.chingo247.settlercraft.structureapi.structure.old;

import com.chingo247.settlercraft.structureapi.exception.StructureDataException;
import com.chingo247.settlercraft.structureapi.structure.old.AbstractStructureAPI;
import com.chingo247.settlercraft.structureapi.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.plan.StructurePlanManager;
import com.chingo247.settlercraft.structureapi.plan.document.PlanDocument;
import com.chingo247.settlercraft.structureapi.plan.document.PlanDocumentPluginElement;
import com.chingo247.settlercraft.util.document.Elements;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;

/**
 *
 * @author Chingo
 */
public class PlanDocumentGenerator {
    
    private final AbstractStructureAPI structureAPI;
    
    public PlanDocumentGenerator(AbstractStructureAPI structureAPI) {
        this.structureAPI = structureAPI;
    }
    
    public void generate(File targetFolder) {
        // Scan the folder called 'SchematicToPlan' for schematic files
        Iterator<File> it = FileUtils.iterateFiles(targetFolder, new String[]{"schematic"}, true);

        int count = 0;
        long start = System.currentTimeMillis();
        

        // Generate Plans
        while (it.hasNext()) {
            File schematic = it.next();

            Document d = DocumentHelper.createDocument();
            d.addElement(Elements.ROOT)
                    .addElement(Elements.SETTLERCRAFT)
                    .addElement(Elements.SCHEMATIC)
                    .setText(schematic.getName());

            File plan = new File(schematic.getParent(), FilenameUtils.getBaseName(schematic.getName()) + ".xml");

            try {
                
                XMLWriter writer = new XMLWriter(new FileWriter(plan));
                writer.write(d);
                writer.close();
                
                
                StructurePlan sp = new StructurePlan();
                PlanDocument pd = new PlanDocument(structureAPI.getPlanDocumentManager(), plan);
                pd.putPluginElement("SettlerCraft", new PlanDocumentPluginElement("SettlerCraft", pd,(Element) d.selectSingleNode("StructurePlan/SettlerCraft")));
                sp.load(pd);
               
                if (sp.getCategory().equals("Default") && !schematic.getParentFile().getName().equals(targetFolder.getName())) {
                    sp.setCategory(schematic.getParentFile().getName());
                }

                sp.save();

            } catch (DocumentException ex) {
                Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | StructureDataException ex) {
                Logger.getLogger(StructurePlanManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            count++;
        }
        if (count > 0) {
            structureAPI.print("Generated " + count + " plans in " + (System.currentTimeMillis() - start) + "ms");
        }
    }
    
}
