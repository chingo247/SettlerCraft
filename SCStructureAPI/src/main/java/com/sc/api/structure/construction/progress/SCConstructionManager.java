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
package com.sc.api.structure.construction.progress;

import com.google.common.collect.Maps;
import com.sc.api.structure.construction.builder.async.SCAsyncStructureBuilder;
import com.sc.api.structure.construction.builder.flag.SCFlags;
import com.sc.api.structure.construction.builder.strategies.SCDefaultCallbackAction;
import com.sc.api.structure.model.Structure;
import com.sc.api.structure.model.StructureJob;
import com.sc.api.structure.model.world.WorldDimension;
import com.sc.api.structure.persistence.ConstructionService;
import com.sc.api.structure.persistence.StructureService;
import com.sc.api.structure.util.plugins.AsyncWorldEditUtil;
import com.sc.api.structure.util.plugins.WorldGuardUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.RegionPermissionModel;
import com.sk89q.worldguard.bukkit.WorldConfiguration;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSession;

/**
 * @author Chingo
 */
public class SCConstructionManager {

    private final ConcurrentMap<String, List<StructureJob>> jobs = Maps.newConcurrentMap();
    private static SCConstructionManager instance;


//    public void addJob(String player, StructureJob structureJob) {
//        if (jobs.get(player) == null) {
//            jobs.put(player, new ArrayList<StructureJob>());
//        }
//        jobs.get(player).add(structureJob);
//    }
//
//    public boolean hasJob(String player) {
//        if (jobs.containsKey(player) && !jobs.get(player).isEmpty()) {
//            return true;
//        }
//        return false;
//    }

//    public void removeJob(String player, int jobId) {
//        Iterator<StructureJob> it = jobs.get(player).iterator();
//        while (it.hasNext()) {
//            StructureJob j = it.next();
//            if (j.getId() == jobId) {
//                it.remove();
//                break;
//            }
//        }
//    }

    public static SCConstructionManager getInstance() {
        if (instance == null) {
            instance = new SCConstructionManager();
        }
        return instance;
    }

    public boolean mayClaim(Player player) {
        return WorldGuardUtil.getRegionPermissionModel(player).mayClaim();
    }

    public boolean regionExists(World world, String id) {
        return WorldGuardUtil.getGlobalRegionManager(world).hasRegion(id);
    }

    /**
     * Checks wheter the target player is allowed to place, disregarding the
     * emplacement area
     *
     * @param placer The player
     * @param ignoreRegionCount Wheter or not to check if the player owns to
     * many regions therefore not being able to place stuff
     * @param feedback Wheter or not feedback should be send to the player
     * @return false if player doesnt have permission to claim or has claimed to
     * many regions
     */
    public boolean mayPlace(Player placer, boolean ignoreRegionCount, boolean feedback) {
        System.out.println("May Place?");
        RegionPermissionModel permissionModel = WorldGuardUtil.getRegionPermissionModel(placer);
        RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(placer.getWorld());

        // Has permission to claim
        if (!permissionModel.mayClaim()) {

            if (feedback) {
                placer.sendMessage(ChatColor.RED + "U have no permission to place stuff or claim regions");
            }
            return false;
        }

        if (!ignoreRegionCount) {
            WorldConfiguration wcfg = WorldGuardUtil.getWorldGuard().getGlobalStateManager().get(placer.getWorld());

            // Check whether the player has created too many regions
            if (!permissionModel.mayClaimRegionsUnbounded()) {
                int maxRegionCount = wcfg.getMaxRegionCount(placer);
                if (maxRegionCount >= 0
                        && mgr.getRegionCountOfPlayer(WorldGuardUtil.getLocalPlayer(placer)) >= maxRegionCount) {
                    if (feedback) {
                        placer.sendMessage(ChatColor.RED + "You own too many regions, delete one first to claim a new one.");
                    }
                    return false;
                }
            }
        }

        return true;
    }
    
//    public boolean overlaps(Structure structure) {
//        
//    }

    public boolean canPlace(Player placer, World world, ProtectedRegion region, boolean ignoreSize, boolean feedback) {
        RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(world);
//        ProtectedRegion existing = mgr.getRegionExact(region.getId());
        RegionPermissionModel permModel = WorldGuardUtil.getRegionPermissionModel(placer);
        WorldConfiguration wcfg = WorldGuardUtil.getWorldGuard().getGlobalStateManager().get(placer.getWorld());

//        // Check for an existing region
//        if (existing != null) {
//            if (!existing.getOwners().contains(WorldGuardUtil.getLocalPlayer(placer))) {
//                if (feedback) {
//                    placer.sendMessage(ChatColor.RED + "There already exists a region with the same id and you don't own it.");
//                }
//                return false;
//            }
//        }
        if (!ignoreSize) {
            // Check claim volume
            if (!permModel.mayClaimRegionsUnbounded()) {
                if (region.volume() > wcfg.maxClaimVolume) {
                    if (feedback) {
                        placer.sendMessage(ChatColor.RED + "This region is too large to claim.");
                        placer.sendMessage(ChatColor.RED
                                + "Max. volume: " + wcfg.maxClaimVolume + ", your volume: " + region.volume());
                    }
                    return false;
                }
            }
        }

        // We have to check whether this region violates the space of any other reion
        ApplicableRegionSet regions = mgr.getApplicableRegions(region);

        // Check if this region overlaps any other region
        if (regions.size() > 0) {
            if (!regions.isOwnerOfAll(WorldGuardUtil.getLocalPlayer(placer))) {
                if (feedback) {
                    placer.sendMessage(ChatColor.RED + "This region overlaps with someone else's region.");
                }
                return false;
            }

            for (ProtectedRegion r : regions) {
                if (r.getFlag(SCFlags.STRUCTURE) != null) {
                    System.out.println(ChatColor.RED + "Structure overlaps another structure");
                    return false;
                }
            }

        } else {
            if (wcfg.claimOnlyInsideExistingRegions) {
                if (feedback) {
                    placer.sendMessage("You may only place things inside "
                            + "existing regions that you or your group own.");
                }
                return false;
            }
        }

        return true;
    }

