/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.model.structure;

import com.chingo247.structureapi.model.interfaces.IStructureRepository;
import com.chingo247.settlercraft.core.Direction;
import com.chingo247.structureapi.model.world.StructureWorldNode;
import com.google.common.collect.Maps;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.Map;
import java.util.logging.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;

/**
 *
 * @author Chingo
 */
public class StructureRepository implements IStructureRepository {
    
    private static final Logger LOG = Logger.getLogger(IStructureRepository.class.getSimpleName());
    private final GraphDatabaseService graph;
    private boolean checked = false;

    public StructureRepository(GraphDatabaseService graph) {
        this.graph = graph;
    }

    @Override
    public StructureNode findById(Long id) {
        StructureNode structure = null;
        Map<String,Object> params = Maps.newHashMap();
        params.put("structureId", id);
       
        String query =
                  " MATCH (s:" + StructureNode.LABEL.name() + " { "+StructureNode.ID_PROPERTY+": {structureId} })"
                + " RETURN s as structure";
        
        Result result = graph.execute(query, params);
        
        
        while(result.hasNext()) {
            Node n = (Node) result.next().get("structure");
            structure = new StructureNode(n);
            break;
        }
        
        return structure;
    }
    
    private long nextId() {
        if(!checked) {
            Result r = graph.execute("MATCH (sid: ID_GENERATOR {name:'STRUCTURE_ID'}) "
                        + "RETURN sid "
                        + "LIMIT 1");
            if(!r.hasNext()) {
                graph.execute("CREATE (sid: ID_GENERATOR {name:'STRUCTURE_ID', nextId: 1})");
                checked = true;
                return 1;
            }
            checked = true;
        }
        
        // Work-around for getting the next Id
        // Sets the lock at this node by removing a non-existent property
        String idQuery = "MATCH (sid:ID_GENERATOR {name:'STRUCTURE_ID'}) "
                        +"REMOVE sid.lock " // NON-EXISTENT PROPERTY
                        +"SET sid.nextId = sid.nextId + 1 "
                        +"RETURN sid.nextId as nextId";
        Result r = graph.execute(idQuery);
        long id = (long) r.next().get("nextId");
        
        return id;
    }

    @Override
    public StructureNode addStructure(StructureWorldNode world, String name, Vector position, CuboidRegion region, Direction direction, double price) {
        long id = nextId();
        Node stNode = graph.createNode(StructureNode.LABEL);
        stNode.setProperty(StructureNode.ID_PROPERTY, id);
        stNode.setProperty(StructureNode.NAME_PROPERTY, name);
        stNode.setProperty(StructureNode.CONSTRUCTION_STATUS_PROPERTY, ConstructionStatus.ON_HOLD.getStatusId());
        stNode.setProperty(StructureNode.DIRECTION_PROPERTY, direction.getDirectionId());
        stNode.setProperty(StructureNode.POS_X_PROPERTY, position.getBlockX());
        stNode.setProperty(StructureNode.POS_Y_PROPERTY, position.getBlockY());
        stNode.setProperty(StructureNode.POS_Z_PROPERTY, position.getBlockZ());
        stNode.setProperty(StructureNode.MIN_X_PROPERTY, region.getMinimumPoint().getBlockX());
        stNode.setProperty(StructureNode.MIN_Y_PROPERTY, region.getMinimumPoint().getBlockY());
        stNode.setProperty(StructureNode.MIN_Z_PROPERTY, region.getMinimumPoint().getBlockZ());
        stNode.setProperty(StructureNode.MAX_X_PROPERTY, region.getMaximumPoint().getBlockX());
        stNode.setProperty(StructureNode.MAX_Y_PROPERTY, region.getMaximumPoint().getBlockY());
        stNode.setProperty(StructureNode.MAX_Z_PROPERTY, region.getMaximumPoint().getBlockZ());
        stNode.setProperty(StructureNode.CREATED_AT_PROPERTY, System.currentTimeMillis());
        stNode.setProperty(StructureNode.SIZE_PROPERTY, region.getArea());
        stNode.setProperty(StructureNode.PRICE_PROPERTY, price);
        StructureNode structure = new StructureNode(stNode);
        world.addStructure(structure);
        return structure;
    }
    
    
    
    

    
}
