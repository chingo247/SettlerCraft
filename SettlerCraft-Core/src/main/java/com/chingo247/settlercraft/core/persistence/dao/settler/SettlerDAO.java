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

import com.chingo247.settlercraft.core.persistence.neo4j.Neo4jHelper;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class SettlerDAO {
    
    private final GraphDatabaseService graph;
    private final Logger LOG = Logger.getLogger(getClass().getCanonicalName());

    public SettlerDAO(GraphDatabaseService graph) {
        this.graph = graph;
        try (Transaction tx = graph.beginTx()) {
            if(!Neo4jHelper.hasUniqueConstraint(graph, SettlerNode.LABEL, SettlerNode.ID_PROPERTY)) {
                graph.schema().constraintFor(SettlerNode.LABEL)
                    .assertPropertyIsUnique(SettlerNode.ID_PROPERTY)
                    .create();
                tx.success();
            }
        }
    }

    public SettlerNode find(UUID settlerId) {
        SettlerNode settlerNode = null;
        try (Transaction tx = graph.beginTx()) {
            String query = "MATCH(settler:"+SettlerNode.LABEL.name()+" { "+SettlerNode.ID_PROPERTY+": '" + settlerId.toString()+ "'}) RETURN settler";
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
    
    public void addSettler(String settlerName, UUID settlerUUID) {
        Transaction tx = graph.beginTx();
        try {
            Node settlerNode = graph.createNode(com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode.LABEL);
            settlerNode.setProperty(com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode.ID_PROPERTY, settlerUUID.toString());
            settlerNode.setProperty(com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode.NAME_PROPERTY, settlerName);
            tx.success();
        } catch (RuntimeException exception) {
            LOG.log(Level.SEVERE, exception.getMessage(), exception);
            tx.failure();
        } catch(Exception ex) {
            tx.failure();
        } finally {
            tx.close();
        }
    }
    
}
