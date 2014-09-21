/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.structure.construction;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.mysema.query.jpa.hibernate.HibernateUpdateClause;
import com.sc.module.structureapi.persistence.ConstructionSiteService;
import com.sc.module.structureapi.persistence.HibernateUtil;
import com.sc.module.structureapi.structure.QStructure;
import com.sc.module.structureapi.structure.Structure;
import com.sc.module.structureapi.structure.Structure.State;
import com.sc.module.structureapi.structure.StructureAPI;
import com.sc.module.structureapi.structure.StructureHologramManager;
import com.sc.module.structureapi.structure.construction.asyncworldedit.SCAsyncCuboidClipboard;
import com.sc.module.structureapi.structure.construction.generator.ClipboardGenerator;
import com.sc.module.structureapi.structure.construction.worldedit.ConstructionBuildingClipboard;
import com.sc.module.structureapi.structure.construction.worldedit.ConstructionDemolisionClipboard;
import com.sc.module.structureapi.structure.construction.worldedit.StructureBlock;
import com.sc.module.structureapi.structure.construction.worldedit.StructureBlockComparators;
import com.sc.module.structureapi.structure.schematic.Schematic;
import com.sc.module.structureapi.structure.schematic.SchematicManager;
import com.sc.module.structureapi.util.AsyncWorldEditUtil;
import com.sc.module.structureapi.util.SchematicUtil;
import com.sc.module.structureapi.util.WorldEditUtil;
import com.sc.module.structureapi.world.Cardinal;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.LocalPlayer;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.data.DataException;
import construction.exception.ConstructionException;
import construction.exception.StructurePlanException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.hibernate.Session;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.blockPlacer.IBlockPlacerListener;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.ThreadSafeEditSession;

/**
 *
 * @author Chingo
 */
public class ConstructionManager {

    private final Map<Long, ConstructionEntry> constructionEntries = Collections.synchronizedMap(new HashMap<Long, ConstructionEntry>());
    private final Map<Long, Executor> executors = new HashMap<>();
    private final Executor executor = Executors.newCachedThreadPool();
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("SettlerCraft");
    private final int FENCE_BLOCK_PLACE_SPEED = 500;
    private final int FENCE_MATERIAL = BlockID.IRON_BARS;
    private static ConstructionManager instance;
    private boolean initialized = false;

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
                        ConstructionSiteService siteService = new ConstructionSiteService();
                        // Set state to complete & Create timestamp of completion
                        Structure structure = entry.getStructure();
                        siteService.setState(structure, State.COMPLETE);
                        entry.getStructure().getLogEntry().setCompletedAt(new Date());
                        siteService.save(structure);

                        // Update Holo
                        StructureHologramManager.getInstance().updateHolo(structure);

                        // Reset JobID
                        getEntry(structure.getId()).setJobId(-1);

                        StructureAPI.yellStatus(structure);
                        
