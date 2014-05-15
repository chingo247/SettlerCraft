/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.util.plugins;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionPermissionModel;
import com.sk89q.worldguard.bukkit.WorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
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
    
    
    
    public static RegionManager getGlobalRegionManager(World world) {
        return getWorldGuard().getGlobalRegionManager().get(world);
    }
    
    public static RegionPermissionModel getRegionPermissionModel(Player player) {
        return new RegionPermissionModel(getWorldGuard(), player);
    }
    
 
    
}
