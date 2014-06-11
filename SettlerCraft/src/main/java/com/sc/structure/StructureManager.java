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
package com.sc.structure;

import com.sc.structure.construction.SmartClipBoard;
import com.sc.entity.Structure;
import com.cc.plugin.api.menu.SCVaultEconomyUtil;
import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.google.common.base.Preconditions;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.persistence.AbstractService;
import com.sc.persistence.HibernateUtil;
import com.sc.persistence.StructureService;
import com.sc.plugin.SettlerCraft;
import com.sc.structure.async.SCAsyncCuboidClipboard;
import com.sc.structure.construction.ConstructionEntry;
import com.sc.structure.construction.ConstructionProcess;
import com.sc.structure.construction.ConstructionProcess.State;
import com.sc.structure.construction.ConstructionStrategyType;
import com.sc.structure.construction.ConstructionStructureCallback;
import com.sc.structure.construction.JobCallback;
import com.sc.entity.plan.StructurePlan;
import com.sc.entity.world.SimpleCardinal;
import static com.sc.entity.world.SimpleCardinal.EAST;
import static com.sc.entity.world.SimpleCardinal.NORTH;
import static com.sc.entity.world.SimpleCardinal.SOUTH;
import static com.sc.entity.world.SimpleCardinal.WEST;
import com.sc.entity.world.WorldDimension;
import com.sc.structure.generator.Enclosures;
import com.sc.structure.sync.SyncBuilder;
import com.sc.structure.sync.SyncPlaceTask;
import com.sc.util.SCAsyncWorldEditUtil;
import com.sc.util.SCWorldGuardUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Countable;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalWorld;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionPermissionModel;
import com.sk89q.worldguard.bukkit.WorldConfiguration;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primesoft.asyncworldedit.ConfigProvider;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class StructureManager {

    private final Plugin plugin;
    public static final int STRUCTURE_ID_INDEX = 0;
    public static final int STRUCTURE_PLAN_INDEX = 1;
    public static final int STRUCTURE_OWNER_INDEX = 2;
    public static final int STRUCTURE_STATUS_INDEX = 3;
    private static final int ENCLOSURE_BUFFER_SIZE = 100;
    private final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(StructureManager.class);
    private static final int INFINITE = -1;
    private final Map<String, ConstructionEntry> playerEntries;
    private final Map<Long, Hologram> holos; // Structure id / Holo
    private final Map<Long, BukkitTask> enclosureTasks; // Structure id , BukkitTaskId
    private boolean initialized = false;

    private static StructureManager instance;

    private StructureManager() {
        this.playerEntries = Collections.synchronizedMap(new HashMap<String, ConstructionEntry>());
        this.holos = Collections.synchronizedMap(new HashMap<Long, Hologram>());
        this.enclosureTasks = Collections.synchronizedMap(new HashMap<Long, BukkitTask>());
        this.plugin = SettlerCraft.getSettlerCraft();
    }

    public static StructureManager getInstance() {
        if (instance == null) {
            instance = new StructureManager();
        }
        return instance;
    }

    public void init() {
        if (!initialized) {
            initHolos();
            initialized = true;
        }
    }
    
    public void removeHolo(Long structureId) {
        holos.get(structureId).delete();
        holos.remove(structureId);
    }
    
    private boolean exceedsLimit(Player player) {
        List<ConstructionProcess> processes = listProgress(player.getName());
        if(processes == null) {
            return false;
        } else {
            int blocks = 0;
            int limit = ConfigProvider.getQueueSoftLimit();
            for(ConstructionProcess p : processes) {
                blocks += StructurePlanManager.getInstance().getSchematic(p.getStructure().getPlan().getChecksum()).getBlocks();
            }
            
            
            return blocks > limit;
        }
        
        
    }

    private void initHolos() {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        List<Structure> structures = query.from(qs).where(qs.progress.progressStatus.ne(State.REMOVED)).list(qs);
        session.close();
        Iterator<Structure> sit = structures.iterator();
        while (sit.hasNext()) {
            Structure s = sit.next();
            holos.put(s.getId(), createStructureHolo(s));
        }
    }

    public List<ConstructionProcess> listProgress(String owner) {
        if (playerEntries.get(owner) == null) {
            return null;
        } else {
            return playerEntries.get(owner).list();
        }
    }

    public void removeProgress(Integer jobId, ConstructionProcess progress) {
        if (playerEntries.get(progress.getStructure().getOwner()) != null) {
            playerEntries.get(progress.getStructure().getOwner()).remove(jobId);
        }
    }

    public void putProgress(Integer jobId, ConstructionProcess progress) {
        if (playerEntries.get(progress.getStructure().getOwner()) == null) {
            playerEntries.put(progress.getStructure().getOwner(), new ConstructionEntry());
        }
        playerEntries.get(progress.getStructure().getOwner()).put(jobId, progress);
    }

    public ConstructionProcess getProgress(String owner, Integer jobId) {
        if (playerEntries.get(owner) != null) {
            return null;
        } else {
            return playerEntries.get(owner).get(jobId);
        }
    }

//  Cause plugin to crash when used Async
//    private synchronized void updateHolo(ConstructionProcess progress) {
//        Hologram holo = holos.get(progress.getId());
//        State state = progress.getStatus();
//
//        if (holo != null) {
//            if (state == State.COMPLETE) {
//                if(progress.getStructure().getPlan().isHideSignOnComplete()) {
//                holo.hide();
//                return;
//                } else {
//                holo.setLine(STRUCTURE_STATUS_INDEX, "");
//                holo.update();
//                return;
//                }
//            } else if (state == State.REMOVED) {
//                holos.remove(progress.getId());
//                holo.delete();
//                return;
//            }
//            String statusString;
//            switch (state) {
//                case DEMOLISHING:
//                    statusString = "Status: " + ChatColor.YELLOW;
//                    statusString += state.name();
//                    break;
//                case BUILDING:
//                    statusString = "Status: " + ChatColor.YELLOW;
//                    statusString += state.name();
//                    break;
//                case COMPLETE:
//                    statusString = "";
//                    break;
//                case STOPPED:
//                    statusString = "Status: " + ChatColor.RED;
//                    statusString += state.name();
//                    break;
//                default:
//                    statusString = "Status: " + ChatColor.WHITE;
//                    statusString += state.name();
//                    break;
//            }
//
//            holo.setLine(STRUCTURE_STATUS_INDEX, statusString);
//            
//        }
//    }

    private Hologram createStructureHolo(Structure structure) {
        Location pos = structure.getLocation(structure.getPlan().getSignLocation());

        org.bukkit.Location location = new org.bukkit.Location(
                Bukkit.getWorld(structure.getLocation().getWorld().getName()),
                pos.getPosition().getX(),
                pos.getPosition().getY() + 1,
                pos.getPosition().getZ()
        );

        

        Hologram hologram = HolographicDisplaysAPI.createHologram(plugin, location,
                "Id: " + ChatColor.GOLD + structure.getId(),
                "Plan: " + ChatColor.BLUE + structure.getPlan().getDisplayName(),
                "Owner: " + ChatColor.GREEN + structure.getOwner()
        );
        return hologram;
    }

    public CuboidClipboard cloneArea(Player player, StructurePlan plan, Location location, SimpleCardinal cardinal) {
        Structure dummy = new Structure("", location, cardinal, plan);
        return cloneArea(dummy);
    }

    public CuboidClipboard cloneArea(Structure structure) {
        WorldDimension dimension = structure.getDimension();
        CuboidRegion region = new CuboidRegion(dimension.getLocalWorld(), dimension.getMin().getPosition(), dimension.getMax().getPosition());
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();
        Vector pos = structure.getLocation().getPosition();
        AsyncEditSession copySession = SCAsyncWorldEditUtil.createAsyncEditSession(structure.getOwner(), structure.getLocation().getWorld(), INFINITE);

        CuboidClipboard clipboard = new CuboidClipboard(
                max.subtract(min).add(Vector.ONE),
                min, min.subtract(pos));

        clipboard.copy(copySession, region);
        return clipboard;
    }

    public Structure construct(Player owner, StructurePlan plan, Location location, SimpleCardinal cardinal) {
        if(exceedsLimit(owner)) {
            owner.sendMessage(ChatColor.RED + "Structure queue is full! Wait for the structure(s) to finish");
            return null;
        }
        
        
        Structure structure = new Structure(owner.getName(), location, cardinal, plan);
        if (overlaps(structure.getPlan(), structure.getLocation(), structure.getCardinal())) {
            owner.sendMessage(ChatColor.RED + " Structure overlaps another structure");
                return null;
        }
        
        StructureService ss = new StructureService();
        structure = ss.save(structure);
        structure.setStructureRegionId("sc"+structure.getId() + "" + new Date().getTime());
        ConstructionProcess progress = new ConstructionProcess(structure);
        structure.setConstructionProgress(progress);
        ss.save(progress);

        ProtectedRegion structureRegion = null;
        try {
            structureRegion = claimGround(owner, structure);
        } catch (ProtectionDatabaseException ex) {
            Logger.getLogger(StructureManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (structureRegion == null) {
            ss.delete(structure);
            owner.sendMessage(ChatColor.RED + "Failed to claim region for structure");
            return null;
        }

        final CuboidClipboard schematic = StructurePlanManager.getInstance().getClipBoard(structure.getPlan().getChecksum());
        align(schematic, structure.getLocation(), structure.getCardinal());
        final AsyncEditSession structureSession = SCAsyncWorldEditUtil.createAsyncEditSession(owner.getName(), structure.getLocation().getWorld(), INFINITE);
        final SmartClipBoard structureClipboard = new SmartClipBoard(schematic, ConstructionStrategyType.LAYERED, false);
        final SCAsyncCuboidClipboard asyncStructureClipboard = new SCAsyncCuboidClipboard(structureSession.getPlayer(), structureClipboard);

        final Vector pos = structure.getDimension().getMin().getPosition();
        final ConstructionStructureCallback sCallback = new ConstructionStructureCallback(owner.getName(), structure, structureSession);
        final Long structureId = structure.getId();
        final CuboidClipboard enclosure = Enclosures.standard(schematic, BlockID.IRON_BARS);
        final EditSession enclosureSession = new EditSession(structure.getLocation().getWorld(), INFINITE);
        
        BukkitTask task = SyncBuilder.placeBuffered(enclosureSession, enclosure, structure.getDimension().getMin(), ENCLOSURE_BUFFER_SIZE, new SyncPlaceTask.PlaceCallback() {

            @Override
            public void onComplete() {
               try {
                    enclosureTasks.remove(structureId); 
                    asyncStructureClipboard.place(structureSession, pos, false, sCallback);
                } catch (MaxChangedBlocksException ex) {
                    Logger.getLogger(StructureManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        enclosureTasks.put(structure.getId(), task);
        holos.put(structure.getId(), createStructureHolo(structure));

        

        

        return structure;
    }

    /**
     * Demolishes a structure, the area of the structure will be restored to the moment before this
     * structure was placed
     *
     * @param player The demolisher
     * @param structure The target structure
     * @return True if demolision was succesfull
     */
    public boolean demolish(Player player, Structure structure) {
        if (!structure.getOwner().equals(player.getName())) {
            player.sendMessage(ChatColor.RED + "You don't own this structure");
            return false;
        }
        ConstructionProcess progress = structure.getProgress();
        progress.setIsDemolishing(true);
        try {
            
            continueProcess(progress, true);
            return true;
        } catch (StructureException ex) {
            java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    private void align(final CuboidClipboard clipboard, Location location, SimpleCardinal direction) {
        switch (direction) {
            case EAST:
                break;
            case SOUTH:
                clipboard.rotate2D(90);
                break;
            case WEST:
                clipboard.rotate2D(180);
                break;
            case NORTH:
                clipboard.rotate2D(270);
                break;
            default:
                throw new AssertionError("Unreachable");
        }
    }

    private ProtectedRegion claimGround(Player player, final Structure structure) throws ProtectionDatabaseException {
        if (structure.getId() == null) {
            throw new AssertionError("Structure id was null, save the structure instance first! (e.g. structure = structureService.save(structure)"); // Should only happen if the programmer forgets to save the instance before this
        }


// FIXME REPLACE WITH CONSTRUCT PERMISSION
//            if (!canClaim(player)) {
//                player.sendMessage(ChatColor.RED + "Can't build structure, region limit reached");
//                return null;
//            }
//
//            if (!mayClaim(player)) {
//                player.sendMessage(ChatColor.RED + "Can't build structure, no permission");
//                return null;
//            }

            

            if (overlapsRegion(player, structure.getPlan(), structure.getLocation(), structure.getCardinal())) {
                player.sendMessage(ChatColor.RED + "Structure overlaps an regions owned other players");
                return null;
            }

            RegionManager mgr = SCWorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));
            WorldDimension dim = structure.getDimension();
            Vector p1 = dim.getMin().getPosition();
            Vector p2 = dim.getMax().getPosition();
            String id = structure.getStructureRegion();

            if (regionExists(structure.getDimension().getWorld(), id)) {
                player.sendMessage(ChatColor.RED + "Assigned region id already exists! This shouldn't happen!");
                return null;
            }

            ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));

            // Set Flag
            region.getOwners().addPlayer(player.getName());
            mgr.addRegion(region);
            mgr.save();

            return region;
        
    }

    /**
     * Removes the region of this structure, a structure without it's region will no longer be
     * recognized by the ConstructionManager. This method is automatically called after demolision
     * is complete or the structure was removed. This method should not be used for any other
     * purpose
     *
     * @param structure The structure
     */
    public void removeRegion(Structure structure) {
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
     *
     * @param player The player
     * @param structure The structure
     * @return True if this player owns the structure
     */
    public boolean owns(Player player, Structure structure) {
        if (getOwners(structure).contains(player.getName())) {
            return true;
        }
        return structure.getOwner().equals(player.getName());
    }

    public Set<String> getOwners(Structure structure) {
        World world = structure.getWorld();
        Set<String> owners = new HashSet<>();
        if (world == null) {
            return owners;
        }

        RegionManager rmgr = SCWorldGuardUtil.getGlobalRegionManager(world);
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if (region == null) {
            return owners;
        }

        owners = region.getOwners().getPlayers();
        return owners;
    }

    public Set<String> getMembers(Structure structure) {
        World world = structure.getWorld();
        Set<String> members = new HashSet<>();
        if (world == null) {
            return members;
        }

        RegionManager rmgr = SCWorldGuardUtil.getGlobalRegionManager(world);
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if (region == null) {
            return members;
        }

        members = region.getMembers().getPlayers();
        return members;
    }

    public boolean regionExists(World world, String id) {
        return SCWorldGuardUtil.getGlobalRegionManager(world).hasRegion(id);
    }

    public boolean mayClaim(Player player) {
        RegionPermissionModel permissionModel = SCWorldGuardUtil.getRegionPermissionModel(player);
        return permissionModel.mayClaim();
    }

    public boolean canClaim(Player player) {
        WorldConfiguration wcfg = SCWorldGuardUtil.getWorldGuard().getGlobalStateManager().get(player.getWorld());
        RegionPermissionModel permissionModel = SCWorldGuardUtil.getRegionPermissionModel(player);
        RegionManager mgr = SCWorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(player.getWorld());

        // Check whether the player has created too many regions
        if (!permissionModel.mayClaimRegionsUnbounded()) {
            int maxRegionCount = wcfg.getMaxRegionCount(player);
            if (maxRegionCount >= 0
                    && mgr.getRegionCountOfPlayer(SCWorldGuardUtil.getLocalPlayer(player)) >= maxRegionCount) {

                return false;
            }
        }
        return true;
    }

    public boolean overlaps(StructurePlan plan, Location location, SimpleCardinal cardinal) {
        Structure structure = new Structure("", location, cardinal, plan);
        StructureService service = new StructureService();
        return service.overlaps(structure);
    }

    public boolean overlapsRegion(Player player, StructurePlan plan, Location location, SimpleCardinal cardinal) {
        Structure structure = new Structure("", location, cardinal, plan);
        LocalPlayer localPlayer = SCWorldGuardUtil.getLocalPlayer(player);
        RegionManager mgr = SCWorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));
        WorldDimension dim = structure.getDimension();
        Vector p1 = dim.getMin().getPosition();
        Vector p2 = dim.getMax().getPosition();
        ProtectedCuboidRegion dummy = new ProtectedCuboidRegion("", new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));
        ApplicableRegionSet regions = mgr.getApplicableRegions(dummy);

        // Check if this region getOverlapping any other region
        if (regions.size() > 0) {
            if (!regions.isOwnerOfAll(localPlayer)) {
                return true;
            }
        }
        return false;
    }

    public int size(StructurePlan plan, boolean noAir) {
        CuboidClipboard clipboard = StructurePlanManager.getInstance().getClipBoard(plan.getChecksum());
        int count = 0;
        for (Countable<BaseBlock> b : clipboard.getBlockDistributionWithData()) {
            if (b.getID().isAir() && noAir) {
                continue;
            }
            count += b.getAmount();
        }
        return count;
    }

    /**
     * Continues construction of a structure
     *
     * @param process The process to continue
     * @param force will ignore the task current state, therefore even if the task was marked
     * completed it will try to continue the task
     * @throws com.sc.api.structure.StructureException
     */
    public void continueProcess(ConstructionProcess process, boolean force) throws StructureException {
        final Structure structure = process.getStructure();
        State status = process.getStatus();
        if ((status == State.BUILDING
                || status == State.QUEUED
                || status == State.COMPLETE) && !force) {
            return;
        }

        if (status == State.REMOVED) {
            throw new StructureException("Tried to continue a removed structure");
        }
        
        
        
        stopProcess(process, true); // also stops enclosure task

        StructureService ss = new StructureService();
        process.setJobId(-1);
        process.setHasPlacedEnclosure(false);
        process = ss.save(process);

        String owner = structure.getOwner();

        LocalWorld world = structure.getLocation().getWorld();
        final AsyncEditSession session = SCAsyncWorldEditUtil.createAsyncEditSession(owner, world, INFINITE);
//        ConstructionStructureCallback dca = 

        List<Vector> vertices;
        final JobCallback jc;
        CuboidClipboard schematic;
        if (!process.isDemolishing()) {
            schematic = StructurePlanManager.getInstance().getClipBoard(structure.getPlan().getChecksum());
            align(schematic, structure.getDimension().getMin(), structure.getCardinal());
            vertices = ConstructionStrategyType.LAYERED.getList(schematic, false);
        } else {
            schematic = StructurePlanManager.getInstance().getClipBoard(structure.getPlan().getChecksum());
            align(schematic, structure.getDimension().getMin(), structure.getCardinal());
            vertices = ConstructionStrategyType.LAYERED.getList(schematic, false);
            Collections.reverse(vertices);
        }
        jc = new ConstructionStructureCallback(owner, structure, session);

        final SmartClipBoard smartClipBoard = new SmartClipBoard(schematic, vertices);
        if(process.isDemolishing()) {
            smartClipBoard.setReverse(true);
        }
        
        final SCAsyncCuboidClipboard asyncCuboidClipboard = new SCAsyncCuboidClipboard(structure.getOwner(), smartClipBoard);

        if (structure.getProgress().isDemolishing()) {
            try {
                asyncCuboidClipboard.place(session, structure.getDimension().getMin().getPosition(), false, jc);
            } catch (MaxChangedBlocksException ex) {
                Logger.getLogger(StructureManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {

            final CuboidClipboard enclosure = Enclosures.standard(schematic, BlockID.IRON_BARS);
//            align(enclosure, structure.getLocation(), structure.getCardinal());
            EditSession enclosureSession = new EditSession(world, INFINITE);
            BukkitTask task = SyncBuilder.placeBuffered(enclosureSession, enclosure, structure.getDimension().getMin(), ENCLOSURE_BUFFER_SIZE, new SyncPlaceTask.PlaceCallback() {

                @Override
                public void onComplete() {
                    try {
                        enclosureTasks.remove(structure.getId());
                        asyncCuboidClipboard.place(session, structure.getDimension().getMin().getPosition(), false, jc);
                    } catch (MaxChangedBlocksException ex) {
                        Logger.getLogger(StructureManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            enclosureTasks.put(structure.getId(), task);
            
            
            
           
        }

    }

    public void refund(Structure structure) {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            Economy economy = SCVaultEconomyUtil.getInstance().getEconomy();
            if (economy != null) {
                Player player = Bukkit.getPlayer(structure.getOwner());
                if (player != null) {
                    double refundValue = structure.getRefundValue();
                    economy.depositPlayer(structure.getOwner(), refundValue);
                    if (player.isOnline()) {
                        player.sendMessage(new String[]{
                            "Refunded " + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.GOLD + refundValue,
                            ChatColor.RESET + "Your new balance: " + ChatColor.GOLD + economy.getBalance(player.getName())
                        });
                    }
                }
            }
        }
    }

    /**
     * Stops the task, the task will be removed from AsyncWorldEdit's blockplacer queue, but will
     * still remain in the database
     *
     * @param process The process to stop
     * @param force whether to check if the progress already has stopped
     */
    
    public void stopProcess(ConstructionProcess process, boolean force) {
        Preconditions.checkArgument(process.getStatus() != State.REMOVED);
        Structure structure = process.getStructure();
        State progressStatus = process.getStatus();

        if (progressStatus == State.STOPPED && !force) {
            return;
        }

        if (progressStatus == State.COMPLETE || process.getJobId() == -1) {
            return;
        }

        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            BlockPlacer placer = SCAsyncWorldEditUtil.getBlockPlacer();
            int jobId = process.getJobId();

            String owner = structure.getOwner();
            placer.cancelJob(owner, jobId);
            BukkitTask task = enclosureTasks.get(structure.getId());
            if(task != null) {
                task.cancel();
                enclosureTasks.remove(structure.getId());
            }
            
            
            process.setProgressStatus(State.STOPPED);
            removeProgress(jobId, process);
            process.setJobId(-1);
            session.merge(process);
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

    /**
     * Stops the task and continue's it. As result the task will be added at the back of the queue
     * by AsyncWorldEdit
     *
     * @param process The process
     * @throws com.sc.api.structure.StructureException
     */
    public void delayProcess(ConstructionProcess process) throws StructureException {
        if (process.getJobId() == -1) {
            return;
        }

        stopProcess(process, true);
        continueProcess(process, true);
    }

    public void stopAll() {
        for (ConstructionEntry ce : playerEntries.values()) {
            for (ConstructionProcess process : ce.list()) {
                if (process.getStatus() != State.COMPLETE && process.getStatus() != State.REMOVED) {
                    stopProcess(process, true);
                }
            }
        }
    }

    public void shutdown() {
        stopAll();
        for (Hologram holo : holos.values()) {
            holo.delete();
        }
    }

}
