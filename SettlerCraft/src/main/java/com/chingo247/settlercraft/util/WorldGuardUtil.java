/*
 * Copyright (C) 2014 Chingo247
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
package com.chingo247.settlercraft.util;

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
    
    private WorldGuardUtil(){}

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
    
     /**
     * Checks wheter the region exists.
     * @param world The world
     * @param id The region's id
     * @return True if regions exists, otherwise false.
     */
    public static boolean regionExists(World world, String id) {
        return getGlobalRegionManager(world).hasRegion(id);
    }
    
    public static boolean mayClaim(Player player) {
        RegionPermissionModel permissionModel = WorldGuardUtil.getRegionPermissionModel(player);
        return permissionModel.mayClaim();
    }
    
    public static boolean canClaim(Player player) {
        WorldConfiguration wcfg = WorldGuardUtil.getWorldGuard().getGlobalStateManager().get(player.getWorld());
        RegionPermissionModel permissionModel = WorldGuardUtil.getRegionPermissionModel(player);
        RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(player.getWorld());

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
