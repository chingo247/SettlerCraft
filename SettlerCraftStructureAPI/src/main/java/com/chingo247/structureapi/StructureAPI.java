/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi;

import com.chingo247.structureapi.construction.BuildOptions;
import com.chingo247.structureapi.construction.ConstructionManager;
import com.chingo247.structureapi.construction.DemolitionOptions;
import com.chingo247.structureapi.construction.Pattern;
import com.chingo247.structureapi.construction.Pattern.Mode;
import com.chingo247.structureapi.event.StructureCreateEvent;
import com.chingo247.structureapi.exception.ConstructionException;
import com.chingo247.structureapi.exception.StructureDataException;
import com.chingo247.structureapi.exception.StructureException;
import com.chingo247.structureapi.persistence.service.PlayerOwnershipService;
import com.chingo247.structureapi.persistence.service.StructureService;
import com.chingo247.structureapi.plan.StructurePlan;
import com.chingo247.structureapi.plan.StructurePlanManager;
import com.chingo247.structureapi.plan.document.PlanDocument;
import com.chingo247.structureapi.plan.document.PlanDocumentGenerator;
import com.chingo247.structureapi.plan.document.PlanDocumentManager;
import com.chingo247.structureapi.plan.document.StructureDocumentManager;
import com.chingo247.structureapi.plan.holograms.StructureHologramManager;
import com.chingo247.structureapi.plan.overview.StructureOverviewManager;
import com.chingo247.structureapi.plan.schematic.Schematic;
import com.chingo247.structureapi.plan.schematic.SchematicManager;
import com.chingo247.structureapi.util.SchematicUtil;
import com.chingo247.structureapi.util.WorldGuardUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Provides 
 * @author Chingo
 */
public abstract class StructureAPI implements IStructureAPI {

    private static final String MSG_PREFIX = ChatColor.YELLOW + "[SettlerCraft]: " + ChatColor.RESET;
    private static final String PREFIX = "SCREG-";

    private final StructurePlanManager structurePlanManager;
    private final PlanDocumentManager planDocumentManager;
    private final StructureDocumentManager structureDocumentManager;
    private final StructureOverviewManager structureOverviewManager;
    private final StructureHologramManager structureHologramManager;
    private final SchematicManager schematicManager;
    private final ConstructionManager constructionManager;
    private final PlanDocumentGenerator planGenerator;
    private final Plugin plugin;
//    private final RollbackService rollbackService;

    public StructureAPI(Plugin plugin, ExecutorService executor) {
        this.plugin = plugin;
        this.structurePlanManager = new StructurePlanManager(this, executor);
        this.planDocumentManager = new PlanDocumentManager(this, executor);
        this.structureDocumentManager = new StructureDocumentManager(this, executor);
        this.schematicManager = new SchematicManager(this, executor);
        this.constructionManager = new ConstructionManager(this, executor);
        this.planGenerator = new PlanDocumentGenerator(this);
        this.structureHologramManager = new StructureHologramManager(this);
        this.structureOverviewManager = new StructureOverviewManager(this);
//        Plugin mPrism = Bukkit.getPluginManager().getPlugin("Prism");
//        if(mPrism != null) {
//            this.rollbackService = new PrismRollbackService((Prism) mPrism);
//        } else {
//            this.rollbackService = null;
//        }
    }
    
