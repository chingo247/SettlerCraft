/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.structureapi.structure.construction;

import com.sc.structureapi.exception.ConstructionException;
import com.sc.structureapi.exception.StructureDataException;
import com.sc.structureapi.persistence.StructureService;
import com.sc.structureapi.structure.entities.structure.Structure;
import com.sc.structureapi.structure.entities.structure.Structure.State;
import com.sc.structureapi.structure.StructureAPI;
import com.sc.structureapi.structure.StructureAPIModule;
import com.sc.structureapi.structure.asyncworldedit.SCAsyncCuboidClipboard;
import com.sc.structureapi.structure.asyncworldedit.SCJobEntry;
import com.sc.structureapi.structure.generator.ClipboardGenerator;
import com.sc.structureapi.structure.schematic.Schematic;
import com.sc.structureapi.structure.schematic.SchematicManager;
import com.sc.structureapi.structure.worldedit.ConstructionClipboard;
import com.sc.structureapi.structure.worldedit.DemolisionClipboard;
import com.sc.structureapi.structure.worldedit.StructureBlock;
import com.sc.structureapi.structure.worldedit.StructureBlockComparators;
import com.sc.structureapi.util.AsyncWorldEditUtil;
import com.sc.structureapi.util.SchematicUtil;
import com.sc.structureapi.util.WorldEditUtil;
import com.sc.structureapi.structure.entities.world.Direction;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.data.DataException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.dom4j.DocumentException;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
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
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final int FENCE_BLOCK_PLACE_SPEED = 1000;
    private final int TIME_OUT = 500;
    private final Material FENCE_MATERIAL = Material.IRON_FENCE;
    private static ConstructionManager instance;

    private ConstructionManager() {
        final BlockPlacer pb = AsyncWorldEditMain.getInstance().getBlockPlacer();
        pb.addListener(new IBlockPlacerListener() {

            @Override
            public void jobAdded(JobEntry je) {
                // DO NOTHING...
            }

            @Override
            public void jobRemoved(final JobEntry je) {
                Iterator<ConstructionEntry> it = constructionEntries.values().iterator();
                while (it.hasNext()) {
                    ConstructionEntry entry = it.next();

                    if (je.getPlayer().equals(entry.getPlayer()) && je.getJobId() == entry.getJobId()) {
                        StructureService structureService = new StructureService();
                        // Set state to complete & Create timestamp of completion
                        Structure structure = entry.getStructure();

                        structure.getLog().setCompletedAt(new Date());
                        structureService.save(structure);

                        constructionEntries.remove(structure.getId());

                        structure.setState(State.COMPLETE);
                        StructureAPI.yellStatus(structure);

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
        executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    if (!force) {
                        performChecks(structure, State.BUILDING);
                    }

                    // Cancel existing task
                    ConstructionEntry entry = constructionEntries.get(structure.getId());
                    if (entry != null && entry.getJobId() != -1 && entry.getPlayer() != null) {
                        AsyncWorldEditMain.getInstance().getBlockPlacer().cancelJob(entry.getPlayer(), entry.getJobId());
                    }
                    setEntry(uuid, structure);
                    entry = constructionEntries.get(structure.getId()); // NEVER NULL
                    entry.setDemolishing(false);

                    // Load schematic if absent
                    File sf = structure.getSchematicFile();
                    long checksum = FileUtils.checksumCRC32(sf);
                    StructureService structureService = new StructureService();
                    if (!SchematicManager.getInstance().hasSchematic(checksum)) {
                        structure.setState(State.LOADING_SCHEMATIC);
                        StructureAPI.tellStatus(structure, Bukkit.getPlayer(uuid));
                        SchematicManager.getInstance().load(sf);
                    }
                    
                    // Get clipboard
                    // Align it!
                    Schematic schematic = SchematicManager.getInstance().getSchematic(sf);
                    CuboidClipboard cc = schematic.getClipboard();
                    SchematicUtil.align(cc, structure.getDirection());

                    // Place a fence
                    placeFence(uuid, structure, cc);

                    

                    queueBuildTask(uuid, structure, cc, new Vector(0, 0, 0));

                } catch (StructureDataException ex) {
                    tell(uuid, ChatColor.RED + "Invalid structureplan");
                } catch (DataException ex) {
                    Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ConstructionException ex) {
                    tell(uuid, ChatColor.RED + ex.getMessage());
                } catch (IOException ex) {
                    Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (DocumentException ex) {
                    Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
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

        SchematicUtil.align(clipboard, structure.getDirection());
        final World w = Bukkit.getWorld(world.getName());
        final Queue<StructureBlock> place = new PriorityQueue<>();
        // Structures are always drawn from the min position
        Vector pos = structure.getDimension().getMinPosition();
        final Location l = new Location(w, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());

        int placed = 0;
        while (queue.peek() != null) {
            if (placed == FENCE_BLOCK_PLACE_SPEED) {
                System.out.println("Flush Queue");
                Bukkit.getScheduler().scheduleSyncDelayedTask(StructureAPIModule.getInstance().getMainPlugin(), new Runnable() {

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
                    System.out.println("Sleep!");
                    Thread.sleep(TIME_OUT);
                    placed = 0;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("Continue");
            }

            place.add(queue.poll());
            placed++;
        }

        // Flush Queue if not empty
        if (place.peek() != null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(StructureAPIModule.getInstance().getMainPlugin(), new Runnable() {

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

        SCAsyncCuboidClipboard asyncStructureClipboard = new SCAsyncCuboidClipboard(uuid, clipboard);
        try {
            // Note: The Clipboard is always drawn from the min position using the place method

            asyncStructureClipboard.place(aes, pos, false, new ConstructionCallback() {

                @Override
                public void onJobAdded(SCJobEntry entry) {
                    // Set JobId
                    final StructureService structureService = new StructureService();
                    getEntry(structure).setJobId(entry.getJobId());

                    // Update status
                    structure.setState(State.QUEUED);
                    StructureAPI.yellStatus(structure);

                    // Set state changeListener
                    entry.addStateChangedListener(new IJobEntryListener() {

                        @Override
                        public void jobStateChanged(JobEntry bpje) {
                            if (bpje.getStatus() == JobEntry.JobStatus.PlacingBlocks) {
                                structure.setState(State.BUILDING);
                                StructureAPI.yellStatus(structure);
                            }
                        }
                    });
                }

                @Override
                public void onJobCanceled(JobEntry entry) {
                    structure.setState(State.STOPPED);
                    // Update Hologram

                    ConstructionManager.getInstance().getEntry(structure).setJobId(-1);
                }
            });
        } catch (MaxChangedBlocksException ex) {
            // shouldnt happen
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
    public synchronized void demolish(final UUID uuid, final Structure structure, final boolean force) throws ConstructionException, StructureDataException {

        // Queue build task
        executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    if (!force) {
                        performChecks(structure, State.DEMOLISHING);
                    }

                    // Stop current task (if any...)
                    ConstructionManager.getInstance().stopTask(structure);

                    setEntry(uuid, structure);

                    // Load schematic if absent
                    File sf = structure.getSchematicFile();
                    long checksum = FileUtils.checksumCRC32(sf);
                    if (!SchematicManager.getInstance().hasSchematic(checksum)) {
                        SchematicManager.getInstance().load(sf);
                    }

                    Schematic schematic = SchematicManager.getInstance().getSchematic(sf);
//                    placeFence(uuid, site, schematic, BlockID.IRON_BARS);

                    // Update status
                    StructureService structureService = new StructureService();
                    structure.setState(State.QUEUED);
                    structureService.save(structure);
                    ConstructionEntry entry = constructionEntries.get(structure.getId());
                    entry.setDemolishing(true);

                    // Start demolision
                    queueDemolisionTask(uuid, structure, schematic);

                } catch (StructureDataException | IOException | DataException | DocumentException ex) {
                    Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ConstructionException ex) {
                    tell(uuid, ex.getMessage());
                }
            }
        });
    }

    private void queueDemolisionTask(final UUID uuid, final Structure structure, Schematic schematic) {
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

        SCAsyncCuboidClipboard asyncStructureClipboard = new SCAsyncCuboidClipboard(uuid, clipboard);
        try {
            // Note: The Clipboard is always drawn from the min position using the place method
            asyncStructureClipboard.place(aSession, pos, false, new ConstructionCallback() {

                @Override
                public void onJobAdded(SCJobEntry entry) {
                    // Set JobId
                    getEntry(structure).setJobId(entry.getJobId());

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

                                // Reset JobID
                                getEntry(structure).setJobId(-1);
                            }
                            structureService.save(structure);
                        }
                    });
                }

                @Override
                public void onJobCanceled(JobEntry entry) {
                    StructureService structureService = new StructureService();
                    structure.setState(State.STOPPED);
                     structureService.save(structure);
                    // Update Hologram

                    getEntry(structure).setJobId(-1);
                }
            });
        } catch (MaxChangedBlocksException ex) {
            // shouldnt happen
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

    private void performChecks(Structure structure, State desired) throws ConstructionException, StructureDataException {
        // Removed structures can't be tasked
        if (structure.getState() == State.REMOVED) {
            throw new ConstructionException("#" + structure.getId() + " can't be tasked, because it was removed");
        }

        switch (desired) {
            case BUILDING:
                // Structure has already stopped constructing
                if (structure.getState() == State.BUILDING) {
                    throw new ConstructionException("#" + structure.getId() + " is already being building");
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
        } catch (IOException ex) {
            Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }

    /**
     * Stops construction of a structure.
     *
     * @param structure The structure
     * @throws construction.exception.ConstructionException
     */
    private synchronized void stopTask(Structure structure) throws ConstructionException {
        ConstructionEntry entry = constructionEntries.get(structure.getId());

        if (entry == null) {
            return;
        }

        // Removed structures can't be tasked
        if (structure.getState() == State.REMOVED) {
            throw new ConstructionException("#" + structure.getId() + " can't be tasked, because it was removed");
        }

        // Removed structures can't be tasked
        if (structure.getState() == State.COMPLETE) {
            throw new ConstructionException("Construction for #" + structure.getId() + " can't be stopped, because it is complete");
        }

        // Cancel task in AsyncWorldEdit
        BlockPlacer pb = AsyncWorldEditMain.getInstance().getBlockPlacer();
        if (entry.getPlayer() != null && pb.getJob(entry.getPlayer(), entry.getJobId()) != null) {
            pb.cancelJob(entry.getPlayer(), entry.getJobId());
        }

        // Set new state: STOPPED
        StructureService structureService = new StructureService();
        structure.setState(State.STOPPED);
        structureService.save(structure);
        StructureAPI.yellStatus(structure);

        // Reset data
        constructionEntries.get(structure.getId()).setDemolishing(false);
        constructionEntries.get(structure.getId()).setJobId(-1);
        constructionEntries.get(structure.getId()).setPlayer(null);
    }

    public synchronized void stop(final Structure structure) throws ConstructionException {
        final ConstructionEntry entry = constructionEntries.get(structure.getId());

        // Removed structures can't be tasked
        if (structure.getState() == State.REMOVED) {
            throw new ConstructionException("#" + structure.getId() + " can't be tasked, because it was removed");
        }

        // Structure was never tasked
        if (entry == null) {
            throw new ConstructionException("#" + structure.getId() + " hasn't been tasked yet");
        }

        

        executor.execute(new Runnable() {

            @Override
            public void run() {
                if (structure.getState() == State.PLACING_FENCE) {
                    entry.setCanceled(true);
                    return;
                }

                // Cancel task in AsyncWorldEdit
                AsyncWorldEditMain.getInstance().getBlockPlacer().cancelJob(entry.getPlayer(), entry.getJobId());

                // Set new state: STOPPED
                StructureService structureService = new StructureService();
                structure.setState(State.STOPPED);
                structureService.save(structure);

                // Reset data
                constructionEntries.get(structure.getId()).setDemolishing(false);
                constructionEntries.get(structure.getId()).setJobId(-1);
                constructionEntries.get(structure.getId()).setPlayer(null);
            }
        });

    }

//    private synchronized Executor getExecutor(long id) {
//        Executor exe = executors.get(id);
//        if (exe == null) {
//            exe = Executors.newSingleThreadExecutor();
//            executors.put(id, exe);
//        }
//        return exe;
//    }


    private ConstructionEntry getEntry(Structure structure) {
        return constructionEntries.get(structure.getId());
    }

    private void tell(UUID player, String message) {
        Player ply = Bukkit.getPlayer(player);
        if (ply != null && ply.isOnline()) {
            ply.sendMessage(message);
        }
    }
}