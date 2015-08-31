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
package com.chingo247.structureapi.construction;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.chingo247.settlercraft.core.util.KeyPool;
import com.chingo247.structureapi.construction.event.StructureTaskCancelledEvent;
import com.chingo247.structureapi.construction.event.StructureTaskCompleteEvent;
import com.chingo247.structureapi.construction.event.StructureTaskStartEvent;
import com.chingo247.structureapi.event.StructureStateChangeEvent;
import com.chingo247.structureapi.event.async.StructureJobAddedEvent;
import com.chingo247.structureapi.exception.ConstructionException;
import com.chingo247.structureapi.model.interfaces.IStructureRepository;
import com.chingo247.structureapi.model.owner.StructureOwnerNode;
import com.chingo247.structureapi.model.structure.Structure;
import com.chingo247.structureapi.model.structure.StructureNode;
import com.chingo247.structureapi.model.structure.StructureRelations;
import com.chingo247.structureapi.model.structure.StructureRepository;
import com.chingo247.structureapi.model.structure.ConstructionStatus;
import com.chingo247.structureapi.structure.StructureAPI;
import com.chingo247.structureapi.structure.plan.placement.options.BuildOptions;
import com.chingo247.structureapi.structure.plan.placement.options.DemolitionOptions;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IColors;
import com.chingo247.xplatform.core.IPlayer;
import com.google.common.collect.Maps;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.TraversalDescription;

/**
 *
 * @author Chingo
 */
public class ConstructionManager implements IConstructionManager {

    private Map<Long, ConstructionEntry> entries;
    private final IStructureTaskFactory taskFactory;
    private static ConstructionManager instance;

    private final GraphDatabaseService graph;
    private final KeyPool<Long> structurePool;
    private final ExecutorService executor;
    private final IStructureRepository structureRepository;
    private final APlatform platform;
    private final IColors colors;

    private ConstructionManager() {
        this.entries = Maps.newConcurrentMap();
        this.taskFactory = new DefaultStructureTaskFactory();
        this.graph = SettlerCraft.getInstance().getNeo4j();
        this.executor = SettlerCraft.getInstance().getExecutor();
        this.structurePool = new KeyPool<>(executor);
        this.structureRepository = new StructureRepository(graph);
        this.platform = SettlerCraft.getInstance().getPlatform();
        this.colors = platform.getChatColors();
    }

    /**
     * Gets the {@link IConstructionManager} instance
     *
     * @return The instance
     */
    public static IConstructionManager getInstance() {
        if (instance == null) {
            instance = new ConstructionManager();
            AsyncEventManager.getInstance().register(instance);
        }
        return instance;
    }

    /**
     * Gets the taskfactory
     *
     * @return The taskfactory
     */
    @Override
    public IStructureTaskFactory getTaskFactory() {
        return taskFactory;
    }

    @Override
    public void remove(ConstructionEntry entry) {
        entries.remove(entry.getStructure().getId());
    }

    @Override
    public ConstructionEntry getEntry(Structure structure) {
        long id = structure.getId();
        ConstructionEntry entry = entries.get(id);
        if (entry == null) {
            entry = new ConstructionEntry(structure);
            entries.put(id, entry);
        }
        return entry;
    }

    @Override
    public void stop(UUID player, Structure structure, boolean useForce) {
        stop(player, getEntry(structure), useForce);
    }

    @Override
    public void stop(UUID player, ConstructionEntry entry, boolean useForce) {
        // Stops it recursively
        entry.purge();
    }

    @Override
    public void build(EditSession session, UUID player, Structure structure, IBuildTaskAssigner assigner, BuildOptions options) throws ConstructionException {
        build(session, player, getEntry(structure), assigner, options);
    }

