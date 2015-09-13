/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.model.world;

import com.chingo247.structureapi.model.structure.StructureNode;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.model.WorldNode;
import com.chingo247.structureapi.model.structure.StructureRelations;
import com.chingo247.structureapi.model.structure.ConstructionStatus;
import com.chingo247.structureapi.model.interfaces.IStructureWorld;
import com.chingo247.structureapi.structure.IStructureAPI;
import com.chingo247.structureapi.structure.StructureAPI;
import com.chingo247.structureapi.world.WorldConfig;
import com.chingo247.xplatform.core.IWorld;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;

/**
 *
 * @author Chingo
 */
public class StructureWorldNode extends WorldNode implements IStructureWorld {

    public StructureWorldNode(Node worldNode) {
        super(worldNode);
    }

    @Override
    public void addStructure(StructureNode structure) {
        structure.getNode().createRelationshipTo(getNode(), DynamicRelationshipType.withName(StructureRelations.RELATION_WITHIN));
    }

    @Override
    public boolean deleteStructure(long id) {
        Node rawNode = getNode();
        for (Relationship rel : rawNode.getRelationships(DynamicRelationshipType.withName(StructureRelations.RELATION_WITHIN))) {
            StructureNode ownerNode = new StructureNode(rel.getOtherNode(getNode()));
            if (ownerNode.getId() == id) {
                rel.delete();
                return true;
            }
        }
        return false;
    }

    @Override
    public int getStructureCount() {
        return underlyingNode.getDegree(DynamicRelationshipType.withName(StructureRelations.RELATION_WITHIN), org.neo4j.graphdb.Direction.INCOMING);
    }

    @Override
    public List<StructureNode> getStructures() {
        Iterable<Relationship> rels = underlyingNode.getRelationships(org.neo4j.graphdb.Direction.INCOMING, DynamicRelationshipType.withName(StructureRelations.RELATION_WITHIN));
        List<StructureNode> structures = Lists.newArrayList();
        for (Relationship rel : rels) {
            Node n = rel.getOtherNode(underlyingNode);
            structures.add(new StructureNode(n));
        }
        return structures;
    }

    @Override
    public List<StructureNode> getDeletedAfter(long date) {
        List<StructureNode> structures = com.google.common.collect.Lists.newArrayList();

        Map<String, Object> params = Maps.newHashMap();
        params.put("worldId", getUUID().toString());
        params.put("date", date);

        String query = "MATCH (world:" + WorldNode.LABEL + " { " + WorldNode.ID_PROPERTY + ": {worldId} })"
                + " WITH world "
                + " MATCH (world)<-[:" + StructureRelations.RELATION_WITHIN + "]-(s:" + StructureNode.LABEL + ")"
                + " WHERE s." + StructureNode.DELETED_AT_PROPERTY + " > {date}"
                + " RETURN s";

        Result r = underlyingNode.getGraphDatabase().execute(query, params);

        while (r.hasNext()) {
            Map<String, Object> map = r.next();

            for (Object o : map.values()) {
                Node n = (Node) o;
                StructureNode sn = new StructureNode(n);
                structures.add(sn);
            }
        }
        return structures;
    }

    @Override
    public List<StructureNode> getCreatedAfter(long date) {
        List<StructureNode> structures = Lists.newArrayList();

        Map<String, Object> params = Maps.newHashMap();
        params.put("worldId", getUUID().toString());
        params.put("date", date);

        String query = "MATCH (world:" + WorldNode.LABEL + " { " + WorldNode.ID_PROPERTY + ": {worldId} })"
                + " WITH world "
                + " MATCH (world)<-[:" + StructureRelations.RELATION_WITHIN + "]-(s:" + StructureNode.LABEL + ")"
                + " WHERE s." + StructureNode.CREATED_AT_PROPERTY + " > {date}"
                + " RETURN s";

        Result r = underlyingNode.getGraphDatabase().execute(query, params);

        while (r.hasNext()) {
            Map<String, Object> map = r.next();

            for (Object o : map.values()) {
                Node n = (Node) o;
                StructureNode sn = new StructureNode(n);
                structures.add(sn);
            }
        }
        return structures;
    }
    
    @Override
    public List<StructureNode> getStructuresWithin(CuboidRegion region, int limit) {
        IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(getName());
        List<StructureNode> structures = new ArrayList<>();
      
        Map<String,Object> params = Maps.newHashMap();
        params.put("worldId", w.getUUID().toString());
        if(limit > 0) {
            params.put("limit", limit);
        }
       
        String query = 
                   "MATCH (world:"+WorldNode.LABEL+" { "+WorldNode.ID_PROPERTY+": {worldId} })"
                + " WITH world "
                + " MATCH (world)<-[:" + StructureRelations.RELATION_WITHIN + "]-(s:" + StructureNode.LABEL + ")"
                + " WHERE s." + StructureNode.DELETED_AT_PROPERTY + " IS NULL"
                + " AND NOT s." + StructureNode.CONSTRUCTION_STATUS_PROPERTY + " = " + ConstructionStatus.REMOVED.getStatusId()
                + " AND s." + StructureNode.MAX_X_PROPERTY + " >= " + region.getMinimumPoint().getBlockX() + " AND s." + StructureNode.MIN_X_PROPERTY + " <= " + region.getMaximumPoint().getBlockX()
                + " AND s." + StructureNode.MAX_Y_PROPERTY + " >= " + region.getMinimumPoint().getBlockY() + " AND s." + StructureNode.MIN_Y_PROPERTY + " <= " + region.getMaximumPoint().getBlockY()
                + " AND s." + StructureNode.MAX_Z_PROPERTY + " >= " + region.getMinimumPoint().getBlockZ() + " AND s." + StructureNode.MIN_Z_PROPERTY + " <= " + region.getMaximumPoint().getBlockZ()
                + " RETURN s";
        
        if(limit > 0) {
            query += " LIMIT {limit}";
        }
        
        Result result = getNode().getGraphDatabase().execute(query, params);
        while (result.hasNext()) {
            Map<String, Object> map = result.next();
            for (Object o : map.values()) {
                structures.add(new StructureNode((Node) o));
            }
        }

        return structures;
    }
    
    @Override
    public boolean hasStructuresWithin(CuboidRegion region) {
        return !getStructuresWithin(region, 1).isEmpty();
    }
   

    @Override
    public File getWorldDirectory() {
        IStructureAPI structureAPI = StructureAPI.getInstance();
        File f = new File(structureAPI.getWorkingDirectory().getAbsolutePath() + "//worlds//" + getName());
        return f;
    }

    /**
     * Not implemented yet...
     * @return 
     */
    @Override
    public WorldConfig getConfig() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
