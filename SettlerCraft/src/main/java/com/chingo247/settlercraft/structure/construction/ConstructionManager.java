/*
 * Copyright (C) 2014 Chingo247
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
package com.chingo247.settlercraft.structure.construction;

import com.chingo247.settlercraft.bukkit.events.StructureStateChangeEvent;
import com.chingo247.settlercraft.exception.ConstructionException;
import com.chingo247.settlercraft.exception.StructureDataException;
import com.chingo247.settlercraft.persistence.StructureService;
import com.chingo247.settlercraft.plugin.SettlerCraft;
import com.chingo247.settlercraft.structure.StructureAPI;
import com.chingo247.settlercraft.structure.construction.asyncworldedit.SCAsyncClipboard;
import com.chingo247.settlercraft.structure.construction.asyncworldedit.SCJobEntry;
import com.chingo247.settlercraft.structure.construction.generator.ClipboardGenerator;
import com.chingo247.settlercraft.structure.construction.worldedit.ConstructionClipboard;
import com.chingo247.settlercraft.structure.construction.worldedit.DemolisionClipboard;
import com.chingo247.settlercraft.structure.construction.worldedit.StructureBlock;
import com.chingo247.settlercraft.structure.construction.worldedit.StructureBlockComparators;
import com.chingo247.settlercraft.structure.entities.structure.PlayerOwnership;
import com.chingo247.settlercraft.structure.entities.structure.Structure;
import com.chingo247.settlercraft.structure.entities.structure.Structure.State;
import com.chingo247.settlercraft.structure.entities.world.Direction;
import com.chingo247.settlercraft.structure.plan.SchematicManager;
import com.chingo247.settlercraft.structure.plan.data.schematic.Schematic;
import com.chingo247.settlercraft.util.AsyncWorldEditUtil;
import com.chingo247.settlercraft.util.KeyPool;
import com.chingo247.settlercraft.util.SchematicUtil;
import com.chingo247.settlercraft.util.WorldEditUtil;
import com.chingo247.settlercraft.util.WorldGuardUtil;
import com.sc.module.menuapi.menus.menu.util.EconomyUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.dom4j.DocumentException;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.blockPlacer.IBlockPlacerListener;
import org.primesoft.asyncworldedit.blockPlacer.IJobEntryListener;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.ThreadSafeEditSession;

/**
 *
 * @author Chingo
 */
public class ConstructionManager {

    private final Map<Long, ConstructionEntry> constructionEntries = Collections.synchronizedMap(new HashMap<Long, ConstructionEntry>());
    private final KeyPool<Long> executor = new KeyPool(SettlerCraft.getInstance().getExecutorService());
    private final int FENCE_BLOCK_PLACE_SPEED = 1000;
    private final int INTERVAL = 500;
    private final Material FENCE_MATERIAL = Material.IRON_FENCE;
    private static ConstructionManager instance;

    private ConstructionManager() {
        final BlockPlacer pb = AsyncWorldEditMain.getInstance().getBlockPlacer();
        Bukkit.getPluginManager().registerEvents(new StructureListener(), SettlerCraft.getInstance());
        pb.addListener(new IBlockPlacerListener() {

            @Override
            public void jobAdded(JobEntry je) {
                // DO NOTHING...
            }

            @Override
            public synchronized void jobRemoved(final JobEntry je) {
                Iterator<ConstructionEntry> it = constructionEntries.values().iterator();
                while (it.hasNext()) {
                    ConstructionEntry entry = it.next();
                    
                    
                    if (!entry.isCanceled() && je.getPlayer().getUUID().equals(entry.getPlayer()) && je.getJobId() == entry.getJobId()) {
                        StructureService structureService = new StructureService();
                      
                        Structure structure = entry.getStructure();

                          // Set state to Complete & set CompletedAt timestamp
                        if (structure.getState() != State.COMPLETE && !entry.isDemolishing()) {
                            structure.getLog().setCompletedAt(new Date());
                            structure.setState(State.COMPLETE);
                            structureService.save(structure);
                            constructionEntries.remove(structure.getId());
                        // Set state to Demolishing & set RemovedAt timestamp
                        } else if (structure.getState() != State.REMOVED && entry.isDemolishing()) {
                            structure.getLog().setRemovedAt(new Date());
                            refund(structure);
                            structure.setState(State.REMOVED);
                            structureService.save(structure);
                            constructionEntries.remove(structure.getId());
                        }

                        break;
                    }
                }

            }

        });
    }

