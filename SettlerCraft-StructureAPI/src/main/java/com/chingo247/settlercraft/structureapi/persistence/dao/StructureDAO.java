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
package com.chingo247.settlercraft.structureapi.persistence.dao;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import com.chingo247.settlercraft.structureapi.structure.ConstructionStatus;
import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureOwnerType;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureRelTypes;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.xplatform.core.IWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import net.minecraft.util.com.google.common.collect.Maps;
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
                  " MATCH (world)<-[:" + StructureRelTypes.RELATION_WITHIN + "]-(s:" + StructureNode.LABEL.name() + " { "+StructureNode.ID_PROPERTY+": {structureId} })"
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
            Result r = graph.execute("MATCH (sid: ID_GENERATOR) "
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
    public StructureNode addStructure(String name, World world, Vector position, CuboidRegion region, Direction direction, double price) {
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
        System.out.println("getStructuresWithin() in " + (System.currentTimeMillis() - start) + " ms");
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
                + " AND s." + StructureNode.MAX_X_PROPERTY + " >= " + region.getMinimumPoint().getBlockX() + " AND s." + StructureNode.MIN_X_PROPERTY + " <= " + region.getMaximumPoint().getBlockX()
                + " AND s." + StructureNode.MAX_Y_PROPERTY + " >= " + region.getMinimumPoint().getBlockY() + " AND s." + StructureNode.MIN_Y_PROPERTY + " <= " + region.getMaximumPoint().getBlockY()
                + " AND s." + StructureNode.MAX_Z_PROPERTY + " >= " + region.getMinimumPoint().getBlockZ() + " AND s." + StructureNode.MIN_Z_PROPERTY + " <= " + region.getMaximumPoint().getBlockZ()
                + " RETURN s"
                + " LIMIT " + limit;
        Result result = graph.execute(query, params);
        System.out.println("getSubStructuresWithinStructure() in " + (System.currentTimeMillis() - start) + " ms");
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
                    "   MATCH (settler:"+SettlerNode.LABEL.name()+" {"+SettlerNode.ID_PROPERTY+"}: {ownerId})"
                    + " WITH settler"
                    + " MATCH (settler)<-[:" + StructureRelTypes.RELATION_OWNED_BY + "]-(s: " + StructureNode.LABEL.name() + ")"
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
        System.out.println("getStructuresForOwner() in " + (System.currentTimeMillis() - start) + "ms");

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
                       "MATCH (settler:"+SettlerNode.LABEL.name()+" {"+SettlerNode.ID_PROPERTY+"}: {ownerId})"
                    + " WITH settler"
                    + " MATCH (settler)<-[:" + StructureRelTypes.RELATION_OWNED_BY + "]-(s: " + StructureNode.LABEL.name() + ")"
                    + " RETURN COUNT(s) as count ";

            Result result = graph.execute(query);

            System.out.println("getAmountOfStructuresForSettler() in " + (System.currentTimeMillis() - start) + "ms");
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

   

//    @Override
//    public List<StructureNode> findSiblingsWithConstructionStatus(UUID world, long structureId, ConstructionStatus status) {
//        Map<String,Object> params = Maps.newHashMap();
//        params.put("worldId", world.toString());
//        params.put("structureId", structureId);
//        params.put("constructionStatus", status.getStatusId());
//        
//        String query = "MATCH (world:"+WorldNode.LABEL.name()+" { "+WorldNode.ID_PROPERTY+": {worldId} })"
//                     + "WITH world "
//                     + "MATCH (structure { "+StructureNode.ID_PROPERTY+" : {structureId} })"
//                     + "<-[:"+StructureRelTypes.RELATION_SUBSTRUCTURE+"]-"
//                     + "(:"+StructureNode.LABEL.name()+")"
//                     + "-[:"+StructureRelTypes.RELATION_SUBSTRUCTURE+"]->";
//    }
//
//    @Override
//    public List<StructureNode> findSubstructuresConstructionStatus(UUID world, long id, ConstructionStatus status) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    @Override
    public SettlerNode getMasterOwnerForStructure(long structureId) {
        long start = System.currentTimeMillis();
      
        Map<String,Object> params = Maps.newHashMap();
        params.put("structureId", structureId);
        SettlerNode settlerNode = null;
       
        String query = 
                  " MATCH (structure:" + StructureNode.LABEL.name() + " { "+StructureNode.ID_PROPERTY+": {structureId} })"
                + " WITH structure "
                + " MATCH (structure)-[:"+StructureRelTypes.RELATION_OWNED_BY+" { Type: "+StructureOwnerType.MASTER.getTypeId()+" } ]->(owner:" + SettlerNode.LABEL.name() + ")"
                + " RETURN owner as theOwner";
        Result result = graph.execute(query, params);
        System.out.println("getOwnerForStructure() in " + (System.currentTimeMillis() - start) + " ms");
        while (result.hasNext()) {
            Map<String, Object> map = result.next();
            Node n = (Node) map.get("theOwner");
            settlerNode = new SettlerNode(n);
            break;
        }
        return settlerNode;
    }
    
    public boolean isOwnerOfStructure(long structureId, Player player) {
        Map<String,Object> params = Maps.newHashMap();
        params.put("structureId", structureId);
        params.put("playerId", player.getUniqueId().toString());
       
        long start = System.currentTimeMillis();
        
        String query = 
                  " MATCH (world)<-[:" + StructureRelTypes.RELATION_WITHIN + "]-(structure:" + StructureNode.LABEL.name() + " { "+StructureNode.ID_PROPERTY+": {structureId} })"
                + " WITH structure "
                + " MATCH (structure)"
                + "-[:"+StructureRelTypes.RELATION_OWNED_BY+"]->"
                + "(owner:" + SettlerNode.LABEL.name() + " { "+SettlerNode.ID_PROPERTY+": {playerId} })"
                + " RETURN owner as theOwner"
                + " LIMIT 1";
        Result result = graph.execute(query, params);
        System.out.println("isOwnerOfStructure() in " + (System.currentTimeMillis() - start) + " ms");
        
        while (result.hasNext()) {
            Map<String, Object> map = result.next();
            Node n = (Node) map.get("theOwner");
            SettlerNode settlerNode = new SettlerNode(n);
            return settlerNode.getId().equals(player.getUniqueId());
        }
        return false;
    }

    @Override
    public boolean hasSubstructures(long id) {
        StructureNode node = find(id);
        Node n = node.getRawNode();
        return n.hasRelationship(org.neo4j.graphdb.Direction.INCOMING, DynamicRelationshipType.withName(StructureRelTypes.RELATION_SUBSTRUCTURE));
    }

    @Override
    public List<StructureNode> getSubstructures(long id) {
        StructureNode node = find(id);
        return node.getSubstructures();
    }

}