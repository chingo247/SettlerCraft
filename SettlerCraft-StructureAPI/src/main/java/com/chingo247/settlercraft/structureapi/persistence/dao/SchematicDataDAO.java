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
package com.chingo247.settlercraft.structureapi.persistence.dao;

import com.chingo247.settlercraft.structureapi.persistence.entities.schematic.SchematicDataNode;
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
