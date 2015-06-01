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
package com.chingo247.settlercraft.core.persistence.dao.world;

import java.util.Map;
import java.util.UUID;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;

/**
 *
 * @author Chingo
 */
public class WorldDAO implements IWorldDAO{

    private GraphDatabaseService graph;

    public WorldDAO(GraphDatabaseService graph) {
        this.graph = graph;
        

    }

    @Override
    public WorldNode find(UUID worldUUID) {
        WorldNode worldNode = null;

        String query = "MATCH (world: " + WorldNode.LABEL.name() + " { " + WorldNode.ID_PROPERTY + ": '" + worldUUID.toString() + "' }) RETURN world";

        Result r = graph.execute(query);
        if (r.hasNext()) {
            Map<String, Object> map = r.next();
            for (Object o : map.values()) {
                worldNode = new WorldNode((Node) o);
            }
        }
        return worldNode;
    }
   
    @Override
    public void addWorld(String worldName, UUID worldUUID) {
        Node worldNode = graph.createNode(WorldNode.LABEL);
        worldNode.setProperty(WorldNode.NAME_PROPERTY, worldName);
        worldNode.setProperty(WorldNode.ID_PROPERTY, worldUUID.toString());
    }

}
