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
package com.chingo247.settlercraft.core.model.settler;

import com.chingo247.settlercraft.core.exception.SettlerException;
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
public class BaseSettlerRepository {

    protected final GraphDatabaseService graph;
    private final Logger LOG = Logger.getLogger(getClass().getCanonicalName());

    public BaseSettlerRepository(GraphDatabaseService graph) {
        this.graph = graph;
    }

    public BaseSettlerNode findByUUID(UUID settlerId) {
        BaseSettlerNode settlerNode = null;
        try (Transaction tx = graph.beginTx()) {
            String query = "MATCH(settler:" + BaseSettlerNode.LABEL + " { " + BaseSettlerNode.UUID_PROPERTY + ": '" + settlerId.toString() + "'}) RETURN settler";
            Result r = graph.execute(query);
            if (r.hasNext()) {
                Map<String, Object> map = r.next();
                for (Object o : map.values()) {
                    settlerNode = new BaseSettlerNode((Node) o);
                }
            }

            tx.success();
        }
        return settlerNode;
    }

    public BaseSettlerNode findById(Long settlerId) {
        BaseSettlerNode settlerNode = null;
        try (Transaction tx = graph.beginTx()) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("settlerId", settlerId);
            String query = "MATCH(settler:" + BaseSettlerNode.LABEL + " { " + BaseSettlerNode.ID_PROPERTY + ": {settlerId} }) RETURN settler";
            Result r = graph.execute(query, params);
            if (r.hasNext()) {
                Map<String, Object> map = r.next();
                for (Object o : map.values()) {
                    settlerNode = new BaseSettlerNode((Node) o);
                }
            }
            tx.success();
        }
        return settlerNode;
    }

    public BaseSettlerNode addSettler(UUID uuid, String name) throws SettlerException {
        BaseSettlerNode settler = null;
        try (Transaction tx = graph.beginTx()) {
            if (findByUUID(uuid) != null) {
                throw new SettlerException("Settler already exists!");
            }
            Long id = nextId();
            Node settlerNode = graph.createNode(BaseSettlerNode.label());
            settlerNode.setProperty(BaseSettlerNode.UUID_PROPERTY, uuid.toString());
            settlerNode.setProperty(BaseSettlerNode.NAME_PROPERTY, name);
            settlerNode.setProperty(BaseSettlerNode.ID_PROPERTY, id);

            settler = new BaseSettlerNode(settlerNode);
            tx.success();
        }
        return settler;
    }

    public BaseSettlerNode addSettler(String name, UUID player) throws SettlerException {
        BaseSettlerNode settler = null;
        try (Transaction tx = graph.beginTx()) {
            settler = findByUUID(player);

            if (settler == null) {
                Long id = nextId();
                Node settlerNode = graph.createNode(BaseSettlerNode.label());
                settlerNode.setProperty(BaseSettlerNode.UUID_PROPERTY, player.toString());
                settlerNode.setProperty(BaseSettlerNode.NAME_PROPERTY, name);
                settlerNode.setProperty(BaseSettlerNode.ID_PROPERTY, id);
                settler = new BaseSettlerNode(settlerNode);
            } else {
                settler.getNode().setProperty(BaseSettlerNode.NAME_PROPERTY, name);
            }

            tx.success();
        }
        return settler;
    }

    private long nextId() {
        // Work-around for getting the next Id
        // Sets the lock at this node by removing a non-existent property
        String idQuery = "MATCH (sid:ID_GENERATOR {name:'SETTLER_ID'}) "
                + "REMOVE sid.lock " // NON-EXISTENT PROPERTY
                + "SET sid.nextId = sid.nextId + 1 "
                + "RETURN sid.nextId as nextId";
        Result r = graph.execute(idQuery);
        long id = (long) r.next().get("nextId");
        return id;
    }

}
