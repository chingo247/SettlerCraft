/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction.builder;

import com.sc.api.structure.util.plugins.WorldGuardUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.RegionPermissionModel;
import com.sk89q.worldguard.bukkit.WorldConfiguration;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class SCConstructionManager {

    private static SCConstructionManager instance;

    public SCConstructionManager getInstance() {
        return instance;
    }

    public boolean mayClaim(Player player) {
        return WorldGuardUtil.getRegionPermissionModel(player).mayClaim();
    }

    public boolean regionExists(World world, String id) {
        return WorldGuardUtil.getGlobalRegionManager(world).hasRegion(id);
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
        ProtectedRegion existing = mgr.getRegionExact(id);

        // Check for an existing region
        if (existing != null) {
            if (!existing.getOwners().contains(WorldGuardUtil.getLocalPlayer(placer))) {
                if (feedback) {
                    placer.sendMessage(ChatColor.RED + "This region already exists and you don't own it.");
                }
                return false;
            }
        }
        
        
        // We have to check whether this region violates the space of any other reion
        ApplicableRegionSet regions = mgr.getApplicableRegions(region);
        WorldConfiguration wcfg = WorldGuardUtil.getWorldGuard().getGlobalStateManager().get(world);
        
        // Check if this region overlaps any other region
        if (regions.size() > 0) {
            if (!regions.isOwnerOfAll(WorldGuardUtil.getLocalPlayer(placer))) {
                placer.sendMessage(ChatColor.RED + "This region overlaps with someone else's region.");
                return false;
            }
        } else {
            if (wcfg.claimOnlyInsideExistingRegions) {
               if(feedback) {
               placer.sendMessage(ChatColor.RED + "You may only claim regions inside " +
                        "existing regions that you or your group own.");
               }
               return false;
            }
        }
        
                // Check whether the player has created too many regions
        if (!permModel.mayClaimRegionsUnbounded()) {
            int maxRegionCount = wcfg.getMaxRegionCount(placer);
            if (maxRegionCount >= 0
                    && mgr.getRegionCountOfPlayer(WorldGuardUtil.getLocalPlayer(placer)) >= maxRegionCount) {
                if(feedback) {
                placer.sendMessage("You own too many regions, delete one first to claim a new one.");
                } 
                return false;
            }
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
