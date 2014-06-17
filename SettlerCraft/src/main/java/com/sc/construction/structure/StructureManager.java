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
package com.sc.construction.structure;

import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.google.common.base.Preconditions;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.hibernate.HibernateQuery;
import com.sc.construction.asyncworldEdit.ConstructionProcess;
import com.sc.construction.asyncworldEdit.ConstructionProcess.State;
import com.sc.construction.exception.StructureException;
import com.sc.construction.plan.StructurePlan;
import com.sc.construction.plan.StructureSchematic;
import com.sc.menu.SCVaultEconomyUtil;
import com.sc.persistence.HibernateUtil;
import com.sc.persistence.SchematicService;
import com.sc.persistence.StructureService;
import com.sc.plugin.ConfigProvider;
import com.sc.plugin.SettlerCraft;
import com.sc.util.SCAsyncWorldEditUtil;
import com.sc.util.SCWorldEditUtil;
import com.sc.util.SCWorldGuardUtil;
import com.sc.util.SettlerCraftUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.util.Location;
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
import org.hibernate.Session;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 *
 * @author Chingo
 */
public class StructureManager {

    public static final int STRUCTURE_ID_INDEX = 0;
    public static final int STRUCTURE_PLAN_INDEX = 1;
    public static final int STRUCTURE_OWNER_INDEX = 2;
    public static final int STRUCTURE_STATUS_INDEX = 3;
    private static StructureManager instance;

    private static final int INFINITE = -1;

    private final Map<Long, Hologram> holos; // Structure id / Holo
    private final Plugin plugin;
    private final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(StructureManager.class);
    private boolean initialized = false;

