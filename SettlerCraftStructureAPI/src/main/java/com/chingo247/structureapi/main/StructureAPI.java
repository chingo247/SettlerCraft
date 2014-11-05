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
package com.chingo247.structureapi.main;

import com.chingo247.structureapi.bukkit.event.StructureCreateEvent;
import com.chingo247.structureapi.main.Structure.State;
import com.chingo247.structureapi.main.construction.BuildOptions;
import com.chingo247.structureapi.main.construction.ConstructionManager;
import com.chingo247.structureapi.main.construction.DemolitionOptions;
import com.chingo247.structureapi.main.construction.Pattern;
import com.chingo247.structureapi.main.exception.ConstructionException;
import com.chingo247.structureapi.main.exception.StructureDataException;
import com.chingo247.structureapi.main.exception.StructureException;
import com.chingo247.structureapi.main.persistence.service.PlayerOwnershipService;
import com.chingo247.structureapi.main.persistence.service.StructureService;
import com.chingo247.structureapi.main.plan.StructurePlan;
import com.chingo247.structureapi.main.plan.StructurePlanManager;
import com.chingo247.structureapi.main.plan.document.PlanDocument;
import com.chingo247.structureapi.main.plan.document.PlanDocumentGenerator;
import com.chingo247.structureapi.main.plan.document.PlanDocumentManager;
import com.chingo247.structureapi.main.plan.document.StructureDocumentManager;
import com.chingo247.structureapi.main.plan.schematic.Schematic;
import com.chingo247.structureapi.main.plan.schematic.SchematicManager;
import com.chingo247.structureapi.main.util.SchematicUtil;
import com.chingo247.xcore.core.APlatform;
import com.chingo247.xcore.core.IPlayer;
import com.chingo247.xcore.util.ChatColors;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.flags.Flag;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;

/**
 *
 * @author Chingo
 */
public abstract class StructureAPI implements IStructureAPI {

    private static final String MSG_PREFIX = ChatColors.YELLOW + "[SettlerCraft]: " + ChatColors.RESET;
    private static final String PREFIX = "SCREG-";

    private final StructurePlanManager structurePlanManager;
    private final PlanDocumentManager planDocumentManager;
    private final StructureDocumentManager structureDocumentManager;
    private final SchematicManager schematicManager;
    private final PlanDocumentGenerator planGenerator;
    protected final StructureService structureService = new StructureService();
    private final APlatform platform;
    private final ConstructionManager constructionManager;

    public StructureAPI(ExecutorService executor, APlatform platform) {
        this.platform = platform;
        this.constructionManager = new ConstructionManager(this, executor);
        this.structurePlanManager = new StructurePlanManager(this, executor);
        this.planDocumentManager = new PlanDocumentManager(this, executor);
        this.structureDocumentManager = new StructureDocumentManager(this, executor);
        this.schematicManager = new SchematicManager(this, executor);
        this.planGenerator = new PlanDocumentGenerator(this);

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
        print("Loaded " + String.valueOf(getPlanDocumentManager().getDocuments().size()) + " PlanDocuments in " + (System.currentTimeMillis() - start));

        // Load structure documents
        start = System.currentTimeMillis();
        getStructureDocumentManager().loadDocuments();
        print("Loaded " + String.valueOf(getStructureDocumentManager().getDocuments().size()) + " StructureDocuments in " + (System.currentTimeMillis() - start));

        // Load StructurePlans
        start = System.currentTimeMillis();
        List<PlanDocument> planDocs = getPlanDocumentManager().getDocuments();
        getStructurePlanManager().load(planDocs);
        print("Loaded " + String.valueOf(getStructurePlanManager().getPlans().size()) + " StructurePlans in " + (System.currentTimeMillis() - start));

        // Load schematics - Add schematic-data to Database
        print("Loading schematic data...");
        getSchematicManager().load();
    }

    public APlatform getPlatform() {
        return platform;
    }

    public Structure setState(Structure structure, State newState) {
        if (structure.getState() != newState) {
            StructureService service = new StructureService();
            switch (newState) {
                case COMPLETE:
                    structure.getLog().setCompletedAt(new Date());
                    break;
                case REMOVED:
                    structure.getLog().setRemovedAt(new Date());
                    break;
                case BUILDING:
                case DEMOLISHING:
                case PLACING_FENCE:
                case QUEUED:
                    structure.getLog().setRemovedAt(null);
                    structure.getLog().setCompletedAt(null);
                    break;
                default:
                    break;
            }
            structure.setState(newState);
            structure = service.save(structure);
        }
        return structure;
    }

