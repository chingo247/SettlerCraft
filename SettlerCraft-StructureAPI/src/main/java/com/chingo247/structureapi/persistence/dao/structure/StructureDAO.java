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
package com.chingo247.structureapi.persistence.dao.structure;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import com.chingo247.settlercraft.core.World;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import com.chingo247.structureapi.structure.ConstructionStatus;
import com.chingo247.settlercraft.core.Direction;
import com.chingo247.xplatform.core.IWorld;
import com.sk89q.worldedit.regions.CuboidRegion;
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
public class StructureDAO {

    private static final Logger LOG = Logger.getLogger(StructureDAO.class.getSimpleName());
    private final GraphDatabaseService graph;

    public StructureDAO(GraphDatabaseService graph) {
        this.graph = graph;
    }

    public StructureNode find(long id) {
        StructureNode structure = null;
        Node n = graph.getNodeById(id);
        if (n != null) {
            structure = new StructureNode(n);
        }
        return structure;
    }

    public StructureNode addStructure(String name, CuboidRegion dimension, Direction direction, double value) {
        Node stNode = graph.createNode(StructureNode.LABEL);
        stNode.setProperty(StructureNode.NAME_PROPERTY, name);
        stNode.setProperty(StructureNode.CONSTRUCTION_STATUS_PROPERTY, ConstructionStatus.ON_HOLD.getStatusId());
        stNode.setProperty(StructureNode.DIRECTION_PROPERTY, direction.getDirectionId());
        stNode.setProperty(StructureNode.MIN_X_PROPERTY, dimension.getMinimumPoint().getBlockX());
        stNode.setProperty(StructureNode.MIN_Y_PROPERTY, dimension.getMinimumPoint().getBlockY());
        stNode.setProperty(StructureNode.MIN_Z_PROPERTY, dimension.getMinimumPoint().getBlockZ());
        stNode.setProperty(StructureNode.MAX_X_PROPERTY, dimension.getMaximumPoint().getBlockX());
        stNode.setProperty(StructureNode.MAX_Y_PROPERTY, dimension.getMaximumPoint().getBlockY());
        stNode.setProperty(StructureNode.MAX_Z_PROPERTY, dimension.getMaximumPoint().getBlockZ());
        stNode.setProperty(StructureNode.CREATED_AT_PROPERTY, System.currentTimeMillis());
        stNode.setProperty(StructureNode.VALUE_PROPERTY, value);
        return new StructureNode(stNode);
    }

    public List<StructureNode> getStructuresWithin(World world, CuboidRegion region, int limit) {
        IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(world.getName());
        long start = System.currentTimeMillis();
        List<StructureNode> structures = new ArrayList<>();
       
        String query = "MATCH (w: " + WorldNode.LABEL.name() + "{" + WorldNode.ID_PROPERTY + ": '" + w.getUUID().toString() + "'} )<-[:" + StructureRelTypes.RELATION_WITHIN + "]-(s:" + StructureNode.LABEL.name() + ")"
                + " WHERE s." + StructureNode.DELETED_AT_PROPERTY + " IS NULL"
                + " AND s." + StructureNode.MAX_X_PROPERTY + " >= " + region.getMinimumPoint().getBlockX() + " AND s." + StructureNode.MIN_X_PROPERTY + " <= " + region.getMaximumPoint().getBlockX()
                + " AND s." + StructureNode.MAX_Y_PROPERTY + " >= " + region.getMinimumPoint().getBlockY() + " AND s." + StructureNode.MIN_Y_PROPERTY + " <= " + region.getMaximumPoint().getBlockY()
                + " AND s." + StructureNode.MAX_Z_PROPERTY + " >= " + region.getMinimumPoint().getBlockZ() + " AND s." + StructureNode.MIN_Z_PROPERTY + " <= " + region.getMaximumPoint().getBlockZ()
                + " RETURN s"
                + " LIMIT " + limit;
        System.out.println("query: " + query);
        Result result = graph.execute(query);
        System.out.println("getStructuresWithin() in " + (System.currentTimeMillis() - start) + " ms");
        while (result.hasNext()) {
            Map<String, Object> map = result.next();
            for (Object o : map.values()) {
                structures.add(new StructureNode((Node) o));
            }
        }

        return structures;
    }

    public boolean hasStructuresWithin(World world, CuboidRegion region) {
        return !getStructuresWithin(world, region, 1).isEmpty();
    }

    public List<StructureNode> getStructuresForOwner(UUID structureOwner, int skip, int limit) {
        long start = System.currentTimeMillis();
        List<StructureNode> structures = new ArrayList<>();
            String query = "MATCH (x: " + SettlerNode.LABEL.name() + " { " + SettlerNode.ID_PROPERTY + ": '" + structureOwner.toString() + "' })<-[:" + StructureRelTypes.RELATION_OWNED_BY + "]-(s: " + StructureNode.LABEL.name() + ")"
                    + " RETURN s"
                    + " ORDER BY s." + StructureNode.CREATED_AT_PROPERTY + " DESC ";

            if (skip > 0) {
                query += " SKIP " + skip;
            }

            if (limit > 0) {
                query += " LIMIT " + limit;
            }

            Result result = graph.execute(query);
            System.out.println("query: " + query);
            while (result.hasNext()) {
                for (Object o : result.next().values()) {
                    StructureNode structureNode = new StructureNode((Node) o);
                    structures.add(structureNode);
                }
            }
        System.out.println("getStructuresForOwner() in " + (System.currentTimeMillis() - start) + "ms");

        return structures;
    }

    public long getStructureCountForSettler(UUID settler) {
        long count = 0;
        long start = System.currentTimeMillis();
        try (Transaction tx = graph.beginTx()) {

            String query = "MATCH (:" + SettlerNode.LABEL.name() + " { " + SettlerNode.ID_PROPERTY + ": '" + settler.toString() + "' })<-[:" + StructureRelTypes.RELATION_OWNED_BY + "]-(s:" + StructureNode.LABEL.name() + ") "
                    + " RETURN COUNT(s) as count";

            Result result = graph.execute(query);
            System.out.println("query: " + query);

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

    public void delete(Long id) {
        Node n = graph.getNodeById(id);
        for(Relationship rel : n.getRelationships()) {
            rel.delete();
        }
        n.delete();
    }

}
