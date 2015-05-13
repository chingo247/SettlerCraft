/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.platforms.bukkit.structure.restriction;

import com.chingo247.settlercraft.structureapi.platforms.bukkit.util.WorldGuardUtil;
import com.chingo247.settlercraft.structureapi.structure.restriction.StructureRestriction;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.Bukkit;

/**
 *
 * @author Chingo
 */
public class WorldGuardRestriction extends StructureRestriction {

    public WorldGuardRestriction() {
        super("worldguard", "worldguard.region.overlap", "Structure overlaps a worldguard region you don't own");
    }

    @Override
    public boolean evaluate(Player whoPlaces, World world, CuboidRegion affectedArea) {
        LocalPlayer localPlayer = null;
        if (whoPlaces != null) {
            localPlayer = WorldGuardUtil.getLocalPlayer(Bukkit.getPlayer(whoPlaces.getUniqueId()));
        }

        RegionManager mgr = WorldGuardUtil.getRegionManager(Bukkit.getWorld(world.getName()));

        Vector p1 = affectedArea.getMinimumPoint();
        Vector p2 = affectedArea.getMaximumPoint();
        ProtectedCuboidRegion dummy = new ProtectedCuboidRegion("DUMMY", new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));
        ApplicableRegionSet regions = mgr.getApplicableRegions(dummy);

        // Check if this region getOverlapping any other region
        if (regions.size() > 0) {
            if (localPlayer == null) {
                return false;
            }

            if (!regions.isOwnerOfAll(localPlayer)) {
                return false;
            }
        }
        return true;
    }
    
}