    public void print(String... message) {
        for (int i = 0; i < message.length; i++) {
            message[i] = MSG_PREFIX + message[i];
        }
        platform.getConsole().printMessage(message);
    }


    public PlanDocumentGenerator getPlanDocumentGenerator() {
        return planGenerator;
    }

    @Override
    public StructurePlanManager getStructurePlanManager() {
        return structurePlanManager;
    }

    public PlanDocumentManager getPlanDocumentManager() {
        return planDocumentManager;
    }

    public StructureDocumentManager getStructureDocumentManager() {
        return structureDocumentManager;
    }

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
    public Schematic getSchematic(Structure structure) throws Exception {
        if (structure.getChecksum() == null) {
            StructurePlan plan = structurePlanManager.getPlan(structure);
            structure.setChecksum(plan.getChecksum());
            structureService.save(structure);
            return schematicManager.load(plan.getSchematic());
        }
        StructurePlan plan = structurePlanManager.getPlan(structure);
        return schematicManager.load(plan.getSchematic());
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

        if (dimension.getMinY() < 0) {
            throw new StructureException("Can't place structures below y:0");
        } else if (dimension.getMaxY() > world.getMaxY()) {
            throw new StructureException("Can't place structurs above " + world.getMaxY() + " (World max height)");
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
            throw new StructureDataException(ChatColors.RED + "Couldn't copy data for structure");
        }

        structureDocumentManager.register(structure);

//        ProtectedRegion structureRegion = claimGround(player, structure);
//        if (structureRegion == null) {
//            getFolder(structure).delete();
//            ss.delete(structure);
//            throw new StructureException("Failed to claim region for structure");
//        }
        if (player != null) {
            makeOwner(player, PlayerOwnership.Type.FULL, structure);
        }
        structure.setState(Structure.State.CREATED);
        structure = ss.save(structure);

        Bukkit.getPluginManager().callEvent(new StructureCreateEvent(structure));
        return structure;
    }

    @Override
    public UUID build(Structure structure, BuildOptions options, boolean force) {
        UUID uuid = UUID.randomUUID();

        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        constructionManager.build(null, uuid, structure, options, force);

        return uuid;

    }

    @Override
    public UUID build(Structure structure, boolean force) {
        BuildOptions options = new BuildOptions(false);
        options.setPattern(Pattern.getPattern(Pattern.Mode.getMode(getBuildMode())));
        return build(structure, options, force);
    }

    @Override
    public boolean build(Player player, Structure structure, BuildOptions options, boolean force) {
        if (structure == null) {
            throw new AssertionError("Null structure");
        }
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }
        if (!structure.isOwner(player, PlayerOwnership.Type.FULL)) {
            player.print(ChatColors.RED + "You don't have FULL ownership of this structure");
            return false;
        }

