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
package com.chingo247.settlercraft.main.structure;

import com.chingo247.settlercraft.main.IConfigProvider;
import com.chingo247.settlercraft.main.event.EventManager;
import com.chingo247.settlercraft.main.event.StructureStateChangeEvent;
import com.chingo247.settlercraft.main.event.StructureSubscriber;
import com.chingo247.settlercraft.main.exception.ConstructionException;
import com.chingo247.settlercraft.main.exception.StructureException;
import com.chingo247.settlercraft.main.persistence.PlayerMembershipDAO;
import com.chingo247.settlercraft.main.persistence.PlayerOwnershipDAO;
import com.chingo247.settlercraft.main.persistence.StructureDAO;
import com.chingo247.settlercraft.main.structure.construction.BuildOptions;
import com.chingo247.settlercraft.main.structure.construction.ConstructionManager;
import com.chingo247.settlercraft.main.structure.construction.DemolitionOptions;
import com.chingo247.settlercraft.main.structure.construction.Pattern;
import com.chingo247.settlercraft.main.structure.plan.StructurePlan;
import com.chingo247.settlercraft.main.structure.plan.StructurePlanManager;
import com.chingo247.settlercraft.main.structure.plan.document.PlanDocument;
import com.chingo247.settlercraft.main.structure.plan.document.PlanDocumentGenerator;
import com.chingo247.settlercraft.main.structure.plan.document.PlanDocumentManager;
import com.chingo247.settlercraft.main.structure.plan.document.StructureDocumentManager;
import com.chingo247.settlercraft.main.structure.plan.schematic.Schematic;
import com.chingo247.settlercraft.main.structure.plan.schematic.SchematicManager;
import com.chingo247.settlercraft.main.world.Dimension;
import com.chingo247.settlercraft.main.world.Direction;
import com.chingo247.xcore.core.APlatform;
import com.chingo247.xcore.core.IPlayer;
import com.chingo247.xcore.util.ChatColors;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author Chingo
 * @param <P>
 * @param <W>
 */
public abstract class AbstractStructureAPI<P, W> {

    private static final String MSG_PREFIX = ChatColors.YELLOW + "[SettlerCraft]: " + ChatColors.RESET;
    private final StructurePlanManager structurePlanManager;
    private final PlanDocumentManager planDocumentManager;
    private final StructureDocumentManager structureDocumentManager;
    private final SchematicManager schematicManager;
    private final PlanDocumentGenerator planGenerator;
    private final EventBus eventBus;
    private final AsyncEventBus asyncEventBus;
    protected final EventManager evtm = EventManager.getInstance();
    protected final APlatform platform;
    protected final ConstructionManager constructionManager;
    protected final IConfigProvider configProvider;
    protected final StructureDAO structureDAO;
    protected final PlayerOwnershipDAO playerOwnershipDAO;
    protected final PlayerMembershipDAO playerMembershipDAO;
    

    public AbstractStructureAPI(ExecutorService executor, APlatform platform, IConfigProvider provider) {
        this.platform = platform;
        this.constructionManager = new ConstructionManager(this, executor);
        this.structurePlanManager = new StructurePlanManager(this, executor);
        this.planDocumentManager = new PlanDocumentManager(this, executor);
        this.structureDocumentManager = new StructureDocumentManager(this, executor);
        this.schematicManager = new SchematicManager(this, executor);
        this.planGenerator = new PlanDocumentGenerator(this);
        this.configProvider = provider;
        this.eventBus = new EventBus();
        this.asyncEventBus = new AsyncEventBus(executor);
        this.eventBus.register(new StructureSubscriber(this));
        this.structureDAO = new StructureDAO();
        this.playerMembershipDAO = new PlayerMembershipDAO();
        this.playerOwnershipDAO = new PlayerOwnershipDAO();
    }
    
    public EventBus getEventBus() {
        return EventManager.getInstance().getEventBus();
    }

    
    
