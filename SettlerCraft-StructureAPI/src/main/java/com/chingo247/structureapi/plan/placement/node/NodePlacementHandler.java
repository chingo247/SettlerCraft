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
import com.chingo247.structureapi.plan.placement.Placement;
import java.io.File;
import org.neo4j.graphdb.Node;

/**
 *
 * @author Chingo
 * @param <T> The placementType
 */
public abstract class NodePlacementHandler <T extends Placement> {
    
    public abstract T fromNode(PlacementDataNode node, File StructureDirectory);
    
    /**
     * Implementation should set the properties that need to be persisted into the database.
     * By default the width, height, length and type are already set
     * @param placement The placement
     * @param node The node
     */
    public abstract void setNodeProperties(T placement, Node node);
    
    
}