        constructionManager.build(player, player.getUniqueId(), structure, options, force);
        return true;
    }

    @Override
    public boolean demolish(UUID uuid, Structure structure, DemolitionOptions options, boolean force) {
        IPlayer player = platform.getServer().getPlayer(uuid);

        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        constructionManager.demolish(null, uuid, structure, options, force);
        return true;
    }

    @Override
    public boolean demolish(UUID uuid, Structure structure, boolean force) {
        DemolitionOptions options = new DemolitionOptions();
        options.setPattern(Pattern.getPattern(Pattern.Mode.getMode(getDemolisionMode())));
        return demolish(uuid, structure, options, true);
    }

    @Override
    public boolean demolish(Player player, Structure structure, DemolitionOptions options, boolean force) {
        if (structure == null) {
            throw new AssertionError("Null structure");
        }
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }
        if (!structure.isOwner(player, PlayerOwnership.Type.FULL)) {
            player.print(ChatColors.RED + "You don't have FULL ownership of this structure");
            return false;
        }

        constructionManager.demolish(player, player.getUniqueId(), structure, options, force);
        return true;
    }

    @Override
    public boolean stop(Player player, Structure structure) {
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        if (!structure.isOwner(player, PlayerOwnership.Type.FULL)) {
            player.print(ChatColors.RED + "You don't have FULL ownership of this structure");
            return false;
        }

        try {
            constructionManager.stop(structure);
        } catch (ConstructionException ex) {
            player.print(ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean makeOwner(Player player, PlayerOwnership.Type type, Structure structure) {
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure == null) {
            throw new AssertionError("Null structure");
        }
        StructureService service = new StructureService();

        if (!structure.addOwner(player, type)) {
            return false;
        }
        service.save(structure);
        return true;
    }

    @Override
    public boolean makeMember(Player player, Structure structure) {
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure == null) {
            throw new AssertionError("Null structure");
        }
        StructureService service = new StructureService();

        if (!structure.addMember(player)) {
            return false;
        }
        service.save(structure);
        return true;
    }

    @Override
    public boolean removeOwner(Player player, Structure structure) {
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure == null) {
            throw new AssertionError("Null structure");
        }
        StructureService service = new StructureService();

        if (!structure.removeOwner(player)) {
            return false;
        }

        service.save(structure);
        return true;
    }

    @Override
    public boolean removeMember(Player player, Structure structure) {
        if (player == null) {
            throw new AssertionError("Null player");
        }
        if (structure == null) {
            throw new AssertionError("Null structure");
        }
        StructureService service = new StructureService();
        if (!structure.addMember(player)) {
            return false;
        }
        service.save(structure);
        return true;
    }

    public boolean delete(Structure structure) {
        if (structure.getState() == Structure.State.REMOVED) {
            return false;
        }

        structure.setState(Structure.State.REMOVED);
        structureService.save(structure);
        return true;
    }

    @Override
    public boolean overlapsStructures(World world, Dimension dimension) {
        StructureService service = new StructureService();
        return service.hasStructuresWithin(world.getName(), dimension);
    }

    /**
     * Sends the status of this structure to every online owner of this structure
     *
     * @param structure The structure
     */
    public void yellStatus(Structure structure) {
        PlayerOwnershipService pos = new PlayerOwnershipService();
        for (PlayerOwnership ownership : pos.getOwners(structure)) {
            tellStatus(structure, platform.getServer().getPlayer(ownership.getPlayerUUID()));
        }
    }

    /**
     * Sends the status of this structure to given player
     *
     * @param structure The structure
     * @param player The player to tell
     */
    public void tellStatus(Structure structure, IPlayer player) {
        if (player == null) {
            return; // No effect
        }

        String statusString;
        Structure.State state = structure.getState();
        switch (state) {
            case BUILDING:
                statusString = ChatColors.GOLD + "BUILDING " + structure;
                break;
            case DEMOLISHING:
                statusString = ChatColors.GOLD + "DEMOLISHING " + structure;
                break;
            case COMPLETE:
                statusString = ChatColors.GREEN + "COMPLETE " + structure;
                break;
            case INITIALIZING:
                statusString = ChatColors.DARK_PURPLE + "INITIALIZING " + structure;
                break;
            case LOADING_SCHEMATIC:
                statusString = ChatColors.DARK_PURPLE + "LOADING SCHEMATIC " + structure;
                break;
            case PLACING_FENCE:
                statusString = ChatColors.DARK_PURPLE + "PLACING FENCE " + structure;
                break;
            case QUEUED:
                statusString = ChatColors.DARK_PURPLE + "QUEUED " + structure;
                break;
            case REMOVED:
                statusString = ChatColors.RED + "REMOVED " + structure;
                break;
            case STOPPED:
                statusString = ChatColors.RED + "STOPPED " + structure;
                break;
            default:
                statusString = state.name();
        }
        player.sendMessage(MSG_PREFIX + statusString);
    }
    
    public File getSchematicToPlanFolder() {
        return new File(getPluginFolder(), "SchematicToPlan");
    }
    
    public File getStructureDataFolder() {
        return new File(getPluginFolder(), "Structures");
    }
    
    @Override
    public File getPlanDataFolder() {
        return new File(getPluginFolder(), "StructurePlans");
    }

    public abstract HashMap<Flag, Object> getDefaultFlags();

    public abstract int getBuildMode();

    public abstract int getDemolisionMode();

    public abstract boolean useHolograms();

    public abstract double getRefundPercentage();

    public abstract File getPluginFolder();

}
