/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure;

import com.google.common.base.Preconditions;
import com.sc.api.structure.construction.ConstructionManager;
import com.sc.api.structure.construction.ConstructionTaskManager;
import com.sc.api.structure.construction.SmartClipBoard;
import com.sc.api.structure.construction.async.ConstructionCallback;
import com.sc.api.structure.construction.async.SCAsyncCuboidClipboard;
import com.sc.api.structure.construction.progress.ConstructionException;
import com.sc.api.structure.construction.progress.ConstructionStrategyType;
import com.sc.api.structure.construction.progress.ConstructionTaskException;
import com.sc.api.structure.entity.Structure;
import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.entity.progress.ConstructionEntry;
import com.sc.api.structure.entity.progress.ConstructionTask;
import com.sc.api.structure.entity.world.SimpleCardinal;
import com.sc.api.structure.entity.world.WorldDimension;
import com.sc.api.structure.persistence.service.StructureService;
import com.sc.api.structure.persistence.service.TaskService;
import com.sc.api.structure.plan.StructurePlanLoader;
import com.sc.api.structure.util.WorldUtil;
import com.sc.api.structure.util.plugins.SCAsyncWorldEditUtil;
import com.sc.api.structure.util.plugins.SCWorldEditUtil;
import com.sc.api.structure.util.plugins.SCWorldGuardUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class SCStructureAPI extends JavaPlugin {
    
    
    private static final int INFINITE_BLOCKS = -1;
    private static final Logger LOGGER = Logger.getLogger(SCStructureAPI.class);
    
    /**
     * Loads structures from a directory
     *
     * @param structureDirectory The directory to search
     */
    public static void loadStructures(File structureDirectory) {
        File structureFolder = new File(structureDirectory.getAbsolutePath());
        if (!structureFolder.exists()) {
            structureFolder.mkdirs();
        }
        StructurePlanLoader spLoader = new StructurePlanLoader();
        try {
            spLoader.loadStructures(structureFolder);
        } catch (FileNotFoundException ex) {
            LOGGER.error(ex);
        }
    }

    /**
     * Selects a the structures cuboid region with worldedit
     *
     * @param player The player to perform the selection
     * @param structure The structure
     */
    public static void select(Player player, Structure structure) {
        Location pos2 = WorldUtil.getPos2(structure.getLocation(), structure.getCardinal(), structure.getPlan().getSchematic());
        SCWorldEditUtil.select(player, structure.getLocation(), pos2);
    }

    /**
     * Selects the target area / cuboid region
     *
     * @param player The player to perform the selection
     * @param plan The structurePlan
     * @param location The target location
     * @param cardinal The cardinal / direction of the structure
     */
    public static void select(Player player, StructurePlan plan, Location location, SimpleCardinal cardinal) {
        Structure structure = new Structure("", location, cardinal, plan);
        select(player, structure);
    }
    
    /**
     * Places a structure
     * @param player The player that places the structure
     * @param plan The structure plan
     * @param location The location
     * @param cardinal The cardinal / direction of the structure
     * @return true if placement was succesful
     * @throws ConstructionException 
     */
    public static boolean place(Player player, StructurePlan plan, Location location, SimpleCardinal cardinal) throws ConstructionException {
        return place(player, new Structure(player.getName(), location, cardinal, plan));
    }
    
    /**
     * Places a structure at it's location
     * @param player The player that places the structure
     * @param structure The structure
     * @return True if placement was succesful
     * @throws ConstructionException 
     */
    public static boolean place(Player player, Structure structure) throws ConstructionException {
        StructureService service = new StructureService();
        structure = service.save(structure);
        WorldDimension dimension = structure.getDimension();
        
        ProtectedRegion protectedRegion = ConstructionManager.claimGround(player, structure, structure.getDimension());
        if (protectedRegion == null) {
            service.delete(structure);
            player.sendMessage(ChatColor.RED + " Failed to claim ground for structure");
            return false;
        }
        
        structure.setStructureRegionId(protectedRegion.getId());
        
        final String placer = player.getName();
        final TaskService constructionService = new TaskService();
        if (constructionService.hasConstructionTask(structure)) {
            service.delete(structure);
            SCWorldGuardUtil.getGlobalRegionManager(player.getWorld()).removeRegion(protectedRegion.getId());
            throw new ConstructionTaskException("Already have a task reserved for structure" + structure.getId());
        }

//        final RegionManager mgr = SCWorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));
//        if (structure.getStructureRegion() == null || !mgr.hasRegion(structure.getStructureRegion())) {
//            service.delete(structure);
//            SCWorldGuardUtil.getGlobalRegionManager(target.getWorld()).removeRegion(region.getId());
//            throw new ConstructionException("Tried to place a structure without a region");
//        }
        CuboidRegion region = new CuboidRegion(dimension.getLocalWorld(), dimension.getMin().getPosition(), dimension.getMax().getPosition());
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();
        Vector pos = structure.getLocation().getPosition();
        EditSession copySession = SCWorldEditUtil.getEditSession(region.getWorld(), INFINITE_BLOCKS);
        
        CuboidClipboard clipboard = new CuboidClipboard(
                max.subtract(min).add(Vector.ONE),
                min, min.subtract(pos));
        
        clipboard.copy(copySession, region);
        structure.setAreaBefore(clipboard);
        structure = service.save(structure);
        
        final ConstructionEntry entry = constructionService.hasEntry(placer) ? constructionService.getEntry(placer) : constructionService.createEntry(placer);
        final AsyncEditSession asyncSession = SCAsyncWorldEditUtil.createAsyncEditSession(placer, structure.getLocation().getWorld(), -1); // -1 = infinite

        ConstructionTask task = new ConstructionTask(placer, entry, structure, ConstructionStrategyType.LAYERED);
        task = constructionService.save(task);
        
        final ConstructionCallback dca = new ConstructionCallback(placer, structure, task, asyncSession);
        
        final CuboidClipboard schematic = structure.getPlan().getSchematic();
        ConstructionManager.align(schematic, structure.getLocation(), structure.getCardinal());
        final SmartClipBoard smartClipboard = new SmartClipBoard(schematic, ConstructionStrategyType.LAYERED, false);
        final SCAsyncCuboidClipboard asyncCuboidClipboard = new SCAsyncCuboidClipboard(asyncSession.getPlayer(), smartClipboard);
        
        try {
            asyncCuboidClipboard.place(asyncSession, structure.getDimension().getMin().getPosition(), false, dca);
        } catch (MaxChangedBlocksException ex) {
            LOGGER.error(ex);
        }
        
        return true;
    }
   
    /**
     * Demolishes a structure, the area of the structure will be restored to the moment before this structure was placed
     * @param player The demolisher
     * @param structure The target structure
     * @return True if demolision was succesfull
     */
    public static boolean demolish(Player player, Structure structure) {
        TaskService service = new TaskService();
        ConstructionTask task = service.getTask(structure.getId());
        ConstructionTaskManager taskManager = new ConstructionTaskManager();
        if(task == null) {
            return false;
        } else {
            if(!structure.getOwner().equals(player.getName())) {
                player.sendMessage(ChatColor.RED + "You don't own this structure");
                return false;
            }
            task.setIsDemolishing(true);
            try {
                taskManager.continueTask(task, true);
                return true;
            } catch (ConstructionTaskException ex) {
                java.util.logging.Logger.getLogger(SCStructureAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    
    /**
     * Removes the region of this structure, a structure without it's region will no longer be recognized by the ConstructionManager. 
     * This method is automatically called after demolision is complete or the structure was removed. This method should not be used for any other purpose
     * @param structure The structure
     */
    public static void removeRegion(Structure structure) {
        Preconditions.checkNotNull(structure);
        RegionManager rmgr = SCWorldGuardUtil.getGlobalRegionManager(structure.getDimension().getWorld());
        if (rmgr.hasRegion(structure.getStructureRegion())) {
            rmgr.removeRegion(structure.getStructureRegion());
            try {
                rmgr.save();
            } catch (ProtectionDatabaseException ex) {
                LOGGER.error(ex);
            }
        }
    }
    
    /**
     * Determines if the player is an owner of this structure
     * @param player The player
     * @param structure The structure
     * @return True if this player owns the structure
     */
    public static boolean owns(Player player, Structure structure) {
        // TODO WorldGuard Region Ownership!
        return structure.getOwner().equals(player.getName());
    }
    
    
}
