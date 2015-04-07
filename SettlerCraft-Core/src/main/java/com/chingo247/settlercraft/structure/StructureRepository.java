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
package com.chingo247.settlercraft.structure;

import com.chingo247.settlercraft.SettlerCraft;
import com.chingo247.settlercraft.persistence.entities.world.CuboidDimension;
import com.chingo247.settlercraft.regions.CuboidDimensional;
import com.chingo247.settlercraft.world.SettlerCraftWorld;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import java.io.File;

/**
 * Handles all database operations regarding structures
 * @author Chingo
 */
public class StructureRepository {
    
    private final OrientGraphFactory factory;
    
    public StructureRepository() {
        File settlercraftDir = SettlerCraft.getInstance().getWorkingDirectory();
        String url = "plocal:" + settlercraftDir.getAbsolutePath() + "/database";
        
        OrientGraph graph = new OrientGraph(url);
        graph.shutdown();
        
        this.factory = new OrientGraphFactory(url).setupPool(1, 10);
    }
    
    public Structure find(long id) {
        throw new UnsupportedOperationException("Not supported yet");
    }
    
    public Structure save(Structure structure) {
        if(structure instanceof StructureComplex) {
            
        } else if (structure instanceof SimpleStructure) {
            
        } else {
            throw new AssertionError("Can't handle structure of type: " + structure.getClass());
        }
        return structure;
    }
    
    
    
    private void saveComplex(StructureComplex structureComplex) {
        
    }
    
    private void saveSimple(SimpleStructure simpleStructure) {
        
    }
    
    public void delete(Structure structure) {
        delete(structure.getId());
    }
    
    public void delete(long id) {
        
    }

    public boolean overlaps(CuboidDimensional cuboidDimensional, SettlerCraftWorld world) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    protected Vertex formVertex(Structure structure, OrientGraph graph) {
        Vertex vertex = graph.addVertex(null);
        vertex.setProperty("id", structure.getId());
        vertex.setProperty("name", structure.getName());
        vertex.setProperty("direction", structure.getDirection().name());
        
        CuboidDimension cb = structure.getDimension();
        vertex.setProperty("minX", cb.getDimension().getMinX());
        vertex.setProperty("minY", cb.getDimension().getMinY());
        vertex.setProperty("minZ", cb.getDimension().getMinZ());
        vertex.setProperty("maxX", cb.getDimension().getMaxX());
        vertex.setProperty("maxY", cb.getDimension().getMaxY());
        vertex.setProperty("maxZ", cb.getDimension().getMaxZ());
        return vertex;
    }
  
}
