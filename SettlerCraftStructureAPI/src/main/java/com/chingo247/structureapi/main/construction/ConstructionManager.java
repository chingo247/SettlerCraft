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
package com.chingo247.structureapi.main.construction;

import com.chingo247.structureapi.main.Structure;
import com.chingo247.structureapi.main.Structure.State;
import static com.chingo247.structureapi.main.Structure.State.BUILDING;
import static com.chingo247.structureapi.main.Structure.State.DEMOLISHING;
import com.chingo247.structureapi.main.StructureAPI;
import com.chingo247.structureapi.main.exception.ConstructionException;
import com.chingo247.structureapi.main.exception.StructureDataException;
import com.chingo247.structureapi.main.persistence.service.StructureService;
import com.chingo247.structureapi.main.plan.StructurePlan;
import com.chingo247.structureapi.main.plan.schematic.Schematic;
import com.chingo247.structureapi.main.util.KeyPool;
import com.chingo247.structureapi.main.util.SchematicUtil;
import com.chingo247.structureapi.main.worldedit.ConstructionClipboard;
import com.chingo247.structureapi.main.worldedit.DemolisionClipboard;
import com.chingo247.structureapi.bukkit.WorldEditUtil;
import com.chingo247.xcore.util.ChatColors;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.entity.Player;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;

/**
 *
 * @author Chingo
 */
public class ConstructionManager {

    private final Map<Long, ConstructionEntry> constructionEntries = Collections.synchronizedMap(new HashMap<Long, ConstructionEntry>());
    private final KeyPool<Long> executor;
    private final StructureAPI structureAPI;

    public ConstructionManager(StructureAPI structureAPI, ExecutorService executor) {
        this.structureAPI = structureAPI;
        this.executor = new KeyPool(executor);
        final BlockPlacer blockPlacer = AsyncWorldEditMain.getInstance().getBlockPlacer();
        blockPlacer.addListener(new SCIBlockPlacerListener() {

            @Override
            public void jobAdded(SCJobEntry jobEntry) {
                final Structure structure = jobEntry.getStructure();
                ConstructionEntry entry = getEntry(structure);
                entry.setJobId(jobEntry.getJobId());
                final boolean building = !entry.isDemolishing();

                // Update status
                ConstructionManager.this.structureAPI.setState(structure, State.QUEUED);
                jobEntry.addStateChangeListener(new SCIJobListener() {

                    @Override
                    public void jobStateChanged(SCJobEntry je) {
                        Structure structure = je.getStructure();
                        if (je.getStatus() == JobEntry.JobStatus.PlacingBlocks) {
                            if (building) {
                                ConstructionManager.this.structureAPI.setState(structure, BUILDING);
                            } else {
                                ConstructionManager.this.structureAPI.setState(structure, DEMOLISHING);
                            }
                        }
                    }
                });
            }

            @Override
            public void jobRemoved(SCJobEntry jobEntry) {
                ConstructionEntry entry = getEntry(jobEntry.getStructure());

                // A job is removed when its either canceled or done
                // Done means it was done building or demolishing
                if (!entry.isCanceled()) {
                    Structure structure = entry.getStructure();
                    StructureAPI sapi = ConstructionManager.this.structureAPI;

                    // Set state to Complete & set CompletedAt timestamp
                    if (structure.getState() != State.COMPLETE && !entry.isDemolishing()) {
                        sapi.setState(structure, State.COMPLETE);

                        // Set state to Demolishing & set RemovedAt timestamp
                    } else if (structure.getState() != State.REMOVED && entry.isDemolishing()) {
                        sapi.setState(structure, State.REMOVED);
                    }
                    remove(structure); // remove this entry
                } else {
                    entry.setJobId(-1);
                }
            }
        });
    }

