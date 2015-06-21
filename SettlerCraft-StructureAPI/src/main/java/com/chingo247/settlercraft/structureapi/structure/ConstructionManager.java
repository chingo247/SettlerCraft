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
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.chingo247.settlercraft.core.platforms.services.IEconomyProvider;
import com.chingo247.settlercraft.core.util.KeyPool;
import com.chingo247.settlercraft.structureapi.event.StructureStateChangeEvent;
import com.chingo247.settlercraft.structureapi.exception.ConstructionException;
import com.chingo247.settlercraft.structureapi.event.async.StructureJobAddedEvent;
import com.chingo247.settlercraft.structureapi.event.async.StructureJobCanceledEvent;
import com.chingo247.settlercraft.structureapi.event.async.StructureJobCompleteEvent;
import com.chingo247.settlercraft.structureapi.event.async.StructureJobStartedEvent;
import com.chingo247.settlercraft.structureapi.model.owner.StructureOwnerType;
import com.chingo247.settlercraft.structureapi.model.structure.StructureRepository;
import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureOwner;
import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureRepository;
import com.chingo247.settlercraft.structureapi.model.owner.StructureOwnerNode;
import com.chingo247.settlercraft.structureapi.model.structure.Structure;
import com.chingo247.settlercraft.structureapi.structure.construction.asyncworldedit.AsyncDemolishingPlacement;
import com.chingo247.settlercraft.structureapi.structure.construction.asyncworldedit.AsyncPlacement;
import com.chingo247.settlercraft.structureapi.structure.construction.asyncworldedit.AsyncPlacementCallback;
import com.chingo247.settlercraft.structureapi.structure.construction.asyncworldedit.SCJobEntry;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.DemolishingOptions;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.BuildOptions;
import com.chingo247.settlercraft.structureapi.structure.plan.IStructurePlan;
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
import org.primesoft.asyncworldedit.api.blockPlacer.IBlockPlacer;
import org.primesoft.asyncworldedit.api.blockPlacer.IBlockPlacerListener;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;
import org.primesoft.asyncworldedit.playerManager.PlayerEntry;

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
    private final IStructureRepository structureRepository;
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
        this.structureRepository = new StructureRepository(graph);

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
                        AsyncEventManager.getInstance().post(new StructureJobCanceledEvent(jobEntry.getTaskID(), jobEntry.getJobId()));
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
     * @param force if the structure has already been completed, the status will be ignored.
     * Therefore forcefully stops and starts a build operation
     */
    public void build(final Structure structure, final UUID player, final EditSession session, final BuildOptions options, boolean force) throws ConstructionException {
        Preconditions.checkNotNull(structure, "Structure may not be null");
        Preconditions.checkNotNull(player, "UUID may not be null");
        Preconditions.checkNotNull(session, "EditSession may not be null");
        Preconditions.checkNotNull(options, "Options may not be null");

        if (structure.getStatus() == StructureStatus.REMOVED) {
            throw new ConstructionException("Can't build a removed structure");
        }
        
        if (structure.getStatus() == StructureStatus.COMPLETED && !force) {
            throw new ConstructionException("Structure has already completed");
        }
       

        // Queue a stop
        // Then Queue a build
        pool.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {

                try {
                    StructureJob currentEntry = tasks.get(structure.getId());
                    if (currentEntry != null) {
                        stopSync(SettlerCraft.getInstance().getPlayer(player), structure, false, true);
                    }

                    try (Transaction tx = graph.beginTx()) {
                        try {
                            StructureNode node = structureRepository.findById(structure.getId());
                           

                            if (node == null) {
                                tx.success();
                                return;
                            }
                            

                            // Ignore substructures!
                            List<StructureNode> substructures = node.getSubstructures();
                            for (StructureNode s : substructures) {
                                final CuboidRegion region = s.getCuboidRegion();

                                options.addIgnore(new BlockPredicate() {

                                    @Override
                                    public boolean evaluate(Vector position, Vector worldPosition, BaseBlock block) {
                                        return region.contains(worldPosition);
                                    }
                                });
                            }

                            PlayerEntry playerEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(player);

                            tasks.put(structure.getId(), new StructureJob(-1, false, player));
                            final World w = SettlerCraft.getInstance().getWorld(structure.getWorld().getUUID());
                            if (w == null) {
                                return;
                            }

                            Placement p = structure.getStructurePlan().getPlacement();
                            if (p instanceof RotationalPlacement) {
                                RotationalPlacement rt = (RotationalPlacement) p;
                                System.out.println("Before rotate: " + rt.getRotation());
                                rt.rotate(structure.getDirection().getRotation());
                                System.out.println("After rotate: " + rt.getRotation());
                            }

                            AsyncPlacement placement = new AsyncPlacement(playerEntry, p, new AsyncPlacementCallback() {

                                @Override
                                public void onJobAdded(int jobId) {
                                    AsyncEventManager.getInstance().post(new StructureJobAddedEvent(structure.getId(), jobId, false));
                                }
                            }, structure.getId());

                            placement.place(session, structure.getCuboidRegion().getMinimumPoint(), options);

                        } catch (Exception exception) {
                            LOG.log(Level.SEVERE, exception.getMessage(), exception);
                        }
                        tx.success();
                    }

                } catch (Exception ex) { // Catch everything or disappear it will dissappear in the abyss!
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }

            }
        });
    }
    
    public void demolish(final Structure structure, final UUID player, final EditSession session, final DemolishingOptions options, boolean force) throws ConstructionException {
        Preconditions.checkNotNull(structure, "Structure may not be null");
        Preconditions.checkNotNull(player, "UUID may not be null");
        Preconditions.checkNotNull(session, "EditSession may not be null");
        Preconditions.checkNotNull(options, "Options may not be null");

        final Long structureId;
        try (Transaction tx = graph.beginTx()) {
            if (structure.getStatus() == StructureStatus.REMOVED) {
                throw new ConstructionException("Can't demolish a removed structure");
            }
            structureId = structure.getId();
            tx.success();
        }

        pool.execute(structureId, new Runnable() {

            @Override
            public void run() {
                PlayerEntry playerEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(player);
                try {
                    final World w = SettlerCraft.getInstance().getWorld(structure.getWorld().getUUID());
                    if (w == null) {
                        return;
                    }

                    StructureNode parent;
                    IStructurePlan plan;
                    CuboidRegion region;

                    try (Transaction tx = graph.beginTx()) {
                        StructureNode structure = structureRepository.findById(structureId);
                        boolean hasSubstructures = structure.hasSubstructures();
                        if (hasSubstructures) {
                            if (player != null) {
                                Player ply = SettlerCraft.getInstance().getPlayer(player);
                                String errorMessage = "[SettlerCraft]: Substructures need to be removed before removing #" + structure.getId() + " " + structure.getName();
                                if (ply != null) {
                                    ply.printError(errorMessage);
                                }
                            }
                            tx.success();
                            return;
                        }
                        plan = structure.getStructurePlan();
                        region = structure.getCuboidRegion();
                        parent = structure.getParent();
                        tx.success();
                    }

                        stopSync(SettlerCraft.getInstance().getPlayer(player), structure, false, true);

                        tasks.put(structureId, new StructureJob(-1, true, player));
                        DemolishingPlacement dp;

                        if (parent == null || (!(parent.getStructurePlan().getPlacement() instanceof AbstractBlockPlacement))) {
                            Vector size = region.getMaximumPoint().subtract(region.getMinimumPoint()).add(1, 1, 1);

                            dp = new DemolishingPlacement(size);
                            AsyncDemolishingPlacement placement = new AsyncDemolishingPlacement(playerEntry, dp, new AsyncPlacementCallback() {

                                @Override
                                public void onJobAdded(int jobId) {
                                    AsyncEventManager.getInstance().post(new StructureJobAddedEvent(structureId, jobId, true));
                                }
                            }, structureId);
                            placement.place(session, region.getMinimumPoint(), options);
                        } else {
                            Placement parentPlacement = plan.getPlacement();

                            if (parentPlacement instanceof RotationalPlacement) {
                                RotationalPlacement rt = (RotationalPlacement) parentPlacement;
                                rt.rotate(parent.getDirection().getRotation());
                            }

                            // Get Area of the child placement
                            final CuboidRegion childRegion = region;
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
                                    AsyncEventManager.getInstance().post(new StructureJobAddedEvent(structureId, jobId, true));
                                }
                            }, structureId);
                            placement.place(session, parent.getCuboidRegion().getMinimumPoint(), options);
                        }
                      

                } catch (Exception ex) { // Catch everything or disappear it will dissappear in the abyss!
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        });
    }

    private void stopSync(final Player player, final Structure structure, final boolean talk, boolean force) {

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
                IBlockPlacer blockPlacer = AsyncWorldEditMain.getInstance().getBlockPlacer();
                blockPlacer.cancelJob(entry, jobId);
                if (talk && player != null) {
                    player.print("Stopping structure #" + structure.getId());
                }
            }
        }
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

        if (structure.getStatus() == StructureStatus.REMOVED) {
            throw new ConstructionException("Can't stop a removed structure");
        }

        pool.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                try {
                    stopSync(player, structure, talk, talk);
                } catch (Exception ex) { // Catch everything or disappear it will dissappear in the abyss!
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
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
        private boolean isPlacingFence;
        private UUID whoStarted;
        private UUID fenceUUID;
        private boolean isCanceled = false;
        private boolean wasChecked = false;

        public StructureJob(int jobId, boolean isDemolishing, UUID whoStarted) {
            this.currentJobId = jobId;
            this.isDemolishing = isDemolishing;
            this.whoStarted = whoStarted;
        }

        public UUID getFenceUUID() {
            return fenceUUID;
        }

        public void setFenceUUID(UUID fenceUUID) {
            this.fenceUUID = fenceUUID;
        }

        public void setIsPlacingFence(boolean isPlacingFence) {
            this.isPlacingFence = isPlacingFence;
        }

        public boolean isPlacingFence() {
            return isPlacingFence;
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
                StructureNode structureNode = structureRepository.findById(structureId);
                structureNode.setStatus(StructureStatus.QUEUED);
                structure = new Structure(structureNode);
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
            String status;
            List<IPlayer> owners = new ArrayList<>();
            try (Transaction tx = graph.beginTx()) {
                StructureNode structureNode = structureRepository.findById(structureId);
                structureNode.setStatus(StructureStatus.STOPPED);
                List<StructureOwnerNode> settlers = structureNode.getOwners();
                for (IStructureOwner settlerNode : settlers) {
                    IPlayer player = platform.getPlayer(settlerNode.getUUID());
                    if(player != null) {
                        owners.add(player);
                    }
                }
                structure = new Structure(structureNode);
                status = getStatusString(structure);
                tx.success();
            }
            EventManager.getInstance().getEventBus().post(new StructureStateChangeEvent(structure));


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
            try (Transaction tx = graph.beginTx()) {
                StructureNode structureNode = structureRepository.findById(structureId);
                if (isDemolishing) {
                    structureNode.setStatus(StructureStatus.REMOVED);

                    double price = structureNode.getPrice();
                    IEconomyProvider economyProvider = SettlerCraft.getInstance().getEconomyProvider();
                    if (economyProvider != null && price > 0) {
                        List<StructureOwnerNode> settlers = structureNode.getOwners(StructureOwnerType.MASTER);
                        double refundValue = price / settlers.size();
                        if (!settlers.isEmpty()) {
                            for (IStructureOwner settler : settlers) {
                                IPlayer player = platform.getPlayer(settler.getUUID());
                                if (player != null) {
                                    economyProvider.give(player.getUniqueId(), refundValue);
                                    double newBalance = economyProvider.getBalance(player.getUniqueId());
                                    player.sendMessage("You've been refunded " + colors.gold() + refundValue, "Your new balance is " + colors.gold() + ShopUtil.valueString(newBalance));
                                }
                            }
                        }
                        structureNode.setPrice(0);
                    }

                } else {
                    structureNode.setStatus(StructureStatus.COMPLETED);
                }
                List<StructureOwnerNode> settlers = structureNode.getOwners();
                for (IStructureOwner settlerNode : settlers) {
                    IPlayer player = platform.getPlayer(settlerNode.getUUID());
                    owners.add(player);
                }
                structure = new Structure(structureNode);

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
            String status;
            try (Transaction tx = graph.beginTx()) {
                StructureNode structureNode = structureRepository.findById(structureId);
                if (isDemolishing) {
                    structureNode.setStatus(StructureStatus.DEMOLISHING);
                } else {
                    structureNode.setStatus(StructureStatus.BUILDING);
                }
                
                

                List<StructureOwnerNode> settlers = structureNode.getOwners();
                for (StructureOwnerNode settlerNode : settlers) {
                    IPlayer ply = platform.getPlayer(settlerNode.getUUID());
                    if(ply != null) {
                        owners.add(ply);
                    }
                }
                
                structure = new Structure(structureNode);
                status = getStatusString(structure);
                
                tx.success();
            }

            EventManager.getInstance().getEventBus().post(new StructureStateChangeEvent(structure));

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
            StructureStatus status = structure.getStatus();
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
