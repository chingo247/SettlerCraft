/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.util.plugins;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionPermissionModel;
import com.sk89q.worldguard.bukkit.WorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class WorldGuardUtil {
    
    public static WorldGuardPlugin getWorldGuard() {
        return WorldGuardPlugin.inst();
    }
    
    public static boolean overlaps(World world, BlockVector pos1, BlockVector pos2) {
        return getWorldGuard().getRegionManager(world)
                .getApplicableRegions(new ProtectedCuboidRegion(null, pos1, pos2)).size() > 0;
    }
    
    public static boolean hasRegion(World world, String id) {
        return getGlobalRegionManager(world).hasRegion(id);
    }
    
    public static WorldConfiguration getWorldConfiguration(World world) {
        return getWorldGuard().getGlobalStateManager().get(world);
    }
    
    public static LocalPlayer getLocalPlayer(Player player) {
        return getWorldGuard().wrapPlayer(player);
    }
    
    public static boolean hasReachedMaxRegionCount(World world, Player player) {
        int maxRegionCount = getWorldConfiguration(world).getMaxRegionCount(player);
        return maxRegionCount >= 0
                    && getGlobalRegionManager(world).getRegionCountOfPlayer(getLocalPlayer(player)) >= maxRegionCount;
    }
    
    public static boolean claim(String id, Player claimer, World world, BlockVector pos1, BlockVector pos2, boolean ignoreRegionCount, boolean ingoreSelf) throws CommandException {
        LocalPlayer player = getLocalPlayer(claimer);
        RegionPermissionModel permModel = getRegionPermissionModel(claimer);
        String validID = validateRegionId(id, false);
        RegionManager mgr = getWorldGuard().getGlobalRegionManager().get(world);ProtectedCuboidRegion cuboidRegion = new ProtectedCuboidRegion(id, pos1, pos2);
        
        
        if(validID != null) {
            
            WorldConfiguration wcfg = getWorldGuard().getGlobalStateManager().get(world);
            if(mgr.hasRegion(validID)) {
                throw new IllegalArgumentException(
                    "That region already exists. Please choose a different name.");
            }
            
            
            // Check whether the player has created too many regions
        if (!permModel.mayClaimRegionsUnbounded()) {
            int maxRegionCount = wcfg.getMaxRegionCount(claimer);
            if (!ignoreRegionCount && (maxRegionCount >= 0
                    && mgr.getRegionCountOfPlayer(getLocalPlayer(claimer)) >= maxRegionCount)) {
                return false;
            }
        }
        
        ApplicableRegionSet regions = mgr.getApplicableRegions(cuboidRegion);
        if(regions.size() > 0) {
            if(!regions.isOwnerOfAll(player)) {
                return false;
            }
            
        }
        
            
        } 
            return false;
        

    }
    
    public static RegionManager getGlobalRegionManager(World world) {
        return getWorldGuard().getGlobalRegionManager().get(world);
    }
    
    public static RegionPermissionModel getRegionPermissionModel(Player player) {
        return new RegionPermissionModel(getWorldGuard(), player);
    }
    
    
        /**
     * Validate a region ID.
     * 
     * @param id the id
     * @param allowGlobal whether __global__ is allowed
     * @return the id given
     * @throws 
     */
    private static String validateRegionId(String id, boolean allowGlobal) {
        if (!ProtectedRegion.isValidId(id)) {
            throw new IllegalArgumentException(
                    "The region name of '" + id + "' contains characters that are not allowed.");
        }

        if (!allowGlobal && id.equalsIgnoreCase("__global__")) { // Sorry, no global
            throw new IllegalArgumentException(
                    "Sorry, you can't use __global__ here.");
        }
        
        return id;
    }
    
}
