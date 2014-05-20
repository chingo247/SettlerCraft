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
package com.sc.api.structure.construction.builder;

import com.google.common.collect.Maps;
import com.sc.api.structure.construction.builder.flag.SCFlags;
import com.sc.api.structure.model.Structure;
import com.sc.api.structure.model.StructureJob;
import com.sc.api.structure.model.world.WorldDimension;
import com.sc.api.structure.persistence.StructureService;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;


/**
 * @author Chingo
 */
public class SCConstructionManager {

    private final ConcurrentMap<String, List<StructureJob>> jobs = Maps.newConcurrentMap();
    private static SCConstructionManager instance;
    private final String prefix = "SC-STAPI";
    private final String regionPrefix = "CONSITE";
    private final String structurefix = "STRUC";

    public void addJob(String player, StructureJob structureJob) {
        if (jobs.get(player) == null) {
            jobs.put(player, new ArrayList<StructureJob>());
        }
        jobs.get(player).add(structureJob);
    }

    public boolean hasJob(String player) {
        if (jobs.containsKey(player) && !jobs.get(player).isEmpty()) {
            return true;
        }
        return false;
    }

    public void removeJob(String player, int jobId) {
        Iterator<StructureJob> it = jobs.get(player).iterator();
        while (it.hasNext()) {
            StructureJob j = it.next();
            if (j.getId() == jobId) {
                it.remove();
                break;
            }
        }
    }

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
     * @param ignoreRegionCount Wheter or not to check if the player owns to many regions therefore not being able to place stuff
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

    public boolean canPlace(Player placer, World world, ProtectedRegion region, boolean ignoreSize, boolean feedback) {
        RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(world);
        ProtectedRegion existing = mgr.getRegionExact(region.getId());
        RegionPermissionModel permModel = WorldGuardUtil.getRegionPermissionModel(placer);
        WorldConfiguration wcfg = WorldGuardUtil.getWorldGuard().getGlobalStateManager().get(placer.getWorld());
        

        // Check for an existing region
        if (existing != null) {
            if (!existing.getOwners().contains(WorldGuardUtil.getLocalPlayer(placer))) {
                if (feedback) {
                    placer.sendMessage(ChatColor.RED + "There already exists a region with the same id and you don't own it.");
                }
                return false;
            }
        }

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
            
            for(ProtectedRegion r : regions) {
                if(r.getFlag(SCFlags.STRUCTURE) != null) {
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
    

    public boolean placeSafe(Player placer, Structure structure, boolean addSelf, boolean feedback) {
        if(!mayPlace(placer, true, feedback)) {
            return false; // Feedback inside mayPlace()
        }
        System.out.println("StructureID: " + UUID.randomUUID().hashCode());
        
        String id = String.valueOf(UUID.randomUUID().hashCode());
        System.out.println("StructureRegionID: " + id);
        structure.setStructureRegionId(id);

        WorldDimension dim = structure.getDimension();
        Vector p1 = dim.getStart().getPosition();
        Vector p2 = dim.getEnd().getPosition();
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));
        region.setFlag(SCFlags.STRUCTURE, structure.getPlan().getDisplayName());
        RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(placer.getWorld());
        
        if(!canPlace(placer, placer.getWorld(), region, true, feedback)) { 
            return false; // Feedback inside canPlace()
        }
        
        if(addSelf) {
//            region.getOwners().addPlayer(WorldGuardUtil.getLocalPlayer(placer));
        }
        
        mgr.addRegion(region);
        try {
            mgr.save();
        }
        catch (ProtectionDatabaseException ex) {
            Logger.getLogger(SCConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            SCStructureBuilder.placeStructure(placer, structure.getLocation(), structure.getDirection(), structure.getPlan(), feedback);
        }
        catch (MaxChangedBlocksException ex) {
            mgr.removeRegion(id);
            Logger.getLogger(SCConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        StructureService service = new StructureService();
        service.save(structure);
        
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

}
