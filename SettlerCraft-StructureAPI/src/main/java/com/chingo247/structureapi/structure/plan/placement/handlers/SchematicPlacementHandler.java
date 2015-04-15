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
package com.chingo247.structureapi.structure.plan.placement.handlers;

import com.chingo247.settlercraft.core.util.LogLevel;
import com.chingo247.settlercraft.core.util.SCLogger;
import com.chingo247.structureapi.world.Direction;
import com.chingo247.structureapi.structure.StructureAPI;
import com.chingo247.structureapi.structure.plan.exception.PlanException;
import com.chingo247.structureapi.structure.plan.placement.SchematicPlacement;
import com.chingo247.structureapi.structure.plan.document.PlacementElement;
import com.chingo247.structureapi.structure.plan.schematic.SchematicManager;
import com.sk89q.worldedit.Vector;
import java.io.File;
import org.dom4j.Element;
import org.dom4j.tree.BaseElement;

/**
 *
 * @author Chingo
 */
public class SchematicPlacementHandler extends PlacementHandler<SchematicPlacement> {

    private final SCLogger LOG = SCLogger.getLogger();

    public SchematicPlacementHandler() {
        super(StructureAPI.PLUGIN_NAME, "Schematic");
    }

    @Override
    public SchematicPlacement handle(PlacementElement e) throws PlanException {
        String schematicPath = e.getSchematic();
        if (schematicPath == null) {
            throw new PlanException("Element '" + e.getElementName() + "' on line " + e.getLine() + " doesn't have an element called 'Schematic', error occured in '" + e.getFile().getAbsolutePath() + "'");
        }
        File schematicFile = new File(e.getFile().getParent(), schematicPath);
        if (!schematicFile.exists()) {
            throw new PlanException("Error in '" + e.getFile().getAbsolutePath() + "': File '" + schematicFile.getAbsolutePath() + "' defined in element '<Schematic>' does not exist!");
        }

        long start = System.currentTimeMillis();

        Vector pos = e.getPosition();
        Direction direction = e.getDirection();

        SchematicManager sdm = SchematicManager.getInstance();

        LOG.print(LogLevel.INFO, schematicFile, "Schematic", System.currentTimeMillis() - start);
        return new SchematicPlacement(sdm.getOrLoadSchematic(schematicFile), direction, pos);
    }

    @Override
    public SchematicPlacement copy(SchematicPlacement t) {
        return new SchematicPlacement(t.getSchematic(), t.getDirection(), t.getPosition());
    }

    @Override
    public Element asElement(SchematicPlacement t) {
        // Define type element
        Element typeElement = new BaseElement("Type");
        typeElement.setText("Schematic");
        // Define schematic file/reference
        Element schematicElement = new BaseElement("Schematic");
//        schematicElement.setText(t.);

        Element placementElement = new BaseElement("Placement");
//        placementElement.add(new BaseElement);

        // Relative Path????!
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
