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

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import com.chingo247.settlercraft.core.platforms.services.IEconomyProvider;
import com.chingo247.settlercraft.core.util.KeyPool;
import com.chingo247.settlercraft.structureapi.event.StructureStateChangeEvent;
import com.chingo247.settlercraft.structureapi.exception.ConstructionException;
import com.chingo247.settlercraft.structureapi.event.async.StructureJobAddedEvent;
import com.chingo247.settlercraft.structureapi.event.async.StructureJobCanceledEvent;
import com.chingo247.settlercraft.structureapi.event.async.StructureJobCompleteEvent;
import com.chingo247.settlercraft.structureapi.event.async.StructureJobStartedEvent;
import com.chingo247.settlercraft.structureapi.persistence.dao.IStructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.dao.StructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.structure.construction.asyncworldedit.AsyncDemolishingPlacement;
import com.chingo247.settlercraft.structureapi.structure.construction.asyncworldedit.AsyncPlacement;
import com.chingo247.settlercraft.structureapi.structure.construction.asyncworldedit.AsyncPlacementCallback;
import com.chingo247.settlercraft.structureapi.structure.construction.asyncworldedit.SCJobEntry;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.DemolishingOptions;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.BuildOptions;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.AbstractBlockPlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.DemolishingPlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.RotationalPlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.RestoringPlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.BlockPredicate;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IColors;
import com.chingo247.xplatform.core.IPlayer;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.blockPlacer.IBlockPlacerListener;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;

/**
 *
 * @author Chingo
 */
public class ConstructionManager {

    private static final Logger LOG = Logger.getLogger(ConstructionManager.class.getName());
    private final Lock jobLock = new ReentrantLock();
    private final ExecutorService executor;
    private final APlatform platform;
    private final KeyPool<Long> pool;
    private final GraphDatabaseService graph;
    private final IStructureDAO structureDAO;
    private final IColors colors;

    private Map<Long, StructureJob> tasks;
    private static ConstructionManager instance;

    private ConstructionManager() {
        this.graph = SettlerCraft.getInstance().getNeo4j();
        this.executor = SettlerCraft.getInstance().getExecutor();
        this.platform = SettlerCraft.getInstance().getPlatform();
        this.colors = platform.getChatColors();
        this.pool = new KeyPool<>(executor);
        this.tasks = new HashMap<>();
        this.structureDAO = new StructureDAO(graph);

        AsyncWorldEditMain.getInstance().getBlockPlacer().addListener(new IBlockPlacerListener() {

            @Override
            public void jobAdded(JobEntry je) {
                // DO NOTHING
            }

            @Override
            public void jobRemoved(JobEntry je) {
                if (je instanceof SCJobEntry) {
                    // I FIRED THIS JOB!
                    SCJobEntry jobEntry = (SCJobEntry) je;

                    boolean isCanceled = false;
                    jobLock.lock();
                    try {

                        StructureJob entry = tasks.get(jobEntry.getTaskID());
                        if (entry != null) {
                            isCanceled = entry.isCanceled();
                            if (isCanceled) {
                                if (entry.isWasChecked()) { // Fixes duplicate state 
                                    isCanceled = false; // dont fire it again...
                                } else {
                                    entry.setWasChecked(true);
                                }
                            }
                        }
                    } finally {
                        jobLock.unlock();
                    }

                    if (isCanceled) {
                        AsyncEventManager.getInstance().post(new StructureJobCanceledEvent(jobEntry.getWorld(), jobEntry.getTaskID(), jobEntry.getJobId()));
                    }
                }
            }
        });
        AsyncEventManager.getInstance().register(new ConstructionEventHandler());
    }

    public static ConstructionManager getInstance() {
        if (instance == null) {
            instance = new ConstructionManager();
        }
        return instance;
    }

