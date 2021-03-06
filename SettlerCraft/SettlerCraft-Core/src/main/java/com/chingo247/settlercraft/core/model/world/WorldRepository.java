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
package com.chingo247.settlercraft.core.model.world;

import java.util.Map;
import java.util.UUID;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;

/**
 *
 * @author Chingo
 */
public class WorldRepository {
    
    private GraphDatabaseService graph;

    public WorldRepository(GraphDatabaseService graph) {
        this.graph = graph;
    }
    
    public WorldNode findByUUID(UUID worldUUID) {
        WorldNode world = null;

        String query = "MATCH (world: " + WorldNode.LABEL + " { " + WorldNode.UUID_PROPERTY + ": '" + worldUUID.toString() + "' }) RETURN world";

        Result r = graph.execute(query);
        if (r.hasNext()) {
            Map<String, Object> map = r.next();
            for (Object o : map.values()) {
                world = new WorldNode((Node) o);
            }
        }
        return world;
    }
   
    public WorldNode addOrGet(String worldName, UUID worldUUID) {
        WorldNode world = findByUUID(worldUUID);
        if(world == null) { 
            Node worldNode = graph.createNode(WorldNode.label());
            worldNode.setProperty(WorldNode.NAME_PROPERTY, worldName);
            worldNode.setProperty(WorldNode.UUID_PROPERTY, worldUUID.toString());
            world = new WorldNode(worldNode);
        }
        return world;
    }
    
}