    private StructureManager() {
        this.holos = Collections.synchronizedMap(new HashMap<Long, Hologram>());
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
            if(ConfigProvider.getInstance().useHolograms()) {
                initHolos();
                initialized = true;
            }
        }
    }

    public void removeHolo(Long structureId) {
        if(ConfigProvider.getInstance().useHolograms()) {
            Hologram hologram = holos.get(structureId);
            if(hologram != null) {
                hologram.delete();
                holos.remove(structureId);
            }
        }
    }

    private boolean exceedsLimit(Player player) {
//        List<ConstructionProcess> processes = listProgress(player.getName());
//        if(processes == null) {
//            return false;
//        } else {
//            int blocks = 0;
//            int limit = ConfigProvider.getQueueSoftLimit();
//            for(ConstructionProcess p : processes) {
//                blocks += StructurePlanManager.getInstance().getSchematic(p.getStructure().getPlan().getSchematicChecksum()).getBlocks();
//            }
//            
//            
//            return blocks > limit;
//        }
        return false;

    }

    private void initHolos() {
        if(!ConfigProvider.getInstance().useHolograms()) {
            return;
        }
        
        Session session = HibernateUtil.getSession();
        JPQLQuery query = new HibernateQuery(session);
        QStructure qs = QStructure.structure;
        List<Structure> structures = query.from(qs).where(qs.progress().progressStatus.ne(State.REMOVED)).list(qs);
        session.close();
        Iterator<Structure> sit = structures.iterator();
        while (sit.hasNext()) {
            Structure s = sit.next();
            if (ConfigProvider.getInstance().useHolograms() && s.getPlan().hasSign()) {
                holos.put(s.getId(), createStructureHolo(s));
            }
        }
    }

    private Hologram createStructureHolo(Structure structure) {
        Location pos = structure.getLocation(structure.getPlan().getSignLocation());

        org.bukkit.Location location = new org.bukkit.Location(
                Bukkit.getWorld(structure.getLocation().getWorld().getName()),
                pos.getX(),
                pos.getY() + 1, // a bit above ground level
                pos.getZ()
        );

        Hologram hologram = HolographicDisplaysAPI.createHologram(plugin, location,
                "Id: " + ChatColor.GOLD + structure.getId(),
                "Plan: " + ChatColor.BLUE + structure.getPlan().getDisplayName(),
                "Owner: " + ChatColor.GREEN + structure.getOwner()
        );
        return hologram;
    }

    public CuboidClipboard cloneArea(Structure structure) {
        WorldDimension dimension = structure.getDimension();
        CuboidRegion region = new CuboidRegion(dimension.getLocalWorld(), dimension.getMinPosition(), dimension.getMaxPosition());
        Vector min = region.getMinimumPoint();
        Vector max = region.getMaximumPoint();
        Vector pos = structure.getPosition();
        AsyncEditSession copySession = SCAsyncWorldEditUtil.createAsyncEditSession(
                SCWorldEditUtil.getLocalPlayer(Bukkit.getPlayer(structure.getOwnerUUID())), INFINITE);

        CuboidClipboard clipboard = new CuboidClipboard(
                max.subtract(min).add(Vector.ONE),
                min, min.subtract(pos));

        clipboard.copy(copySession, region);
        return clipboard;
    }

    public Structure construct(final Player owner, final StructurePlan plan, final Vector location, final SimpleCardinal cardinal) {
        if (exceedsLimit(owner)) {
            owner.sendMessage(ChatColor.RED + "Construction queue is full! Wait for the structure(s) to finish");
            return null;
        }
        SchematicService service = new SchematicService();
        StructureSchematic schematic = service.getSchematic(plan.getSchematicChecksum());

        
        
        Structure structure = new Structure(SCWorldEditUtil.getWorld(owner),owner, location, cardinal, plan, schematic);
        if (overlaps(structure)) {
            owner.sendMessage(ChatColor.RED + " Structure overlaps another structure");
            return null;
        }

        StructureService ss = new StructureService();
        structure = ss.save(structure);
        structure.setStructureRegionId("sc" + structure.getId() + "-" + new Date().getTime()); // id's are unique for index and time
        ConstructionProcess progress = new ConstructionProcess(structure);
        structure.setConstructionProgress(progress);
        ss.save(progress);

        ProtectedRegion structureRegion = claimGround(owner, structure);
        if (structureRegion == null) {
            ss.delete(structure);
            owner.sendMessage(ChatColor.RED + "Failed to claim region for structure");
            return null;
        }

        try {
            StructureConstructionManager.getInstance().continueProcess(owner, progress, true);
        } catch (StructureException ex) {
            Logger.getLogger(StructureManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (ConfigProvider.getInstance().useHolograms() && structure.getPlan().hasSign()) {
            holos.put(structure.getId(), createStructureHolo(structure));
        }

        return structure;
    }

    public void align(final CuboidClipboard clipboard, SimpleCardinal direction) {
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

    private synchronized ProtectedRegion claimGround(Player player, final Structure structure) {
        if (structure.getId() == null) {
            // Sanity check
            throw new AssertionError("Structure id was null, save the structure instance first! (e.g. structure = structureService.save(structure)");
        }

        //
        if (overlapsRegion(player, structure)) {
            player.sendMessage(ChatColor.RED + "Structure overlaps an regions owned other players");
            return null;
        }

        RegionManager mgr = SCWorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));
        WorldDimension dim = structure.getDimension();
        Vector p1 = dim.getMinPosition();
        Vector p2 = dim.getMaxPosition();
        String id = structure.getStructureRegion();

        if (regionExists(structure.getDimension().getWorld(), id)) {
            player.sendMessage(ChatColor.RED + "Assigned region id already exists! This shouldn't happen!");
            return null;
        }

        ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));

        // Set Flag
        region.setFlags(ConfigProvider.getInstance().getDefaultFlags());
        
        region.getOwners().addPlayer(player.getName());
        mgr.addRegion(region);
        try {
            mgr.save();
        } catch (ProtectionDatabaseException ex) {
            Logger.getLogger(StructureManager.class.getName()).log(Level.SEVERE, null, ex);
        }

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
        World world = Bukkit.getWorld(structure.getWorld());
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
        World world = Bukkit.getWorld(structure.getWorld());
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

    public boolean overlaps(com.sk89q.worldedit.world.World world, Vector location, SimpleCardinal cardinal, StructurePlan plan, StructureSchematic schematic) {
        Structure structure = new Structure(world, location, cardinal, plan, schematic);
        return overlaps(structure);
    }

    public boolean overlaps(Structure structure) {
        StructureService service = new StructureService();
        return service.overlaps(structure);
    }

    public boolean overlapsRegion(Player player, Structure structure) {
        LocalPlayer localPlayer = SCWorldGuardUtil.getLocalPlayer(player);
        RegionManager mgr = SCWorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));
        WorldDimension dim = structure.getDimension();
        Vector p1 = dim.getMinPosition();
        Vector p2 = dim.getMaxPosition();
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

    public boolean overlapsRegion(Player player, World world, Vector pos, SimpleCardinal cardinal, StructurePlan plan, StructureSchematic schematic) {
        Structure structure = new Structure(world, new BlockVector(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ()), cardinal, plan, schematic);
        return overlapsRegion(player, structure);
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
                            "Refunded " + ChatColor.BLUE + structure.getPlan().getDisplayName() + ChatColor.GOLD + SettlerCraftUtil.valueString(refundValue),
                            ChatColor.RESET + "Your new balance: " + ChatColor.GOLD + SettlerCraftUtil.valueString(economy.getBalance(player.getName()))
                        });
                    }
                }
            }
        }
    }

    public void shutdown() {
        for (Hologram holo : holos.values()) {
            holo.delete();
        }
    }

}