    /**
     * Builds the structure
     *
     * @param structure The structure that has to be build
     * @param player The player uuid, The UUID will be used to register this
     * build-operation. This method does NOT check if the player is allowed to
     * build
     * @param session The session to use
     * @param options The options, use {@link Options#defaultOptions() } to get
     * the default options
     * @param force whether the current construction state should be ignored.
     * Therefore forcefully stops and starts a build operation
     */
    public void build(final Structure structure, final UUID player, final EditSession session, final BuildOptions options, boolean force) throws ConstructionException {
        Preconditions.checkNotNull(structure, "Structure may not be null");
        Preconditions.checkNotNull(player, "UUID may not be null");
        Preconditions.checkNotNull(session, "EditSession may not be null");
        Preconditions.checkNotNull(options, "Options may not be null");

        if (structure.getConstructionStatus() == ConstructionStatus.REMOVED) {
            throw new ConstructionException("Can't build a removed structure");
        }
        
        pool.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {

                // Check if task already added
                // If so check if the task was building!
                // If so continue, otherwise recursively stop
                StructureJob currentEntry = tasks.get(structure.getId());
                if (currentEntry != null) {
                    try {
                        stop(structure, false, true);
                    } catch (ConstructionException ex) {
                        // silent
                    }
                }

                try (Transaction tx = graph.beginTx()) {
                    try {
                        StructureNode node = structureDAO.find(structure.getId());
                        
                        
                        
                        if(node == null) {
                            tx.success();
                            return;
                        }
                        System.out.println("Building: " + node.getId());
                        
                        List<StructureNode> substructures = node.getSubstructures();
                        for(StructureNode s : substructures) {
                            final CuboidRegion region = s.getCuboidRegion();
                            System.out.println("Ignoring: " + s.getId() + ", " + region.getMinimumPoint() + ", " + region.getMaximumPoint());
                            
                            options.addIgnore(new BlockPredicate() {

                                @Override
                                public boolean evaluate(Vector position, Vector worldPosition, BaseBlock block) {
                                    return region.contains(worldPosition);
                                }
                            });
                        }
                        

                        PlayerEntry playerEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(player);

                        tasks.put(structure.getId(), new StructureJob(-1, false, player));
                        final World w = SettlerCraft.getInstance().getWorld(structure.getWorld());
                        if (w == null) {
                            return;
                        }

                        Placement p = structure.getStructurePlan().getPlacement();
                        System.out.println("Should rotate?");
                        if (p instanceof RotationalPlacement) {
                            System.out.println("Rotate!");
                            RotationalPlacement rt = (RotationalPlacement) p;
                            rt.rotate(structure.getDirection());
                        }

                        AsyncPlacement placement = new AsyncPlacement(playerEntry, p, new AsyncPlacementCallback() {

                            @Override
                            public void onJobAdded(int jobId) {
                                AsyncEventManager.getInstance().post(new StructureJobAddedEvent(w, structure.getId(), jobId, false));
                            }
                        }, structure);

                        System.out.println("Min point: " + structure.getCuboidRegion().getMinimumPoint());
                        System.out.println("Max point: " + structure.getCuboidRegion().getMaximumPoint());

                        placement.place(session, structure.getCuboidRegion().getMinimumPoint(), options);

                    } catch (Exception exception) {
                        LOG.log(Level.SEVERE, exception.getMessage(), exception);
                    }
                    tx.success();
                }

                try {

                } catch (Exception exception) {
                    LOG.log(Level.SEVERE, exception.getMessage(), exception);
                }

            }
        });
    }

    public void demolish(final Structure structure, final UUID player, final EditSession session, final DemolishingOptions options, boolean force) throws ConstructionException {
        Preconditions.checkNotNull(structure, "Structure may not be null");
        Preconditions.checkNotNull(player, "UUID may not be null");
        Preconditions.checkNotNull(session, "EditSession may not be null");
        Preconditions.checkNotNull(options, "Options may not be null");

        if (structure.getConstructionStatus() == ConstructionStatus.REMOVED) {
            throw new ConstructionException("Can't demolish a removed structure");
        }

        pool.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                PlayerEntry playerEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(player);

                final World w = SettlerCraft.getInstance().getWorld(structure.getWorld());
                if (w == null) {
                    return;
                }

                Structure parent = null;

                try (Transaction tx = graph.beginTx()) {
                    StructureNode node = structureDAO.find(structure.getId());
                    boolean hasSubstructures = node.hasSubstructures();
                    if (hasSubstructures) {
                        if (player != null) {
                            Player ply = SettlerCraft.getInstance().getPlayer(player);
                            String errorMessage = "Substructures need to be removed before removing #" + structure.getId() + " " + structure.getName();
                            if (ply != null) {
                                ply.printError(errorMessage);
                            } else {
                                System.out.println(errorMessage);
                            }

                        }
                        tx.success();
                        return;
                    }

                    StructureNode parentNode = node.getParent();

                    System.out.println("Parent: " + parentNode);
                    if (parentNode != null) {
                        parent = DefaultStructureFactory.getInstance().makeStructure(parentNode);
                    }

                    tx.success();
                }

                StructureJob currentEntry = tasks.get(structure.getId());
                if (currentEntry != null) {
                    try {
                        stop(structure, false, true);
                    } catch (ConstructionException ex) {
                        // silent
                    }
                }

                tasks.put(structure.getId(), new StructureJob(-1, true, player));
                DemolishingPlacement dp;

                if (parent == null || (!(parent.getStructurePlan().getPlacement() instanceof AbstractBlockPlacement))) {
                    
                    CuboidRegion region = structure.getCuboidRegion();
                    Vector size = region.getMaximumPoint().subtract(region.getMinimumPoint()).add(1, 1, 1);
                    
                    dp = new DemolishingPlacement(size);
                    AsyncDemolishingPlacement placement = new AsyncDemolishingPlacement(playerEntry, dp, new AsyncPlacementCallback() {

                        @Override
                        public void onJobAdded(int jobId) {
                            AsyncEventManager.getInstance().post(new StructureJobAddedEvent(w, structure.getId(), jobId, true));
                        }
                    }, structure);
                    placement.place(session, structure.getCuboidRegion().getMinimumPoint(), options);
                } else {
                    StructurePlan plan = parent.getStructurePlan();
                    Placement parentPlacement = plan.getPlacement();

                    if (parentPlacement instanceof RotationalPlacement) {
                        RotationalPlacement rt = (RotationalPlacement) parentPlacement;
                        rt.rotate(parent.getDirection());
                    }

                    // Get Area of the child placement
                    final CuboidRegion childRegion = structure.getCuboidRegion();
                    options.addIgnore(new BlockPredicate() {

                        @Override
                        public boolean evaluate(Vector position, Vector worldPosition, BaseBlock block) {
                            return !childRegion.contains(worldPosition);
                        }
                    });
                    
                    dp = new RestoringPlacement((AbstractBlockPlacement) parentPlacement);
                    AsyncDemolishingPlacement placement = new AsyncDemolishingPlacement(playerEntry, dp, new AsyncPlacementCallback() {

                        @Override
                        public void onJobAdded(int jobId) {
                            AsyncEventManager.getInstance().post(new StructureJobAddedEvent(w, structure.getId(), jobId, true));
                        }
                    }, structure);
                    placement.place(session, parent.getCuboidRegion().getMinimumPoint(), options);
                }

                // Set Negative MASK
                // Set Negative not natural MASK
            }
        });
    }

    /**
     * Stops a structure Build/Demolish operation
     *
     * @param player The player to report back to or null
     * @param structure The structure
     * @param force
     */
    public void stop(final Player player, final Structure structure, final boolean talk, boolean force) throws ConstructionException {
        Preconditions.checkNotNull(structure, "Structure may not be null");

        if (structure.getConstructionStatus() == ConstructionStatus.REMOVED) {
            throw new ConstructionException("Can't stop a removed structure");
        }

        pool.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                UUID uuid = null;
                Integer jobId = null;

                jobLock.lock();
                try {
                    StructureJob entry = tasks.get(structure.getId());
                    if (entry == null) {
                        return;
                    } else {
                        entry.setIsCanceled(true);
                    }

                    uuid = entry.getWhoStarted();
                    jobId = entry.getJobId();
                } finally {
                    jobLock.unlock();
                }

                if (uuid != null && jobId != null) {
                    PlayerEntry entry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(uuid);
                    if (entry != null) {
                        BlockPlacer blockPlacer = AsyncWorldEditMain.getInstance().getBlockPlacer();
                        blockPlacer.cancelJob(entry, jobId);
                        if (talk && player != null) {
                            player.print("Stopping structure #" + structure.getId());
                        }
                    }
                }
            }
        });
    }

    /**
     * Stops a structure Build/Demolish
     *
     * @param structure
     * @param talk
     * @param force
     */
    public void stop(final Structure structure, boolean talk, boolean force) throws ConstructionException {
        stop(null, structure, false, force);
    }

    private class StructureJob {

        private int currentJobId;
        private boolean isDemolishing;
        private UUID whoStarted;
        private boolean isCanceled = false;
        private boolean wasChecked = false;

        public StructureJob(int jobId, boolean isDemolishing, UUID whoStarted) {
            this.currentJobId = jobId;
            this.isDemolishing = isDemolishing;
            this.whoStarted = whoStarted;
        }

        public void setIsCanceled(boolean isCanceled) {
            this.isCanceled = isCanceled;
        }

        public boolean isCanceled() {
            return isCanceled;
        }

        // Used to fix duplicate state report 
        public boolean isWasChecked() {
            return wasChecked;
        }

        public void setWasChecked(boolean wasChecked) {
            this.wasChecked = wasChecked;
        }

        public int getJobId() {
            return currentJobId;
        }

        public UUID getWhoStarted() {
            return whoStarted;
        }

        public boolean isDemolishing() {
            return isDemolishing;
        }

        public void setIsDemolishing(boolean isDemolishing) {
            this.isDemolishing = isDemolishing;
        }

        public void setJobId(int jobId) {
            this.currentJobId = jobId;
        }

        public void setWhoStarted(UUID whoStarted) {
            this.whoStarted = whoStarted;
        }

    }

    /**
     *
     * @author Chingo
     */
    private class ConstructionEventHandler {

        ConstructionEventHandler() {
        }

        @Subscribe
        @AllowConcurrentEvents
        public void onJobAddedEvent(StructureJobAddedEvent jobAddedEvent) {
            long structureId = jobAddedEvent.getStructure();
            World w = jobAddedEvent.getWorld();
            UUID uuid;
            jobLock.lock();
            try {
                int jobId = jobAddedEvent.getJobId();
                StructureJob entry = tasks.get(structureId);
                entry.setJobId(jobId);
                uuid = entry.getWhoStarted();
            } finally {
                jobLock.unlock();
            }

            Structure structure;
            try (Transaction tx = graph.beginTx()) {
                StructureNode structureNode = structureDAO.find(structureId);
                structureNode.setConstructionStatus(ConstructionStatus.QUEUED);
                structure = DefaultStructureFactory.getInstance().makeStructure(structureNode);
                tx.success();
            }

            EventManager.getInstance().getEventBus().post(new StructureStateChangeEvent(structure));

            if (uuid != null) {
                IPlayer player = platform.getServer().getPlayer(uuid);
                if (player != null) {
                    String status = getStatusString(structure);
                    player.sendMessage(status);
                }
            }

        }

        @Subscribe
        @AllowConcurrentEvents
        public void onJobCanceledEvent(StructureJobCanceledEvent jobCanceledEvent) {
            long structureId = jobCanceledEvent.getStructure();

            jobLock.lock();
            try {
                tasks.remove(structureId);
            } finally {
                jobLock.unlock();
            }

            Structure structure;
            List<IPlayer> owners = new ArrayList<>();
            try (Transaction tx = graph.beginTx()) {
                StructureNode structureNode = structureDAO.find(structureId);
                structureNode.setConstructionStatus(ConstructionStatus.STOPPED);
                structure = DefaultStructureFactory.getInstance().makeStructure(structureNode);
                List<SettlerNode> settlers = structureNode.getOwners();
                for (SettlerNode settlerNode : settlers) {
                    IPlayer player = platform.getPlayer(settlerNode.getId());
                    owners.add(player);
                }
                tx.success();
            }
            EventManager.getInstance().getEventBus().post(new StructureStateChangeEvent(structure));

            String status = getStatusString(structure);

            // Tell the starter
            for (IPlayer p : owners) {
                p.sendMessage(status);
            }
        }

        @Subscribe
        @AllowConcurrentEvents
        public void onJobCompleteEvent(StructureJobCompleteEvent jobCompleteEvent) {
            boolean isDemolishing;
            long structureId = jobCompleteEvent.getStructure();
            World w = jobCompleteEvent.getWorld();

            jobLock.lock();
            try {
                StructureJob job = tasks.get(structureId);
                if (job == null) {
                    return;
                }
                isDemolishing = job.isDemolishing;
                tasks.remove(structureId);
            } finally {
                jobLock.unlock();
            }

            // Set the status
            Structure structure;
            List<IPlayer> owners = new ArrayList<>();
            StructureNode structureNode;
            try (Transaction tx = graph.beginTx()) {
                structureNode = structureDAO.find(structureId);
                if (isDemolishing) {
                    structureNode.setConstructionStatus(ConstructionStatus.REMOVED);

                    double price = structureNode.getPrice();
                    IEconomyProvider economyProvider = SettlerCraft.getInstance().getEconomyProvider();
                    if (economyProvider != null && price > 0) {
                        SettlerNode settler = structureDAO.getMasterOwnerForStructure(structureId);
                        if (settler != null) {
                            IPlayer player = platform.getPlayer(settler.getId());
                            if (player != null) {
                                economyProvider.give(player.getUniqueId(), price);
                                player.sendMessage("You've been refunded " + colors.gold() + price);
                                structureNode.setPrice(0);
                            }
                        }
                    }

                } else {
                    structureNode.setConstructionStatus(ConstructionStatus.COMPLETED);
                }
                structure = DefaultStructureFactory.getInstance().makeStructure(structureNode);
                List<SettlerNode> settlers = structureNode.getOwners();
                for (SettlerNode settlerNode : settlers) {
                    IPlayer player = platform.getPlayer(settlerNode.getId());
                    owners.add(player);
                }

                tx.success();
            }
            EventManager.getInstance().getEventBus().post(new StructureStateChangeEvent(structure));

            String status = getStatusString(structure);

            // Tell the starter
            for (IPlayer p : owners) {
                p.sendMessage(status);
            }
        }

        @Subscribe
        @AllowConcurrentEvents
        public void onJobStartedEvent(StructureJobStartedEvent jobStartedEvent) {
            long structureId = jobStartedEvent.getStructure();
            World w = jobStartedEvent.getWorld();

            boolean isDemolishing = false;
            jobLock.lock();
            try {
                StructureJob entry = tasks.get(structureId);
                isDemolishing = entry.isDemolishing();
            } finally {
                jobLock.unlock();
            }

            // Set the status!
            Structure structure;
            List<IPlayer> owners = new ArrayList<>();
            try (Transaction tx = graph.beginTx()) {
                StructureNode structureNode = structureDAO.find(structureId);
                if (isDemolishing) {
                    structureNode.setConstructionStatus(ConstructionStatus.DEMOLISHING);
                } else {
                    structureNode.setConstructionStatus(ConstructionStatus.BUILDING);
                }
                structure = DefaultStructureFactory.getInstance().makeStructure(structureNode);

                List<SettlerNode> settlers = structureNode.getOwners();
                for (SettlerNode settlerNode : settlers) {
                    IPlayer ply = platform.getPlayer(settlerNode.getId());
                    owners.add(ply);
                }
                tx.success();
            }

            EventManager.getInstance().getEventBus().post(new StructureStateChangeEvent(structure));

            String status = getStatusString(structure);
            // Tell the new status!
            for (IPlayer p : owners) {
                p.sendMessage(status);
            }
        }

        /**
         * Sends the status of this structure to given player
         *
         * @param structure The structure
         * @param player The player to tell
         */
        public String getStatusString(Structure structure) {
            String statusString;
            ConstructionStatus status = structure.getConstructionStatus();
            switch (status) {
                case BUILDING:
                    statusString = colors.yellow() + "BUILDING " + colors.reset() + "#" + colors.gold() + structure.getId() + colors.blue() + " " + structure.getName();
                    break;
                case DEMOLISHING:
                    statusString = colors.yellow() + "DEMOLISHING " + colors.reset() + "#" + colors.gold() + structure.getId() + colors.blue() + " " + structure.getName();
                    break;
                case COMPLETED:
                    statusString = colors.green() + "COMPLETE " + colors.reset() + "#" + colors.gold() + structure.getId() + colors.blue() + " " + structure.getName();
                    break;
                case ON_HOLD:
                    statusString = colors.red() + "ON HOLD " + colors.reset() + "#" + colors.gold() + structure.getId() + colors.blue() + " " + structure.getName();
                    break;
                case QUEUED:
                    statusString = colors.yellow() + "QUEUED " + colors.reset() + "#" + colors.gold() + structure.getId() + colors.blue() + " " + structure.getName();
                    break;
                case REMOVED:
                    statusString = colors.red() + "REMOVED " + colors.reset() + "#" + colors.gold() + structure.getId() + colors.blue() + " " + structure.getName();
                    break;
                case STOPPED:
                    statusString = colors.red() + "STOPPED " + colors.reset() + "#" + colors.gold() + structure.getId() + colors.blue() + " " + structure.getName();
                    break;
                default:
                    statusString = status.name();
            }
            return statusString;
        }

    }

}
