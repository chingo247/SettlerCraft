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
package com.chingo247.settlercraft.structure.placement.handler;

import com.chingo247.settlercraft.exception.PlacementHandleException;
import com.chingo247.settlercraft.exception.PlanException;
import com.chingo247.settlercraft.structure.placement.SchematicPlacement;
import com.chingo247.settlercraft.structure.plan.xml.XMLUtils;
import java.io.File;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 *
 * @author Chingo
 */
public class SchematicPlacementHandler extends PlacementHandler<SchematicPlacement> {

    public SchematicPlacementHandler() {
        super("SettlerCraft", "Schematic");
    }

    @Override
    public SchematicPlacement handle(Element e) {
        String schematic = XMLUtils.getXPathNodeValue(e, "Schematic");
        if (schematic == null) {
            throw new PlacementHandleException("Element '" + e.getName() + "' doesn't have an element called 'Schematic'");
        }
        File schematicFile = new File(structurePlanFile.getParent(), schematic);
        if (!schematicFile.exists()) {
            throw new PlanException("Error in '" + structurePlanFile.getAbsolutePath() + "': File '" + schematicFile.getAbsolutePath() + "' defined in element '<Schematic>' does not exist!");
        }

    }

    @Override
    public SchematicPlacement copy(SchematicPlacement t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Element asDocument(SchematicPlacement t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
