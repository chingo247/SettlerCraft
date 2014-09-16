/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.structure;

import com.sc.module.structureapi.construction.ConstructionEntry;
import com.sc.module.structureapi.construction.ConstructionManager;
import com.sc.module.structureapi.persistence.AbstractService;
import com.sc.module.structureapi.persistence.HibernateUtil;
import com.sc.module.structureapi.persistence.StructureService;
import com.sc.module.structureapi.plan.Schematic;
import com.sc.module.structureapi.plan.SchematicManager;
import com.sc.module.structureapi.plan.StructurePlan;
import static com.sc.module.structureapi.util.SchematicUtil.overlaps;
import static com.sc.module.structureapi.util.SchematicUtil.overlapsRegion;
import com.sc.module.structureapi.util.WorldGuardUtil;
import com.sc.module.structureapi.world.Cardinal;
import com.sc.module.structureapi.world.Dimension;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import construction.exception.ConstructionException;
import java.io.IOException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Chingo
 */
public class StructureAPI {
    
    private static final String PREFIX = "SC_REG_";

    private StructureAPI() {
    }

    /**
     * Places a structure
     * @param plugin The plugin
     * @param player The player
     * @param plan The structureplan
     * @param world The world
     * @param pos The position
     * @param cardinal The cardinal / direction
     * @return The structure that was placed
     */
    public static Structure place(Plugin plugin, Player player, StructurePlan plan, World world, Vector pos, Cardinal cardinal) {
        // Retrieve schematic
        Schematic schematic;
        try {
            schematic = SchematicManager.getInstance().getSchematic(plan);
        } catch (IOException | DataException ex) {
            player.sendMessage(ChatColor.RED + "Invalid schematic");
            return null;
        }

        // Check if structure overlaps another structure
        if (overlaps(schematic, world, pos, cardinal)) {
            player.sendMessage(ChatColor.RED + " Structure overlaps another structure");
            return null;
        }
        
        Structure structure = new Structure(world, pos, cardinal, schematic);
        structure.setName(plan.getName() == null ? "Structure #" + structure.getId() : plan.getName());
        structure.setPrice(plan.getPrice());
        
        StructureService ss = new StructureService();
        structure.setStructureRegionId(PREFIX + structure.getId()); 
        structure = ss.save(structure);
        makeOwner(player, structure);

        ProtectedRegion structureRegion = claimGround(player, structure, schematic);
        if (structureRegion == null) {
            ss.delete(structure);
            player.sendMessage(ChatColor.RED + "Failed to claim region for structure");
            return null;
        }

        try {
            ConstructionManager.getInstance().build(plugin, player.getUniqueId(), structure.getConstructionSite());
        } catch (ConstructionException ex) {
            player.sendMessage(ex.getMessage());
        }
        
        
        StructureHologramManager.getInstance().createHologram(plugin, structure);

        return structure;
    }

 

