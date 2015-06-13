/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.model;

import com.chingo247.settlercraft.structureapi.model.structure.StructureStatus;
import com.chingo247.settlercraft.structureapi.model.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureHologram;
import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureHologramRepository;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
public class StructureHologramRepository implements IStructureHologramRepository {
    
    private GraphDatabaseService graph;

    public StructureHologramRepository(GraphDatabaseService graph) {
        this.graph = graph;
    }
    
    @Override
    public IStructureHologram addHologram(StructureNode structure, Vector relativePosition) {
        Node n = graph.createNode(StructureHologramNode.LABEL);
        n.setProperty(StructureHologramNode.RELATIVE_X_PROPERTY, relativePosition.getBlockX());
        n.setProperty(StructureHologramNode.RELATIVE_Y_PROPERTY, relativePosition.getBlockY());
        n.setProperty(StructureHologramNode.RELATIVE_Z_PROPERTY, relativePosition.getBlockZ());
        structure.getNode().setProperty(StructureNode.CHECKED_HOLOGRAM_PROPERTY, true);
        structure.getNode().createRelationshipTo(n, StructureHologramNode.RELATION_HAS_HOLOGRAM);
        return new StructureHologramNode(n);
    }
    
    @Override
    public List<IStructureHologram> findAll() {
        Map<String,Object> params = Maps.newHashMap();
        params.put("removed", (Integer) StructureStatus.REMOVED.getStatusId());
        
        String query = "MATCH (h:"+StructureHologramNode.LABEL.name()+")"
                + "<-[r:"+StructureHologramNode.RELATION_HAS_HOLOGRAM.name()+"]-"
                + "(s:"+StructureNode.LABEL.name()+") "
                + "WHERE NOT s." + StructureNode.CONSTRUCTION_STATUS_PROPERTY + " = {removed}"
                + "RETURN h";
        Result r = graph.execute(query, params);
        List<IStructureHologram> holograms = Lists.newArrayList();
        
        while(r.hasNext()) {
            Map<String,Object> map = r.next();
            for(Object o : map.values()) {
                Node n = (Node) o;
                IStructureHologram shn = new StructureHologramNode(n);
                holograms.add(shn);
            }
        }
        return holograms;
    }
    
}
