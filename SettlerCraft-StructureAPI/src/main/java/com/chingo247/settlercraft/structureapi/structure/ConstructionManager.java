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
import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import com.chingo247.settlercraft.core.util.KeyPool;
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
import com.chingo247.settlercraft.structureapi.structure.options.DemolishingOptions;
import com.chingo247.settlercraft.structureapi.structure.options.PlaceOptions;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.DemolishingPlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IColors;
import com.chingo247.xplatform.core.IPlayer;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
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
                                if (entry.isWasChecked()) {
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
                        AsyncEventManager.getInstance().post(new StructureJobCanceledEvent(jobEntry.getTaskID(), jobEntry.getJobId()));
                    }
                }
            }
        });
        AsyncEventManager.getInstance().register(new ConstructionEventHandler());
    }
    
    public static ConstructionManager getInstance() {
        if(instance == null) {
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
    public void build(final Structure structure, final UUID player, final EditSession session, final PlaceOptions options, boolean force) {
        Preconditions.checkNotNull(structure, "Structure may not be null");
        Preconditions.checkNotNull(player, "UUID may not be null");
        Preconditions.checkNotNull(session, "EditSession may not be null");
        Preconditions.checkNotNull(options, "Options may not be null");

        pool.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                // Check if task already added
                // If so check if the task was building!
                // If so continue, otherwise recursively stop

                StructureJob currentEntry = tasks.get(structure.getId());
                if (currentEntry != null) {
                    stop(structure, true);
                }

                try (Transaction tx = graph.beginTx()) {
                    try {

                        PlayerEntry playerEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(player);
                        tasks.put(structure.getId(), new StructureJob(-1, false, player));

                        AsyncPlacement placement = new AsyncPlacement(playerEntry, structure.getStructurePlan().getPlacement(), new AsyncPlacementCallback() {

                            @Override
                            public void onJobAdded(int jobId) {
                                AsyncEventManager.getInstance().post(new StructureJobAddedEvent(structure.getId(), jobId, false));
                            }
                        }, structure);
                        placement.rotate(structure.getDirection());
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

    public void demolish(final Structure structure, final UUID player, final EditSession session, final DemolishingOptions options, boolean force) {
        pool.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                PlayerEntry playerEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(player);
                Placement p = structure.getStructurePlan().getPlacement();
                p.rotate(structure.getDirection());
                CuboidRegion region = p.getCuboidRegion();

                DemolishingPlacement dp = new DemolishingPlacement(region.getMaximumPoint());
                tasks.put(structure.getId(), new StructureJob(-1, true, player));
                AsyncDemolishingPlacement placement = new AsyncDemolishingPlacement(playerEntry, dp, new AsyncPlacementCallback() {

                    @Override
                    public void onJobAdded(int jobId) {
                        AsyncEventManager.getInstance().post(new StructureJobAddedEvent(structure.getId(), jobId, true));
                    }
                }, structure);

                // Set Negative MASK
                // Set Negative not natural MASK
                placement.place(session, structure.getCuboidRegion().getMinimumPoint(), options);
            }
        });
    }

    /**
     * Stops a structure Build/Demolish operation
     *
     * @param player The player, which will only be used for feedback. May be
     * null
     * @param structure The structure
     * @param force
     */
    public void stop(final Player player, final Structure structure, boolean force) {
        Preconditions.checkNotNull(structure, "Structure may not be null");
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
                        if (player != null) {
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
     * @param force
     */
    public void stop(final Structure structure, boolean force) {
        stop(null, structure, force);
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
                structure = DefaultStructureFactory.instance().makeStructure(structureNode);
                tx.success();
            }

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
                structure = DefaultStructureFactory.instance().makeStructure(structureNode);
                List<SettlerNode> settlers = structureNode.getOwners();
                for (SettlerNode settlerNode : settlers) {
                    IPlayer player = platform.getPlayer(settlerNode.getId());
                    owners.add(player);
                }
                tx.success();
            }

            String status = getStatusString(structure);

            // Tell the starter
            for (IPlayer p : owners) {
                p.sendMessage(status);
            }
        }

        @Subscribe
        @AllowConcurrentEvents
        public void onJobCompleteEvent(StructureJobCompleteEvent jobCompleteEvent) {
            boolean isDemolishing = false;
            long structureId = jobCompleteEvent.getStructure();

            jobLock.lock();
            try {
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
                } else {
                    structureNode.setConstructionStatus(ConstructionStatus.COMPLETED);
                }
                structure = DefaultStructureFactory.instance().makeStructure(structureNode);
                List<SettlerNode> settlers = structureNode.getOwners();
                for (SettlerNode settlerNode : settlers) {
                    IPlayer player = platform.getPlayer(settlerNode.getId());
                    owners.add(player);
                }
                
                tx.success();
            }
            
            

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
                structure = DefaultStructureFactory.instance().makeStructure(structureNode);

                List<SettlerNode> settlers = structureNode.getOwners();
                for (SettlerNode settlerNode : settlers) {
                    IPlayer ply = platform.getPlayer(settlerNode.getId());
                    owners.add(ply);
                }
                tx.success();
            }

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
