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
package com.chingo247.structureapi.plan.placement.node;

import com.chingo247.structureapi.persistence.dao.placement.PlacementDataNode;
import com.chingo247.structureapi.persistence.dao.schematic.SchematicDataNode;
import com.chingo247.structureapi.plan.placement.SchematicPlacement;
import com.chingo247.structureapi.plan.schematic.Schematic;
import com.chingo247.structureapi.plan.schematic.SchematicManager;
import java.io.File;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Chingo
 */
public class NodeSchematicPlacementHandler extends NodePlacementHandler<SchematicPlacement>{

    @Override
    public SchematicPlacement fromNode(PlacementDataNode node, File structureDirectory) {
        Node n =  node.getRawNode();
        String schematic = (String)n.getProperty(SchematicDataNode.NAME_PROPERTY);
        File schematicFile = new File(structureDirectory, schematic);
        Schematic s = SchematicManager.getInstance().getOrLoadSchematic(schematicFile);
        return new SchematicPlacement(s);
    }

    @Override
    public void setNodeProperties(SchematicPlacement placement, Node node) {
        node.setProperty(SchematicDataNode.NAME_PROPERTY, placement.getSchematic().getFile().getName());
        node.setProperty(SchematicDataNode.XXHASH_PROPERTY, placement.getSchematic().getHash());
    }
    
}
