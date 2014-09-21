/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.structure.construction;

import com.sc.module.structureapi.persistence.ConstructionSiteService;
import com.sc.module.structureapi.structure.Structure;
import com.sc.module.structureapi.structure.Structure.State;
import static com.sc.module.structureapi.structure.Structure.State.BUILDING;
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
import com.sk89q.worldedit.world.World;
import construction.exception.ConstructionException;
import construction.exception.StructurePlanException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;
import org.primesoft.asyncworldedit.worldedit.ThreadSafeEditSession;

/**
 *
 * @author Chingo
 */
public class ConstructionManager {

    private final Map<Long, ConstructionEntry> constructionEntries = Collections.synchronizedMap(new HashMap<Long, ConstructionEntry>());
    private final Map<Long, Executor> executors = new HashMap<>();
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("SettlerCraft");
    private final int FENCE_BLOCK_PLACE_SPEED = 500;
    private final int FENCE_MATERIAL = BlockID.IRON_BARS;
    private static ConstructionManager instance;

    public synchronized void build(final UUID uuid, final Structure structure) throws StructurePlanException, ConstructionException, IOException {
        performChecks(structure, State.BUILDING);
        setEntry(uuid, structure);

        // Queue build task
        getExecutor(structure.getId()).execute(new Runnable() {

            @Override
            public void run() {
                try {
                    // Stop current task (if any...)
                    ConstructionManager.getInstance().stopTask(structure);

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
                    
                    placeFence(uuid, structure, cc, BlockID.IRON_BARS);

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
        final World world = WorldEditUtil.getWorld(structure.getWorldName());
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

    public synchronized void demolish(final UUID uuid, final Structure structure) throws ConstructionException, StructurePlanException {
        performChecks(structure, State.DEMOLISHING);
        setEntry(uuid, structure);

        // Queue build task
        getExecutor(structure.getId()).execute(new Runnable() {

            @Override
            public void run() {
                try {
                    // Stop current task (if any...)
                    ConstructionManager.getInstance().stopTask(structure);

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

                    // Start building
                    queueBuildTask(uuid, structure, schematic.getClipboard());

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

    private void startDemolision(final UUID uuid, final Structure structure, Schematic schematic) {
        // Align schematic
        Cardinal cardinal = structure.getCardinal();
        final ConstructionDemolisionClipboard clipboard = new ConstructionDemolisionClipboard(schematic.getClipboard(), StructureBlockComparators.PERFORMANCE.reversed());
        SchematicUtil.align(clipboard, cardinal);

        Player ply = Bukkit.getPlayer(uuid);
        LocalPlayer lPlayer = null;
        if (ply != null) {
            lPlayer = WorldEditUtil.getLocalPlayer(ply);
        }

        final World world = WorldEditUtil.getWorld(structure.getWorldName());
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

    public static ConstructionManager getInstance() {
        if (instance == null) {
            instance = new ConstructionManager();
        }
        return instance;
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
        World world = WorldEditUtil.getWorld(structure.getWorldName());

        ThreadSafeEditSession editSession;
        if (localPlayer != null) {
            editSession = AsyncWorldEditUtil.getAsyncSessionFactory().getThreadSafeEditSession(world, FENCE_BLOCK_PLACE_SPEED, localPlayer);
        } else {
            editSession = AsyncWorldEditUtil.getAsyncSessionFactory().getThreadSafeEditSession(world, FENCE_BLOCK_PLACE_SPEED);
        }
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

        Vector location = structure.getPosition();
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
            placed++;
        }

    }

    private synchronized void setEntry(UUID player, Structure site) {
        ConstructionEntry entry = constructionEntries.get(site.getId());
        if (entry == null) {
            entry = new ConstructionEntry(site);
        }
        entry.setPlayer(player);
        constructionEntries.put(site.getId(), entry);
    }

    private void performChecks(Structure structure, State desired) throws ConstructionException, StructurePlanException {
        // Removed structures can't be tasked
        if (structure.getState() == State.REMOVED) {
            throw new ConstructionException("#" + structure.getId() + " can't be tasked, because it was removed");
        }

        // Structure has already stopped constructing
        if (structure.getState() == State.COMPLETE) {
            throw new ConstructionException("#" + structure.getId() + " is already complete");
        }

        switch (desired) {
            case BUILDING:
                // Structure has already stopped constructing
                if (structure.getState() == State.BUILDING) {
                    throw new ConstructionException("#" + structure.getId() + " is already being building");
                }
                break;
            case DEMOLISHING:
                if(structure.getState() == State.DEMOLISHING) {
                     throw new ConstructionException("#" + structure.getId() + " is already being demolished");
                }
            break;
            default:break;
        }

        // Check schematic
        File schematic = structure.getSchematicFile();
        if (!schematic.exists()) {
            throw new ConstructionException("Missing schematic file!");
        }

    }

    /**
     * Stop construction of a structure.
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

        // Structure was never tasked
        if (entry == null) {
            throw new ConstructionException("#" + structure.getId() + " hasn't been tasked yet");
        }

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
