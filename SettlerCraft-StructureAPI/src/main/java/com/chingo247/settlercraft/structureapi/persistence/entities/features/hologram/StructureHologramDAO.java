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
package com.chingo247.settlercraft.structureapi.persistence.entities.features.hologram;

import com.chingo247.settlercraft.structureapi.persistence.dao.StructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.structure.ConstructionStatus;
import com.chingo247.settlercraft.structureapi.structure.Structure;
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
        Map<String,Object> params = Maps.newHashMap();
        params.put("removed", (Integer) ConstructionStatus.REMOVED.getStatusId());
        
        String query = "MATCH (h:"+StructureHologramNode.LABEL.name()+")"
                + "<-[r:"+StructureHologramNode.RELATION_HAS_HOLOGRAM.name()+"]-"
                + "(s:"+StructureNode.LABEL.name()+") "
                + "WHERE NOT s." + StructureNode.CONSTRUCTION_STATUS_PROPERTY + " = {removed}"
                + "RETURN h";
        Result r = graph.execute(query, params);
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
