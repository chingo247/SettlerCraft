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

import com.arangodb.ArangoDriver;
import com.arangodb.ArangoException;
import com.arangodb.ErrorNums;
import com.arangodb.entity.CursorEntity;
import com.arangodb.entity.DocumentEntity;
import com.arangodb.entity.EdgeDefinitionEntity;
import com.arangodb.util.MapBuilder;
import com.chingo247.settlercraft.persistence.ArangoDB;
import com.chingo247.settlercraft.persistence.graph.documents.StructureDocument;
import com.chingo247.settlercraft.persistence.entities.world.CuboidDimension;
import com.chingo247.settlercraft.regions.CuboidDimensional;
import com.chingo247.settlercraft.world.SettlerCraftWorld;
import com.sk89q.worldedit.Vector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles all database operations regarding structures
 *
 * @author Chingo
 */
public class StructureRepository {

//    private final OrientGraphFactory graphFactory;
    private final StructureFactory structureFactory;
    private final ArangoDriver ad;
    private static final String WORLD_GRAPH = "sc_world";
    private static final String STRUCTURE_COLLECTION = "Structures";

    public StructureRepository() {
//        File settlercraftDir = SettlerCraft.getInstance().getWorkingDirectory();
        ArangoDB db;
        try {
            db = new ArangoDB(8529, "localhost", null, null, "SettlerCraft");
        } catch (ArangoException ex) {
            throw new RuntimeException(ex);
        }

        this.ad = db.getDriver();
        
        
        try {
            if(!db.getGraphHelper().hasGraph(WORLD_GRAPH)) {
                List<EdgeDefinitionEntity> edgeDefinitions = new ArrayList<>();
                edgeDefinitions.add(createSubstructureDefinition());
                List<String> orphanCollections = new ArrayList<>();

                
                db.createNumericKeyCollection(STRUCTURE_COLLECTION);
                ad.createGraph(WORLD_GRAPH, edgeDefinitions, orphanCollections, true);
            }
            
            
        } catch (ArangoException ex) {
            Logger.getLogger(StructureRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.structureFactory = new StructureFactory();
    }

    private EdgeDefinitionEntity createSubstructureDefinition() {
        EdgeDefinitionEntity substructureDefinition = new EdgeDefinitionEntity();
        substructureDefinition.setCollection("SubStructures");
        
        List<String> from = new ArrayList<>();
        from.add(STRUCTURE_COLLECTION);
        substructureDefinition.setFrom(from);

        List<String> to = new ArrayList<>();
        to.add(STRUCTURE_COLLECTION);
        substructureDefinition.setTo(to);
        return substructureDefinition;
    }
    
    Structure save(Structure structure) {
        if (structure instanceof ComplexStructure) {

        } else if (structure instanceof SimpleStructure) {
            return saveSimple((SimpleStructure) structure);
        } else {
            throw new AssertionError("Can't handle structure of type: " + structure.getClass());
        }
        return structure;
    }

    private void saveComplex(ComplexStructure structureComplex) {
        
    }

    private Structure saveSimple(SimpleStructure simpleSt) {
        try {
            CuboidDimension d = simpleSt.getDimension();
            StructureDocument entity = new StructureDocument(
                    simpleSt.getId() != null ? String.valueOf(simpleSt.getId()) : null, 
                    simpleSt.getName(),
                    simpleSt.getWorld().getName(),
                    simpleSt.getWorld().getUUID().toString(), 
                    simpleSt.getDirection(),
                    d.getMinX(), d.getMinY(), d.getMinZ(), d.getMaxX(), d.getMaxY(), d.getMaxZ());
            
            DocumentEntity<StructureDocument> sd;
            if(simpleSt.getId() == null) {
                sd = ad.graphCreateVertex(WORLD_GRAPH, STRUCTURE_COLLECTION, entity, true);
            } else {
                sd = ad.graphUpdateVertex(WORLD_GRAPH, STRUCTURE_COLLECTION, String.valueOf(simpleSt.getId()), entity, Boolean.TRUE);
            }
            return structureFactory.createSimple(sd.getEntity());
        } catch (ArangoException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void delete(Structure structure) {
        delete(structure.getId());
    }

    public boolean delete(long id) {
        try {
            ad.graphDeleteVertex(WORLD_GRAPH, STRUCTURE_COLLECTION, String.valueOf(id), true);
            return true;
        } catch (ArangoException ex) {
            if(ex.getErrorNumber() != ErrorNums.ERROR_ARANGO_DOCUMENT_NOT_FOUND) {
                Logger.getLogger(StructureRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }
    }
    
    public SimpleStructure findSimple(long id) {
        try {
            DocumentEntity<StructureDocument> document = ad.graphGetVertex(WORLD_GRAPH, STRUCTURE_COLLECTION, String.valueOf(id), StructureDocument.class);
            return structureFactory.createSimple(document.getEntity());
        } catch (ArangoException ex) {
            if(ex.getErrorNumber() != ErrorNums.ERROR_ARANGO_DOCUMENT_NOT_FOUND) {
                Logger.getLogger(StructureRepository.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    
    public ComplexStructure findComplex(long id) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    public boolean overlaps(CuboidDimensional cuboidDimensional, SettlerCraftWorld world) throws ArangoException {
        CuboidDimension cd = cuboidDimensional.getDimension();
        Map<String,Object> map = new MapBuilder()
                .put("minX", cd.getMinX())
                .put("minY", cd.getMinY())
                .put("minZ", cd.getMinZ())
                .put("maxX", cd.getMaxX())
                .put("maxY", cd.getMaxY())
                .put("maxZ", cd.getMaxZ())
                .put("world", "test")
                .put("state", State.DELETED)
                .get();
                
        String query = "FOR s IN " + STRUCTURE_COLLECTION + " "
                + "FILTER s.maxX >= @minX && s.minX <= @maxX "
                + "&& s.maxY >= @minY && s.minY <= @maxY "
                + "&& s.maxZ >= @minZ && s.minZ <= @maxZ "
                + "&& s.world == @world "
                + "&& s.state != @state "
                + "LIMIT 1 "
                + "RETURN s";
        CursorEntity<StructureDocument> cursor = ad.executeQuery(query,map,  StructureDocument.class, false, 1);
        return cursor.getCount() > 0;    
    }
    
    public static void main(String[] args) {
        StructureRepository repository = new StructureRepository();
      
        try {  
            repository.overlaps(new CuboidDimension(Vector.ZERO, Vector.ONE), null);
            CursorEntity<StructureDocument> cursor = repository.ad.executeSimpleAll("Structures", 0, 10, StructureDocument.class);
            
            Iterator<StructureDocument> it = cursor.iterator();
            while(it.hasNext()) {
                StructureDocument entity = it.next();
            }
            
        } catch (ArangoException ex) {
            Logger.getLogger(StructureRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
