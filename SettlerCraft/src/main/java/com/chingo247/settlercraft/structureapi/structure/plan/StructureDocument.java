/*
 * Copyright (C) 2014 Chingo
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

import com.chingo247.settlercraft.structureapi.plan.document.PlanDocumentManager;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMDocument;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 *
 * @author Chingo
 */
public class StructureDocument {

    private final Document document;
    private final File config;
    private boolean isDirty;
    
    public StructureDocument(File config) throws DocumentException {
        this.isDirty = false;
        this.config = config;
        this.document = new SAXReader().read(config);
    }
    
    public File getDirectory() {
        return config.getParentFile();
    }
    
    public boolean supports(String pluginName) {
        return document.selectSingleNode("StructurePlan/" + pluginName) != null;
    }

    synchronized void setElement(PluginElement element) {
        isDirty = true;
        Node n = document.selectSingleNode(element.getPluginName());
        if (n != null) {
            n.detach();
        }
        document.add(element.toElement());
    }
    
    /**
     * Returns a deep copy of the element with the given name. 
     * @param pluginName
     * @return 
     */
    public Element getElement(String pluginName) {
        Element e = ((Element) document.selectSingleNode("StructurePlan/" + pluginName));
        if(e != null) {
            return e.createCopy();
        }
        return null;
    }

    /**
     * Writes to file
     */
    public synchronized void save() {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setExpandEmptyElements(true);
        XMLWriter writer = new XMLWriter();
        try {
            writer = new XMLWriter(new FileWriter(config), format);
            writer.write(document);
        } catch (IOException ex) {
            Logger.getLogger(PlanDocumentManager.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(PlanDocumentManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        isDirty = false;
    }

    public synchronized void saveChanges() {
        if (isDirty) {
            save();
        }
    }

}
