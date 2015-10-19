/*
 * Copyright (C) 2015 ching
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
package com.chingo247.structureapi.model.zone;

import com.chingo247.settlercraft.core.model.WorldNode;
import com.chingo247.settlercraft.core.persistence.neo4j.NodeHelper;
import com.chingo247.structureapi.model.RelTypes;
import com.chingo247.structureapi.model.plot.Plot;
import com.chingo247.structureapi.model.structure.ConstructionStatus;
import com.chingo247.structureapi.model.structure.StructureNode;
import com.chingo247.xplatform.core.ILocation;
import com.google.common.collect.Maps;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;

/**
 *
 * @author ching
 */
public class ConstructionZoneRepository implements IConstructionZoneRepository {

    private boolean checked;
    private final GraphDatabaseService graph;

    public ConstructionZoneRepository(GraphDatabaseService graph) {
        this.checked = false;
        this.graph = graph;
    }

    private long nextId() {
        if (!checked) {
            Result r = graph.execute("MATCH (sid: ID_GENERATOR {name:'CONSTRUCTION_ZONE_ID'}) "
                    + "RETURN sid "
                    + "LIMIT 1");
            if (!r.hasNext()) {
                graph.execute("CREATE (sid: ID_GENERATOR {name:'CONSTRUCTION_ZONE_ID', nextId: 1})");
                checked = true;
                return 1;
            }
            checked = true;
        }

        // Work-around for getting the next Id
        // Sets the lock at this node by removing a non-existent property
        String idQuery = "MATCH (sid:ID_GENERATOR {name:'CONSTRUCTION_ZONE_ID'}) "
                + "REMOVE sid.lock " // NON-EXISTENT PROPERTY
                + "SET sid.nextId = sid.nextId + 1 "
                + "RETURN sid.nextId as nextId";
        Result r = graph.execute(idQuery);
        long id = (long) r.next().get("nextId");

        return id;
    }

    @Override
    public ConstructionZone findById(long id) {
        ConstructionZone zone = null;
        Map<String, Object> params = Maps.newHashMap();
        params.put("id", id);

        String query
                = " MATCH (s:" + StructureNode.LABEL + " { " + StructureNode.ID_PROPERTY + ": {id} })"
                + " RETURN s as zone";

        Result result = graph.execute(query, params);

        while (result.hasNext()) {
            Node n = (Node) result.next().get("zone");
            zone = new ConstructionZone(n);
            break;
        }
        return zone;
    }

    @Override
    public ConstructionZone findOnPosition(ILocation location) {
        return findOnPosition(
                location.getWorld().getUUID(),
                new BlockVector(location.getBlockX(), location.getBlockY(), location.getBlockZ())
        );
    }

    @Override
    public ConstructionZone findOnPosition(UUID worldUUID, Vector position) {
        ConstructionZone constructionZone = null;
        Map<String, Object> params = Maps.newHashMap();
        params.put("worldId", worldUUID.toString());
        String query
                = "MATCH ( world: " + WorldNode.LABEL + " { " + WorldNode.ID_PROPERTY + ": {worldId} })"
                + " WITH world "
                + " MATCH (world)<-[:" + RelTypes.WITHIN + "]-(s:" + ConstructionZone.LABEL + ")"
                + " WHERE "
                + " AND s." + ConstructionZone.MAX_X_PROPERTY + " >= " + position.getBlockX() + " AND s." + ConstructionZone.MIN_X_PROPERTY + " <= " + position.getBlockX()
                + " AND s." + ConstructionZone.MAX_Y_PROPERTY + " >= " + position.getBlockY() + " AND s." + ConstructionZone.MIN_Y_PROPERTY + " <= " + position.getBlockY()
                + " AND s." + ConstructionZone.MAX_Z_PROPERTY + " >= " + position.getBlockZ() + " AND s." + ConstructionZone.MIN_Z_PROPERTY + " <= " + position.getBlockZ()
                + " RETURN s as zone"
                + " LIMIT 1";

        Result result = graph.execute(query, params);
        while (result.hasNext()) {
            Map<String, Object> map = result.next();
            Node n = (Node) map.get("zone");
            constructionZone = new ConstructionZone(n);
        }
        return constructionZone;
    }

    @Override
    public Collection<ConstructionZone> findWithin(UUID worldUUID, CuboidRegion searchArea, int limit) {
        List<ConstructionZone> zones = new ArrayList<>();

        Map<String, Object> params = Maps.newHashMap();
        params.put("worldId", worldUUID.toString());
        if (limit > 0) {
            params.put("limit", limit);
        }

        Vector min = searchArea.getMinimumPoint();
        Vector max = searchArea.getMaximumPoint();

        String query
                = "MATCH (world:" + WorldNode.LABEL + " { " + WorldNode.ID_PROPERTY + ": {worldId} })"
                + " WITH world "
                + " MATCH (world)<-[:" + RelTypes.WITHIN.name() + "]-(s:" + ConstructionZone.LABEL + ")"
                + " WHERE "
                + " s." + ConstructionZone.MAX_X_PROPERTY + " >= " + min.getBlockX() + " AND s." + ConstructionZone.MIN_X_PROPERTY + " <= " + max.getBlockX()
                + " AND s." + ConstructionZone.MAX_Y_PROPERTY + " >= " + min.getBlockY() + " AND s." + ConstructionZone.MIN_Y_PROPERTY + " <= " + max.getBlockY()
                + " AND s." + ConstructionZone.MAX_Z_PROPERTY + " >= " + min.getBlockZ() + " AND s." + ConstructionZone.MIN_Z_PROPERTY + " <= " + max.getBlockZ()
                + " RETURN s";

        if (limit > 0) {
            query += " LIMIT {limit}";
        }

        Result result = graph.execute(query, params);
        while (result.hasNext()) {
            Map<String, Object> map = result.next();
            for (Object o : map.values()) {
                zones.add(new ConstructionZone((Node) o));
            }
        }

        return zones;
    }

    @Override
    public Iterable<ConstructionZone> findAll() {
        return NodeHelper.makeIterable(graph.findNodes(ConstructionZone.label()), ConstructionZone.class);
    }

    @Override
    public ConstructionZone add(CuboidRegion region) {
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();
        Node n = graph.createNode(Plot.plotLabel(), ConstructionZone.label());
        n.setProperty(ConstructionZone.ID_PROPERTY, nextId());
        ConstructionZone zone = new ConstructionZone(n);
        zone.setAccessType(AccessType.PRIVATE);
        zone.setMinX(min.getBlockX());
        zone.setMinY(min.getBlockY());
        zone.setMinZ(min.getBlockZ());
        zone.setMaxX(max.getBlockX());
        zone.setMaxY(max.getBlockY());
        zone.setMaxZ(max.getBlockZ());
        return zone;
    }

    @Override
    public void delete(long id) {
        ConstructionZone zone = findById(id);
        if (zone != null) {
            delete(zone);
        }
    }

    @Override
    public void delete(ConstructionZone zone) {
        Node n = zone.getNode();
        for(Relationship rel : n.getRelationships()) {
            rel.delete();
        }
        n.delete();
    }

}
