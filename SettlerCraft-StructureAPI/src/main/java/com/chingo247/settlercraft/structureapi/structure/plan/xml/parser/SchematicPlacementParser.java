/*
 * Copyright (C) 2015 Chingo
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
