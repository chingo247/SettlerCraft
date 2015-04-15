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
package com.chingo247.structureapi.persistence.repository;

import com.chingo247.structureapi.structure.ConstructionStatus;
import com.chingo247.xplatform.core.IWorld;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.persistence.repository.settler.SettlerNode;
import com.chingo247.settlercraft.core.persistence.repository.world.WorldNode;
import com.chingo247.settlercraft.core.persistence.repository.world.WorldRepository;
import com.chingo247.settlercraft.core.regions.CuboidDimension;
import com.chingo247.settlercraft.core.regions.CuboidDimensional;
import com.chingo247.structureapi.persistence.repository.StructureNode;
import com.chingo247.structureapi.structure.State;
import com.chingo247.structureapi.world.Direction;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.util.com.google.common.collect.Lists;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class StructureRepository {

    private final GraphDatabaseService graph;
    private final WorldRepository worldRepository;

    public StructureRepository() {
        this(SettlerCraft.getInstance().getNeo4j());
    }
    
    public StructureRepository(GraphDatabaseService graph) {
        this.graph = graph;
        this.worldRepository = new WorldRepository(graph);
    }

    public StructureNode findStructureById(long id) {
        StructureNode structure = null;
        try (Transaction tx = graph.beginTx()) {
            Node n = graph.getNodeById(id);
            if (n != null) {
                structure = new StructureNode(n);
            }
            tx.success();
        }
        return structure;
    }
    
    public void deleteStructure(long id) {
        try (Transaction tx = graph.beginTx()) {
            Node n = graph.getNodeById(id);

            if (n != null) {
                n.delete();
            }
            tx.success();
        }
    }
    
    public StructureNode addStructureWithPlayer(SettlerNode settlerNode, WorldNode worldNode, String structureName, CuboidDimension dimension, Direction direction) {
        StructureNode structureNode = null;
        try (Transaction tx = graph.beginTx()) {
            Node stNode = graph.createNode(StructureNode.LABEL);
            stNode.setProperty(StructureNode.NAME_PROPERTY, structureName);
            stNode.setProperty(StructureNode.CONSTRUCTION_STATUS_PROPERTY, ConstructionStatus.ON_HOLD.getStatusId());
            stNode.setProperty(StructureNode.STATE_PROPERTY, State.CREATED.getStateId());
            stNode.setProperty(StructureNode.DIRECTION_PROPERTY, direction.getDirectionId());
            stNode.setProperty(StructureNode.MIN_X_PROPERTY, dimension.getMinX());
            stNode.setProperty(StructureNode.MIN_Y_PROPERTY, dimension.getMinY());
            stNode.setProperty(StructureNode.MIN_Z_PROPERTY, dimension.getMinZ());
            stNode.setProperty(StructureNode.MAX_X_PROPERTY, dimension.getMaxX());
            stNode.setProperty(StructureNode.MAX_Y_PROPERTY, dimension.getMaxY());
            stNode.setProperty(StructureNode.MAX_Z_PROPERTY, dimension.getMaxZ());
            stNode.setProperty(StructureNode.CREATED_AT_PROPERTY, System.currentTimeMillis());
            
            stNode.createRelationshipTo(worldNode.getRawNode(), DynamicRelationshipType.withName(WorldNode.RELATION_WITHIN));
            if(settlerNode != null) {
                Relationship owningRelation = settlerNode.getRawNode().createRelationshipTo(stNode, DynamicRelationshipType.withName(StructureNode.RELATION_OWNED_BY));
                owningRelation.setProperty("type", StructureOwnerTypes.MASTER);
            }
            
            
            structureNode = new StructureNode(stNode);
            tx.success();
        }
        return structureNode;
    }

    public StructureNode addStructure(WorldNode world, String structureName, CuboidDimension dimension, Direction direction) {
        return addStructureWithPlayer(null, world, structureName, dimension, direction);
    }
    
    public List<StructureNode> bulkCreateStructures(WorldNode node, List<StructureData> toCreate) {
        List<StructureNode> structures = Lists.newArrayList();
        try (Transaction tx = graph.beginTx()) {
            for(StructureData sd : toCreate) {
                Node stNode = graph.createNode(StructureNode.LABEL);
                stNode.setProperty(StructureNode.NAME_PROPERTY, sd.getName());
                stNode.setProperty(StructureNode.CONSTRUCTION_STATUS_PROPERTY, ConstructionStatus.ON_HOLD.getStatusId());
                stNode.setProperty(StructureNode.STATE_PROPERTY, State.CREATED.getStateId());
                stNode.setProperty(StructureNode.DIRECTION_PROPERTY, sd.getDirection().getDirectionId());
                
                CuboidDimension dimension = sd.getDimension();
                stNode.setProperty(StructureNode.MIN_X_PROPERTY, dimension.getMinX());
                stNode.setProperty(StructureNode.MIN_Y_PROPERTY, dimension.getMinY());
                stNode.setProperty(StructureNode.MIN_Z_PROPERTY, dimension.getMinZ());
                stNode.setProperty(StructureNode.MAX_X_PROPERTY, dimension.getMaxX());
                stNode.setProperty(StructureNode.MAX_Y_PROPERTY, dimension.getMaxY());
                stNode.setProperty(StructureNode.MAX_Z_PROPERTY, dimension.getMaxZ());
                stNode.setProperty(StructureNode.CREATED_AT_PROPERTY, System.currentTimeMillis());

                stNode.createRelationshipTo(node.getRawNode(), DynamicRelationshipType.withName(WorldNode.RELATION_WITHIN));
                structures.add(new StructureNode(stNode));
            }
            tx.success();
        }
        return structures;
    }

    public List<StructureNode> getStructuresWithin(World world, CuboidDimensional dimensional, int limit) {
        IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(world.getName());
        List<StructureNode> structures = new ArrayList<>();
        try (Transaction tx = graph.beginTx()) {
            CuboidDimension dim = dimensional.getDimension();
            String query = "MATCH (s:" + StructureNode.LABEL.name() + ")-[:"+WorldNode.RELATION_WITHIN+"]->(w: "+WorldNode.LABEL.name()+"{"+WorldNode.ID_PROPERTY+ ": '"+w.getUUID().toString()+"'} )"
                    + " WHERE "+ StructureNode.MAX_X_PROPERTY + " >= " + dim.getMinX() + " AND " + StructureNode.MIN_X_PROPERTY + " <= " + dim.getMaxX() 
                            + " AND " + StructureNode.MAX_Y_PROPERTY + " >= " + dim.getMinY() + " AND " + StructureNode.MIN_Y_PROPERTY + " <= " + dim.getMaxY()
                            + " AND " + StructureNode.MAX_Z_PROPERTY + " >= " + dim.getMinZ() + " AND " + StructureNode.MIN_Z_PROPERTY + " <= " + dim.getMaxZ()
                            + " AND " + StructureNode.STATE_PROPERTY + " != '" + State.DELETED.name() + "'"
                    + " RETURN s"
                    + " LIMIT " + limit;
            
            Result result = graph.execute(query);
            
            
            
            
            while(result.hasNext()) {
                Map<String,Object> map = result.next();
                
                System.out.println("Keys: " + Arrays.toString(map.keySet().toArray()));
                
            }
            
            tx.success();
        }
        return structures;
    }
    
    public boolean hasStructuresWithin(World world, CuboidDimensional dimensional) {
        return !getStructuresWithin(world, dimensional, 1).isEmpty();
    }

    
    
   
}
