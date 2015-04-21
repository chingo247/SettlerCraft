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
package com.chingo247.structureapi.persistence.dao.schematic;

import java.util.List;
import java.util.Map;
import net.minecraft.util.com.google.common.collect.Lists;
import net.minecraft.util.com.google.common.collect.Maps;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class SchematicDataDAO {
    
    private final GraphDatabaseService graph;

    public SchematicDataDAO(GraphDatabaseService graph) {
        this.graph = graph;
    }
    
    public SchematicDataNode find(long hash) {
        SchematicDataNode node = null;
        try(Transaction tx = graph.beginTx()) {
            Map<String,Object> parameters = Maps.newHashMap();
            parameters.put("hash", hash);
            String query = "MATCH(s:"+SchematicDataNode.LABEL_NAME+" { "+SchematicDataNode.XXHASH_PROPERTY + ": {hash} }) RETURN s as Schematic";
            
            Result r = graph.execute(query, parameters);
            if(r.hasNext()) {
                Map<String,Object> n = r.next();
                Node resultNode = (Node) n.get("Schematic");
                node = new SchematicDataNode(resultNode);
            }
            tx.success();
        }
        return node;
    }
    
    public List<SchematicDataNode> findSchematicsBeforeDate(long date) {
        List<SchematicDataNode> schematics = Lists.newArrayList();
        try(Transaction tx = graph.beginTx()) {
            Map<String,Object> parameters = Maps.newHashMap();
            parameters.put("date", date);
            String query = "MATCH(s:"+SchematicDataNode.LABEL_NAME+") WHERE s."+SchematicDataNode.LAST_IMPORT+" < {date}  RETURN s as Schematics";
            
            Result r = graph.execute(query, parameters);
            while(r.hasNext()) {
                Map<String,Object> n = r.next();
                Node resultNode = (Node) n.get("Schematics");
                schematics.add(new SchematicDataNode(resultNode));
            }
            tx.success();
        }
        return schematics;
    }
    
     public List<SchematicDataNode> findSchematicsAfterDate(long date) {
        List<SchematicDataNode> schematics = Lists.newArrayList();
        try(Transaction tx = graph.beginTx()) {
            Map<String,Object> parameters = Maps.newHashMap();
            parameters.put("date", date);
            String query = "MATCH(s:"+SchematicDataNode.LABEL_NAME+") WHERE s."+SchematicDataNode.LAST_IMPORT+" > {date}  RETURN s as Schematics";
            
            Result r = graph.execute(query, parameters);
            while(r.hasNext()) {
                Map<String,Object> n = r.next();
                Node resultNode = (Node) n.get("Schematics");
                schematics.add(new SchematicDataNode(resultNode));
            }
            tx.success();
        }
        return schematics;
    }
    
    public void addSchematic(String name, long xxhash64, int width, int height, int length, long importDate) {
        Node n = graph.createNode(SchematicDataNode.LABEL);
        n.setProperty(SchematicDataNode.NAME_PROPERTY, name);
        n.setProperty(SchematicDataNode.WIDTH_PROPERTY, width);
        n.setProperty(SchematicDataNode.HEIGHT_PROPERTY, height);
        n.setProperty(SchematicDataNode.LENGTH_PROPERTY, length);
        n.setProperty(SchematicDataNode.XXHASH_PROPERTY, xxhash64);
        n.setProperty(SchematicDataNode.LAST_IMPORT, importDate);
    } 
     
    
}
