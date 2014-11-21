/*
 * Copyright (C) 2014 Chingo
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
package com.chingo247.settlercraft.structure.construction.tasks;

import com.chingo247.settlercraft.bukkit.WorldEditUtil;
import com.chingo247.settlercraft.structure.AbstractStructureAPI;
import com.chingo247.settlercraft.structure.Structure;
import static com.chingo247.settlercraft.structure.Structure.State.BUILDING;
import static com.chingo247.settlercraft.structure.Structure.State.DEMOLISHING;
import com.chingo247.settlercraft.structure.construction.BuildOptions;
import com.chingo247.settlercraft.structure.construction.ConstructionManager;
import com.chingo247.settlercraft.structure.construction.DemolitionOptions;
import com.chingo247.settlercraft.structure.construction.asyncworldedit.SCIBlockPlacerListener;
import com.chingo247.settlercraft.structure.construction.asyncworldedit.SCIJobListener;
import com.chingo247.settlercraft.structure.construction.asyncworldedit.SCJobEntry;
import com.chingo247.settlercraft.structure.exception.ConstructionException;
import com.chingo247.settlercraft.structure.exception.StructureDataException;
import com.chingo247.settlercraft.structure.persistence.hibernate.StructureDAO;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
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
public class StructureTaskHandler extends ConstructionHandler {

    private final StructureDAO structureDAO = new StructureDAO();
    private final Map<Long, ConstructionEntry> entries = Collections.synchronizedMap(new HashMap<Long, ConstructionEntry>());

    public StructureTaskHandler(final AbstractStructureAPI api, ExecutorService service) {
        super(api, service);
        final BlockPlacer blockPlacer = AsyncWorldEditMain.getInstance().getBlockPlacer();
        blockPlacer.addListener(new SCIBlockPlacerListener() {

            @Override
            public void jobAdded(SCJobEntry entry) {
                final Structure structure = structureDAO.find(entry.getTaskID());
                final boolean building = !entry.isDemolishing();
                entries.put(entry.getTaskID(), new ConstructionEntry(entry.getJobId(), entry.getPlayer().getUUID()));

                // Update status
                api.setState(structure, Structure.State.QUEUED);
                entry.addStateChangeListener(new SCIJobListener() {

                    @Override
                    public void jobStateChanged(SCJobEntry entry) {
                        Structure structure = structureDAO.find(entry.getTaskID());
                        if (entry.getStatus() == JobEntry.JobStatus.PlacingBlocks) {
                            if (building) {
                                api.setState(structure, BUILDING);
                            } else {
                                api.setState(structure, DEMOLISHING);
                            }
                        }
                    }
                });
            }

            @Override
            public void jobRemoved(SCJobEntry entry) {
                // A job is removed when its either canceled or done
                // Done means it was done building or demolishing
                Structure structure = structureDAO.find(entry.getTaskID());
                if (!entry.isCanceled()) {
                    // Set state to Complete & set CompletedAt timestamp
                    if (!entry.isDemolishing()) {
                        api.setState(structure, Structure.State.COMPLETE);
                    } else  {
                        api.setState(structure, Structure.State.REMOVED);
                    }
                } 
                remove(structure);
            }
        });
    }

    private void remove(Structure structure) {
        synchronized(entries) {
            entries.remove(structure.getId());
        }
    }

    private void build(final Player player, final UUID uuid, final Structure structure, final BuildOptions options, final boolean force) {
        executor.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                if (!force) {
                    try {
                        performChecks(structure, Structure.State.BUILDING);
                    } catch (ConstructionException | StructureDataException ex) {
                        if (player != null) {
                            player.printError(ex.getMessage());
                        } else {
                            Logger.getLogger(StructureTaskHandler.class.getName()).log(Level.SEVERE, ex.getMessage());
                        }
                        return;
                    }
                }

                StructurePlan plan;
                try {
                    plan = structureAPI.getStructurePlanManager().getPlan(structure);
                    
                    System.out.println("Schematic:" + plan.getSchematic());
                    
                    
                    structureAPI.setState(structure, Structure.State.QUEUED);

                    World world = WorldEditUtil.getWorld(structure.getWorldName());

                    executor.execute(structure.getId(),
                            new BuildTask(
                                    StructureTaskHandler.this,
                                    structure.getId(),
                                    plan.getSchematic(),
                                    player,
                                    uuid,
                                    world,
                                    structure.getDimension().getMinPosition(),
                                    structure.getDirection(),
                                    options
                            ));
                } catch (StructureDataException | IOException ex) {
                    Logger.getLogger(StructureTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
                    if (player != null) {
                        player.printError("Something went wrong");
                    }
                }

            }

        });
    }

    public void build(final Player player, final Structure structure, final BuildOptions options, final boolean force) {
        build(player, player.getUniqueId(), structure, options, force);
    }

    public void build(final UUID uuid, final Structure structure, final BuildOptions options, final boolean force) {
        build(null, uuid, structure, options, force);
    }

    private void demolish(final Player player, final UUID uuid, final Structure structure, final DemolitionOptions options, final boolean force) {
        if (!force) {
            try {
                performChecks(structure, Structure.State.DEMOLISHING);
            } catch (ConstructionException | StructureDataException ex) {
                if (player != null) {
                    player.printError(ex.getMessage());
                } else {
                    Logger.getLogger(StructureTaskHandler.class.getName()).log(Level.SEVERE, ex.getMessage());
                }
                return;
            }
        }
        StructurePlan plan;
        try {
            plan = structureAPI.getStructurePlanManager().getPlan(structure);
            structureAPI.setState(structure, Structure.State.QUEUED);
            World world = WorldEditUtil.getWorld(structure.getWorldName());
            executor.execute(structure.getId(),
                    new DemolitionTask(
                            StructureTaskHandler.this,
                            structure.getId(),
                            plan.getSchematic(),
                            player,
                            uuid,
                            world,
                            structure.getDimension().getMinPosition(),
                            structure.getDirection(),
                            options
                    ));
        } catch (StructureDataException | IOException ex) {
            Logger.getLogger(StructureTaskHandler.class.getName()).log(Level.SEVERE, null, ex);
            if (player != null) {
                player.printError("Something went wrong");
            }
        }
    }

    public void demolish(final Player player, final Structure structure, final DemolitionOptions options, final boolean force) {
        demolish(player, player.getUniqueId(), structure, options, force);
    }

    public void demolish(final UUID uuid, final Structure structure, final DemolitionOptions options, final boolean force) {
        demolish(null, uuid, structure, options, force);
    }
    
    public boolean stop(Structure structure) {
        ConstructionEntry entry = entries.get(structure.getId());
        if(entry != null) {
            PlayerEntry plyEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(entry.taskUUID);
            AsyncWorldEditMain.getInstance().getBlockPlacer().cancelJob(plyEntry, entry.jobId);
            structureAPI.setState(structure, Structure.State.STOPPED);
            return true;
        }
        return false;
    }
    
    

    private void performChecks(Structure structure, Structure.State newState) throws ConstructionException, StructureDataException {

        switch (newState) {
            case BUILDING:
                // Structure has already stopped constructing
                if (structure.getState() == Structure.State.BUILDING) {
                    throw new ConstructionException("#" + structure.getId() + " is already being build");
                }
                // Structure has already completed construction
                if (structure.getState() == Structure.State.COMPLETE) {
                    throw new ConstructionException("#" + structure.getId() + " is already complete");
                }
                break;
            case DEMOLISHING:
                if (structure.getState() == Structure.State.DEMOLISHING) {
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

    @Override
    void fail(StructureAPITask task, String reason) {

    }

}