    /**
     * Builds a structure
     *
     * @param uuid The player or UUID to use to issue this task
     * @param structure The structure
     * @param force if True the method will skip checks, including the checking if the structure was
     * removed or the structure is already being build
     * @throws StructureDataException
     * @throws ConstructionException
     * @throws IOException
     */
    public void build(final UUID uuid, final Structure structure, final boolean force) throws StructureDataException, ConstructionException, IOException {

        // Queue build task
        executor.execute(structure.getId(),new Runnable() {

            @Override
            public void run() {
                try {
                    if (!force) {
                        performChecks(structure, State.BUILDING);
                    }

                    // Cancel existing task
                    ConstructionEntry entry = getEntry(structure);
                    entry.setCanceled(true);

                    // Cancel task in AsyncWorldEdit
                    if(entry.getJobId() != -1 ) {
                        PlayerEntry plyEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(entry.getPlayer());
                        AsyncWorldEditMain.getInstance().getBlockPlacer().cancelJob(plyEntry, entry.getJobId());
                    }

                    setEntry(uuid, structure);
                    entry = constructionEntries.get(structure.getId()); // NEVER NULL
                    entry.setDemolishing(false);

                    // Load schematic if absent
                    File sf = structure.getSchematicFile();
                    long checksum = FileUtils.checksumCRC32(sf);
                    if (!SchematicManager.getInstance().hasSchematic(checksum)) {
                        structure.setState(State.LOADING_SCHEMATIC);
                        SchematicManager.getInstance().load(sf);
                    }

                    // Get clipboard
                    // Align it!
                    Schematic schematic = SchematicManager.getInstance().getSmartSchematic(sf);
                    CuboidClipboard cc = schematic.getClipboard();
                    SchematicUtil.align(cc, structure.getDirection());

                    // Place a fence
                    placeFence(uuid, structure, cc);

                    queueBuildTask(uuid, structure, cc, new Vector(0, 0, 0));

                } catch (StructureDataException ex) {
                    tell(uuid, ChatColor.RED + "Invalid structureplan");
                } catch (DataException | IOException | DocumentException ex) {
                    Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ConstructionException ex) {
                    tell(uuid, ChatColor.RED + ex.getMessage());
                }
            }
        });
    }

    /**
     * Places a fence
     *
     * @param uuid The uuid for the job
     * @param structure The structure
     * @param schematic The schematic
     * @param material The material used for the fence
     */
    private void placeFence(UUID uuid, Structure structure, CuboidClipboard schematic) {
        //Get player
        Player player = Bukkit.getPlayer(uuid);
        LocalPlayer localPlayer = null;
        if (player != null) {
            localPlayer = WorldEditUtil.getLocalPlayer(player);
        }
        com.sk89q.worldedit.world.World world = WorldEditUtil.getWorld(structure.getWorldName());

        ThreadSafeEditSession editSession;
        if (localPlayer != null) {
            editSession = AsyncWorldEditUtil.getAsyncSessionFactory().getThreadSafeEditSession(world, FENCE_BLOCK_PLACE_SPEED, localPlayer);
        } else {
            editSession = AsyncWorldEditUtil.getAsyncSessionFactory().getThreadSafeEditSession(world, FENCE_BLOCK_PLACE_SPEED);
        }
        editSession.setAsyncForced(false);
        PriorityQueue<StructureBlock> queue = new PriorityQueue<>();
        CuboidClipboard clipboard = ClipboardGenerator.createFence(schematic, FENCE_MATERIAL.getId(), 1);

        for (int x = 0; x < clipboard.getWidth(); x++) {
            for (int z = 0; z < clipboard.getLength(); z++) {
                for (int y = 0; y < clipboard.getHeight(); y++) {
                    BlockVector v = new BlockVector(x, y, z);
                    BaseBlock b = clipboard.getBlock(v);
                    if (b == null) {
                        continue;
                    }
                    queue.add(new StructureBlock(v, clipboard.getBlock(v)));
                }
            }
        }

//        SchematicUtil.align(clipboard, structure.getDirection());
        final World w = Bukkit.getWorld(world.getName());
        final Queue<StructureBlock> place = new PriorityQueue<>();
        // Structures are always drawn from the min position
        Vector pos = structure.getDimension().getMinPosition();
        final Location l = new Location(w, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());

        int placed = 0;
        while (queue.peek() != null) {
            if (placed == FENCE_BLOCK_PLACE_SPEED) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(SettlerCraft.getInstance(), new Runnable() {

                    @Override
                    public void run() {
                        while (place.peek() != null) {
                            StructureBlock b = place.poll();
                            Vector p = b.getPosition();
                            Location loc = l.clone().add(p.getBlockX(), p.getBlockY() + 1, p.getBlockZ());
                            w.getBlockAt(loc).setType(FENCE_MATERIAL);
                        }
                    }
                });

                try {
                    Thread.sleep(INTERVAL);
                    placed = 0;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }

            place.add(queue.poll());
            placed++;
        }

        // Flush Queue if not empty
        if (place.peek() != null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(SettlerCraft.getInstance(), new Runnable() {

                @Override
                public void run() {
                    while (place.peek() != null) {
                        StructureBlock b = place.poll();
                        Vector p = b.getPosition();
                        Location loc = l.clone().add(p.getBlockX(), p.getBlockY() + 1, p.getBlockZ());
                        w.getBlockAt(loc).setType(FENCE_MATERIAL);
                    }
                }
            });
        }

    }

