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
package com.sc.api.structure;

import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.google.common.base.Preconditions;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.api.structure.ConstructionProcess.State;
import com.sc.api.structure.async.SCAsyncCuboidClipboard;
import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.entity.world.SimpleCardinal;
import com.sc.api.structure.entity.world.WorldDimension;
import com.sc.api.structure.flag.SCFlags;
import com.sc.api.structure.generator.Enclosures;
import com.sc.api.structure.persistence.HibernateUtil;
import com.sc.api.structure.persistence.service.AbstractService;
import com.sc.api.structure.persistence.service.StructureService;
import com.sc.api.structure.progress.ConstructionStrategyType;
import com.sc.api.structure.util.plugins.SCAsyncWorldEditUtil;
import com.sc.api.structure.util.plugins.SCWorldGuardUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Countable;
import com.sk89q.worldedit.CuboidClipboard;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class StructureManager {

    public static final String ENCLOSURE_INFIX = "sc_enclosure";
    public static final int STRUCTURE_ID_INDEX = 0;
    public static final int STRUCTURE_PLAN_INDEX = 1;
    public static final int STRUCTURE_OWNER_INDEX = 2;
    public static final int STRUCTURE_STATUS_INDEX = 3;
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(StructureManager.class);
    private static final int INFINITE = -1;
    private final Map<String, ConstructionEntry> playerEntries;
    private final Map<Long, Hologram> holos; // Structure id / Holo
    private final Lock regionLock;
    private boolean initialized = false;

    private static StructureManager instance;

    private StructureManager() {
        this.regionLock = new ReentrantLock();
        this.playerEntries = Collections.synchronizedMap(new HashMap<String, ConstructionEntry>());
        this.holos = Collections.synchronizedMap(new HashMap<Long, Hologram>());

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

    private void initHolos() {
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        List<Structure> structures = query.from(qs).where(qs.progress.progressStatus.ne(State.REMOVED)).list(qs);
        session.close();
        for (Structure s : structures) {
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
        updateHolo(progress);
    }

    public void putProgress(Integer jobId, ConstructionProcess progress) {
        if (playerEntries.get(progress.getStructure().getOwner()) == null) {
            playerEntries.put(progress.getStructure().getOwner(), new ConstructionEntry());
        }
        playerEntries.get(progress.getStructure().getOwner()).put(jobId, progress);
        System.out.println(playerEntries.get(progress.getStructure().getOwner()).get(jobId));
        updateHolo(progress);
    }

    public ConstructionProcess getProgress(String owner, Integer jobId) {
        if (playerEntries.get(owner) != null) {
            return null;
        } else {
            return playerEntries.get(owner).get(jobId);
        }
    }

    private void updateHolo(ConstructionProcess progress) {
        Hologram holo = holos.get(progress.getId());
        State state = progress.getStatus();

        if (holo != null) {
            if (state == State.COMPLETE) {
                holo.setLine(STRUCTURE_STATUS_INDEX, "");
                holo.update();
                return;
            } else if (state == State.REMOVED) {
                holos.remove(progress.getId());
                holo.delete();
                return;
            }
            String statusString;
            switch (state) {
                case DEMOLISHING:
                    statusString = "Status: " + ChatColor.YELLOW;
                    statusString += state.name();
                    break;
                case BUILDING:
                    statusString = "Status: " + ChatColor.YELLOW;
                    statusString += state.name();
                    break;
                case COMPLETE:
                    statusString = "";
                    break;
                case STOPPED:
                    statusString = "Status: " + ChatColor.RED;
                    statusString += state.name();
                    break;
                default:
                    statusString = "Status: " + ChatColor.WHITE;
                    statusString += state.name();
                    break;
            }

            holo.setLine(STRUCTURE_STATUS_INDEX, statusString);
            holo.update();
        }
    }

    private Hologram createStructureHolo(Structure structure) {
        Location pos = structure.getLocation(structure.getPlan().getSignLocation());

        org.bukkit.Location location = new org.bukkit.Location(
                Bukkit.getWorld(structure.getLocation().getWorld().getName()),
                pos.getPosition().getX(),
                pos.getPosition().getY() + 1,
                pos.getPosition().getZ()
        );

        String statusString;
        State state = structure.getProgress().getStatus();

        switch (state) {
            case DEMOLISHING:
                statusString = "Status: " + ChatColor.YELLOW;
                statusString += state.name();
                break;
            case BUILDING:
                statusString = "Status: " + ChatColor.YELLOW;
                statusString += state.name();
                break;
            case COMPLETE:
                statusString = "";
                break;
            case STOPPED:
                statusString = "Status: " + ChatColor.RED;
                statusString += state.name();
                break;
            default:
                statusString = "Status: " + ChatColor.WHITE;
                statusString += state.name();
                break;
        }

        Hologram hologram = HolographicDisplaysAPI.createHologram(SCStructureAPI.getInstance().getMainPlugin(), location,
                "Id: " + ChatColor.GOLD + structure.getId(),
                "Plan: " + ChatColor.BLUE + structure.getPlan().getDisplayName(),
                "Owner: " + ChatColor.GREEN + structure.getOwner(),
                statusString
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

        Structure structure = new Structure(owner.getName(), location, cardinal, plan);
        StructureService ss = new StructureService();
        structure = ss.save(structure);
        structure.setStructureRegionId(UUID.randomUUID().toString());
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

        final CuboidClipboard enclosure = Enclosures.standard(schematic, BlockID.IRON_BARS);
        align(enclosure, structure.getLocation(), structure.getCardinal());
        final AsyncEditSession enclosureSession = SCAsyncWorldEditUtil.createAsyncEditSession(owner.getName() + ENCLOSURE_INFIX + structure.getId(), structure.getLocation().getWorld(), INFINITE);
        final SmartClipBoard enclosurClipBoard = new SmartClipBoard(enclosure, ConstructionStrategyType.LAYERED);
        final SCAsyncCuboidClipboard asyncEnclosurClipBoard = new SCAsyncCuboidClipboard(enclosureSession.getPlayer(), enclosurClipBoard);

        final Vector pos = structure.getDimension().getMin().getPosition();
        final ConstructionStructureCallback sCallback = new ConstructionStructureCallback(owner.getName(), structure, structureSession);
        final EnclosureCompleteCallback eCallback = new EnclosureCompleteCallback(structure) {

            @Override
            public void onComplete() {
                try {
                    asyncStructureClipboard.place(structureSession, pos, false, sCallback);
                } catch (MaxChangedBlocksException ex) {
                    Logger.getLogger(StructureManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        holos.put(structure.getId(), createStructureHolo(structure));

        try {
            asyncEnclosurClipBoard.place(enclosureSession, pos, false, eCallback);
        } catch (MaxChangedBlocksException ex) {
            LOGGER.error(ex);
        }

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
            java.util.logging.Logger.getLogger(SCStructureAPI.class.getName()).log(Level.SEVERE, null, ex);
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

        try {

            regionLock.lock();

            if (!canClaim(player)) {
                player.sendMessage(ChatColor.RED + "Can't build structure, region limit reached");
                return null;
            }

            if (!mayClaim(player)) {
                player.sendMessage(ChatColor.RED + "Can't build structure, no permission");
                return null;
            }

            if (overlaps(structure.getPlan(), structure.getLocation(), structure.getCardinal())) {
                player.sendMessage(ChatColor.RED + " Structure overlaps another structure");
                return null;
            }

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
            region.setFlag(SCFlags.STRUCTURE, String.valueOf(structure.getId()));
            region.getOwners().addPlayer(player.getName());
            mgr.addRegion(region);
            mgr.save();

            return region;
        } finally {
            regionLock.unlock();
        }

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
        // TODO WorldGuard Region Ownership!
        return structure.getOwner().equals(player.getName());
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
        RegionManager mgr = SCWorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));
        WorldDimension dim = structure.getDimension();
        Vector p1 = dim.getMin().getPosition();
        Vector p2 = dim.getMax().getPosition();
        ProtectedCuboidRegion region = new ProtectedCuboidRegion("", new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));
        ApplicableRegionSet regions = mgr.getApplicableRegions(region);

        for (ProtectedRegion r : regions) {
            if (r.getId().equals("")) {
                continue;
            }
            if (r.getFlag(SCFlags.STRUCTURE) != null) {
                return true;
            }
        }
        return false;
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

        StructureService ss = new StructureService();
        process.setJobId(-1);
        process.setHasPlacedEnclosure(false); 
        process = ss.save(process);

        String owner = structure.getOwner();

        LocalWorld world = structure.getLocation().getWorld();
        final AsyncEditSession session = SCAsyncWorldEditUtil.createAsyncEditSession(owner, world, INFINITE);
//        ConstructionStructureCallback dca = 

        List<Vector> vertices;
        final SCJobCallback jc;
        CuboidClipboard schematic;
        if (!process.isDemolishing()) {

            schematic = StructurePlanManager.getInstance().getClipBoard(structure.getPlan().getChecksum());
            vertices = ConstructionStrategyType.LAYERED.getList(schematic, false);
            align(schematic, structure.getDimension().getMin(), structure.getCardinal());
        } else {
            CuboidClipboard structureSchematic = StructurePlanManager.getInstance().getClipBoard(structure.getPlan().getChecksum());
            schematic = new CuboidClipboard(structureSchematic.getSize());
            align(schematic, structure.getDimension().getMin(), structure.getCardinal());
            for (int x = 0; x < schematic.getWidth(); x++) {
                for (int y = 0; y < schematic.getHeight(); y++) {
                    for (int z = 0; z < schematic.getLength(); z++) {
                        if (y != 0) {
                            schematic.setBlock(new BlockVector(x, y, z), new BaseBlock(0));
                        } else {
                            BaseBlock worldBlock = session.getBlock(new BlockVector(x, y - 1, z).add(structure.getDimension().getMin().getPosition()));
                            schematic.setBlock(new BlockVector(x, y, z), worldBlock);
                        }
                    }
                }
            }
            vertices = ConstructionStrategyType.LAYERED.getList(schematic, false);
            Collections.reverse(vertices);
        }
        jc = new ConstructionStructureCallback(owner, structure, session);

        final SmartClipBoard smartClipBoard = new SmartClipBoard(schematic, vertices);
        final SCAsyncCuboidClipboard asyncCuboidClipboard = new SCAsyncCuboidClipboard(session.getPlayer(), smartClipBoard);
        
        if(structure.getProgress().isDemolishing()) {
            try {
                asyncCuboidClipboard.place(session, structure.getDimension().getMin().getPosition(), false, jc);
            } catch (MaxChangedBlocksException ex) {
                Logger.getLogger(StructureManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            
        final CuboidClipboard enclosure = Enclosures.standard(schematic, BlockID.IRON_BARS);
        align(enclosure, structure.getLocation(), structure.getCardinal());
        final AsyncEditSession enclosureSession = SCAsyncWorldEditUtil.createAsyncEditSession(structure.getOwner() + ENCLOSURE_INFIX + structure.getId(), structure.getLocation().getWorld(), INFINITE);
        final SmartClipBoard enclosurClipBoard = new SmartClipBoard(enclosure, ConstructionStrategyType.LAYERED);
        final SCAsyncCuboidClipboard asyncEnclosurClipBoard = new SCAsyncCuboidClipboard(enclosureSession.getPlayer(), enclosurClipBoard);
        final EnclosureCompleteCallback eCallback = new EnclosureCompleteCallback(structure) {

                @Override
                public void onComplete() {
                    try {
                        
                        asyncCuboidClipboard.place(session, structure.getDimension().getMin().getPosition(), false, jc);
                    } catch (MaxChangedBlocksException ex) {
                        Logger.getLogger(StructureManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            try {
                asyncEnclosurClipBoard.place(enclosureSession, structure.getDimension().getMin().getPosition(), false, eCallback);
            } catch (MaxChangedBlocksException ex) {
                Logger.getLogger(StructureManager.class.getName()).log(Level.SEVERE, null, ex);
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

        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            BlockPlacer placer = SCAsyncWorldEditUtil.getBlockPlacer();
            int jobId = process.getJobId();

            String owner = structure.getOwner();
            if (placer.getJob(owner, jobId) == null) {
                owner = owner + ENCLOSURE_INFIX + structure.getId();
                if (placer.getJob(owner, jobId) != null) {
                    placer.cancelJob(owner, jobId);
                }
            } else {
                placer.cancelJob(owner, jobId);
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
     * @throws com.sc.api.structure.construction.StructureException
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
                stopProcess(process, true);
            }
        }
    }

    public void shutdown() {
        stopAll();
        for (Hologram holo : holos.values()) {
            holo.delete();
        }
    }

    public void continueAll() {

    }
}
