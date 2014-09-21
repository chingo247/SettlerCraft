/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.structure;

import com.sc.module.structureapi.persistence.AbstractService;
import com.sc.module.structureapi.persistence.HibernateUtil;
import com.sc.module.structureapi.persistence.StructureService;
import com.sc.module.structureapi.structure.construction.ConstructionEntry;
import com.sc.module.structureapi.structure.construction.ConstructionManager;
import com.sc.module.structureapi.structure.plan.StructurePlan;
import com.sc.module.structureapi.structure.schematic.Schematic;
import com.sc.module.structureapi.structure.schematic.SchematicManager;
import static com.sc.module.structureapi.util.SchematicUtil.calculateDimension;
import com.sc.module.structureapi.util.WorldGuardUtil;
import com.sc.module.structureapi.world.Cardinal;
import com.sc.module.structureapi.world.Dimension;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import construction.exception.ConstructionException;
import construction.exception.StructureException;
import construction.exception.StructurePlanException;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
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
    private static final String MAIN_PLUGIN_NAME = "SettlerCraft";
    private static final Plugin MAIN_PLUGIN = Bukkit.getPluginManager().getPlugin(MAIN_PLUGIN_NAME);

    private StructureAPI() {
    }
    
    public static Plugin getPlugin() {
        return MAIN_PLUGIN;
    }

    /**
     * Creates a structure.
     *
     * @param plan The structureplan
     * @param world The world
     * @param pos The position
     * @param cardinal The cardinal / direction
     * @return The structure that was placed
     */
    public static Structure create(StructurePlan plan, World world, Vector pos, Cardinal cardinal) {
        return create(null, plan, world, pos, cardinal);
    }

    /**
     * Creates a structure
     *
     * @param player The player
     * @param plan The structureplan
     * @param world The world
     * @param pos The position
     * @param cardinal The cardinal / direction
     * @return The structure that was placed
     */
    public static Structure create(Player player, StructurePlan plan, World world, Vector pos, Cardinal cardinal) {

        // Retrieve schematic
        Schematic schematic;
        try {
            schematic = SchematicManager.getInstance().getSchematic(plan.getSchematic());
        } catch (DataException | IOException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            return null;
        }
        

        // Check if structure overlaps another structure
        if (overlaps(schematic, world, pos, cardinal)) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + " Structure overlaps another structure");
            }
            return null;
        }

        // Create structure
        Structure structure = new Structure(world, pos, cardinal, schematic);
        structure.setName(plan.getName() == null ? "Structure #" + structure.getId() : plan.getName());
        structure.setPrice(plan.getPrice());

        // Save structure
        StructureService ss = new StructureService();
        structure.setStructureRegionId(PREFIX + structure.getId());
        structure = ss.save(structure);
        if (player != null) {
            makeOwner(player, structure);
        }

        ProtectedRegion structureRegion = claimGround(player, structure, schematic);
        if (structureRegion == null) {
            ss.delete(structure);
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Failed to claim region for structure");
            } else {
                System.out.println("[SettlerCraft]: Failed to claim region for structure");
            }

            return null;
        }
        
        

        try {
            createDataFolder(structure, plan);
        } catch (StructureException | IOException ex) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Couldn't copy data for structure");
            }
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            return null;
        }
        
        StructureHologramManager.getInstance().createHologram(Bukkit.getPluginManager().getPlugin(MAIN_PLUGIN_NAME), structure);
        
        
        return structure;
    }

    /**
     * Builds a structure
     * @param uuid The uuid to backtrack this construction process
     * @param structure The structure
     * @return true if succesfully started
     */
    public static boolean build(UUID uuid, Structure structure) {
        Player player = Bukkit.getPlayer(uuid);
        
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        
        if (player != null && !structure.isOwner(player)) {
                player.sendMessage(ChatColor.RED + "You have don't own this structure");
            return false;
        }

        try {
            ConstructionManager.getInstance().build(uuid, structure);
        } catch (ConstructionException ex) {
            if(player != null) {
                player.sendMessage(ex.getMessage());
            } 
            
            return false;
        } catch (StructurePlanException | IOException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        
        return true;
    }
    
    public static boolean build(Player player, Structure structure) {
        return build(player.getUniqueId(), structure);
    }
    
    public static boolean demolish(UUID uuid, Structure structure) {
         Player player = Bukkit.getPlayer(uuid);
        
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        if (player != null && !structure.isOwner(player)) {
            player.sendMessage(ChatColor.RED + "You have no permission to manage task #" + ChatColor.GOLD + structure.getId());
            return false;
        }

        try {
            ConstructionManager.getInstance().demolish(uuid, structure);
        } catch (ConstructionException ex) {
            if(player != null) {
                player.sendMessage(ex.getMessage());
            }
            return false;
        } catch (StructurePlanException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return true;
    }

    public static boolean demolish(Player player, Structure structure) {
        return demolish(player.getUniqueId(), structure);
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
            ConstructionManager.getInstance().stop(structure);
        } catch (ConstructionException ex) {
            player.sendMessage(ex.getMessage());
            return false;
        }
        return true;
    }
    
    public static boolean stop(Structure structure) throws ConstructionException {
         if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        ConstructionManager.getInstance().stop(structure);
        
        return true;
    }

    public static boolean delay(Plugin plugin, Player player, Structure structure) {
        return delay(plugin, player.getUniqueId(), structure);
    }
    
    public static boolean delay(Plugin plugin, UUID uuid, Structure structure) {
        Player player = Bukkit.getPlayer(uuid);
            
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        if (player != null  && !structure.isOwner(player)) {
            player.sendMessage(ChatColor.RED + "You have no permission to manage task #" + ChatColor.GOLD + structure.getId());
            return false;
        }

        try {
            long siteId = structure.getId();
            
            ConstructionEntry entry = ConstructionManager.getInstance().getEntry(siteId);
            // Structure was never tasked
            if (entry == null) {
                throw new ConstructionException("#" + siteId + " hasn't been tasked yet");
            }
            boolean demolishing = entry.isDemolishing();
            ConstructionManager.getInstance().stop(structure);

            if (demolishing) {
                ConstructionManager.getInstance().demolish(uuid, structure);
            } else {
                ConstructionManager.getInstance().build(uuid, structure);
            }
        } catch (ConstructionException ex) {
            if(player != null) {
                player.sendMessage(ex.getMessage());
            }
            return false;
        } catch (StructurePlanException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return true;
    }

    private static void createDataFolder(Structure structure, StructurePlan plan) throws StructureException, IOException {
        final String WORLD = structure.getWorldName();
        final long STRUCTURE_ID = structure.getId();

        final File STRUCTURE_DIR = new File(getDataFolder(), WORLD + "//" + STRUCTURE_ID);
        if (!STRUCTURE_DIR.exists()) {
            STRUCTURE_DIR.mkdirs();
        }

        File config = plan.getConfig();
        File schematic = plan.getSchematic();

        FileUtils.copyFile(config, new File(STRUCTURE_DIR, "Config.xml"));
        FileUtils.copyFile(schematic, new File(STRUCTURE_DIR, schematic.getName()));
    }

    public static final File getStructureDataFolder(Structure structure) {
        return new File(getDataFolder(), structure.getWorldName() + "//" + structure.getId());
    }

    private static Plugin getMainPlugin() {
        return Bukkit.getPluginManager().getPlugin(MAIN_PLUGIN_NAME);
    }

    public static final File getDataFolder() {
        File structureDirectory = new File(getMainPlugin().getDataFolder(), "Data//Structures");
        if (!structureDirectory.exists()) {
            structureDirectory.mkdirs();
        }
        return structureDirectory;
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
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Structure overlaps an regions owned other players");
            }
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

    public static boolean overlaps(Schematic schematic, World world, Vector pos, Cardinal cardinal) {
        Dimension dimension = calculateDimension(schematic, pos, cardinal);
        StructureService service = new StructureService();
        return !service.getStructuresWithinDimension(world, dimension).isEmpty();
    }

    public static boolean overlapsRegion(Schematic schematic, World world, Vector pos, Cardinal cardinal) {
        return overlapsRegion(null, schematic, world, pos, cardinal);
    }

    public static boolean overlapsRegion(Player player, Schematic schematic, World world, Vector pos, Cardinal cardinal) {
        LocalPlayer localPlayer = null;
        if (player != null) {
            localPlayer = WorldGuardUtil.getLocalPlayer(player);
        }
        RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(world.getName()));
        Dimension dimension = calculateDimension(schematic, pos, cardinal);

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