    @Override
    public void build(final EditSession session, final UUID player, final ConstructionEntry entry, final IBuildTaskAssigner assigner, final BuildOptions options) throws ConstructionException {
        // Perform async
        System.out.println("[ConstructionManager]: build!");

        executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    System.out.println("[ConstructionManager]: run build async 1");

                    // Get the id of the root structure, the structure that has no parent
                    // All actions will be queued on the root
                    Long rootId;
                    try (Transaction tx = graph.beginTx()) {
                        StructureNode sn = new StructureNode(entry.getStructure().getNode());
                        StructureNode root = sn.getRoot();
                        rootId = root.getId();
                        tx.success();
                    }

                    // Make sure actions are performed in a queue wise
                    // Therefore we set a lock on the root structure
                    structurePool.execute(rootId, new Runnable() {

                        @Override
                        public void run() {
                            try {
                                System.out.println("[ConstructionManager]: run build async 2");
                                List<Structure> structures = new ArrayList<>();

                                // Traverse the structures from the database/graph
                                try (Transaction tx = graph.beginTx()) {
                                    TraversalDescription description = graph.traversalDescription();
                                    if (options.getTraveral() == StructureTraversal.BREADTH_FIRST) {
                                        description = description.breadthFirst();
                                    } else {
                                        description = description.depthFirst();
                                    }
                                    ResourceIterator<Node> it = description.relationships(DynamicRelationshipType.withName(StructureRelations.RELATION_SUBSTRUCTURE), Direction.INCOMING)
                                            .traverse(entry.getStructure().getNode())
                                            .nodes()
                                            .iterator();

                                    while (it.hasNext()) {
                                        structures.add(new Structure(it.next()));
                                    }
                                    tx.success();
                                }

                                System.out.println("[ConstructionManager]: Purging existing tasks");
                                // Purge existing tasks from entries
                                for (Structure s : structures) {
                                    ConstructionEntry e = getEntry(s);
                                    e.purge();
                                }

                                System.out.println("[ConstructionManager]: Asigning new tasks");
                                // Set entries
                                ConstructionEntry prevEntry = null;

                                try (Transaction tx = graph.beginTx()) {
                                    for (Structure s : structures) {
                                        ConstructionEntry e = getEntry(s);
                                        assigner.assignTasks(session, player, e, options);

                                        if (prevEntry != null) {
                                            prevEntry.setNextEntry(e);
                                        }
                                    }
                                    tx.success();
                                }

                                // Fire first structure entry
                                System.out.println("[ConstructionManager]: Starting tasks!");
                                entry.proceed();
                            } catch (ConstructionException ex) {
                                Player ply = SettlerCraft.getInstance().getPlayer(player);
                                if (ply != null) {
                                    ply.printError(ex.getMessage());
                                }
                            } catch (Exception ex) {
                                // Catch everything else or it will disappear in the abyss
                                Logger.getLogger(StructureAPI.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                            }
                        }
                    });

                } catch (Exception ex) {
                    // Catch everything else or it will disappear in the abyss
                    Logger.getLogger(StructureAPI.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        });

    }