    /**
     * Builds a structure
     *
     * @param player
     * @param uuid The player or UUID to use to issue this task
     * @param structure The structure
     * @param options The buildOptions
     * @param force if True the method will skip checks, including the checking if the structure was
     * removed or the structure is already being build
     */
    public void build(final Player player, final UUID uuid, final Structure structure, final BuildOptions options, final boolean force) {

        // Queue build task
        executor.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                try {
                    // Removed structures can't be tasked
                    if (structure.getState() == State.REMOVED) {
                        throw new ConstructionException("#" + structure.getId() + " can't be tasked, because it was removed");
                    }

                    if (!force) {
                        performChecks(structure, State.BUILDING);
                    }

                    queueTask(player, uuid, structure, options);

                } catch (StructureDataException ex) {
                    tell(player, ChatColors.RED + "Invalid structureplan");
                } catch (DataException | IOException ex) {
                    Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ConstructionException ex) {
                    tell(player, ChatColors.RED + ex.getMessage());
                }
            }
        });
    }

    /**
     * Demolished a structure
     *
     * @param player
     * @param uuid The player or uuid to issue this task
     * @param structure The structure
     * @param options The demolition Options
     * @param force Whether to perform checks, if true it will ignore the check that determines if
     * the structure is removed or that the structure is already being demolished.
     */
    public void demolish(final Player player, final UUID uuid, final Structure structure, final DemolitionOptions options, final boolean force) {

        // Queue build task
        executor.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                try {
                    if (!force) {
                        performChecks(structure, State.DEMOLISHING);
                    }

                    // Start demolision
                    queueTask(player, uuid, structure, options);
              
                } catch (ConstructionException ex) {
                    tell(player, ChatColors.RED + ex.getMessage());
                } catch (StructureDataException | IOException | DataException ex) {
                    Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    private void queueTask(Player player, UUID uuid, Structure structure, final ConstructionOptions options) throws StructureDataException, IOException, DataException {
        // Cancel existing task // Never returns null
        ConstructionEntry entry = getEntry(structure);
        entry.setCanceled(true);

        // Cancel task in AsyncWorldEdit
        if (entry.getJobId() != -1) {
            PlayerEntry plyEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(entry.getPlayer());
            AsyncWorldEditMain.getInstance().getBlockPlacer().cancelJob(plyEntry, entry.getJobId());
        }
        
        // Reset
        setEntry(uuid, structure);
        entry = getEntry(structure);
        
        // Load schematic if absent
        StructurePlan plan = structureAPI.getStructurePlanManager().getPlan(structure);
        if (!structureAPI.getSchematicManager().hasSchematic(plan.getChecksum())) {
            structureAPI.setState(structure, State.LOADING_SCHEMATIC);
        }

        // Get clipboard & Align!
        Schematic schematic = structureAPI.getSchematicManager().load(plan.getSchematic());
        CuboidClipboard cc = schematic.getClipboard();
        SchematicUtil.align(cc, structure.getDirection());
        
        if(entry.isDemolishing()) {
            entry.setClipboard(new ConstructionClipboard(cc, options.getBuildPattern().getComparator()));
        } else {
            entry.setClipboard(new DemolisionClipboard(cc, options.getBuildPattern().getComparator()));
        }
        

        // Create & Place enclosure
        final com.sk89q.worldedit.world.World world = WorldEditUtil.getWorld(structure.getWorldName());
        final Vector pos = entry.getStructure().getDimension().getMinPosition();

        EditSession editSession;

        if (player == null) {
            editSession = AsyncWorldEditUtil.getAsyncSessionFactory().getThreadSafeEditSession(world, -1);
        } else {
            editSession = AsyncWorldEditUtil.getAsyncSessionFactory().getThreadSafeEditSession(world, -1, player);
        }

        PlayerEntry plyEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(uuid);
        SCAsyncClipboard asyncStructureClipboard = new SCAsyncClipboard(plyEntry, entry);

        // Note: The Clipboard is always drawn from the min position using the place method
        try {
            asyncStructureClipboard.place(editSession, pos, options);
        } catch (MaxChangedBlocksException ex) {
            Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

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

    private void remove(Structure structure) {
        constructionEntries.remove(structure.getId());
    }

    private void performChecks(Structure structure, State newState) throws ConstructionException, StructureDataException {

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
        StructurePlan plan;
        try {
            plan = structureAPI.getStructurePlanManager().getPlan(structure);
            if (!plan.getSchematic().exists()) {
                throw new ConstructionException("Missing schematic file!");
            }
        } catch (IOException ex) {
            Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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

    private void tell(Player player, String message) {
        if (player != null) {
            player.printError(message);
        }
    }
}
