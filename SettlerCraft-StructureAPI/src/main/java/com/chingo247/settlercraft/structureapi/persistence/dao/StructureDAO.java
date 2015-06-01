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

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import com.chingo247.settlercraft.structureapi.structure.ConstructionStatus;
import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureRelTypes;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.xplatform.core.IWorld;
import com.google.common.collect.Maps;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class StructureDAO implements IStructureDAO{

    private static final Logger LOG = Logger.getLogger(StructureDAO.class.getSimpleName());
    private final GraphDatabaseService graph;
    private boolean checked = false;

    public StructureDAO(GraphDatabaseService graph) {
        this.graph = graph;
    }

    @Override
    public StructureNode find(long id) {
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
    public StructureNode addStructure(String name, Vector position, CuboidRegion region, Direction direction, double price) {
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
        return new StructureNode(stNode);
    }

    @Override
    public List<StructureNode> getStructuresWithin(World world, CuboidRegion region, int limit) {
        IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(world.getName());
        long start = System.currentTimeMillis();
        List<StructureNode> structures = new ArrayList<>();
      
        Map<String,Object> params = Maps.newHashMap();
        params.put("worldId", w.getUUID().toString());
       
        String query = 
                   "MATCH (world:"+WorldNode.LABEL.name()+" { "+WorldNode.ID_PROPERTY+": {worldId} })"
                + " WITH world "
                + " MATCH (world)<-[:" + StructureRelTypes.RELATION_WITHIN + "]-(s:" + StructureNode.LABEL.name() + ")"
                + " WHERE s." + StructureNode.DELETED_AT_PROPERTY + " IS NULL"
                + " AND NOT s." + StructureNode.CONSTRUCTION_STATUS_PROPERTY + " = " + ConstructionStatus.REMOVED.getStatusId()
                + " AND s." + StructureNode.MAX_X_PROPERTY + " >= " + region.getMinimumPoint().getBlockX() + " AND s." + StructureNode.MIN_X_PROPERTY + " <= " + region.getMaximumPoint().getBlockX()
                + " AND s." + StructureNode.MAX_Y_PROPERTY + " >= " + region.getMinimumPoint().getBlockY() + " AND s." + StructureNode.MIN_Y_PROPERTY + " <= " + region.getMaximumPoint().getBlockY()
                + " AND s." + StructureNode.MAX_Z_PROPERTY + " >= " + region.getMinimumPoint().getBlockZ() + " AND s." + StructureNode.MIN_Z_PROPERTY + " <= " + region.getMaximumPoint().getBlockZ()
                + " RETURN s"
                + " LIMIT " + limit;
        Result result = graph.execute(query, params);
        while (result.hasNext()) {
            Map<String, Object> map = result.next();
            for (Object o : map.values()) {
                structures.add(new StructureNode((Node) o));
            }
        }

        return structures;
    }
    
    @Override
    public List<StructureNode> getSubStructuresWithinStructure(Structure parent, World world, CuboidRegion region, int limit) {
        IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(world.getName());
        long start = System.currentTimeMillis();
        List<StructureNode> structures = new ArrayList<>();
      
        Map<String,Object> params = Maps.newHashMap();
        params.put("worldId", w.getUUID().toString());
       
        String query = 
                   "MATCH (world:"+WorldNode.LABEL.name()+" { "+WorldNode.ID_PROPERTY+": {worldId} })"
                + " WITH world "
                + " MATCH (world)<-[:" + StructureRelTypes.RELATION_WITHIN + "]-(s:" + StructureNode.LABEL.name() + ")"
                + " WHERE s." + StructureNode.DELETED_AT_PROPERTY + " IS NULL"
                + " AND NOT s." + StructureNode.ID_PROPERTY + " = " + parent.getId()
                + " AND NOT s." + StructureNode.CONSTRUCTION_STATUS_PROPERTY + " = " + ConstructionStatus.REMOVED.getStatusId()
                + " AND s." + StructureNode.SIZE_PROPERTY  + " <= " + parent.getCuboidRegion().getArea()
                + " AND s." + StructureNode.MAX_X_PROPERTY + " >= " + region.getMinimumPoint().getBlockX() + " AND s." + StructureNode.MIN_X_PROPERTY + " <= " + region.getMaximumPoint().getBlockX()
                + " AND s." + StructureNode.MAX_Y_PROPERTY + " >= " + region.getMinimumPoint().getBlockY() + " AND s." + StructureNode.MIN_Y_PROPERTY + " <= " + region.getMaximumPoint().getBlockY()
                + " AND s." + StructureNode.MAX_Z_PROPERTY + " >= " + region.getMinimumPoint().getBlockZ() + " AND s." + StructureNode.MIN_Z_PROPERTY + " <= " + region.getMaximumPoint().getBlockZ()
                + " RETURN s"
                + " LIMIT " + limit;
        Result result = graph.execute(query, params);
        while (result.hasNext()) {
            Map<String, Object> map = result.next();
            for (Object o : map.values()) {
                structures.add(new StructureNode((Node) o));
            }
        }
        
        return structures;
    }
    
    @Override
    public boolean hasSubstructuresWithin(Structure parent, World world, CuboidRegion region) {
        return !getSubStructuresWithinStructure(parent, world, region, 1).isEmpty();
    }

    @Override
    public boolean hasStructuresWithin(World world, CuboidRegion region) {
        return !getStructuresWithin(world, region, 1).isEmpty();
    }

    @Override
    public List<StructureNode> getStructuresForSettler(UUID settler, int skip, int limit) {
        long start = System.currentTimeMillis();
        List<StructureNode> structures = new ArrayList<>();
        
        Map<String,Object> params = Maps.newHashMap();
        params.put("ownerId", settler.toString());
        if(skip > 0) {
            params.put("skip", skip);
        }
        
        if(limit > 0) {
            params.put("limit", limit);
        }
        
            String query =
                    "   MATCH (settler:"+SettlerNode.LABEL.name()+" {"+SettlerNode.UUID_PROPERTY+": {ownerId} })"
                    + " WITH settler"
                    + " MATCH (settler)<-[:" + StructureRelTypes.RELATION_OWNED_BY + "]-(s: " + StructureNode.LABEL.name() + ")"
                    + " WHERE NOT s." + StructureNode.CONSTRUCTION_STATUS_PROPERTY + " = " + ConstructionStatus.REMOVED.getStatusId()
                    + " RETURN s"
                    + " ORDER BY s." + StructureNode.CREATED_AT_PROPERTY + " DESC ";

            if (skip > 0) {
                query += " SKIP {skip}";
            }

            if (limit > 0) {
                query += " LIMIT {limit}";
            }

            Result result = graph.execute(query, params);
            while (result.hasNext()) {
                for (Object o : result.next().values()) {
                    StructureNode structureNode = new StructureNode((Node) o);
                    structures.add(structureNode);
                }
            }

        return structures;
    }

    @Override
    public long getStructureCountForSettler(UUID settler) {
        long count = 0;
        long start = System.currentTimeMillis();
        try (Transaction tx = graph.beginTx()) {
            
            Map<String,Object> params = Maps.newHashMap();
            params.put("ownerId", settler.toString());
        
            String query =
                       "MATCH (settler:"+SettlerNode.LABEL.name()+" {"+SettlerNode.UUID_PROPERTY+": {ownerId} })"
                    + " WITH settler"
                    + " MATCH (settler)<-[:" + StructureRelTypes.RELATION_OWNED_BY + "]-(s: " + StructureNode.LABEL.name() + ") "
                    + " WHERE NOT s." + StructureNode.CONSTRUCTION_STATUS_PROPERTY + " = " + ConstructionStatus.REMOVED.getStatusId()
                    + " RETURN COUNT(s) as count ";

            Result result = graph.execute(query, params);

            while (result.hasNext()) {
                Map<String, Object> map = result.next();
                count = (long) map.get("count");
                break;
            }
            tx.success();
        }

        return count;
    }
    
  

    @Override
    public void delete(Long id) {
        StructureNode structureNode = find(id);
        Node n = structureNode.getRawNode();
        for(Relationship rel : n.getRelationships()) {
            rel.delete();
        }
        n.delete();
    }

   


}
