
/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.structureapi.platforms.bukkit.util;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldConfiguration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.bukkit.permission.RegionPermissionModel;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class WorldGuardUtil {
    
    private WorldGuardUtil(){}

    public static WorldGuardPlugin getWorldGuard() {
        return WorldGuardPlugin.inst();
    }

    public static boolean overlaps(World world, BlockVector pos1, BlockVector pos2) {
        return getWorldGuard().getRegionManager(world)
                .getApplicableRegions(new ProtectedCuboidRegion(null, pos1, pos2)).size() > 0;
    }

    public static boolean hasRegion(World world, String id) {
        return getRegionManager(world).hasRegion(id);
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
                && getRegionManager(world).getRegionCountOfPlayer(getLocalPlayer(player)) >= maxRegionCount;
    }

    public static RegionManager getRegionManager(World world) {
        return WGBukkit.getRegionManager(world);
    }

    public static RegionPermissionModel getRegionPermissionModel(Player player) {
        return new RegionPermissionModel(getWorldGuard(), player);
    }
    
     /**
     * Checks wheter the region exists.
     * @param world The world
     * @param id The region's id
     * @return True if regions exists, otherwise false.
     */
    public static boolean regionExists(World world, String id) {
        return getRegionManager(world).hasRegion(id);
    }
    
    public static boolean mayClaim(Player player) {
        RegionPermissionModel permissionModel = WorldGuardUtil.getRegionPermissionModel(player);
        return permissionModel.mayClaim();
    }
    
    /**
     * Checks whether the player can claim a region within a world
     * @param player The player
     * @param world The world
     * @return True if player can claim, false if region count was exceeded or when there is no region manager for the specified world
     */
    public static boolean canClaim(Player player, World world) {
        WorldConfiguration wcfg = WorldGuardUtil.getWorldGuard().getGlobalStateManager().get(player.getWorld());
        RegionPermissionModel permissionModel = WorldGuardUtil.getRegionPermissionModel(player);
        RegionManager mgr = WorldGuardUtil.getWorldGuard().getRegionManager(world);
        if(mgr == null) {
            return false;
        }

        // Check whether the player has created too many regions
        if (!permissionModel.mayClaimRegionsUnbounded()) {
            int maxRegionCount = wcfg.getMaxRegionCount(player);
            if (maxRegionCount >= 0
                    && mgr.getRegionCountOfPlayer(WorldGuardUtil.getLocalPlayer(player)) >= maxRegionCount) {

                return false;
            }
        }
        return true;
    }
    
   

}
