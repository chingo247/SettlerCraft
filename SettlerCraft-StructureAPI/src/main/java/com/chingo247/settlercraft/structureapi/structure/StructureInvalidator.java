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
package com.chingo247.settlercraft.structureapi.structure;

import com.chingo247.settlercraft.structureapi.model.structure.StructureStatus;
import com.chingo247.settlercraft.structureapi.model.structure.StructureNode;
import com.chingo247.menuapi.menu.util.ShopUtil;
import com.chingo247.settlercraft.core.model.BaseSettlerNode;
import com.chingo247.settlercraft.core.model.WorldNode;
import com.chingo247.settlercraft.core.platforms.services.IEconomyProvider;
import com.chingo247.settlercraft.core.util.XXHasher;
import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureRepository;
import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureWorldRepository;
import com.chingo247.settlercraft.structureapi.model.owner.StructureOwnerNode;
import com.chingo247.settlercraft.structureapi.model.owner.StructureOwnerType;
import com.chingo247.settlercraft.structureapi.model.structure.StructureRepository;
import com.chingo247.settlercraft.structureapi.model.util.StructureRelations;
import com.chingo247.settlercraft.structureapi.model.world.StructureWorldRepository;
import com.chingo247.xplatform.core.IServer;
import com.chingo247.xplatform.core.IWorld;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.common.collect.Lists;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class StructureInvalidator {

    private IServer server;
    private ExecutorService executor;
    private GraphDatabaseService graph;
    private IStructureRepository structureRepository;
    private IStructureWorldRepository structureWorldRepository;
    private IEconomyProvider economy;
    

    private static String LOCK_DATA = "lockData";

    public StructureInvalidator(IServer server, ExecutorService executor, GraphDatabaseService graph, IEconomyProvider economyProvider) {
        this.server = server;
        this.executor = executor;
        this.graph = graph;
        this.structureRepository = new StructureRepository(graph);
        this.economy = economyProvider;
        this.structureWorldRepository = new StructureWorldRepository(graph);
    }

    private File getSessionFile(IWorld world) {
        File wd = server.getWorldFolder(world.getName());
        return new File(wd, "session.lock");
    }

    public void invalidate() {
        long start = System.currentTimeMillis();
        System.out.println("[SettlerCraft]: Starting structure invalidation...");
        List<IWorld> toCheck = Lists.newArrayList();

        XXHasher hasher = new XXHasher();

        try (Transaction tx = graph.beginTx()) {
            for (IWorld world : server.getWorlds()) {
                WorldNode w = structureWorldRepository.findByUUID(world.getUUID());
                if (w != null) {
                    Node n = w.getNode();
                    if (n.hasProperty(LOCK_DATA)) {
                        Long lockData = (Long) n.getProperty(LOCK_DATA);
                        File sessionFile = getSessionFile(world);
                        try {
                            Long hash = hasher.hash64(sessionFile);
                            if (!hash.equals(lockData)) {
                                toCheck.add(world);
                            }
                            n.setProperty(LOCK_DATA, hash); // And update to new lock
                        } catch (IOException ex) {
                            Logger.getLogger(StructureInvalidator.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } else {
                        File sessionFile = getSessionFile(world);

                        try {
                            long hash = hasher.hash64(sessionFile);
                            n.setProperty(LOCK_DATA, hash);
                            toCheck.add(world);
                        } catch (IOException ex) {
                            System.out.println("[SettlerCraft]: Something went wrong during invalidation!");
                            Logger.getLogger(StructureInvalidator.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                        }
                    }
                }
            }
            tx.success();
        }

        for (IWorld world : toCheck) {

            File levelDat = new File(server.getWorldFolder(world.getName()), "level.dat");
            long date = levelDat.lastModified();

            // If there are region files...
            processDeletedAfter(world, date);
            processCreatedAfter(world, date);
        }
    }

    private void processDeletedAfter(IWorld world, long date) {
        
        try (Transaction tx = graph.beginTx()) {
            List<StructureNode> structureNodes = Lists.newArrayList();

            Map<String, Object> params = Maps.newHashMap();
            params.put("worldId", world.getUUID().toString());
            params.put("date", date);

            String query = "MATCH (world:" + WorldNode.LABEL.name() + " { " + WorldNode.ID_PROPERTY + ": {worldId} })"
                    + " WITH world "
                    + " MATCH (world)<-[:" + StructureRelations.RELATION_WITHIN + "]-(s:" + StructureNode.LABEL.name() + ")"
                    + " WHERE s." + StructureNode.DELETED_AT_PROPERTY + " > {date}"
                    + " RETURN s";

            Result r = graph.execute(query, params);

            while (r.hasNext()) {
                Map<String, Object> map = r.next();

                for (Object o : map.values()) {
                    Node n = (Node) o;
                    StructureNode sn = new StructureNode(n);
                    structureNodes.add(sn);
                }
            }

            if (!structureNodes.isEmpty()) {
                System.out.println("[SettlerCraft]: Restoring " + structureNodes.size() + " from '" + world.getName() + "' which have been deleted after the last world save ");
            } else {
                System.out.println("[SettlerCraft]: Nothing to restore");
                tx.success();
                return;
            }

//            // Re-protect from structures
//            List<IStructureProtector> protectors = ((StructureAPI) StructureAPI.getInstance()).getStructureProtectors();
//            for (IStructureProtector protector : protectors) {
//                for (Structure s : structures) {
//                    if (protector.hasProtection(s)) {
//                        System.out.println("[SettlerCraft]: Restored and protected structure #" + s.getId() + " with '" + protector.getName() + "'");
//                        protector.protect(s);
//                    }
//                }
//            }

            for (StructureNode structureNode : structureNodes) {
                structureNode.setStatus(StructureStatus.ON_HOLD);
                structureNode.setDeletedAt(null);
            }

            tx.success();
        }
    }

    private void processCreatedAfter(IWorld world, long date) {
       
        try (Transaction tx = graph.beginTx()) {
            List<StructureNode> structureNodes = Lists.newArrayList();
            
            Map<String, Object> params = Maps.newHashMap();
            params.put("worldId", world.getUUID().toString());
            params.put("date", date);

            String query = "MATCH (world:" + WorldNode.LABEL.name() + " { " + WorldNode.ID_PROPERTY + ": {worldId} })"
                    + " WITH world "
                    + " MATCH (world)<-[:" + StructureRelations.RELATION_WITHIN + "]-(s:" + StructureNode.LABEL.name() + ")"
                    + " WHERE s." + StructureNode.CREATED_AT_PROPERTY + " > {date}"
                    + " RETURN s";


            Result r = graph.execute(query, params);

            while (r.hasNext()) {
                Map<String, Object> map = r.next();

                for (Object o : map.values()) {
                    Node n = (Node) o;
                    StructureNode sn = new StructureNode(n);
                    structureNodes.add(sn);
                }
            }

            if (!structureNodes.isEmpty()) {
                System.out.println("[SettlerCraft]: Found a total of " + structureNodes.size() + " structures within " + world.getName() + " that are invalid");
                System.out.println("[SettlerCraft]: These structures have been placed after the last world save ");
            } else {
                System.out.println("[SettlerCraft]: Nothing to invalidate");
                tx.success();
                return;
            }

            // AUTO REFUND
            if (economy != null) {
                System.out.println("[SettlerCraft]: Refunding players which own invalid structures within " + world.getName());
                for (StructureNode sn : structureNodes) {
                    if (sn.getPrice() > 0 && !sn.isAutoremoved()) {
                        List<StructureOwnerNode> masters = sn.getOwners(StructureOwnerType.MASTER);
                        double pricePerOwner = sn.getPrice() / masters.size();
                        for (BaseSettlerNode settler : masters) {
                            economy.give(settler.getUUID(), pricePerOwner);
                            System.out.println("[SettlerCraft]: Refunded " + ShopUtil.valueString(pricePerOwner) + " to " + settler.getName()
                                    + " for structure #" + sn.getId() + " (" + ShopUtil.valueString(sn.getPrice()) + ")");
                        }
                    }
                }
            }

            for (StructureNode n : structureNodes) {
                n.setStatus(StructureStatus.REMOVED);
            }

            tx.success();
        }
    }

}
