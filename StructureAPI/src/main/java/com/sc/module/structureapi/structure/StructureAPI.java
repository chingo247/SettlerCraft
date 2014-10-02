/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.structure;

import com.mysema.query.jpa.hibernate.HibernateUpdateClause;
import com.sc.module.structureapi.event.structure.StructureConstructionEvent;
import com.sc.module.structureapi.listener.PlanListener;
import com.sc.module.structureapi.persistence.HibernateUtil;
import com.sc.module.structureapi.persistence.PlayerMembershipService;
import com.sc.module.structureapi.persistence.PlayerOwnershipService;
import com.sc.module.structureapi.persistence.StructureService;
import com.sc.module.structureapi.structure.dataplans.Nodes;
import com.sc.module.structureapi.structure.dataplans.holograms.StructureHologramManager;
import com.sc.module.structureapi.structure.dataplans.overview.StructureOverviewManager;
import com.sc.module.structureapi.structure.schematic.Schematic;
import com.sc.module.structureapi.structure.schematic.SchematicManager;
import com.sc.module.structureapi.util.SchematicUtil;
import com.sc.module.structureapi.util.WorldGuardUtil;
import com.sc.module.structureapi.world.Cardinal;
import com.sc.module.structureapi.world.Dimension;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;
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
import construction.exception.ConstructionException;
import construction.exception.StructureDataException;
import construction.exception.StructureException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class StructureAPI {

    private static final String PREFIX = "SCREG#";
    private static final String MAIN_PLUGIN_NAME = "SettlerCraft";
    private static final Plugin MAIN_PLUGIN = Bukkit.getPluginManager().getPlugin(MAIN_PLUGIN_NAME);
    private final List<StructureListener> structureListeners = new ArrayList<>();
    private static StructureAPI instance;
    private boolean initialized = false;

    private StructureAPI() {
        Bukkit.getPluginManager().registerEvents(new PlanListener(), MAIN_PLUGIN);
    }

    public synchronized void init() {
        if (!initialized) {
            setStates();
            boolean useHolograms = MAIN_PLUGIN.getConfig().getBoolean("structure.use-holograms");
            Plugin plugin = Bukkit.getPluginManager().getPlugin("HolographicDisplays");
            if (useHolograms && plugin != null && plugin.isEnabled()) {
                StructureOverviewManager overviewManager = new StructureOverviewManager();
                StructureHologramManager hologramManager = new StructureHologramManager();

                structureListeners.add(overviewManager);
                structureListeners.add(hologramManager);

                overviewManager.init();
                hologramManager.init();
            }
            initialized = true;
        }
    }

    private void setStates() {
        Session session = HibernateUtil.getSession();
        QStructure qs = QStructure.structure;

        new HibernateUpdateClause(session, qs).where(qs.state.ne(Structure.State.COMPLETE).and(qs.state.ne(Structure.State.REMOVED)))
                .set(qs.state, Structure.State.STOPPED)
                .execute();
        session.close();
    }

    public static Plugin getPlugin() {
        return MAIN_PLUGIN;
    }

    public void addListener(StructureListener listener) {
        structureListeners.add(listener);
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
     * @param player The player, which will also be added as an owner of the structure
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

        Dimension dimension = SchematicUtil.calculateDimension(schematic, pos, cardinal);

        // Check if structure overlapsStructures another structure
        if (overlapsStructures(world, dimension)) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Structure overlaps another structure");
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
            try {
                makeOwner(player, structure);
            } catch (StructureException ex) {
                java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
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

        ProtectedRegion structureRegion = claimGround(player, structure, dimension);
        if (structureRegion == null) {
            structure.getDataFolder().delete();
            ss.delete(structure);
            if (player != null) {
                player.sendMessage(ChatColor.RED + "Failed to claim region for structure");
            } else {
                System.out.println("[SettlerCraft]: Failed to claim region for structure");
            }

            return null;
        }

        for (StructureListener sl : StructureAPI.getInstance().structureListeners) {
            sl.onCreate(structure);
        }

        return structure;
    }

    public static StructureAPI getInstance() {
        if (instance == null) {
            instance = new StructureAPI();
        }

        return instance;
    }

    static void setState(final Structure structure, Structure.State newState) {
        if (structure.getState() != newState) {
            Bukkit.getPluginManager().callEvent(new StructureConstructionEvent(structure));
            for (StructureListener listener : getInstance().structureListeners) {
                listener.onStateChanged(structure, newState);
            }
            structure.setState(newState);
            new StructureService().save(structure);
        }
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

        if (player != null && !isOwner(player, structure)) {
            player.sendMessage(ChatColor.RED + "You have don't own this structure");
            return false;
        }

        try {
            ConstructionManager.getInstance().build(uuid, structure, false);
        } catch (ConstructionException ex) {
            if (player != null) {
                player.sendMessage(ex.getMessage());
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

        if (player != null && !isOwner(player, structure)) {
            player.sendMessage(ChatColor.RED + "You have no permission to manage task #" + ChatColor.GOLD + structure.getId());
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
     * @param player The player to register the demolision oredr (for asyncwordedit api calls)
     * @param structure The structure
     * @return True if succesfully started to build
     */
    public static boolean demolish(Player player, Structure structure) {
        return demolish(player.getUniqueId(), structure);
    }

    /**
     * Stops construction of this structure
     *
     * @param player The player to authorize the stop order
     * @param structure The structure
     * @return True if succesfully stopped
     */
    public static boolean stop(Player player, Structure structure) {
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        if (!isOwner(player, structure)) {
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

    public static void makeOwner(Player player, Structure structure) throws StructureException {
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure == null) {
            throw new AssertionError("Null structure");
        }
        PlayerOwnershipService service = new PlayerOwnershipService();

        if (service.isOwner(player, structure)) {
            throw new StructureException(ChatColor.RED + "Player: " + player.getName() + " is already owner");
        }

        service.save(new PlayerOwnership(player, structure));
    }

    /**
     * Checks if player is an owner of the structure
     *
     * @param player The player to check
     * @param structure The structure
     * @return True if player is an owner
     * @deprecated Use structure.isOwner() instead
     */
    @Deprecated
    public static boolean isOwner(Player player, Structure structure) {
        PlayerOwnershipService ownershipService = new PlayerOwnershipService();
        return ownershipService.isOwner(player, structure);
    }

    /**
     * Checks if player is a member of this structure
     *
     * @param player The player to check
     * @param structure The structure
     * @return True if player is an owner
     * @deprecated Use structure.isMember() instead
     */
    @Deprecated
    public static boolean isMember(Player player, Structure structure) {
        PlayerMembershipService membershipService = new PlayerMembershipService();
        return membershipService.isMember(player, structure);
    }

    private static void createDataFolder(Structure structure, StructurePlan plan) throws StructureException, IOException {
        final String WORLD = structure.getWorldName();
        final long STRUCTURE_ID = structure.getId();

        final File STRUCTURE_DIR = new File(getDataFolder(), WORLD + "//" + structure.getWorldUUID() + "//" + STRUCTURE_ID);
        if (!STRUCTURE_DIR.exists()) {
            STRUCTURE_DIR.mkdirs();
        }

        File config = plan.getConfigXML();
        File schematic = plan.getSchematic();

        FileUtils.copyFile(config, new File(STRUCTURE_DIR, "Config.xml"));
        FileUtils.copyFile(schematic, new File(STRUCTURE_DIR, schematic.getName()));
    }

    private static Plugin getMainPlugin() {
        return Bukkit.getPluginManager().getPlugin(MAIN_PLUGIN_NAME);
    }

    /**
     * Gets the datafolder for the StructureAPI or creates them if none exists
     *
     * @return The datafolder
     */
    public static final File getDataFolder() {
        File structureDirectory = new File(getMainPlugin().getDataFolder(), "Data//Structures");
        if (!structureDirectory.exists()) {
            structureDirectory.mkdirs();
        }
        return structureDirectory;
    }

    private static synchronized ProtectedRegion claimGround(Player player, final Structure structure, Dimension dimension) {
        if (structure.getId() == null) {
            // Sanity check
            throw new AssertionError("Structure id was null, save the structure instance first! (e.g. structure = structureService.save(structure)");
        }

        World world = Bukkit.getWorld(structure.getWorldName());
        if (overlapsRegion(player, world, dimension)) {
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
                    System.out.println("Error in config File: " + config.getAbsolutePath());
                    java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
            }

        } catch (DocumentException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
     * @return True if dimensio overlaps any region the player is not an owner of.
     */
    public static boolean overlapsRegion(Player player, World world, Dimension dimension) {
        LocalPlayer localPlayer = null;
        if (player != null) {
            localPlayer = WorldGuardUtil.getLocalPlayer(player);
        }
        RegionManager mgr = WorldGuardUtil.getWorldGuard().getGlobalRegionManager().get(Bukkit.getWorld(world.getName()));
//        Dimension dimension = calculateDimension(schematic, pos, cardinal);

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
            tellStatus(structure, Bukkit.getPlayer(ownership.getUUID()));
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
        switch (structure.getState()) {
            case BUILDING:
                statusString = ChatColor.YELLOW + "BUILDING: " + ChatColor.RESET + structure;
                break;
            case DEMOLISHING:
                statusString = ChatColor.YELLOW + "DEMOLISHING: " + ChatColor.RESET + structure;
                break;
            case COMPLETE:
                statusString = "Construction " + ChatColor.GREEN + "COMPLETE" + ChatColor.RESET + ": " + ChatColor.RESET + structure;
                break;
            case INITIALIZING:
                statusString = ChatColor.YELLOW + "INITIALIZING: " + ChatColor.RESET + structure;
                break;
            case LOADING_SCHEMATIC:
                statusString = ChatColor.YELLOW + "LOADING SCHEMATIC: " + ChatColor.RESET + structure;
                break;
            case PLACING_FENCE:
                statusString = ChatColor.YELLOW + "PLACING FENCE: " + ChatColor.RESET + structure;
                break;
            case QUEUED:
                statusString = ChatColor.YELLOW + "QUEUED: " + ChatColor.RESET + structure;
                break;
            case REMOVED:
                statusString = ChatColor.RED + "REMOVED: " + ChatColor.RESET + structure;
                break;
            case STOPPED:
                statusString = ChatColor.RED + "STOPPED: " + ChatColor.RESET + structure;
                break;
            default:
                throw new AssertionError("Unknown state: " + structure.getState());
        }
        player.sendMessage(statusString);
    }
    
    private class PluginListener implements Listener {

        @EventHandler
        public void onReload(PluginDisableEvent disableEvent) {
            if (disableEvent.getPlugin().getName().equals(StructureAPI.getPlugin().getName())) {
                HibernateUtil.getSessionFactory().close();
            }
        }

    }

}
