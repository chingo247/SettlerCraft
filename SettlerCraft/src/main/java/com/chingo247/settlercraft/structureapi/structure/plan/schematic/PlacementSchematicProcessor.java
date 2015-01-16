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
package com.chingo247.settlercraft.structureapi.structure.plan.schematic;

import com.chingo247.settlercraft.structureapi.persistence.hibernate.SchematicDataDAO;
import com.chingo247.settlercraft.structureapi.structure.plan.Placement;
import com.chingo247.settlercraft.structureapi.structure.plan.PlacementProcessor;
import com.chingo247.settlercraft.structureapi.structure.plan.schematic.SchematicData;
import com.chingo247.settlercraft.structureapi.structure.plan.schematic.SchematicData;
import com.chingo247.settlercraft.structureapi.structure.plan.XMLUtils;
import com.chingo247.settlercraft.structureapi.structure.plan.XMLUtils;
import com.chingo247.settlercraft.structureapi.structure.plan.XMLUtils;
import com.chingo247.settlercraft.structureapi.world.Direction;
import com.chingo247.settlercraft.util.LogLevel;
import com.chingo247.settlercraft.util.Logger;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;
import java.io.File;
import java.io.IOException;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author Chingo
 */
public class PlacementSchematicProcessor extends PlacementProcessor {
    
    private final File schematiFile;
    private final Logger LOG = new Logger();

    public PlacementSchematicProcessor(File structurePlan, Node placeableNode, File schematicFile) {
        super(structurePlan, placeableNode);
        this.schematiFile = schematicFile;
    }

    @Override
    protected SchematicPlacement compute() {
        
        try {
            long start = System.currentTimeMillis();
            long checksum = FileUtils.checksumCRC32(schematiFile);
            
            Element pe = (Element) placeableNode;
            Vector pos;
             
            if(XMLUtils.hasPosition(pe)) pos = XMLUtils.getXYZFrom((Element)pe.selectSingleNode("Position"));
            else pos = Vector.ZERO;
            
            Direction direction;
            if(XMLUtils.hasDirection(pe)) direction = XMLUtils.getDirectionFrom(pe);
            else direction = Direction.NORTH;
            
            SchematicPlacement schematic = new SchematicPlacement(schematiFile, checksum, direction, pos);
            LOG.print(LogLevel.INFO, schematiFile, "Schematic", System.currentTimeMillis() - start);
            return schematic;
            
        } catch (IOException  ex) {
            java.util.logging.Logger.getLogger(PlacementSchematicProcessor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
}