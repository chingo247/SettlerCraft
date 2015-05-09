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
package com.chingo247.settlercraft.structureapi.structure.plan.xml.parser;

import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.SchematicPlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.document.PlacementElement;
import com.chingo247.settlercraft.structureapi.structure.plan.exception.PlanException;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.PlacementTypes;
import com.chingo247.settlercraft.structureapi.structure.plan.schematic.SchematicManager;
import com.sk89q.worldedit.Vector;
import java.io.File;
import org.dom4j.Document;

/**
 *
 * @author Chingo
 */
public class SchematicPlacementParser implements PlacementParser<SchematicPlacement> {

    @Override
    public SchematicPlacement parse(File xmlFile, Document d, PlacementElement placementElement) {
        String schematicPath = placementElement.getSchematic();
        if (schematicPath == null) {
            throw new PlanException("Element '" + placementElement.getElementName() + "' on line " + placementElement.getLine() + " doesn't have an element called 'Schematic', error occured in '" + placementElement.getFile().getAbsolutePath() + "'");
        }
        File schematicFile = new File(placementElement.getFile().getParent(), schematicPath);
        if (!schematicFile.exists()) {
            throw new PlanException("Error in '" + placementElement.getFile().getAbsolutePath() + "': File '" + schematicFile.getAbsolutePath() + "' defined in element '<Schematic>' does not exist!");
        }

        Vector pos = placementElement.getPosition();
        Direction direction = placementElement.getDirection();

        SchematicManager sdm = SchematicManager.getInstance();
        return new SchematicPlacement(sdm.getOrLoadSchematic(schematicFile), direction, pos);
    }

    @Override
    public String getType() {
        return PlacementTypes.SCHEMATIC;
    }

   
}
