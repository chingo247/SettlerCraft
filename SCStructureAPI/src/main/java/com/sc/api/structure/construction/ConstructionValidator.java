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
package com.sc.api.structure.construction;

import com.sc.api.structure.construction.builder.flag.SCFlags;
import com.sc.api.structure.model.Structure;
import com.sc.api.structure.model.plan.StructurePlan;
import com.sc.api.structure.model.world.SimpleCardinal;
import com.sc.api.structure.model.world.WorldDimension;
import com.sc.api.structure.util.plugins.WorldGuardUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionPermissionModel;
import com.sk89q.worldguard.bukkit.WorldConfiguration;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class ConstructionValidator {
    
    public static boolean overlapsUnowned(Player player, Structure structure) {
        return overlapsUnowned(WorldGuardUtil.getLocalPlayer(player), structure);
    }
    
    public static boolean overlapsUnowned(Player player, StructurePlan plan, Location location, SimpleCardinal cardinal) {
        return overlapsUnowned(WorldGuardUtil.getLocalPlayer(player), plan, location, cardinal);
    }
    
    public static boolean overlapsUnowned(LocalPlayer player, Structure structure) {
        RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));
        WorldDimension dim = structure.getDimension();
        Vector p1 = dim.getStart().getPosition();
        Vector p2 = dim.getEnd().getPosition();
        ProtectedCuboidRegion dummy = new ProtectedCuboidRegion("", new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));
        ApplicableRegionSet regions = mgr.getApplicableRegions(dummy);

        // Check if this region overlaps any other region
        if (regions.size() > 0) {
            if (!regions.isOwnerOfAll(player)) {
                return true;
            }
        }
        return false;
    }

    public static boolean overlapsUnowned(LocalPlayer player, StructurePlan plan, Location location, SimpleCardinal cardinal) {
        Structure structure = new Structure("", location, cardinal, plan);
        return overlapsUnowned(player, structure);
    }
    
    public static boolean overlapsStructure(Structure structure) {
        RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));
        WorldDimension dim = structure.getDimension();
        Vector p1 = dim.getStart().getPosition();
        Vector p2 = dim.getEnd().getPosition();
        ProtectedCuboidRegion dummy = new ProtectedCuboidRegion("", new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));
        ApplicableRegionSet regions = mgr.getApplicableRegions(dummy);
        
        for (ProtectedRegion r : regions) {
            if (r.getFlag(SCFlags.STRUCTURE) != null) {
                System.out.println(ChatColor.RED + "Structure overlaps another structure");
                return true;
            }
        }
        return false;
    }

    public static boolean overlapsStructure(StructurePlan plan, Location location, SimpleCardinal cardinal) {
        Structure structure = new Structure("", location, cardinal, plan);
        return overlapsStructure(structure);
    }

    public static boolean mayClaim(Player player) {
        RegionPermissionModel permissionModel = WorldGuardUtil.getRegionPermissionModel(player);
        // Has permission to claim
        if (!permissionModel.mayClaim()) {
            return false;
        }
        return true;
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
    
    public static boolean exists(World world, String id) { 
        return WorldGuardUtil.getGlobalRegionManager(world).hasRegion(id);
    }
    
    public static boolean isWithinConstructionZone(Structure structure) {
        return false;
    }
    
    public static boolean exceedsLimit(Player player, Structure structure) {
        return false;
    }

}