    public void initialize() {
        // Make dirs if needed
        getSchematicToPlanFolder().mkdirs();
        getPlanDataFolder().mkdirs();
        getStructureDataFolder().mkdirs();

        // Generate plans
        getPlanGenerator().generate(getSchematicToPlanFolder());

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

    public PlanDocumentGenerator getPlanGenerator() {
        return planGenerator;
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

    public UUID build(Structure structure, boolean force) {
        BuildOptions options = new BuildOptions(false);
        options.setPattern(Pattern.getPattern(Pattern.Mode.getMode(getBuildMode())));
        return build(structure, options, force);
    }

    public boolean demolish(UUID uuid, Structure structure, DemolitionOptions options, boolean force) {
        if (structure == null) {
            throw new AssertionError("Null structure");
        }

        if (structure.getId() == null) {
            throw new AssertionError("structure is not a saved instance");
        }

        constructionManager.demolish(null, uuid, structure, options, force);
        return true;
    }

    public boolean demolish(UUID uuid, Structure structure, boolean force) {
        DemolitionOptions options = new DemolitionOptions();
        options.setPattern(Pattern.getPattern(Pattern.Mode.getMode(getDemolisionMode())));
        return demolish(uuid, structure, options, true);
    }

    public boolean stop(Structure structure) {
        if (structure == null) {
            throw new AssertionError();
        }
        try {
            constructionManager.stop(structure);
            return true;
        } catch (ConstructionException ex) {
            return false;
        }
    }

    public File getStructureDataFolder() {
        return new File(getAPIFolder(), "Structures");
    }

    public File getPlanDataFolder() {
        return new File(getAPIFolder(), "Plans");
    }

    public File getSchematicToPlanFolder() {
        return new File(getAPIFolder(), "SchematicToPlan");
    }

    public StructurePlanManager getStructurePlanManager() {
        return structurePlanManager;
    }

    public File getFolder(Structure structure) {
        return new File(getStructureDataFolder(), structure.getWorldName() + "//" + structure.getId());
    }

    public File getStructurePlanFile(Structure structure) {
        return new File(getFolder(structure), "StructurePlan.xml");
    }

    public Schematic getSchematic(Structure structure) throws Exception {
        if (structure.getChecksum() == null) {
            StructurePlan plan = structurePlanManager.getPlan(structure);
            structure.setChecksum(plan.getChecksum());
            structureDAO.save(structure);
            return schematicManager.load(plan.getSchematic());
        }
        StructurePlan plan = structurePlanManager.getPlan(structure);
        return schematicManager.load(plan.getSchematic());
    }

    public Structure setState(Structure structure, Structure.State newState) {
        if (structure.getState() != newState) {
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
            structure = structureDAO.save(structure);
            evtm.getEventBus().post(new StructureStateChangeEvent(structure));
        }
        return structure;
    }

    public int getBuildMode() {
        return configProvider.getBuildMode();
    }

    public int getDemolisionMode() {
        return configProvider.getDemolisionMode();
    }

    public double getRefundPercentage() {
        return configProvider.getRefundPercentage();
    }

    protected boolean overlaps(String world, Dimension dimension) {
        return structureDAO.overlaps(world, dimension);
    }

    public File getAPIFolder() {
        return new File(platform.getPluginsFolder(), "SettlerCraft");
    }

    /**
     * Sends the status of this structure to every online owner of this structure
     *
     * @param structure The structure
     */
    public void yellStatus(Structure structure) {
        for (PlayerOwnership ownership : playerOwnershipDAO.getOwners(structure)) {
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

    public void print(String... message) {
        for (int i = 0; i < message.length; i++) {
            message[i] = MSG_PREFIX + message[i];
        }
        platform.getConsole().printMessage(message);
    }

    /**
     * Creates a structure.
     * @param plan The StructurePlan
     * @param world The world
     * @param pos The position
     * @param direction The direction / direction
     * @return The structure or null if failed to claim the ground
     */
    public abstract Structure create(StructurePlan plan, W world, Vector pos, Direction direction) throws StructureException;

    /**
     * Creates a structure
     * @param player The player who places the structure, which will also be added as an owner of
     * the structure
     * @param plan The StructurePlan
     * @param world The world
     * @param pos The position
     * @param direction The direction / direction
     * @return The structure or null if failed to claim the ground
     */
    public abstract Structure create(P player, StructurePlan plan, W world, Vector pos, Direction direction) throws StructureException;

    /**
     * Starts construction of a structure
     * @param player The player that issues the build order
     * @param structure The structure to build
     * @param options
     * @param force
     * @return true if successfully started to build
     */
    public abstract boolean build(P player, Structure structure, BuildOptions options, boolean force);

    /**
     * Stops construction/demolishment of this structure
     * @param player The player to authorise the stop order
     * @param structure The structure
     * @return True if successfully stopped
     */
    public abstract boolean stop(P player, Structure structure);

    /**
     * Starts construction of a structure
     * @param player The player that issues the build order
     * @param structure The structure to build
     * @param options
     * @param force
     * @return true if successfully started to build
     */
    public abstract boolean demolish(P player, Structure structure, DemolitionOptions options, boolean force);

    /**
     * Adds the player as owner to this structure
     * @param player The player
     * @param type The owner type
     * @param structure The structure to add the player as owner
     * @return
     */
    public abstract boolean makeOwner(P player, PlayerOwnership.Type type, Structure structure);

    /**
     * Adds the player as member to this structure
     * @param player The player to add
     * @param structure The structure to add the player to
     * @return
     */
    public abstract boolean makeMember(P player, Structure structure);

    /**
     * Removes an owner of this structure
     * @param player The player to remove
     * @param structure The structure
     * @return if player was successfully removed
     */
    public abstract boolean removeOwner(P player, Structure structure);

    /**
     * Removes a member of this structure
     * @param player The player to remove
     * @param structure The structure
     * @return if player was successfully removed
     */
    public abstract boolean removeMember(P player, Structure structure);

}
