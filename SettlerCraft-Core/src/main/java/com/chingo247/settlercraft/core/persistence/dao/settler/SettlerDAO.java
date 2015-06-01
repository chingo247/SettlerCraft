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
package com.chingo247.settlercraft.core.persistence.dao.settler;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import com.google.common.collect.Maps;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class SettlerDAO implements ISettlerDAO{
    
    private final GraphDatabaseService graph;
    private final Logger LOG = Logger.getLogger(getClass().getCanonicalName());
    private boolean checked = false;

    public SettlerDAO(GraphDatabaseService graph) {
        this.graph = graph;
        
    }
    
    private long nextId() {
        if(!checked) {
            Result r = graph.execute("MATCH (sid: ID_GENERATOR {name:'SETTLER_ID'}) "
                        + "RETURN sid "
                        + "LIMIT 1");
            if(!r.hasNext()) {
                graph.execute("CREATE (sid: ID_GENERATOR {name:'SETTLER_ID', nextId: 1 })");
                checked = true;
                return 1;
            }
            checked = true;
        }
        
        // Work-around for getting the next Id
        // Sets the lock at this node by removing a non-existent property
        String idQuery = "MATCH (sid:ID_GENERATOR {name:'SETTLER_ID'}) "
                        +"REMOVE sid.lock " // NON-EXISTENT PROPERTY
                        +"SET sid.nextId = sid.nextId + 1 "
                        +"RETURN sid.nextId as nextId";
        Result r = graph.execute(idQuery);
        long id = (long) r.next().get("nextId");
        
        return id;
    }

    @Override
    public SettlerNode find(UUID settlerId) {
        SettlerNode settlerNode = null;
        try (Transaction tx = graph.beginTx()) {
            String query = "MATCH(settler:"+SettlerNode.LABEL.name()+" { "+SettlerNode.UUID_PROPERTY+": '" + settlerId.toString()+ "'}) RETURN settler";
            Result r = graph.execute(query);
            if(r.hasNext()) {
                Map<String,Object> map = r.next();
                for(Object o : map.values()) {
                    settlerNode = new SettlerNode((Node) o);
                }
            }
            tx.success();
        }
        return settlerNode;
    }
    
    @Override
    public void addSettler(String settlerName, UUID settlerUUID) {
        
        try(Transaction tx = graph.beginTx()) {
            Long id = nextId();
            Node settlerNode = graph.createNode(SettlerNode.LABEL);
            settlerNode.setProperty(SettlerNode.UUID_PROPERTY, settlerUUID.toString());
            settlerNode.setProperty(SettlerNode.NAME_PROPERTY, settlerName);
            settlerNode.setProperty(SettlerNode.ID_PROPERTY, id);
            tx.success();
        } 
    }
    
    public SettlerNode find(Long settlerId) {
        SettlerNode settlerNode = null;
        try (Transaction tx = graph.beginTx()) {
            Map<String,Object> params = Maps.newHashMap();
            params.put("settlerId", settlerId);
            String query = "MATCH(settler:"+SettlerNode.LABEL.name()+" { "+SettlerNode.ID_PROPERTY+": {settlerId} }) RETURN settler";
            Result r = graph.execute(query, params);
            if(r.hasNext()) {
                Map<String,Object> map = r.next();
                for(Object o : map.values()) {
                    settlerNode = new SettlerNode((Node) o);
                }
            }
            tx.success();
        }
        return settlerNode;
    }
    
}