                        constructionEntries.remove(structure.getId());
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
     * @throws StructurePlanException
     * @throws ConstructionException
     * @throws IOException
     */
    public synchronized void build(final UUID uuid, final Structure structure, boolean force) throws StructurePlanException, ConstructionException, IOException {
        if (!force) {
            performChecks(structure, State.BUILDING);
        }
       

        // Queue build task
        getExecutor(structure.getId()).execute(new Runnable() {

            @Override
            public void run() {
                try {
                    // Stop current task (if any...)
                    ConstructionManager.getInstance().stopTask(structure);

                    setEntry(uuid, structure);
                    
                    // Load schematic if absent
                    File sf = structure.getSchematicFile();
                    long checksum = FileUtils.checksumCRC32(sf);
                    if (!SchematicManager.getInstance().hasSchematic(checksum)) {
                        SchematicManager.getInstance().load(sf);
                    }

                    // Place fence
                    Schematic schematic = SchematicManager.getInstance().getSchematic(sf);
                    CuboidClipboard cc = schematic.getClipboard();
                    SchematicUtil.align(cc, structure.getCardinal());

                    placeFence(uuid, structure, cc, FENCE_MATERIAL);

                    // Update status
                    ConstructionSiteService css = new ConstructionSiteService();
                    css.setState(structure, State.QUEUED);
                    ConstructionEntry entry = constructionEntries.get(structure.getId());
                    entry.setDemolishing(false);

                    // Start building
                    queueBuildTask(uuid, structure, cc);

                } catch (StructurePlanException | IOException ex) {
                    Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (DataException ex) {
                    Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ConstructionException ex) {
                    tell(uuid, ex.getMessage());
                }
            }
        });
    }

    private void queueBuildTask(final UUID uuid, final Structure structure, CuboidClipboard schematic) {
        final ConstructionBuildingClipboard clipboard = new ConstructionBuildingClipboard(schematic, StructureBlockComparators.PERFORMANCE);
        Player ply = Bukkit.getPlayer(uuid);
        LocalPlayer lPlayer = null;
        if (ply != null) {
            lPlayer = WorldEditUtil.getLocalPlayer(ply);
        }

        // Create & Place enclosure
        final com.sk89q.worldedit.world.World world = WorldEditUtil.getWorld(structure.getWorldName());
        final Vector pos = structure.getDimension().getMinPosition();

        AsyncEditSession aes;
        if (lPlayer == null) {
            aes = (AsyncEditSession) AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(world, -1);
        } else {
            aes = (AsyncEditSession) AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(world, -1, lPlayer);
        }

        SCAsyncCuboidClipboard asyncStructureClipboard = new SCAsyncCuboidClipboard(uuid, clipboard);
        try {
            // Note: The Clipboard is always drawn from the min position using the place method
            asyncStructureClipboard.place(aes, pos, false, new ConstructionBuildingCallback(uuid, structure));
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
     * @throws StructurePlanException
     */
    public synchronized void demolish(final UUID uuid, final Structure structure, boolean force) throws ConstructionException, StructurePlanException {
        if (!force) {
            performChecks(structure, State.DEMOLISHING);
        }
       

        // Queue build task
        getExecutor(structure.getId()).execute(new Runnable() {

            @Override
            public void run() {
                try {
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
                    ConstructionSiteService css = new ConstructionSiteService();
                    css.setState(structure, State.QUEUED);
                    ConstructionEntry entry = constructionEntries.get(structure.getId());
                    entry.setDemolishing(true);

                    // Start demolision
                    queueDemolisionTask(uuid, structure, schematic);

                } catch (StructurePlanException | IOException ex) {
                    Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (DataException ex) {
                    Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ConstructionException ex) {
                    tell(uuid, ex.getMessage());
                }
            }
        });
    }

    private void queueDemolisionTask(final UUID uuid, final Structure structure, Schematic schematic) {
        // Align schematic
        Cardinal cardinal = structure.getCardinal();
        final ConstructionDemolisionClipboard clipboard = new ConstructionDemolisionClipboard(schematic.getClipboard(), StructureBlockComparators.PERFORMANCE.reversed());
        SchematicUtil.align(clipboard, cardinal);

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
            asyncStructureClipboard.place(aSession, pos, false, new ConstructionDemolisionCallback(uuid, structure));
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

    public synchronized void init() {
        if (!initialized) {
            initHolos();
            setStates();
            initialized = true;
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    private void setStates() {
        Session session = HibernateUtil.getSession();
        QStructure qs = QStructure.structure;
        new HibernateUpdateClause(session, qs).where(qs.state.ne(State.COMPLETE).and(qs.state.ne(State.REMOVED)))
                .set(qs.state, State.STOPPED)
                .execute();
        session.close();
    }

    private void initHolos() {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        final List<Structure> structures = query.from(qs).where(qs.state.ne(State.REMOVED)).list(qs);
        session.close();
        executor.execute(new Runnable() {

            @Override
            public void run() {
                Queue<Structure> ss = new LinkedList<>(structures);
                int batchsize = 100;
                int count = 0;
                while (ss.peek() != null) {
                    if (count == batchsize) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        count = 0;
                    }
                    StructureHologramManager.getInstance().createHologram(plugin, ss.poll());
                    count++;
                }
            }
        });
    }

    /**
     * Places a fence, SHOULDNT BE CALLED IN MAIN THREAD
     *
     * @param uuid The uuid for the job
     * @param structure The structure
     * @param schematic The schematic
     * @param material The material used for the fence
     */
    private void placeFence(UUID uuid, Structure structure, CuboidClipboard schematic, int material) {
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
        CuboidClipboard clipboard = ClipboardGenerator.createEnclosure(schematic, material);

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

        SchematicUtil.align(clipboard, structure.getCardinal());

        // Structures are always drawn from the min position
        Vector location = structure.getDimension().getMinPosition().add(0, 1, 0); // above ground
        int placed = 0;
        while (queue.peek() != null) {
            if (placed == FENCE_BLOCK_PLACE_SPEED) {
                try {
                    Thread.sleep(1000);
                    placed = 0;
                } catch (InterruptedException ex) {
                    Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            StructureBlock b = queue.poll();
            editSession.rawSetBlock(location.add(b.getPosition()), b.getBlock());
            editSession.flushQueue();
            placed++;
        }

    }

    private synchronized void setEntry(UUID player, Structure structure) {
        ConstructionEntry entry = constructionEntries.get(structure.getId());
        if (entry == null) {
            entry = new ConstructionEntry(structure);
        }
        entry.setPlayer(player);
        constructionEntries.put(structure.getId(), entry);
    }

    private void performChecks(Structure structure, State desired) throws ConstructionException, StructurePlanException {
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
                // Structure has already stopped constructing
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
        File schematic = structure.getSchematicFile();
        if (!schematic.exists()) {
            throw new ConstructionException("Missing schematic file!");
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

        // Removed structures can't be tasked
        if (structure.getState() == State.REMOVED) {
            throw new ConstructionException("#" + structure.getId() + " can't be tasked, because it was removed");
        }

        // Removed structures can't be tasked
        if (structure.getState() == State.COMPLETE) {
            throw new ConstructionException("Construction for #" + structure.getId() + " can't be stopped, because it is complete");
        }


        if (entry == null || entry.getPlayer() == null) {
            return;
        }

        // Cancel task in AsyncWorldEdit
        BlockPlacer pb = AsyncWorldEditMain.getInstance().getBlockPlacer();

        if (pb.getJob(entry.getPlayer(), entry.getJobId()) != null) {
            pb.cancelJob(entry.getPlayer(), entry.getJobId());
        }

        // Set new state: STOPPED
        ConstructionSiteService css = new ConstructionSiteService();
        css.setState(structure, State.STOPPED);

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

        Executor exe = executors.get(structure.getId());

        exe.execute(new Runnable() {

            @Override
            public void run() {
                // Cancel task in AsyncWorldEdit
                AsyncWorldEditMain.getInstance().getBlockPlacer().cancelJob(entry.getPlayer(), entry.getJobId());

                // Set new state: STOPPED
                ConstructionSiteService css = new ConstructionSiteService();
                css.setState(structure, State.STOPPED);

                // Reset data
                constructionEntries.get(structure.getId()).setDemolishing(false);
                constructionEntries.get(structure.getId()).setJobId(-1);
                constructionEntries.get(structure.getId()).setPlayer(null);
            }
        });

    }

    private synchronized Executor getExecutor(long id) {
        Executor exe = executors.get(id);
        if (exe == null) {
            exe = Executors.newSingleThreadExecutor();
            executors.put(id, exe);
        }
        return exe;
    }

    public ConstructionEntry getEntry(Long structureId) {
        return constructionEntries.get(structureId);
    }

    public ConstructionEntry getEntry(Structure structure) {
        return getEntry(structure.getId());
    }

    private void tell(UUID player, String message) {
        Player ply = Bukkit.getPlayer(player);
        if (ply != null && ply.isOnline()) {
            ply.sendMessage(message);
        }
    }
}
