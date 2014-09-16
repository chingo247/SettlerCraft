/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.construction;

import com.sc.module.structureapi.construction.asyncworldedit.SCAsyncCuboidClipboard;
import com.sc.module.structureapi.construction.generator.Enclosure;
import com.sc.module.structureapi.construction.sync.SyncBuilder;
import com.sc.module.structureapi.construction.sync.TaskCompleteCallback;
import com.sc.module.structureapi.construction.worldedit.ConstructionBuildingClipboard;
import com.sc.module.structureapi.construction.worldedit.ConstructionDemolisionClipboard;
import com.sc.module.structureapi.construction.worldedit.StructureBlockComparators;
import com.sc.module.structureapi.persistence.ConstructionSiteService;
import com.sc.module.structureapi.structure.ConstructionSite;
import com.sc.module.structureapi.structure.ConstructionSite.State;
import com.sc.module.structureapi.structure.Structure;
import com.sc.module.structureapi.util.AsyncWorldEditUtil;
import com.sc.module.structureapi.util.SchematicUtil;
import com.sc.module.structureapi.util.WorldEditUtil;
import com.sc.module.structureapi.world.Cardinal;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.world.World;
import construction.exception.ConstructionException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 * @author Chingo
 */
public class ConstructionManager {

    private final Map<Long, ConstructionEntry> constructionEntries = Collections.synchronizedMap(new HashMap<Long, ConstructionEntry>());
    
    private static ConstructionManager instance;

    /**
     * Private Constructor.
     */
    private ConstructionManager() {
    }

    public static ConstructionManager getInstance() {
        if (instance == null) {
            instance = new ConstructionManager();
        }
        return instance;
    }

    /**
     * Builds a structure.
     *
     * @param player The player's UUID
     * @param constructionSite The ConstructionSite
     * @param plugin The plugin used for the bukkit-runnable that will be created
     * @throws construction.exception.ConstructionException
     */
    public synchronized void build(final Plugin plugin, final UUID player, final ConstructionSite constructionSite) throws ConstructionException {
        
        // Removed structures can't be tasked
        if(constructionSite.getState() == ConstructionSite.State.REMOVED) {
            throw new ConstructionException("#" + constructionSite.getId() + " can't be tasked, because it was removed");
        }
        
        // Structure has already stopped constructing
        if(constructionSite.getState() == ConstructionSite.State.BUILDING) {
            throw new ConstructionException("#" + constructionSite.getId() + " is already being building");
        }
        
        // Structure has already stopped constructing
        if(constructionSite.getState() == ConstructionSite.State.COMPLETE) {
            throw new ConstructionException("#" + constructionSite.getId() + " is already complete");
        }
        
        // Quit whatever your are doing now
        if(constructionSite.getState() == State.QUEUED) {
            stop(constructionSite);
        }

        ConstructionEntry entry = constructionEntries.get(constructionSite.getId());

        if (entry != null) {
            startBuilding(plugin, player, entry);
            entry.setPlayer(player);
            ConstructionSiteService css = new ConstructionSiteService();
            css.setState(constructionSite, State.QUEUED);
            constructionEntries.put(constructionSite.getId(), entry);
        } else {
//            
//                    Schematic schematic = SchematicManager.getInstance().getSchematic(checksum);
//                    try {
//                        ConstructionSiteService css = new ConstructionSiteService();
//                        css.setState(constructionSite, State.QUEUED);
//                        CuboidClipboard cc = SchematicFormat.MCEDIT.load(schematic.getFile());
//                        ConstructionEntry entry = new ConstructionEntry(constructionSite, cc);
//                        entry.setPlayer(player);
//                        entry.setDemolishing(false);
//                        constructionEntries.put(constructionSite.getId(), entry);
//                        startBuilding(plugin, player, entry);
//                    } catch (IOException | DataException ex) {
//                        Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
//                    }

        }

        ConstructionSiteService css = new ConstructionSiteService();
        constructionSite.setState(ConstructionSite.State.QUEUED);
        css.save(constructionSite);
    }

    private void startBuilding(final Plugin plugin, final UUID player, final ConstructionEntry entry) {
        // Align schematic
        Cardinal cardinal = entry.getConstructionSite().getStructure().getCardinal();
        final ConstructionBuildingClipboard clipboard = new ConstructionBuildingClipboard(entry.getCuboidClipboard(), StructureBlockComparators.PERFORMANCE);
        SchematicUtil.align(clipboard, cardinal);
        final Enclosure enclosure = new Enclosure(clipboard, 1, BlockID.IRON_BARS);

        // Create & Place enclosure
        final Structure structure = entry.getConstructionSite().getStructure();
        final World world = WorldEditUtil.getWorld(structure.getWorldName());
        final Vector pos = structure.getDimension().getMinPosition();
        final EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);

