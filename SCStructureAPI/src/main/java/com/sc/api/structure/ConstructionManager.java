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
package com.sc.api.structure;

import com.sc.api.structure.construction.Flags.SCFlags;
import com.sc.api.structure.entity.Structure;
import com.sc.api.structure.entity.plan.StructurePlan;
import com.sc.api.structure.entity.world.SimpleCardinal;
import com.sc.api.structure.entity.world.WorldDimension;
import com.sc.api.structure.util.WorldUtil;
import com.sc.api.structure.util.plugins.SCWorldEditUtil;
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

    /**
     * Selects a region between two positions
     *
     * @param player The player to create an editSession
     * @param cardinal The cardinal direction
     * @param target The target location
     * @param cuboidClipboard The cuboidClipboard
     */
    public static void select(Player player, Location target, SimpleCardinal cardinal, CuboidClipboard cuboidClipboard) {
        Location pos2 = WorldUtil.calculateEndLocation(target, cardinal, cuboidClipboard);
        select(player, target, pos2);
    }

    /**
     * Selects a region between two points
     *
     * @param player The player to create an editsession
     * @param pos1 The first position
     * @param pos2 The secondary position
     */
    public static void select(Player player, Location pos1, Location pos2) {
        SCWorldEditUtil.selectClipboardArea(player, pos1, pos2);
    }

    public static void selectStructure(Player player, Structure structure) {
        Location pos2 = WorldUtil.calculateEndLocation(structure.getLocation(), structure.getCardinal(), structure.getPlan().getSchematic());
        select(player, structure.getLocation(), pos2);
    }

    public static void selectStructure(Player player, StructurePlan plan, Location location, SimpleCardinal cardinal) {
        Structure structure = new Structure("", location, cardinal, plan);
        selectStructure(player, structure);
    }

    private static String getIdForStructure(Structure structure) {
        String s = String.valueOf("sc_s_" + (structure.getPlan().getDisplayName().replaceAll("\\s", "") + "_" + structure.getId())).toLowerCase();
        return s;
    }

    public static ProtectedRegion claimGround(final Player player, final Structure structure) {
        if (structure.getId() == null) {
            throw new AssertionError("Save the structure instance first! (e.g. structure = structureService.save(structure)"); // Should only happen if the programmer forgets to save the instance before this
        }
        if (!ConstructionManager.canClaim(player)
                || !ConstructionManager.mayClaim(player)
                || ConstructionManager.overlapsStructure(structure)
                || ConstructionManager.overlapsUnowned(player, structure)) {
            return null;
        }

        RegionManager mgr = SCWorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));
        WorldDimension dim = structure.getDimension();
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

    public boolean createConstructionSite(String id, Player placer, World world, BlockVector pos1, BlockVector pos2, boolean feedback, boolean addSelf) {
        ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, pos1, pos2);
        RegionPermissionModel permModel = SCWorldGuardUtil.getRegionPermissionModel(placer);
        // Can't replace existing regions
        RegionManager mgr = SCWorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(world);
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
        } catch (ProtectionDatabaseException ex) {
            Logger.getLogger(ConstructionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;

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

    public static boolean exists(World world, String id) {
        return SCWorldGuardUtil.getGlobalRegionManager(world).hasRegion(id);
    }

    public static boolean isWithinConstructionZone(Structure structure) {
        return false;
    }

    public static boolean exceedsLimit(Player player, Structure structure) {
        return false;
    }

}