    private String getIdForStructure(Structure structure) {
        String s = String.valueOf("sc_s_" + (structure.getPlan().getDisplayName().replaceAll("\\s", "") + "_" + structure.getId())).toLowerCase();
        return s;
    }

    public boolean placeSafe(Player placer, Structure structure, boolean addSelf, boolean feedback) {

        // Handles permissions and limits
        if (!mayPlace(placer, true, feedback)) {
            return false; // Feedback inside mayPlace()
        }

        // All structure placement related things
        WorldDimension dim = structure.getDimension();
        Vector p1 = dim.getStart().getPosition();
        Vector p2 = dim.getEnd().getPosition();
        ProtectedCuboidRegion dummy = new ProtectedCuboidRegion("", new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));

        dummy.setFlag(SCFlags.STRUCTURE, structure.getPlan().getDisplayName());
        RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(placer.getWorld());
        

        if (!canPlace(placer, placer.getWorld(), dummy, true, feedback)) {
            return false; // Feedback inside canPlace()
        }

        // Save the structure to get an unique identifier
        StructureService service = new StructureService();
        structure = service.save(structure);

        String id = getIdForStructure(structure);
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));
        structure.setStructureRegionId(id);
        
        // Set Flag
        region.setFlag(SCFlags.STRUCTURE, structure.getId().intValue());
        
        // Add self as owner if true
        if (addSelf) {
            region.getOwners().addPlayer(WorldGuardUtil.getLocalPlayer(placer));
        }

        // Save the region with worldguard
        mgr.addRegion(region);
        try {
            mgr.save();
        }
        catch (ProtectionDatabaseException ex) {
            service.delete(structure);
            Logger.getLogger(SCConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Add and save the task to an Construction entry
        ConstructionService cs = new ConstructionService();
        ConstructionEntry entry;
        String issuer = placer.getName();
        
        // Create an entry for the issuer
        if (!cs.hasEntry(issuer)) {
            entry = cs.createEntry(issuer);
        } else {
            entry = cs.getEntry(issuer);
        }
        structure = service.save(structure);
        cs.save(entry);
        ConstructionTask task = new ConstructionTask(entry, structure, ConstructionTask.ConstructionType.BUILDING_AUTO, ConstructionStrategyType.LAYERED);
        task = cs.save(task);
        

        // Actually place the structure or add them to the construction queue
        try {
            placeStructure(placer, task, structure, feedback);
        }
        catch (MaxChangedBlocksException ex) {
            service.delete(structure);
            cs.removeConstructionTask(issuer, task);
            mgr.removeRegion(id);
            Logger.getLogger(SCConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    public boolean createConstructionSite(String id, Player placer, World world, BlockVector pos1, BlockVector pos2, boolean feedback, boolean addSelf) {
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, pos1, pos2);
        RegionPermissionModel permModel = WorldGuardUtil.getRegionPermissionModel(placer);
        // Can't replace existing regions
        RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(world);
        if (mgr.hasRegion(id)) {
            if (feedback) {
                placer.sendMessage(ChatColor.RED + "That region already exists. Please choose a different name.");
            }
            return false;
        }

        region.getOwners().addPlayer(placer.getName());

        mgr.addRegion(region);
        try {
            mgr.save();
        }
        catch (ProtectionDatabaseException ex) {
            Logger.getLogger(SCConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;

    }

    private void placeStructure(final Player player, final ConstructionTask task, Structure structure, boolean defaultFeedback) throws MaxChangedBlocksException {
        final ConstructionService service = new ConstructionService();
        final AsyncEditSession asyncSession = AsyncWorldEditUtil.createAsyncEditSession(player, -1); // -1 = infinite
//        final EditSession session = new EditSession(location.getWorld(), -1);
    
        
//        SCFoundationBuilder.placeDefault(session, structure, Material.COBBLESTONE, true);

        SCDefaultCallbackAction dca = new SCDefaultCallbackAction(player, structure, task, asyncSession, defaultFeedback);

        SCAsyncStructureBuilder.place(
                structure,
                task,
                asyncSession,
                dca);
        task.setState(ConstructionState.IN_QUEUE);
        service.save(task);

    }

}