    @Override
    public void demolish(final EditSession session, final UUID player, final ConstructionEntry entry, final IDemolitionTaskAssigner assigner, final DemolitionOptions options) throws ConstructionException {

        // Perform async
        executor.execute(new Runnable() {

            @Override
            public void run() {
                System.out.println("[ConstructionManager]: run build async 1");
                try {

                    // Get the id of the root structure, the structure that has no parent
                    Long rootId;
                    try (Transaction tx = graph.beginTx()) {
                        StructureNode sn = new StructureNode(entry.getStructure().getNode());

                        StructureNode root = sn.getRoot();
                        rootId = root.getId();

                        tx.success();
                    }

                    // Make sure actions are performed in a queue wise
                    // Therefore we set a lock on the root structure
                    structurePool.execute(rootId, new Runnable() {

                        @Override
                        public void run() {
                            System.out.println("[ConstructionManager]: run build async 2");
                            try {
                                List<Structure> structures = new ArrayList<>();

                                // Traverse the structures from the database/graph
                                try (Transaction tx = graph.beginTx()) {
                                    TraversalDescription description = graph.traversalDescription();
                                    if (options.getTraveral() == StructureTraversal.BREADTH_FIRST) {
                                        description = description.breadthFirst();
                                    } else {
                                        description = description.depthFirst();
                                    }
                                    ResourceIterator<Node> it = description.relationships(DynamicRelationshipType.withName(StructureRelations.RELATION_SUBSTRUCTURE), Direction.INCOMING)
                                            .reverse()
                                            .traverse(entry.getStructure().getNode())
                                            .nodes()
                                            .iterator();

                                    while (it.hasNext()) {
                                        structures.add(new Structure(it.next()));
                                    }
                                    tx.success();
                                }

                                System.out.println("[ConstructionManager]: Purging tasks");
                                // Purge existing tasks from entries
                                for (Structure s : structures) {
                                    ConstructionEntry e = getEntry(s);
                                    e.purge();
                                }

                                System.out.println("[ConstructionManager]: Assigning tasks");
                                // Set entries
                                ConstructionEntry prevEntry = null;
                                try (Transaction tx = graph.beginTx()) {
                                    for (Structure s : structures) {
                                        ConstructionEntry e = getEntry(s);
                                        assigner.assignTasks(session, player, e, options);

                                        if (prevEntry != null) {
                                            prevEntry.setNextEntry(e);
                                        }
                                    }
                                    tx.success();
                                }

                                // Fire first structure entry
                                entry.proceed();
                            } catch (ConstructionException ex) {
                                Player ply = SettlerCraft.getInstance().getPlayer(player);
                                if (ply != null) {
                                    ply.printError(ex.getMessage());
                                }
                            } catch (Exception ex) {
                                // Catch everything else or it will disappear in the abyss
                                Logger.getLogger(StructureAPI.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                            }
                        }
                    });

                } catch (Exception ex) {
                    // Catch everything else or it will disappear in the abyss
                    Logger.getLogger(StructureAPI.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        });

    }

    @Override
    public void demolish(EditSession session, UUID player, Structure structure, IDemolitionTaskAssigner assigner, DemolitionOptions options) throws ConstructionException {
        demolish(session, player, getEntry(structure), assigner, options);
    }

    @AllowConcurrentEvents
    @Subscribe
    public void onJobAdded(StructureJobAddedEvent addedEvent) {
        long structureId = addedEvent.getStructureId();
        UUID uuid = addedEvent.getPlayerUUID();

        Structure structure;
        try (Transaction tx = graph.beginTx()) {
            StructureNode structureNode = structureRepository.findById(structureId);
            structureNode.setStatus(ConstructionStatus.QUEUED);
            structure = new Structure(structureNode);
            tx.success();
        }

        EventManager.getInstance().getEventBus().post(new StructureStateChangeEvent(structure));

        if (uuid != null) {
            IPlayer player = platform.getServer().getPlayer(uuid);
            if (player != null) {
                String status = getStatusString(ConstructionStatus.QUEUED.name(), structure);
                player.sendMessage(status);
            }
        }
    }

    @AllowConcurrentEvents
    @Subscribe
    public void onTaskStartEvent(StructureTaskStartEvent startEvent) {
        StructureTask task = startEvent.getTask();
        Structure structure = task.getConstructionEntry().getStructure();
        ConstructionStatus newStatus = ConstructionStatus.getStatus(startEvent.getTask().getAction());
        updateStatus(newStatus, task.getAction(), structure);
    }

    @AllowConcurrentEvents
    @Subscribe
    public void onTaskCancelled(StructureTaskCancelledEvent cancelledEvent) {
        StructureTask task = cancelledEvent.getTask();
        Structure structure = task.getConstructionEntry().getStructure();
        ConstructionStatus newStatus = ConstructionStatus.STOPPED;
        updateStatus(newStatus, newStatus.name(), structure);
    }

    @AllowConcurrentEvents
    @Subscribe
    public void onTaskComplete(StructureTaskCompleteEvent taskCompleteEvent) {
        StructureTask task = taskCompleteEvent.getTask();
        Structure structure = task.getConstructionEntry().getStructure();
        ConstructionStatus newStatus = ConstructionStatus.COMPLETED;
        updateStatus(newStatus, newStatus.name(), structure);
    }

    private void updateStatus(ConstructionStatus newStatus, String statusToTell, Structure structure) {
        List<IPlayer> owners = new ArrayList<>();

        String status;
        try (Transaction tx = graph.beginTx()) {
            StructureNode structureNode = new StructureNode(structure.getNode());
            structureNode.setStatus(newStatus);

            List<StructureOwnerNode> settlers = structureNode.getOwners();
            for (StructureOwnerNode settlerNode : settlers) {
                IPlayer ply = platform.getPlayer(settlerNode.getUUID());
                if (ply != null) {
                    owners.add(ply);
                }
            }
            status = getStatusString(statusToTell.toUpperCase().replaceAll("_", " "), new Structure(structureNode));

            tx.success();
        }

        EventManager.getInstance().getEventBus().post(new StructureStateChangeEvent(structure));

        // Tell the new status!
        for (IPlayer p : owners) {
            p.sendMessage(status);
        }
    }

    private String getStatusString(String status, Structure structure) {
        String structureInfo = colors.reset() + ": #" + colors.gold() + structure.getId() + colors.blue() + " " + structure.getName();
        String statusString = status.replaceAll("_", " ");
        switch (structure.getConstructionStatus()) {
            case COMPLETED:
                return colors.green() + statusString + structureInfo;
            case STOPPED:
            case ON_HOLD:
                return colors.red() + statusString + structureInfo;
            default:
                return colors.yellow() + statusString + structureInfo;
        }
    }

}
