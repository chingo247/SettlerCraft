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

import com.sc.api.structure.construction.flag.SCFlags;
import com.sc.api.structure.entity.Structure;
import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.entity.world.SimpleCardinal;
import com.sc.api.structure.entity.world.WorldDimension;
import com.sc.api.structure.util.plugins.SCWorldGuardUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionPermissionModel;
import com.sk89q.worldguard.bukkit.WorldConfiguration;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class ConstructionManager {
    
    private static final int INFINITE_BLOCKS = -1;

    /**
     * Aligns target clipboard to speficied direction, assuming that the initial state is pointed to
     * EAST (entrance to the west)
     *
     * @param clipboard
     * @param location
     * @param direction
     * @return The new target location
     */
    public static Location align(final CuboidClipboard clipboard, Location location, SimpleCardinal direction) {
        switch (direction) {
            case EAST:
                return location;
            case SOUTH:
                clipboard.rotate2D(90);
                return location.add(new BlockVector(-(clipboard.getWidth() - 1), 0, 0));
            case WEST:
                clipboard.rotate2D(180);
                return location.add(new BlockVector(-(clipboard.getWidth() - 1), 0, -(clipboard.getLength() - 1)));
            case NORTH:
                clipboard.rotate2D(270);
                return location.add(new BlockVector(0, 0, -(clipboard.getLength() - 1)));
            default:
                throw new AssertionError("unreachable");
        }
    }

    private static String getIdForStructure(Structure structure) {
        String s = String.valueOf("sc_s_" + (structure.getPlan().getDisplayName().replaceAll("\\s", "") + "_" + structure.getId())).toLowerCase();
        return s;
    }

    public static ProtectedRegion claimGround(final Player player, final Structure structure, final WorldDimension dimension) {
        if (structure.getId() == null) {
            throw new AssertionError("Structure id was null, save the structure instance first! (e.g. structure = structureService.save(structure)"); // Should only happen if the programmer forgets to save the instance before this
        }

        if (!ConstructionManager.canClaim(player)
                || !ConstructionManager.mayClaim(player)
                || ConstructionManager.overlapsStructure(structure)
                || ConstructionManager.overlapsUnowned(player, structure)) {
            return null;
        }

        RegionManager mgr = SCWorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));
        WorldDimension dim = dimension;
        Vector p1 = dim.getStart().getPosition();
        Vector p2 = dim.getEnd().getPosition();
        String id = getIdForStructure(structure);
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));

        // Set Flag
        region.setFlag(SCFlags.STRUCTURE, structure.getPlan().getDisplayName());
        region.getOwners().addPlayer(player.getName());
        try {
            mgr.addRegion(region);
            mgr.save();
        } catch (ProtectionDatabaseException ex) {
            Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return region;

    }

    public static ProtectedRegion claimGround(final Player player, final Structure structure) {
        return claimGround(player, structure, structure.getDimension());
    }

    public static boolean overlapsUnowned(Player player, Structure structure) {
        return overlapsUnowned(SCWorldGuardUtil.getLocalPlayer(player), structure);
    }

    public static boolean overlapsUnowned(Player player, StructurePlan plan, Location location, SimpleCardinal cardinal) {
        return overlapsUnowned(SCWorldGuardUtil.getLocalPlayer(player), plan, location, cardinal);
    }

    public static boolean overlapsUnowned(LocalPlayer player, Structure structure) {
        RegionManager mgr = SCWorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));
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
        RegionManager mgr = SCWorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));
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
        RegionPermissionModel permissionModel = SCWorldGuardUtil.getRegionPermissionModel(player);
        // Has permission to claim
        if (!permissionModel.mayClaim()) {
            return false;
        }
        return true;
    }

    public static boolean canClaim(Player player) {
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

    public static boolean regionExists(World world, String id) {
        return SCWorldGuardUtil.getGlobalRegionManager(world).hasRegion(id);
    }
    
   

//    public static boolean isWithinConstructionZone(Structure structure) {
//        return false;
//    }
//
//    public static boolean exceedsLimit(Player player, Structure structure) {
//        return false;
//    }

}
