/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.model.owner;

import com.chingo247.structureapi.model.structure.StructureNode;
import com.chingo247.settlercraft.core.model.BaseSettlerNode;
import com.chingo247.structureapi.model.structure.StructureRelations;
import com.chingo247.structureapi.model.structure.ConstructionStatus;
import com.chingo247.structureapi.model.interfaces.IStructureOwner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;

/**
 * Defines a StructureOwner, all operations in this class require an active Neo4j transaction.
 * @author Chingo
 */
public class StructureOwnerNode extends BaseSettlerNode implements IStructureOwner {

    public StructureOwnerNode(Node node) {
        super(node);
    }
    
    @Override
    public List<StructureNode> getStructures() {
        return getStructures(-1, -1);
    }

    @Override
    public List<StructureNode> getStructures(int skip, int limit) {
        List<StructureNode> structures = Lists.newArrayList();
        Map<String, Object> params = Maps.newHashMap();
        params.put("ownerId", getUUID().toString());
        if (skip > 0) {
            params.put("skip", skip);
        }

        if (limit > 0) {
            params.put("limit", limit);
        }

        String query
                = " MATCH (settler:" + BaseSettlerNode.LABEL + " {" + BaseSettlerNode.UUID_PROPERTY + ": {ownerId} })"
                + " WITH settler"
                + " MATCH (settler)<-[:" + StructureRelations.RELATION_OWNED_BY + "]-(s: " + StructureNode.LABEL + ")"
                + " WHERE NOT s." + StructureNode.CONSTRUCTION_STATUS_PROPERTY + " = " + ConstructionStatus.REMOVED.getStatusId()
                + " RETURN s"
                + " ORDER BY s." + StructureNode.CREATED_AT_PROPERTY + " DESC ";

        if (skip > 0) {
            query += " SKIP {skip}";
        }

        if (limit > 0) {
            query += " LIMIT {limit}";
        }

        Result result = underlyingNode.getGraphDatabase().execute(query, params);
        while (result.hasNext()) {
            for (Object o : result.next().values()) {
                StructureNode structureNode = new StructureNode((Node) o);
                structures.add(structureNode);
            }
        }
        return structures;
    }
    
    @Override
    public int getStructureCount() {
        return underlyingNode.getDegree(DynamicRelationshipType.withName(StructureRelations.RELATION_OWNED_BY), Direction.INCOMING);
    }

    

}