    private void queueBuildTask(final UUID uuid, final Structure structure, CuboidClipboard schematic, Vector offset) {
        final ConstructionClipboard clipboard = new ConstructionClipboard(schematic, StructureBlockComparators.PERFORMANCE);
        Player ply = Bukkit.getPlayer(uuid);
        LocalPlayer lPlayer = null;
        if (ply != null) {
            lPlayer = WorldEditUtil.getLocalPlayer(ply);
        }

        // Create & Place enclosure
        final com.sk89q.worldedit.world.World world = WorldEditUtil.getWorld(structure.getWorldName());
        final Vector pos = structure.getDimension().getMinPosition().add(offset);

        AsyncEditSession aes;

        if (lPlayer == null) {
            aes = (AsyncEditSession) AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(world, -1);
        } else {
            aes = (AsyncEditSession) AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(world, -1, lPlayer);
        }

        PlayerEntry plyEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(uuid);
        SCAsyncClipboard asyncStructureClipboard = new SCAsyncClipboard(plyEntry, clipboard);
            // Note: The Clipboard is always drawn from the min position using the place method
        try {
            asyncStructureClipboard.place(aes, pos, false, new ConstructionCallback() {

                @Override
                public void onJobAdded(SCJobEntry entry) {
                    // Set JobId
                    final StructureService structureService = new StructureService();
                    getEntry(structure).setJobId(entry.getJobId());

                    // Update status
                    if (structure.getState() != State.QUEUED) {
                        structure.setState(State.QUEUED);
                    }
                    structureService.save(structure);

                    // Set state changeListener
                    entry.addStateChangedListener(new IJobEntryListener() {

                        @Override
                        public void jobStateChanged(JobEntry bpje) {
                            if (bpje.getStatus() == JobEntry.JobStatus.PlacingBlocks) {
                                if (structure.getState() != State.BUILDING) {
                                    structure.setState(State.BUILDING);
                                }
                                structureService.save(structure);
                            }
                        }
                    });
                }

                @Override
                public void onJobCanceled(JobEntry entry) {
                    ConstructionManager.getInstance().getEntry(structure).setJobId(-1);
                }
            });
        } catch (MaxChangedBlocksException ex) {
            Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     * Demolished a structure
     *
     * @param uuid The player or uuid to issue this task
     * @param structure The structure
     * @param force Whether to perform checks, if true it will ignore the check that determines if
     * the structure is removed or that the structure is already being demolished.
     * @throws ConstructionException
     * @throws StructureDataException
     */
    public void demolish(final UUID uuid, final Structure structure, final boolean force) throws ConstructionException, StructureDataException {

        // Queue build task
        executor.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                try {
                    if (!force) {
                        performChecks(structure, State.DEMOLISHING);
                    }
                    
                    ConstructionEntry entry = getEntry(structure);
                    entry.setCanceled(true);

                    // Cancel task in AsyncWorldEdit
                    if(entry.getJobId() != -1 ) {
                        PlayerEntry plyEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(entry.getPlayer());
                        AsyncWorldEditMain.getInstance().getBlockPlacer().cancelJob(plyEntry, entry.getJobId());
                    }

                    setEntry(uuid, structure);

                    // Load schematic if absent
                    File sf = structure.getSchematicFile();
                    long checksum = FileUtils.checksumCRC32(sf);
                    if (!SchematicManager.getInstance().hasSchematic(checksum)) {
                        SchematicManager.getInstance().load(sf);
                    }

                    Schematic schematic = SchematicManager.getInstance().getSmartSchematic(sf);
//                    placeFence(uuid, site, schematic, BlockID.IRON_BARS);

                    // Update status
                    StructureService structureService = new StructureService();

                    structureService.save(structure);
                    entry.setDemolishing(true);

                    // Start demolision
                    queueDemolisionTask(uuid, structure, schematic);

                } catch (StructureDataException | IOException | DataException | DocumentException ex) {
                    Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ConstructionException ex) {
                    tell(uuid, ChatColor.RED + ex.getMessage());
                }
            }
        });
    }

    private void queueDemolisionTask(final UUID uuid, final Structure structure, Schematic schematic) throws IOException, DataException {
        // Align schematic
        Direction direction = structure.getDirection();
        final DemolisionClipboard clipboard = new DemolisionClipboard(schematic.getClipboard(), StructureBlockComparators.PERFORMANCE.reversed());
        SchematicUtil.align(clipboard, direction);

        Player ply = Bukkit.getPlayer(uuid);
        LocalPlayer lPlayer = null;
        if (ply != null) {
            lPlayer = WorldEditUtil.getLocalPlayer(ply);
        }

        final com.sk89q.worldedit.world.World world = WorldEditUtil.getWorld(structure.getWorldName());
        final Vector pos = structure.getDimension().getMinPosition();

        AsyncEditSession aSession;
        if (lPlayer == null) {
            aSession = (AsyncEditSession) AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(world, -1);
        } else {
            aSession = (AsyncEditSession) AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(world, -1, lPlayer);
        }

        PlayerEntry plyEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(uuid);
        SCAsyncClipboard asyncStructureClipboard = new SCAsyncClipboard(plyEntry, clipboard);
        try {
            // Note: The Clipboard is always drawn from the min position using the place method
            asyncStructureClipboard.place(aSession, pos, false, new ConstructionCallback() {

                @Override
                public void onJobAdded(SCJobEntry entry) {
                    // Set JobId
                    getEntry(structure).setJobId(entry.getJobId());
                    if (structure.getState() != State.QUEUED) {
                        structure.setState(State.QUEUED);
                    }

                    // Set state changeListener
                    entry.addStateChangedListener(new IJobEntryListener() {

                        @Override
                        public void jobStateChanged(JobEntry bpje) {
                            StructureService structureService = new StructureService();
                            if (bpje.getStatus() == JobEntry.JobStatus.PlacingBlocks) {
                                structure.setState(State.DEMOLISHING);
                            } else if (bpje.getStatus() == JobEntry.JobStatus.Done) {
                                // Set state to complete & Create timestamp of completion
                                structure.setState(State.REMOVED);

                                structure.getLog().setRemovedAt(new Date());
                                structureService.save(structure);

                                RegionManager rmgr = WorldGuardUtil.getGlobalRegionManager(Bukkit.getWorld(structure.getWorldName()));
                                rmgr.removeRegion(structure.getStructureRegion());
                                try {
                                    rmgr.save();
                                } catch (ProtectionDatabaseException ex) {
                                    Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                // Reset JobID
                                getEntry(structure).setJobId(-1);
                            }
                            structureService.save(structure);
                        }
                    });
                }

                @Override
                public void onJobCanceled(JobEntry entry) {
                    getEntry(structure).setJobId(-1);
                }
            });
        } catch (MaxChangedBlocksException ex) {
            Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Gets the instance of the ConstructionManager
     *
     * @return The ConstructionManager instance
     */
    public static ConstructionManager getInstance() {
        if (instance == null) {
            instance = new ConstructionManager();
        }
        return instance;
    }

    /**
     * Creates a new entry or resets one if already exists
     *
     * @param player The player
     * @param structure The structure
     */
    private synchronized void setEntry(UUID player, Structure structure) {
        ConstructionEntry entry = constructionEntries.get(structure.getId());
        if (entry == null) {
            entry = new ConstructionEntry(structure);
        }
        entry.setPlayer(player);
        entry.setFence(null);
        entry.setJobId(-1);
        entry.setCanceled(false);
        constructionEntries.put(structure.getId(), entry);
    }

    private void performChecks(Structure structure, State newState) throws ConstructionException, StructureDataException {
        // Removed structures can't be tasked
        if (structure.getState() == State.REMOVED) {
            throw new ConstructionException("#" + structure.getId() + " can't be tasked, because it was removed");
        }

        switch (newState) {
            case BUILDING:
                // Structure has already stopped constructing
                if (structure.getState() == State.BUILDING) {
                    throw new ConstructionException("#" + structure.getId() + " is already being build");
                }
                // Structure has already completed construction
                if (structure.getState() == State.COMPLETE) {
                    throw new ConstructionException("#" + structure.getId() + " is already complete");
                }
                break;
            case DEMOLISHING:
                if (structure.getState() == State.DEMOLISHING) {
                    throw new ConstructionException("#" + structure.getId() + " is already being demolished");
                }
                break;
            default:
                break;
        }

        // Check schematic
        File schematic;
        try {
            schematic = structure.getSchematicFile();
            if (!schematic.exists()) {
                throw new ConstructionException("Missing schematic file!");
            }
        } catch (DocumentException ex) {
            Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
            

    }

//    /**
//     * Stops construction of a structure.
//     *
//     * @param structure The structure
//     * @throws construction.exception.ConstructionException
//     */
//    private void stopTask(final Structure structure) throws ConstructionException {
//        // Removed structures can't be tasked
//        if (structure.getState() == State.REMOVED) {
//            throw new ConstructionException("#" + structure.getId() + " can't be tasked, because it was removed");
//        }
//
//        // Removed structures can't be tasked
//        if (structure.getState() == State.COMPLETE) {
//            throw new ConstructionException("Construction for #" + structure.getId() + " can't be stopped, because it is complete");
//        }
//
//        executor.submit(new Runnable() {
//
//            @Override
//            public void run() {
//                ConstructionEntry entry = constructionEntries.get(structure.getId());
//                if (entry == null) {
//                    return;
//                }
//
//                // Cancel task in AsyncWorldEdit
//                BlockPlacer pb = AsyncWorldEditMain.getInstance().getBlockPlacer();
//                PlayerEntry plyEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(entry.getPlayer());
//                if (entry.getPlayer() != null && pb.getJob(plyEntry, entry.getJobId()) != null) {
//                    pb.cancelJob(plyEntry, entry.getJobId());
//                }
//
//                // Set new state: STOPPED
//                StructureService structureService = new StructureService();
//                structure.setState(State.STOPPED);
//                structureService.save(structure);
//
//                // Reset data
//                constructionEntries.get(structure.getId()).setDemolishing(false);
//                constructionEntries.get(structure.getId()).setJobId(-1);
//                constructionEntries.get(structure.getId()).setPlayer(null);
//            }
//        });
//
//    }

    public void stop(final Structure structure) throws ConstructionException {
        final ConstructionEntry entry = constructionEntries.get(structure.getId());

        // Removed structures can't be tasked
        if (structure.getState() == State.REMOVED) {
            throw new ConstructionException("#" + structure.getId() + " can't be tasked, because it was removed");
        }

        // Structure was never tasked
        if (entry == null) {
            throw new ConstructionException("#" + structure.getId() + " hasn't been tasked yet");
        }

        executor.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                entry.setCanceled(true);

                // Cancel task in AsyncWorldEdit
                PlayerEntry plyEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(entry.getPlayer());
                AsyncWorldEditMain.getInstance().getBlockPlacer().cancelJob(plyEntry, entry.getJobId());

                // Set new state: STOPPED
                StructureService structureService = new StructureService();
                structure.setState(State.STOPPED);
                structureService.save(structure);

                // Reset data
                getEntry(structure).setDemolishing(false);
                getEntry(structure).setJobId(-1);
                getEntry(structure).setPlayer(null);
            }
        });

    }

    private ConstructionEntry getEntry(Structure structure) {
        if (constructionEntries.get(structure.getId()) == null) {
            constructionEntries.put(structure.getId(), new ConstructionEntry(structure));
        }

        return constructionEntries.get(structure.getId());
    }

    private void tell(UUID player, String message) {
        Player ply = Bukkit.getPlayer(player);
        if (ply != null && ply.isOnline()) {
            ply.sendMessage(message);
        }
    }

    private void refund(Structure structure) {
        double value = structure.getRefundValue();
        if (value > 0) {
            Set<PlayerOwnership> ownerships = structure.getOwnerships(PlayerOwnership.Type.FULL);
            int owners = ownerships.size();
            double pricePerOwner = Math.floor(value / owners);
            for (PlayerOwnership po : ownerships) {
                Player player = Bukkit.getPlayer(po.getPlayerUUID());
                EconomyUtil.getInstance().pay(player.getUniqueId(), pricePerOwner);
            }
            structure.setRefundValue(0d);
            new StructureService().save(structure);
        }
    }

    private class StructureListener implements Listener {

        @EventHandler
        public void onStructureStateChange(StructureStateChangeEvent changeEvent) {
            if (changeEvent.getStructure().getState() == State.QUEUED) {
                return;
            }

            StructureAPI.yellStatus(changeEvent.getStructure());
        }

    }
}
