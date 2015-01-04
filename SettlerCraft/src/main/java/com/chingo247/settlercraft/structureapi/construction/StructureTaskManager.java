
/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft.structureapi.construction;

import com.chingo247.settlercraft.structureapi.construction.options.BuildOptions;
import com.chingo247.settlercraft.structureapi.construction.options.DemolitionOptions;
import com.chingo247.settlercraft.bukkit.WorldEditUtil;
import com.chingo247.settlercraft.structureapi.structure.old.AbstractStructureAPI;
import com.chingo247.settlercraft.structureapi.structure.old.Structure;
import static com.chingo247.settlercraft.structureapi.structure.old.Structure.State.BUILDING;
import static com.chingo247.settlercraft.structureapi.structure.old.Structure.State.DEMOLISHING;
import com.chingo247.settlercraft.structureapi.construction.asyncworldedit.AsyncWorldEditUtil;
import com.chingo247.settlercraft.structureapi.construction.asyncworldedit.SCAsyncClipboard;
import com.chingo247.settlercraft.structureapi.construction.asyncworldedit.SCIBlockPlacerListener;
import com.chingo247.settlercraft.structureapi.construction.asyncworldedit.SCIJobListener;
import com.chingo247.settlercraft.structureapi.construction.asyncworldedit.SCJobEntry;
import com.chingo247.settlercraft.structureapi.exception.ConstructionException;
import com.chingo247.settlercraft.structureapi.exception.StructureDataException;
import com.chingo247.settlercraft.structureapi.persistence.hibernate.StructureDAO;
import com.chingo247.settlercraft.structureapi.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.structure.regions.CuboidDimension;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.botsko.prism.Prism;
import me.botsko.prism.actionlibs.ActionsQuery;
import me.botsko.prism.actionlibs.QueryParameters;
import me.botsko.prism.actionlibs.QueryResult;
import me.botsko.prism.actions.Handler;
import me.botsko.prism.appliers.PrismProcessType;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class StructureTaskManager extends ConstructionTaskManager {

    private final StructureDAO structureDAO = new StructureDAO();
    private final Map<Long, ConstructionEntry> entries = Collections.synchronizedMap(new HashMap<Long, ConstructionEntry>());
    private final Prism prism;

    public StructureTaskManager(final AbstractStructureAPI api, ExecutorService service) {
        super(api, service);
        this.prism = (Prism) Bukkit.getPluginManager().getPlugin("Prism");
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
                            Logger.getLogger(StructureTaskManager.class.getName()).log(Level.SEVERE, ex.getMessage());
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
                                    StructureTaskManager.this,
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
                    Logger.getLogger(StructureTaskManager.class.getName()).log(Level.SEVERE, null, ex);
                    if (player != null) {
                        player.printError("Something went wrong");
                    }
                }

            }

        });
    }
    
    public void rollback(final Player player, Structure structure, Date targetDate) {
        CuboidDimension dim = structure.getDimension();
        String world = structure.getWorldName();
        
        QueryParameters params = new QueryParameters();
        
        params.setSinceTime(targetDate.getTime());
        params.setBeforeTime(new Date().getTime());
        params.setWorld(world);
        params.setMinLocation(new Vector(dim.getMinX(), dim.getMinY(), dim.getMinZ()));
        params.setMaxLocation(new Vector(dim.getMaxX(), dim.getMaxY(), dim.getMaxZ()));
        params.setProcessType(PrismProcessType.LOOKUP);
        ActionsQuery aq = new ActionsQuery(prism);
        
        QueryResult lookupResult = aq.lookup(params);
        HashMap<Integer, Handler> map = new HashMap<>();
        for(Handler h : lookupResult.getActionResults()) {
            int key = Objects.hashCode(String.valueOf(h.getX() + "" + h.getY() + "" + h.getZ()));
            Handler current = map.get(key);
            
            // 
            if(current == null ||(current.getId() > h.getId())) {
                map.put(key, h);
            }
        }
        
        CuboidClipboard cc = new CuboidClipboard(new com.sk89q.worldedit.Vector(dim.getMaxX() - dim.getMinX(), dim.getMaxY() - dim.getMinY(), dim.getMaxZ() - dim.getMinZ()));
        for(Handler h : map.values()) {
            com.sk89q.worldedit.Vector v = structure.getRelativePosition(new com.sk89q.worldedit.Vector(h.getX(), h.getY(), h.getZ()));
            cc.setBlock(v, new BaseBlock(h.getOldBlockId(), h.getOldBlockSubId()));
        }
        
        AsyncEditSession editSession = (AsyncEditSession)AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(WorldEditUtil.getWorld(world), -1, player);
        
        PlayerEntry plyEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(player.getUniqueId());
        SCAsyncClipboard asyncStructureClipboard = new SCAsyncClipboard(plyEntry, cc, structure.getId());
        try {
            asyncStructureClipboard.place(editSession, structure.getDimension().getMinPosition(), false, false);
        } catch (MaxChangedBlocksException ex) {
            throw new AssertionError(ex); // Should never happen
        }
        
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
                    Logger.getLogger(StructureTaskManager.class.getName()).log(Level.SEVERE, ex.getMessage());
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
                            StructureTaskManager.this,
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
            Logger.getLogger(StructureTaskManager.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(StructureTaskManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    void fail(SettlerCraftTask task, String reason) {

    }

}