    public void initialize() {
        // Make dirs if needed
        getSchematicToPlanFolder().mkdirs();
        getPlanDataFolder().mkdirs();
        getStructureDataFolder().mkdirs();
        
        // Generate plans
        getPlanDocumentGenerator().generate(getSchematicToPlanFolder());
        
        // Load plan documents
        long start = System.currentTimeMillis();
        getPlanDocumentManager().loadDocuments();
        StructureAPI.print("Loaded " + String.valueOf(getPlanDocumentManager().getDocuments().size()) + " PlanDocuments in " + (System.currentTimeMillis() - start));
        
        // Load structure documents
        start = System.currentTimeMillis();
        getStructureDocumentManager().loadDocuments();
        StructureAPI.print("Loaded " + String.valueOf(getStructureDocumentManager().getDocuments().size()) + " StructureDocuments in " + (System.currentTimeMillis() - start));
        
        // Load StructurePlans
        start = System.currentTimeMillis();
        List<PlanDocument> planDocs = getPlanDocumentManager().getDocuments();
        getStructurePlanManager().load(planDocs);
        StructureAPI.print("Loaded " + String.valueOf(getStructurePlanManager().getPlans().size()) + " StructurePlans in " + (System.currentTimeMillis() - start));
        
        // Load schematics - Add schematic-data to Database
        StructureAPI.print("Loading schematic data...");
        getSchematicManager().load();
        
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
    public PlanDocumentGenerator getPlanDocumentGenerator() {
        return planGenerator;
    }

    @Override
    public StructurePlanManager getStructurePlanManager() {
        return structurePlanManager;
    }

    @Override
    public PlanDocumentManager getPlanDocumentManager() {
        return planDocumentManager;
    }

    @Override
    public StructureDocumentManager getStructureDocumentManager() {
        return structureDocumentManager;
    }

    @Override
    public SchematicManager getSchematicManager() {
        return schematicManager;
    }
    
    @Override
    public File getFolder(Structure structure) {
        return new File(getStructureDataFolder(), structure.getWorldName() + "//" + structure.getId());
    }
    
    @Override
    public File getStructurePlanFile(Structure structure) {
        return new File(getFolder(structure), "StructurePlan.xml");
    }

    @Override
    public Schematic getSchematic(Structure structure) throws StructureDataException, IOException, DataException {
        if (structure.getChecksum() == null) {
            StructurePlan plan = structurePlanManager.getPlan(structure);
            structure.setChecksum(plan.getChecksum());
            getStructureService().save(structure);
            return schematicManager.load(plan.getSchematic());
        }
        StructurePlan plan = structurePlanManager.getPlan(structure);
        return schematicManager.load(plan.getSchematic());
    }

    @Override
    public StructureService getStructureService() {
        return new StructureService();
    }


    @Override
    public Structure create(StructurePlan plan, World world, Vector pos, Direction direction) throws StructureException {
        return create(null, plan, world, pos, direction);
    }

    @Override
    public Structure create(Player player, StructurePlan plan, World world, Vector pos, Direction direction) throws StructureException {
        // Retrieve schematic
        Schematic schematic;
        try {
            schematic = schematicManager.load(plan.getSchematic());
        } catch (DataException | IOException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            return null;
        }

        Dimension dimension = SchematicUtil.calculateDimension(schematic, pos, direction);
        
        if(dimension.getMinY() < 0) {
            throw new StructureException("Can't place structures below y:0");
        } else if(dimension.getMaxY() > world.getMaxHeight()) {
            throw new StructureException("Can't place structurs above " + world.getMaxHeight() + " (World max height)");
        }
        
        // Check if structure overlapsStructures another structure
        if (overlapsStructures(world, dimension)) {
            throw new StructureException("Structure overlaps another structure");
        }
        
        // Create structure
        Structure structure = new Structure(world, pos, direction, schematic);
        structure.setName(plan.getName() == null ? "Structure #" + structure.getId() : plan.getName());
        structure.setRefundValue(plan.getPrice());

        // Save structure
        StructureService ss = new StructureService();
        structure = ss.save(structure); // Set ID
        
        
        structure.setStructureRegionId(PREFIX + structure.getId());
        
        

        try {
            final File STRUCTURE_DIR = getFolder(structure);
            if (!STRUCTURE_DIR.exists()) {
                STRUCTURE_DIR.mkdirs();
            }

            File config = plan.getConfig();
            File schematicFile = plan.getSchematic();

            FileUtils.copyFile(config, new File(STRUCTURE_DIR, "StructurePlan.xml"));
            FileUtils.copyFile(schematicFile, new File(STRUCTURE_DIR, schematicFile.getName()));
        } catch (IOException ex) {
            
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            throw new StructureDataException(ChatColor.RED + "Couldn't copy data for structure");
        }
        
        structureDocumentManager.register(structure);
        

        ProtectedRegion structureRegion = claimGround(player, structure);
        if (structureRegion == null) {
            getFolder(structure).delete();
            ss.delete(structure);
            throw new StructureException("Failed to claim region for structure");
        }

        
        if (player != null) {
            try {
                makeOwner(player, PlayerOwnership.Type.FULL, structure);
            } catch (StructureException ex) {
                java.util.logging.Logger.getLogger(Structure.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        structure.setState(Structure.State.CREATED);
        structure = ss.save(structure);

        Bukkit.getPluginManager().callEvent(new StructureCreateEvent(structure));
        return structure;
    }
    

    @Override
    public boolean build(UUID uuid, Structure structure, BuildOptions options, boolean force) {
        Player player = Bukkit.getPlayer(uuid);

        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        if (player != null && !player.isOp() && !structure.isOwner(player, PlayerOwnership.Type.FULL)) {
            player.sendMessage(ChatColor.RED + "You don't have FULL ownership of this structure");
            return false;
        }

        try {
            constructionManager.build(uuid, structure, options, force);
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


    @Override
    public boolean build(UUID uuid, Structure structure, boolean force) {
        BuildOptions options = new BuildOptions(false);
        options.setPattern(Pattern.getPattern(Mode.getMode(getBuildMode())));
        return build(uuid, structure, options, force);
    }


    @Override
    public boolean build(Player player, Structure structure, boolean force) {
        return build(player.getUniqueId(), structure, force);
    }


    @Override
    public boolean demolish(UUID uuid, Structure structure, DemolitionOptions options, boolean force) {
        Player player = Bukkit.getPlayer(uuid);

        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        if (player != null && !player.isOp() && !structure.isOwner(player, PlayerOwnership.Type.FULL)) {
            player.sendMessage(ChatColor.RED + "You don't have FULL ownership of this structure");
            return false;
        }

        try {
            constructionManager.demolish(uuid, structure, options, force);
        } catch (ConstructionException ex) {
            if (player != null) {
                player.sendMessage(ChatColor.RED + ex.getMessage());
            }

            return false;
        } catch (StructureDataException ex) {
            if(player != null) {
                player.sendMessage(ChatColor.RED + "Something went wrong...");
            }
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        return true;
    }
    

    @Override
    public boolean demolish(UUID uuid, Structure structure, boolean force) {
        DemolitionOptions options = new DemolitionOptions();
        options.setPattern(Pattern.getPattern(Mode.getMode(getDemolisionMode())));
        return demolish(uuid, structure, options, true);
    }


    @Override
    public boolean demolish(Player player, Structure structure, boolean force) {
        return demolish(player.getUniqueId(), structure, force);
    }


    @Override
    public boolean stop(Player player, Structure structure) {
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        if (!player.isOp() && !structure.isOwner(player, PlayerOwnership.Type.FULL)) {
            player.sendMessage(ChatColor.RED + "You don't have FULL ownership of this structure");
            return false;
        }

        try {
            constructionManager.stop(structure);
        } catch (ConstructionException ex) {
            player.sendMessage(ex.getMessage());
            return false;
        }
        return true;
    }
    


    @Override
    public void makeOwner(Player player, PlayerOwnership.Type type, Structure structure) throws StructureException {
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
        RegionManager rmgr = WorldGuardUtil.getRegionManager(Bukkit.getWorld(structure.getWorldName()));
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if (region == null) {
            throw new StructureException(structure.stringValue() + ", doesnt have a region");
        }

        LocalPlayer lp = WorldGuardUtil.getLocalPlayer(player);
        if (!region.getOwners().contains(lp)) {
            region.getOwners().addPlayer(lp);
        }
        try {
            rmgr.save();
            // StructureAPI
            structure.addOwner(player, type);
            service.save(structure);
        } catch (StorageException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }


    @Override
    public void makeMember(Player player, Structure structure) throws StructureException {
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
        RegionManager rmgr = WorldGuardUtil.getRegionManager(Bukkit.getWorld(structure.getWorldName()));
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if (region == null) {
            throw new StructureException(structure.stringValue() + ", doesnt have a region");
        }

        LocalPlayer lp = WorldGuardUtil.getLocalPlayer(player);
        if (!region.getMembers().contains(lp)) {
            region.getMembers().addPlayer(lp);
        }
        try {
            rmgr.save();
            // StructureAPI
            structure.addMember(player);
            service.save(structure);
        } catch (StorageException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

    }


    @Override
    public boolean removeOwner(Player player, Structure structure) throws StructureException {
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure == null) {
            throw new AssertionError("Null structure");
        }
        StructureService service = new StructureService();

        if (!structure.isOwner(player)) {
            return false;
        }
        // WorldGuard
        RegionManager rmgr = WorldGuardUtil.getRegionManager(Bukkit.getWorld(structure.getWorldName()));
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if (region == null) {
            throw new StructureException(structure.stringValue() + ", doesnt have a region");
        }

        LocalPlayer lp = WorldGuardUtil.getLocalPlayer(player);
        if (!region.getOwners().contains(lp)) {
            region.getOwners().removePlayer(lp);
        }
        try {
            rmgr.save();
            // StructureAPI
            structure.removeOwner(player);
            service.save(structure);
        } catch (StorageException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        return true;
    }


    @Override
    public boolean removeMember(Player player, Structure structure) throws StructureException {
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
        RegionManager rmgr = WorldGuardUtil.getRegionManager(Bukkit.getWorld(structure.getWorldName()));
        ProtectedRegion region = rmgr.getRegion(structure.getStructureRegion());
        if (region == null) {
            throw new StructureException(structure + ", doesnt have a region");
        }

        LocalPlayer lp = WorldGuardUtil.getLocalPlayer(player);
        if (!region.getMembers().contains(lp)) {
            region.getMembers().removePlayer(lp);
        }
        try {
            rmgr.save();
            // StructureAPI
            structure.removeMember(player);
            service.save(structure);
        } catch (StorageException ex) {
            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        service.save(structure);
        return true;
    }

    /**
     * Claims ground for target structure using the player to authorise the action
     *
     * @param player The player
     * @param structure The structure
     * @return The ProtectedRegion or null if claiming was unsuccessful
     */
    public synchronized ProtectedRegion claimGround(Player player, final Structure structure) {
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

        RegionManager mgr = WorldGuardUtil.getRegionManager(world);

        Vector p1 = dimension.getMinPosition();
        Vector p2 = dimension.getMaxPosition();
        String id = structure.getStructureRegion();

        if (WorldGuardUtil.regionExists(world, id)) {
            mgr.removeRegion(id);
        }

        ProtectedCuboidRegion region = new ProtectedCuboidRegion(id, new BlockVector(p1.getBlockX(), p1.getBlockY(), p1.getBlockZ()), new BlockVector(p2.getBlockX(), p2.getBlockY(), p2.getBlockZ()));

        // Set Flag
//        StructurePlan plan;
//        try {
//            plan = structurePlanManager.getPlan(structure);
//            for (StructureRegionFlag flag : plan.getRegionFlags()) {
//                region.setFlag(flag.getFlag(), flag.getValue());
//            }
//        } catch (StructureDataException | IOException ex) {
//            java.util.logging.Logger.getLogger(StructureAPI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }

        
//        region.setFlags(getDefaultFlags());
        
//        for(Entry<Flag, Object> e : getDefaultFlags().entrySet()) {
//            region.setFlag(e.getKey(), e.getValue());
//        }
        

        
        // region.setFlags(ConfigProvider.getInstance().getDefaultFlags());
        if (player != null) {
            region.getOwners().addPlayer(player.getName());
        }
        mgr.addRegion(region);
        try {
            mgr.save();
        } catch (StorageException ex) {
            Logger.getLogger(StructureAPI.class.getName()).log(Level.ERROR, null, ex);
        }

        return region;

    }

    
    @Override
    public boolean overlapsStructures(World world, Dimension dimension) {
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
    @Override
    public boolean overlapsRegion(World world, Dimension dimension) {
        return overlapsRegion(null, world, dimension);
    }

    /**
     * Checks if the dimension overlaps any region which the target player does is not an own
     *
     * @param player The player
     * @param world The world
     * @param dimension The dimension
     * @return True if dimension overlaps any region the player is not an owner of.
     */
    @Override
    public boolean overlapsRegion(Player player, World world, Dimension dimension) {
        LocalPlayer localPlayer = null;
        if (player != null) {
            localPlayer = WorldGuardUtil.getLocalPlayer(player);
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

    /**
     * Sends the status of this structure to every online owner of this structure
     *
     * @param structure The structure
     */
    public void yellStatus(Structure structure) {
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
        Structure.State state = structure.getState();
        switch (state) {
            case BUILDING:
                statusString = ChatColor.GOLD + "BUILDING " + structure;
                break;
            case DEMOLISHING:
                statusString = ChatColor.GOLD + "DEMOLISHING " + structure;
                break;
            case COMPLETE:
                statusString = ChatColor.GREEN + "COMPLETE " + structure;
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
                statusString = ChatColor.DARK_PURPLE + "QUEUED " + structure;
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
        player.sendMessage(MSG_PREFIX + statusString);
    }

    public static void print(String... message) {
        for (int i = 0; i < message.length; i++) {
            message[i] = MSG_PREFIX + message[i];
        }
        Bukkit.getConsoleSender().sendMessage(message);
    }

    @Override
    public abstract HashMap<Flag, Object> getDefaultFlags();

    @Override
    public abstract int getBuildMode();

    @Override
    public abstract int getDemolisionMode();

    @Override
    public abstract boolean useHolograms();

    @Override
    public abstract double getRefundPercentage();

    @Override
    public abstract File getStructureDataFolder();

    @Override
    public abstract File getPlanDataFolder();
    
    @Override
    public abstract File getSchematicToPlanFolder();
    
    

}