    public static boolean build(Plugin plugin, Player player, Structure structure) {
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        if (!structure.isOwner(player)) {
            player.sendMessage(ChatColor.RED + "You have no permission to manage task #" + ChatColor.GOLD + structure.getId());
            return false;
        }

        try {
            getConstructionManager().build(plugin, player.getUniqueId(), structure.getConstructionSite());
        } catch (ConstructionException ex) {
            player.sendMessage(ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean demolish(Plugin plugin, Player player, Structure structure) {
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        if (!structure.isOwner(player)) {
            player.sendMessage(ChatColor.RED + "You have no permission to manage task #" + ChatColor.GOLD + structure.getId());
            return false;
        }

        try {
            getConstructionManager().demolish(plugin, player.getUniqueId(), structure.getConstructionSite());
        } catch (ConstructionException ex) {
            player.sendMessage(ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean stop(Player player, Structure structure) {
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        if (!structure.isOwner(player)) {
            player.sendMessage(ChatColor.RED + "You have no permission to manage task #" + ChatColor.GOLD + structure.getId());
            return false;
        }

        try {
            getConstructionManager().stop(structure.getConstructionSite());
        } catch (ConstructionException ex) {
            player.sendMessage(ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean delay(Plugin plugin, Player player, Structure structure) {
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        if (!structure.isOwner(player)) {
            player.sendMessage(ChatColor.RED + "You have no permission to manage task #" + ChatColor.GOLD + structure.getId());
            return false;
        }

        try {
            long siteId = structure.getConstructionSite().getId();
            ConstructionEntry entry = getConstructionManager().getEntry(siteId);
            // Structure was never tasked
            if (entry == null) {
                throw new ConstructionException("#" + siteId + " hasn't been tasked yet");
            }
            boolean demolishing = entry.isDemolishing();
            getConstructionManager().stop(structure.getConstructionSite());

            if (demolishing) {
                getConstructionManager().demolish(plugin, player.getUniqueId(), structure.getConstructionSite());
            } else {
                getConstructionManager().build(plugin, player.getUniqueId(), structure.getConstructionSite());
            }
        } catch (ConstructionException ex) {
            player.sendMessage(ex.getMessage());
            return false;
        }
        return true;
    }

    private static synchronized ProtectedRegion claimGround(Player player, final Structure structure, Schematic schematic) {
        if (structure.getId() == null) {
            // Sanity check
            throw new AssertionError("Structure id was null, save the structure instance first! (e.g. structure = structureService.save(structure)");
        }

        World world = Bukkit.getWorld(structure.getWorldName());
        Vector pos = structure.getPosition();
        Cardinal cardinal = structure.getCardinal();

        //
        if (overlapsRegion(player, schematic, world, pos, cardinal)) {
            player.sendMessage(ChatColor.RED + "Structure overlaps an regions owned other players");
            return null;
        }

        RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(structure.getLocation().getWorld().getName()));
        Dimension dim = structure.getDimension();
        Vector p1 = dim.getMinPosition();
        Vector p2 = dim.getMaxPosition();
        String id = structure.getStructureRegion();

        if (WorldGuardUtil.regionExists(world, id)) {
            mgr.removeRegion(id);
        }

        ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));

        // Set Flag
        // region.setFlags(ConfigProvider.getInstance().getDefaultFlags());
        region.getOwners().addPlayer(player.getName());
        mgr.addRegion(region);
        try {
            mgr.save();
        } catch (ProtectionDatabaseException ex) {
            Logger.getLogger(StructureAPI.class.getName()).log(Level.ERROR, null, ex);
        }

        return region;

    }

    public static ConstructionManager getConstructionManager() {
        return ConstructionManager.getInstance();
    }

    private static PlayerOwnership save(PlayerOwnership playerOwnership) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            playerOwnership = (PlayerOwnership) session.merge(playerOwnership);
            tx.commit();
        } catch (HibernateException e) {
            try {
                tx.rollback();
            } catch (HibernateException rbe) {
                java.util.logging.Logger.getLogger(AbstractService.class.getName()).log(java.util.logging.Level.SEVERE, "Couldn’t roll back transaction", rbe);
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return playerOwnership;
    }

    private static PlayerMembership save(PlayerMembership playerMembership) {
        Session session = null;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSession();
            tx = session.beginTransaction();
            playerMembership = (PlayerMembership) session.merge(playerMembership);
            tx.commit();
        } catch (HibernateException e) {
            try {
                tx.rollback();
            } catch (HibernateException rbe) {
                java.util.logging.Logger.getLogger(AbstractService.class.getName()).log(java.util.logging.Level.SEVERE, "Couldn’t roll back transaction", rbe);
            }
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return playerMembership;
    }

    public static void makeOwner(Player player, Structure structure) {
        StructureService service = new StructureService();
        PlayerOwnership ownership = new PlayerOwnership(player.getUniqueId(), structure);
        ownership = save(ownership);
        structure.addOwner(ownership);
        service.save(structure);
    }

    public static void makeMember(Player player, Structure structure) {
        StructureService service = new StructureService();
        PlayerMembership playerMembership = new PlayerMembership(player.getUniqueId(), structure);
        playerMembership = save(playerMembership);
        structure.addMember(playerMembership);
        service.save(structure);
    }

    public static void removeOwner(Player player, Structure structure) {
        StructureService service = new StructureService();
        PlayerOwnership ownership = new PlayerOwnership(player.getUniqueId(), structure);
        structure.removeOwner(ownership);
        service.save(structure);
    }

    public static void removeMember(Player player, Structure structure) {
        StructureService service = new StructureService();
        PlayerMembership playerMembership = new PlayerMembership(player.getUniqueId(), structure);
        structure.removeMember(playerMembership);
        service.save(structure);
    }

}
