/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure;

import com.google.common.base.Preconditions;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.api.structure.construction.ConstructionManager;
import com.sc.api.structure.construction.async.SCAsyncCuboidClipboard;
import com.sc.api.structure.construction.async.SCDefaultCallbackAction;
import com.sc.api.structure.construction.progress.ConstructionException;
import com.sc.api.structure.construction.progress.ConstructionStrategyType;
import com.sc.api.structure.construction.progress.ConstructionTaskException;
import com.sc.api.structure.entity.QStructure;
import com.sc.api.structure.entity.SCSession;
import com.sc.api.structure.entity.Structure;
import com.sc.api.structure.entity.StructureJob;
import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.entity.progress.ConstructionEntry;
import com.sc.api.structure.entity.progress.ConstructionTask;
import com.sc.api.structure.entity.progress.MaterialLayerProgress;
import com.sc.api.structure.entity.progress.MaterialProgress;
import com.sc.api.structure.entity.progress.MaterialResourceProgress;
import com.sc.api.structure.entity.progress.QConstructionTask;
import com.sc.api.structure.entity.world.SimpleCardinal;
import com.sc.api.structure.entity.world.WorldDimension;
import com.sc.api.structure.io.StructurePlanLoader;
import com.sc.api.structure.listener.StructurePlanListener;
import com.sc.api.structure.persistence.HSQLServer;
import com.sc.api.structure.persistence.HibernateUtil;
import com.sc.api.structure.persistence.MemDBUtil;
import com.sc.api.structure.persistence.service.AbstractService;
import com.sc.api.structure.persistence.service.StructureService;
import com.sc.api.structure.persistence.service.TaskService;
import com.sc.api.structure.util.WorldUtil;
import com.sc.api.structure.util.plugins.SCAsyncWorldEditUtil;
import com.sc.api.structure.util.plugins.SCWorldEditUtil;
import com.sc.api.structure.util.plugins.SCWorldGuardUtil;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.WorldEditAPI;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class SCStructureAPI extends JavaPlugin {
    
    private boolean restrictZones = false;
    private static final int INFINITE_BLOCKS = -1;
    private StructurePlanListener spl;
    private static final Logger LOGGER = Logger.getLogger(SCStructureAPI.class);
    
    public boolean isRestrictZonesEnabled() {
        
        return restrictZones;
    }
    
    public void setRestrictZonesEnabled(boolean restrictZones) {
        this.restrictZones = restrictZones;
    }
    
    public static WorldEditAPI getWorldEditAPI() {
        return new WorldEditAPI((WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit"));
    }
    
    public static SCStructureAPI getSCStructureAPI() {
        return (SCStructureAPI) Bukkit.getPluginManager().getPlugin("SCStructureAPI");
    }
    
    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            System.out.println("[SCStructureAPI]: WorldEdit NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("AsyncWorldEdit") == null) {
            System.out.println("[SCStructureAPI]: AsyncWorldEdit NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }
        
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            System.out.println("[SCStructureAPI]: WorldGuard NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }
        
        Bukkit.getPluginManager().registerEvents(new StructurePlanListener(), this);
        HSQLServer.getInstance().start();
        initDB();
        
        RestoreService service = new RestoreService();
        service.restore();
        
        loadStructures(FileUtils.getFile(getDataFolder(), "Structures"));
        
    }
    
    public static WorldEditPlugin getWorldEditPlugin() {
        return (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
    }
    
    private static void initDB() {
        MemDBUtil.addAnnotatedClasses(
                SCSession.class,
                StructurePlan.class);
        HibernateUtil.addAnnotatedClasses(
                Structure.class,
                MaterialProgress.class,
                MaterialLayerProgress.class,
                MaterialResourceProgress.class,
                StructurePlan.class,
                StructureJob.class,
                ConstructionEntry.class,
                ConstructionTask.class);
    }

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
    
    public static boolean place(Player player, StructurePlan plan, Location location, SimpleCardinal cardinal) throws ConstructionException {
        return place(player, new Structure(player.getName(), location, cardinal, plan));
    }
    
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
        structure = service.save(structure);
        
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
        CuboidRegion region = new CuboidRegion(dimension.getLocalWorld(), dimension.getStart().getPosition(), dimension.getEnd().getPosition());
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

        ConstructionTask task = new ConstructionTask(placer, entry, structure, ConstructionTask.ConstructionType.BUILDING, ConstructionStrategyType.LAYERED);
        task = constructionService.save(task);
        
        final SCDefaultCallbackAction dca = new SCDefaultCallbackAction(placer, structure, task, asyncSession);
        
        final CuboidClipboard schematic = structure.getPlan().getSchematic();
        final Location t = ConstructionManager.align(schematic, structure.getLocation(), structure.getCardinal());
        final SmartClipBoard smartClipboard = new SmartClipBoard(schematic, ConstructionStrategyType.LAYERED, false);
        final SCAsyncCuboidClipboard asyncCuboidClipboard = new SCAsyncCuboidClipboard(asyncSession.getPlayer(), smartClipboard);
        
        try {
            asyncCuboidClipboard.place(asyncSession, t.getPosition(), false, dca);
        } catch (MaxChangedBlocksException ex) {
            LOGGER.error(ex);
        }
        
        return true;
    }
    
    public static void remove(Structure structure) {
        Preconditions.checkNotNull(structure);
        Session session = HibernateUtil.getSession();
        QConstructionTask qct = QConstructionTask.constructionTask;
        JPQLQuery query = new HibernateQuery(session);
        ConstructionTask task = query.from(qct).where(qct.structure().id.eq(structure.getId())).uniqueResult(qct);
        if (task == null) {
            LOGGER.debug("Task was null");
            if (structure.getId() == null) {
                LOGGER.debug("Structure id was null");
                session.close();
                return;
            } else {
                QStructure qs = QStructure.structure;
                Structure s = query.from(qs).where(qs.id.eq(structure.getId())).uniqueResult(qs);
                if (s == null) {
                    LOGGER.debug("structure was null in remove()");
                    session.close();
                    return;
                } else {
                    session.lock(s, LockMode.WRITE);
                    removeRegion(structure);
                    LOGGER.debug("deleting structure, task was null and structure still exists");
                    session.delete(s);
                    session.close();
                    return;
                }
            }
        }
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            task.setState(ConstructionTask.State.REMOVED);
            task.setJobId(-1);
            if (task.hasPlacedBlocks()) {
                task.setConstructionType(ConstructionTask.ConstructionType.DEMOLISHING);
                task.setState(ConstructionTask.State.CANCELED);
                task = (ConstructionTask) session.merge(task);
                undo(task.getStructure());
            } else {
                task.setState(ConstructionTask.State.REMOVED);
                task = (ConstructionTask) session.merge(task);
                removeRegion(task.getStructure());
            }
            tx.commit();
        } catch (HibernateException e) {
            try {
                tx.rollback();
            } catch (HibernateException rbe) {
                java.util.logging.Logger.getLogger(AbstractService.class.getName()).log(Level.SEVERE, "Couldn’t roll back transaction", rbe);
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
    
    public static void removeRegion(Structure structure) {
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
    
    public static void undo(Structure structure) {
        CuboidClipboard clipboard = structure.getAreaBefore();
        AsyncEditSession session = SCAsyncWorldEditUtil.createAsyncEditSession(structure.getOwner(), structure.getLocation().getWorld(), INFINITE_BLOCKS);
        List<Vector> vertices = ConstructionStrategyType.LAYERED.getList(clipboard);
        Collections.reverse(vertices);
        SmartClipBoard smartClipBoard = new SmartClipBoard(clipboard, ConstructionStrategyType.LAYERED);
        SCAsyncCuboidClipboard scacc = new SCAsyncCuboidClipboard(structure.getOwner(), smartClipBoard);
        try {
            scacc.place(session, structure.getLocation().getPosition(), true);
        } catch (MaxChangedBlocksException ex) {
            LOGGER.error(ex);
        }
    }
    
}
