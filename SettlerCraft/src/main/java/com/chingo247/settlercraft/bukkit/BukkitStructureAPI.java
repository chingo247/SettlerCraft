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
package com.chingo247.settlercraft.bukkit;

import com.chingo247.settlercraft.bukkit.plan.holograms.StructureHologramManager;
import com.chingo247.settlercraft.bukkit.plan.overviews.StructureOverviewManager;
import com.chingo247.settlercraft.structure.AbstractStructureAPI;
import com.chingo247.settlercraft.structure.PlayerOwnership;
import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.structure.StructureAPI;
import com.chingo247.settlercraft.structure.construction.BuildOptions;
import com.chingo247.settlercraft.structure.construction.DemolitionOptions;
import com.chingo247.settlercraft.structure.exception.StructureException;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.util.WorldGuardUtil;
import com.chingo247.settlercraft.structure.world.Dimension;
import com.chingo247.settlercraft.structure.world.Direction;
import com.chingo247.xcore.core.APlatform;
import com.chingo247.xcore.platforms.bukkit.BukkitPlugin;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class BukkitStructureAPI extends AbstractStructureAPI<Player, World> {

    private final StructureAPI impl;
    private final SettlerCraftPlugin settlerCraft;
    private static final String PREFIX = "SCREG-";
    private final StructureOverviewManager structureOverviewManager;
    private final StructureHologramManager structureHologramManager;

    BukkitStructureAPI(SettlerCraftPlugin settlerCraft, ExecutorService executor, APlatform platform) {
        super(executor, platform, settlerCraft.getConfigProvider(), new BukkitPlugin(settlerCraft));
        this.impl = new StructureAPI(executor, platform, settlerCraft.getConfigProvider(), new BukkitPlugin(settlerCraft));
        this.settlerCraft = settlerCraft;
        this.structureHologramManager = new StructureHologramManager(settlerCraft, this);
        this.structureOverviewManager = new StructureOverviewManager(settlerCraft, this);
    }

    private com.sk89q.worldedit.world.World wrapWorld(World world) {
        if(world == null) {
            return null;
        }
        return WorldEditUtil.getWorld(world.getName());
    }

    private com.sk89q.worldedit.entity.Player wrapPlayer(Player player) {
        if (player == null) {
            return null;
        }
        return WorldEditUtil.wrapPlayer(player);
    }
    
    
    @Override
    public void initialize() {
        super.initialize();
        if (useHolograms() && Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null) {
            getEventBus().register(structureHologramManager);
            getEventBus().register(structureOverviewManager);

            structureHologramManager.init();
            structureOverviewManager.init();
        }
    }
    
    public void rollback(Player player, Structure structure, Date date) {
        structureTaskHandler.rollback(WorldEditUtil.wrapPlayer(player), structure, date);
    }

    public Structure create(StructurePlan plan, Location location, Direction direction) throws StructureException {
        return create(null, plan, location, direction);
    }

    public Structure create(Player player, StructurePlan plan, Location location, Direction direction) throws StructureException {
        return create(player, plan, location.getWorld(), new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ()), direction);
    }

    @Override
    public Structure create(StructurePlan plan, World world, Vector pos, Direction direction) throws StructureException {
        return create(null, plan, world, pos, direction);
    }

    @Override
    public Structure create(Player player, StructurePlan plan, World world, Vector pos, Direction direction) throws StructureException {
        Structure structure = impl.create(wrapPlayer(player), plan, wrapWorld(world), pos, direction);
        if (structure != null) {
            structure.setStructureRegionId(PREFIX + structure.getId());
            structure = structureDAO.save(structure);
        }
        return structure;
    }

    @Override
    public boolean build(Player player, Structure structure, BuildOptions options, boolean force) {
        return impl.build(wrapPlayer(player), structure, options, force);
    }

    @Override
    public boolean demolish(Player player, Structure structure, DemolitionOptions options, boolean force) {
        return impl.demolish(wrapPlayer(player), structure, options, force);
    }

    @Override
    public boolean stop(Player player, Structure structure) {
        return impl.stop(wrapPlayer(player), structure);
    }

    @Override
    public boolean makeOwner(Player player, PlayerOwnership.Type type, Structure structure) {
        if (!impl.makeOwner(wrapPlayer(player), type, structure)) {
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
                player.sendMessage("Something went wrong...");
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
                player.sendMessage("Something went wrong...");
                java.util.logging.Logger.getLogger(BukkitStructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean makeMember(Player player, Structure structure) {
        if (!impl.makeMember(wrapPlayer(player), structure)) {

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
                player.sendMessage("Something went wrong...");
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
                player.sendMessage("Something went wrong...");
                java.util.logging.Logger.getLogger(BukkitStructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean removeOwner(Player player, Structure structure) {
        if (!impl.removeOwner(wrapPlayer(player), structure)) {
            // WorldGuard
            RegionManager rmgr = WorldGuardUtil.getRegionManager(Bukkit.getWorld(structure.getWorldName()));
            ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
            if (region == null) {
                player.sendMessage("Something went wrong...");
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
                player.sendMessage("Something went wrong...");
                java.util.logging.Logger.getLogger(BukkitStructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean removeMember(Player player, Structure structure) {
        if (!impl.removeMember(wrapPlayer(player), structure)) {

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
                player.sendMessage("Something went wrong...");
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
                player.sendMessage("Something went wrong...");
                java.util.logging.Logger.getLogger(BukkitStructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean overlaps(World world, Dimension dimension) {
        return super.overlaps(world.getName(), dimension);
    }

    public boolean overlaps(Player player, World world, Dimension dimension) {
        if (overlaps(world.getName(), dimension)) {
            return true;
        } else {

            LocalPlayer localPlayer = WorldGuardUtil.getLocalPlayer(player);

            if (world == null) {
                throw new IllegalArgumentException("World was null");
            }

            RegionManager mgr = WorldGuardUtil.getRegionManager(world);

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
    }

    public HashMap<Flag, Object> getDefaultFlags() {
        return settlerCraft.getConfigProvider().getDefaultRegionFlags();
    }

    public boolean useHolograms() {
        return settlerCraft.getConfigProvider().useHolograms();
    }

    @Override
    public File getAPIFolder() {
        return settlerCraft.getDataFolder();
    }

    public boolean isOnStructure(Location location) {
        return getStructure(location) != null;
    }

    public Structure getStructure(Location location) {
        return structureDAO.getStructure(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

}
