/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.persistence.entities.features.hologram;

import com.chingo247.settlercraft.structureapi.persistence.dao.StructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.Vector;
import java.util.List;
import java.util.Map;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;

/**
 *
 * @author Chingo
 */
public class StructureHologramDAO {
    
    private GraphDatabaseService graph;
    private StructureDAO structureDAO;

    public StructureHologramDAO(GraphDatabaseService graph) {
        this.graph = graph;
        this.structureDAO = new StructureDAO(graph);
    }
    
    public void addHologram(Structure structure, Vector relativePosition) {
        Node n = graph.createNode(StructureHologramNode.LABEL);
        n.setProperty(StructureHologramNode.RELATIVE_X_PROPERTY, relativePosition.getBlockX());
        n.setProperty(StructureHologramNode.RELATIVE_Y_PROPERTY, relativePosition.getBlockY());
        n.setProperty(StructureHologramNode.RELATIVE_Z_PROPERTY, relativePosition.getBlockZ());
        StructureNode structureNode = structureDAO.find(structure.getId());
        structureNode.getRawNode().setProperty(StructureNode.CHECKED_HOLOGRAM_PROPERTY, true);
        structureNode.getRawNode().createRelationshipTo(n, StructureHologramNode.RELATION_HAS_HOLOGRAM);
    }
    
    public List<StructureHologramNode> findAll() {
        String query = "MATCH (h:"+StructureHologramNode.LABEL.name()+") RETURN h";
        Result r = graph.execute(query);
        List<StructureHologramNode> holograms = Lists.newArrayList();
        
        while(r.hasNext()) {
            Map<String,Object> map = r.next();
            for(Object o : map.values()) {
                Node n = (Node) o;
                StructureHologramNode shn = new StructureHologramNode(n);
                holograms.add(shn);
            }
        }
        return holograms;
    }
    
}
