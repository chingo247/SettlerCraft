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
package com.chingo247.structureapi.bukkit;

import com.chingo247.structureapi.bukkit.holograms.holograms.StructureHologramManager;
import com.chingo247.structureapi.bukkit.holograms.overview.StructureOverviewManager;
import com.chingo247.structureapi.bukkit.listener.StructureListener;
import com.chingo247.structureapi.main.Dimension;
import com.chingo247.structureapi.main.PlayerOwnership;
import com.chingo247.structureapi.main.Structure;
import com.chingo247.structureapi.main.StructureAPI;
import com.chingo247.structureapi.main.util.WorldGuardUtil;
import com.chingo247.xcore.platforms.bukkit.BukkitPlatform;
import com.chingo247.xcore.platforms.bukkit.BukkitServer;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.io.File;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Chingo
 */
public class BukkitStructureAPI extends StructureAPI {
    
    private final StructureOverviewManager structureOverviewManager;
    private final StructureHologramManager structureHologramManager;
    private final Plugin plugin;
    private final IBukkitConfigProvider configProvider;

    public BukkitStructureAPI(Plugin plugin, IBukkitConfigProvider configProvider, ExecutorService executor) {
        super(executor, new BukkitPlatform(new BukkitServer(plugin.getServer())));
        this.configProvider = configProvider;
        this.plugin = plugin;
        this.structureHologramManager = new StructureHologramManager(this);
        this.structureOverviewManager = new StructureOverviewManager(this);
        Bukkit.getPluginManager().registerEvents(new StructureListener(this), plugin);
    }
    
    public void initialize() {
        super.initialize();
        
        if(useHolograms() && Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null) {
            Bukkit.getPluginManager().registerEvents(structureHologramManager, getPlugin());
            Bukkit.getPluginManager().registerEvents(structureOverviewManager, getPlugin());

            structureHologramManager.init();
            structureOverviewManager.init();
        }
        
    }
    
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public boolean makeOwner(Player player, PlayerOwnership.Type type, Structure structure)  {
        boolean alreadyOwner = !super.makeOwner(player, type, structure);
        if(alreadyOwner) {
            return false;
        }
        
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure == null) {
            throw new AssertionError("Null structure");
        }
        
        // WorldGuard
        RegionManager rmgr = WorldGuardUtil.getRegionManager(Bukkit.getWorld(structure.getWorldName()));
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if (region == null) {
            player.printError("Something went wrong...");
            throw new AssertionError(structure.stringValue() + ", doesnt have a region");
        }

        LocalPlayer lp = WorldGuardUtil.getLocalPlayer(Bukkit.getPlayer(player.getUniqueId()));
        if (!region.getOwners().contains(lp)) {
            region.getOwners().addPlayer(lp);
        } else {
            return false;
        }
        try {
            rmgr.save();
            return true;
        } catch (StorageException ex) {
            player.printError("Something went wrong...");
            java.util.logging.Logger.getLogger(BukkitStructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            return false;
        }
        
    }

    @Override
    public boolean makeMember(Player player, Structure structure) {
        boolean alreadyMember = super.makeMember(player, structure);
        if(alreadyMember) return false;
        
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        // WorldGuard
        RegionManager rmgr = WorldGuardUtil.getRegionManager(Bukkit.getWorld(structure.getWorldName()));
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if (region == null) {
            player.printError("Something went wrong...");
            throw new AssertionError(structure.stringValue() + ", doesnt have a region");
        }

        LocalPlayer lp = WorldGuardUtil.getLocalPlayer(Bukkit.getPlayer(player.getUniqueId()));
        if (!region.getMembers().contains(lp)) {
            region.getMembers().addPlayer(lp);
        }
        try {
            rmgr.save();
            return true;
        } catch (StorageException ex) {
            player.printError("Something went wrong...");
            java.util.logging.Logger.getLogger(BukkitStructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            return false;
        }
        
    }

    @Override
    public boolean removeOwner(Player player, Structure structure)  {
        super.removeOwner(player, structure);
        
        // WorldGuard
        RegionManager rmgr = WorldGuardUtil.getRegionManager(Bukkit.getWorld(structure.getWorldName()));
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if (region == null) {
            player.printError("Something went wrong...");
            throw new AssertionError(structure.stringValue() + ", doesnt have a region");
        }

        LocalPlayer lp = WorldGuardUtil.getLocalPlayer(Bukkit.getPlayer(player.getUniqueId()));
        if (region.getOwners().contains(lp)) {
            region.getOwners().removePlayer(lp);
        } else {
            return false;
        }
        try {
            rmgr.save();
            return true;
        } catch (StorageException ex) {
            player.printError("Something went wrong...");
            java.util.logging.Logger.getLogger(BukkitStructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean removeMember(Player player, Structure structure) {
        super.removeMember(player, structure);
        
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        // WorldGuard
        RegionManager rmgr = WorldGuardUtil.getRegionManager(Bukkit.getWorld(structure.getWorldName()));
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if (region == null) {
            player.printError("Something went wrong...");
            throw new AssertionError(structure + ", doesnt have a region");
        }

        LocalPlayer lp = WorldGuardUtil.getLocalPlayer(Bukkit.getPlayer(player.getUniqueId()));
        if (region.getMembers().contains(lp)) {
            region.getMembers().removePlayer(lp);
        } else {
            return false;
        }
        try {
            rmgr.save();
            return true;
        } catch (StorageException ex) {
            player.printError("Something went wrong...");
            java.util.logging.Logger.getLogger(BukkitStructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            return false;
        }
    }
    
    /**
     * Checks if the dimension overlaps any region which the target player does is not an own
     * @param localPlayer
     * @param world The world
     * @param dimension The dimension
     * @return True if dimension overlaps any region the player is not an owner of.
     */
    public boolean overlapsRegion(LocalPlayer localPlayer, String world, Dimension dimension) {
        World w = Bukkit.getWorld(world);
        if(w == null) {
            throw new IllegalArgumentException("World was null");
        }
        
        RegionManager mgr = WorldGuardUtil.getRegionManager(w);

        Vector p1 = dimension.getMinPosition();
        Vector p2 = dimension.getMaxPosition();
        ProtectedCuboidRegion dummy = new ProtectedCuboidRegion("DUMMY", new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));
        ApplicableRegionSet regions = mgr.getApplicableRegions(dummy);

        // Check if this region getOverlapping any other region
        if (regions.size() > 0) {
            if (localPlayer == null) {
                return true;
            }

            if (!regions.isOwnerOfAll(localPlayer)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public HashMap<Flag, Object> getDefaultFlags() {
        return configProvider.getDefaultRegionFlags();
    }

    @Override
    public int getBuildMode() {
        return configProvider.getBuildMode();
    }

    @Override
    public int getDemolisionMode() {
        return configProvider.getDemolisionMode();
    }

    @Override
    public boolean useHolograms() {
        return configProvider.useHolgrams();
    }

    @Override
    public double getRefundPercentage() {
        return configProvider.getRefundPercentage();
    }

    @Override
    public File getPluginFolder() {
        return plugin.getDataFolder();
    }

    @Override
    public boolean overlapsRegion(com.sk89q.worldedit.world.World world, Dimension dimension) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean overlapsRegion(Player player, com.sk89q.worldedit.world.World world, Dimension dimension) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
    
    
    
    
}