        BukkitTask st = SyncBuilder.placeLayered(plugin, session, enclosure, pos, new TaskCompleteCallback() {

            @Override
            public void onComplete() {
                Bukkit.getScheduler().runTaskLater(plugin, new BukkitRunnable() {

                    @Override
                    public void run() {
                        AsyncEditSession aSession = (AsyncEditSession) AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(world, -1);
                        SCAsyncCuboidClipboard asyncStructureClipboard = new SCAsyncCuboidClipboard(player, clipboard);
                        try {
                            // Note: The Clipboard is always drawn from the min position using the place method
                            asyncStructureClipboard.place(aSession, pos, false, new ConstructionBuildingCallback(plugin, player, entry.getConstructionSite()));
                        } catch (MaxChangedBlocksException ex) { // shouldnt happen

                        }
                    }
                }, 100); //FIXME Should replace this with a NON-SESSION Task that places the enclosure

            }
        });

    }

    /**
     * Demolishes a structure.
     *
     * @param player The player's UUID
     * @param constructionSite The ConstructionSite
     * @param plugin The plugin used for the bukkit-runnable that will be created
     * @throws construction.exception.ConstructionException
     */
    public synchronized void demolish(final Plugin plugin, final UUID player, final ConstructionSite constructionSite) throws ConstructionException {
        ConstructionEntry entry = constructionEntries.get(constructionSite.getId());
        
        // Removed structures can't be tasked
        if(constructionSite.getState() == ConstructionSite.State.REMOVED) {
            throw new ConstructionException("#" + constructionSite.getId() + " can't be tasked, because it was removed");
        }
        
        // Structure has already stopped constructing
        if(constructionSite.getState() == ConstructionSite.State.DEMOLISHING) {
            throw new ConstructionException("#" + constructionSite.getId() + " is already being demolished");
        }
        
        // Quit whatever your are doing now
        if(constructionSite.getState() == State.QUEUED) {
            stop(constructionSite);
        }

        // First time schematic may not be loaded yet
        if (entry != null) {
            startDemolision(plugin, player, entry);
            ConstructionSiteService css = new ConstructionSiteService();
            css.setState(constructionSite, State.QUEUED);
            constructionEntries.put(constructionSite.getId(), entry);
        } else {
//            Bukkit.getScheduler().runTaskAsynchronously(plugin, new BukkitRunnable() {
//
//                @Override
//                public void run() {
//                    SchematicService ss = new SchematicService();
//                    Schematic schematic = ss.getSchematic(constructionSite.getStructure().getPlan().getSchematicChecksum());
//                    try {
//                        ConstructionSiteService css = new ConstructionSiteService();
//                        css.setState(constructionSite, State.QUEUED);
//                        CuboidClipboard cc = SchematicFormat.MCEDIT.load(schematic.getFile());
//                        ConstructionEntry entry = new ConstructionEntry(constructionSite,cc);
//                        entry.setDemolishing(true);
//                        constructionEntries.put(constructionSite.getId(), entry);
//                        startDemolision(plugin, player, entry);
//                    } catch (IOException | DataException ex) {
//                        Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//
//                }
//            });
        }
    }

    private void startDemolision(final Plugin plugin, final UUID player, final ConstructionEntry entry) {
        // Align schematic
        Cardinal cardinal = entry.getConstructionSite().getStructure().getCardinal();
        final ConstructionDemolisionClipboard clipboard = new ConstructionDemolisionClipboard(entry.getCuboidClipboard(), StructureBlockComparators.PERFORMANCE.reversed());
        SchematicUtil.align(clipboard, cardinal);

        // Create & Place enclosure
        final Structure structure = entry.getConstructionSite().getStructure();
        final World world = WorldEditUtil.getWorld(structure.getWorldName());
        final Vector pos = structure.getDimension().getMinPosition();
        final EditSession session = WorldEdit.getInstance().getEditSessionFactory().getEditSession(world, -1);

        EditSession aSession = AsyncWorldEditUtil.getAsyncSessionFactory().getEditSession(world, -1);
        SCAsyncCuboidClipboard asyncStructureClipboard = new SCAsyncCuboidClipboard(player, clipboard);
        try {
            // Note: The Clipboard is always drawn from the min position using the place method
            asyncStructureClipboard.place(aSession, pos, false, new ConstructionDemolisionCallback(plugin,player, entry.getConstructionSite()));
        } catch (MaxChangedBlocksException ex) { // shouldnt happen

        }

    }

    /**
     * Stop construction of a structure.
     * @param site The ConstructionSite
     * @throws construction.exception.ConstructionException
     */
    public synchronized void stop(ConstructionSite site) throws ConstructionException {
        ConstructionEntry entry = constructionEntries.get(site.getId());
        
        // Removed structures can't be tasked
        if(site.getState() == ConstructionSite.State.REMOVED) {
            throw new ConstructionException("#" + site.getId() + " can't be tasked, because it was removed");
        }

        // Structure was never tasked
        if(entry == null) {
            throw new ConstructionException("#" + site.getId() + " hasn't been tasked yet");
        }
        
        // Structure has already stopped constructing
        if(site.getState() == ConstructionSite.State.STOPPED) {
            throw new ConstructionException("#" + site.getId() + " already has stopped");
        }
        
        // Cancel task in AsyncWorldEdit
        AsyncWorldEditMain.getInstance().getBlockPlacer().cancelJob(entry.getPlayer(), entry.getJobId());
        
        // Set new state: STOPPED
        ConstructionSiteService css = new ConstructionSiteService();
        css.setState(site, State.STOPPED);

        // Reset data
        constructionEntries.get(site.getId()).setDemolishing(false);
        constructionEntries.get(site.getId()).setJobId(-1);
        constructionEntries.get(site.getId()).setPlayer(null);
    }

    public ConstructionEntry getEntry(Long constructionSiteID) {
        return constructionEntries.get(constructionSiteID);
    }
    
    

}
