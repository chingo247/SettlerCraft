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

package com.chingo247.settlercraft.structure;

import com.chingo247.settlercraft.exception.ConstructionException;
import com.chingo247.settlercraft.exception.StructureDataException;
import com.chingo247.settlercraft.exception.StructureException;
import com.chingo247.settlercraft.persistence.service.PlayerOwnershipService;
import com.chingo247.settlercraft.persistence.service.StructureService;
import com.chingo247.settlercraft.plugin.ConfigProvider;
import com.chingo247.settlercraft.plugin.SettlerCraft;
import com.chingo247.settlercraft.structure.construction.ConstructionManager;
import com.chingo247.settlercraft.structure.entities.structure.PlayerOwnership;
import com.chingo247.settlercraft.structure.entities.structure.PlayerOwnership.Type;
import com.chingo247.settlercraft.structure.entities.structure.Structure;
import com.chingo247.settlercraft.structure.entities.structure.Structure.State;
import com.chingo247.settlercraft.structure.entities.world.Dimension;
import com.chingo247.settlercraft.structure.plan.data.Nodes;
import com.chingo247.settlercraft.util.WorldGuardUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 *
 * @author Chingo
 */
public class StructureAPI {

    private StructureAPI() {
    }

    /**
     * Builds a structure
     *
     * @param uuid The uuid to backtrack this construction process
     * @param structure The structure
     * @return true if succesfully started to build
     */
    public static boolean build(UUID uuid, Structure structure) {
        Player player = Bukkit.getPlayer(uuid);

        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        if (player != null && !player.isOp()  && !structure.isOwner(player, Type.FULL)) {
            player.sendMessage(ChatColor.RED + "You don't have FULL ownership of this structure");
            return false;
        }

        try {
            ConstructionManager.getInstance().build(uuid, structure, false);
        } catch (ConstructionException ex) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + ex.getMessage());
            }

            return false;
        } catch (StructureDataException | IOException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        return true;
    }

    /**
     * Builds a structure
     *
     * @param player The player that issues the build order
     * @param structure The structure to build
     * @return true if succesfully started to build
     */
    public static boolean build(Player player, Structure structure) {
        return build(player.getUniqueId(), structure);
    }

    /**
     * Demolishes a structure
     *
     * @param uuid The uuid to register the construction order (for asyncworldedit's api calls)
     * @param structure The structure
     * @return True if succesfully started to demolish the structure
     */
    public static boolean demolish(UUID uuid, Structure structure) {
        Player player = Bukkit.getPlayer(uuid);

        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        if (player != null && !player.isOp()  && !structure.isOwner(player, Type.FULL)) {
            player.sendMessage(ChatColor.RED + "You don't have FULL ownership of this structure");
            return false;
        }

        try {
            ConstructionManager.getInstance().demolish(uuid, structure, false);
        } catch (ConstructionException ex) {
            if (player != null) {
                player.sendMessage(ex.getMessage());
            }
            return false;
        } catch (StructureDataException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return true;
    }

    /**
     * Demolishes a structure
     *
     * @param player The player to register the demolision order (for AsyncWorldEdit API calls)
     * and authorize the order
     * @param structure The structure
     * @return True if successfully started to build
     */
    public static boolean demolish(Player player, Structure structure) {
        return demolish(player.getUniqueId(), structure);
    }

    /**
     * Stops construction of this structure
     *
     * @param player The player to authorize the stop order
     * @param structure The structure
     * @return True if successfully stopped
     */
    public static boolean stop(Player player, Structure structure) {
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        if (!player.isOp() && !structure.isOwner(player, Type.FULL)) {
            player.sendMessage(ChatColor.RED + "You don't have FULL ownership of this structure");
            return false;
        }

        try {
            ConstructionManager.getInstance().stop(structure);
        } catch (ConstructionException ex) {
            player.sendMessage(ex.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Stops construction of a structure (ignoring authorization)
     *
     * @param structure The structure
     * @throws ConstructionException if structure was removed or structure hasn't been tasked to
     * construct
     */
    public static void stop(Structure structure) throws ConstructionException {
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        ConstructionManager.getInstance().stop(structure);

    }

    public static void makeOwner(Player player, Type type, Structure structure) throws StructureException {
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure == null) {
            throw new AssertionError("Null structure");
        }
        StructureService service = new StructureService();

        if (structure.isOwner(player)) {
            throw new StructureException("Player: " + player.getName() + " is already owner");
        }
        
         // WorldGuard
        RegionManager rmgr =  WorldGuardUtil.getRegionManager(Bukkit.getWorld(structure.getWorldName()));
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if(region == null) {
            throw new StructureException(structure.stringValue() + ", doesnt have a region");
        }
        
        LocalPlayer lp = WorldGuardUtil.getLocalPlayer(player);
        if(!region.getOwners().contains(lp)) {
            region.getOwners().addPlayer(lp);
        }
        try {
            rmgr.save();
              // StructureAPI
            structure.addOwner(player, type);
            service.save(structure);
        } catch (ProtectionDatabaseException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public static void makeMember(Player player, Structure structure) throws StructureException {
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure == null) {
            throw new AssertionError("Null structure");
        }
        StructureService service = new StructureService();

        if (structure.isMember(player)) {
            throw new StructureException("Player: " + player.getName() + " is already owner");
        }
        
        // WorldGuard
        RegionManager rmgr =  WorldGuardUtil.getRegionManager(Bukkit.getWorld(structure.getWorldName()));
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if(region == null) {
            throw new StructureException(structure.stringValue() + ", doesnt have a region");
        }
        
        LocalPlayer lp = WorldGuardUtil.getLocalPlayer(player);
        if(!region.getMembers().contains(lp)) {
            region.getMembers().addPlayer(lp);
        }
        try {
            rmgr.save();
              // StructureAPI
            structure.addMember(player);
            service.save(structure);
        } catch (ProtectionDatabaseException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
      
    }

    public static void removeOwner(Player player, Structure structure) throws StructureException {
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure == null) {
            throw new AssertionError("Null structure");
        }
        StructureService service = new StructureService();

        if (structure.isOwner(player)) {
            throw new StructureException("Player: " + player.getName() + " is already owner");
        }
         // WorldGuard
        RegionManager rmgr =  WorldGuardUtil.getRegionManager(Bukkit.getWorld(structure.getWorldName()));
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if(region == null) {
            throw new StructureException(structure.stringValue() + ", doesnt have a region");
        }
        
        LocalPlayer lp = WorldGuardUtil.getLocalPlayer(player);
        if(!region.getOwners().contains(lp)) {
            region.getOwners().removePlayer(lp);
        }
        try {
            rmgr.save();
              // StructureAPI
            structure.removeOwner(player);
            service.save(structure);
        } catch (ProtectionDatabaseException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public static void removeMember(Player player, Structure structure) throws StructureException {
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure == null) {
            throw new AssertionError("Null structure");
        }
        StructureService service = new StructureService();

        if (structure.isMember(player)) {
            throw new StructureException(ChatColor.RED + "Player: " + player.getName() + " is already owner");
        }
         // WorldGuard
        RegionManager rmgr =  WorldGuardUtil.getRegionManager(Bukkit.getWorld(structure.getWorldName()));
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if(region == null) {
            throw new StructureException(structure + ", doesnt have a region");
        }
        
        LocalPlayer lp = WorldGuardUtil.getLocalPlayer(player);
        if(!region.getMembers().contains(lp)) {
            region.getMembers().removePlayer(lp);
        }
        try {
            rmgr.save();
              // StructureAPI
            structure.removeMember(player);
            service.save(structure);
        } catch (ProtectionDatabaseException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        service.save(structure);
    }

    public static synchronized ProtectedRegion claimGround(Player player, final Structure structure) {
        if (structure.getId() == null) {
            // Sanity check
            throw new AssertionError("Structure id was null, save the structure instance first! (e.g. structure = structureService.save(structure)");
        }

        Dimension dimension = structure.getDimension();
        World world = Bukkit.getWorld(structure.getWorldName());
        if (overlapsRegion(player, world, dimension)) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Structure overlaps an regions owned other players");
            }
            return null;
        }

        RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));
        Vector p1 = dimension.getMinPosition();
        Vector p2 = dimension.getMaxPosition();
        String id = structure.getStructureRegion();

        if (WorldGuardUtil.regionExists(world, id)) {
            mgr.removeRegion(id);
        }

        ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));

        // Set Flag
        File config = structure.getConfig();
        SAXReader reader = new SAXReader();
        try {
            Document d = reader.read(config);
            List<Node> nodes = d.selectNodes(Nodes.WORLDGUARD_FLAG_NODE);
            for (Node n : nodes) {
                Flag f = DefaultFlag.fuzzyMatchFlag(n.selectSingleNode("Name").getText());
                try {
                    Object v = f.parseInput(WorldGuardPlugin.inst(), Bukkit.getConsoleSender(), n.selectSingleNode("Value").getText());
                    region.setFlag(f, v);
                } catch (InvalidFlagFormat ex) {
                    System.out.println("Error in xml file: " + config.getAbsolutePath());
                    java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
            }

        } catch (DocumentException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        // Override by config
        for(Entry<Flag, Object> e : ConfigProvider.getInstance().getDefaultFlags().entrySet()) {
            
            region.setFlag(e.getKey(), e.getValue());
        }

        // region.setFlags(ConfigProvider.getInstance().getDefaultFlags());
        if (player != null) {
            region.getOwners().addPlayer(player.getName());
        }

        mgr.addRegion(region);
        try {
            mgr.save();
        } catch (ProtectionDatabaseException ex) {
            Logger.getLogger(StructureAPI.class.getName()).log(Level.ERROR, null, ex);
        }

        return region;

    }

    /**
     * Checks if the given dimension overlaps any structures.
     *
     * @param world The world
     * @param dimension The dimension
     * @return True if dimension overlaps any structure
     */
    public static boolean overlapsStructures(World world, Dimension dimension) {
        StructureService service = new StructureService();
        return service.hasStructuresWithin(world, dimension);
    }

    /**
     * Checks if the dimension overlaps any (WorldGuard) region
     *
     * @param world The world
     * @param dimension The dimension
     * @return True if dimension overlaps any region
     */
    public static boolean overlapsRegion(World world, Dimension dimension) {
        return overlapsRegion(null, world, dimension);
    }

    /**
     * Checks if the dimension overlaps any region which the target player does is not an owner of.
     *
     * @param player The player
     * @param world The world
     * @param dimension The dimension
     * @return True if dimension overlaps any region the player is not an owner of.
     */
    public static boolean overlapsRegion(Player player, World world, Dimension dimension) {
        LocalPlayer localPlayer = null;
        if (player != null) {
            localPlayer = WorldGuardUtil.getLocalPlayer(player);
        }
        RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(world.getName()));
//        Dimension dimension = calculateDimension(schematic, pos, direction);

        Vector p1 = dimension.getMinPosition();
        Vector p2 = dimension.getMaxPosition();
        ProtectedCuboidRegion dummy = new ProtectedCuboidRegion("", new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));
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

    /**
     * Sends the status of this structure to every online owner of this structure
     *
     * @param structure The structure
     */
    public static void yellStatus(Structure structure) {
        PlayerOwnershipService pos = new PlayerOwnershipService();
        for (PlayerOwnership ownership : pos.getOwners(structure)) {
            tellStatus(structure, Bukkit.getPlayer(ownership.getPlayerUUID()));
        }
    }

    /**
     * Sends the status of this structure to given player
     *
     * @param structure The structure
     * @param player The player to tell
     */
    public static void tellStatus(Structure structure, Player player) {
        if (player == null || !player.isOnline()) {
            return; // No effect
        }

        String statusString;
        State state = structure.getState();
        switch (state) {
            case BUILDING:
                statusString = ChatColor.GOLD + "BUILDING " + structure;
                break;
            case DEMOLISHING:
                statusString = ChatColor.GOLD + "DEMOLISHING " + structure;
                break;
            case COMPLETE:
                statusString = ChatColor.GREEN + "COMPLETE "  + structure;
                break;
            case INITIALIZING:
                statusString = ChatColor.DARK_PURPLE + "INITIALIZING " + structure;
                break;
            case LOADING_SCHEMATIC:
                statusString = ChatColor.DARK_PURPLE + "LOADING SCHEMATIC " + structure;
                break;
            case PLACING_FENCE:
                statusString = ChatColor.DARK_PURPLE + "PLACING FENCE " + structure;
                break;
            case QUEUED:
                statusString = ChatColor.DARK_PURPLE + "QUEUED " + structure ;
                break;
            case REMOVED:
                statusString = ChatColor.RED + "REMOVED " + structure;
                break;
            case STOPPED:
                statusString = ChatColor.RED + "STOPPED " + structure;
                break;
            default:
                statusString = state.name();
        }
        player.sendMessage(SettlerCraft.MSG_PREFIX + statusString);
    }

}
