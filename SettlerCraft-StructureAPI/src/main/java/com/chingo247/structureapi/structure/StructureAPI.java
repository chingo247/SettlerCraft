/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.structure;

import com.chingo247.menuapi.menu.CategoryMenu;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.ILocation;
import com.chingo247.xplatform.core.IPlugin;
import com.chingo247.xplatform.core.IWorld;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.structureapi.world.Direction;
import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.settlercraft.core.exception.SettlerCraftException;
import com.chingo247.settlercraft.core.persistence.repository.world.WorldNode;
import com.chingo247.settlercraft.core.persistence.repository.world.WorldRepository;
import com.chingo247.settlercraft.core.regions.CuboidDimension;
import com.chingo247.settlercraft.core.util.KeyPool;
import com.chingo247.structureapi.exception.StructureAPIException;
import com.chingo247.structureapi.structure.exception.ElementValueException;
import com.chingo247.structureapi.structure.exception.StructureException;
import com.chingo247.structureapi.menu.StructurePlanMenuFactory;
import com.chingo247.structureapi.menu.StructurePlanMenuReader;
import com.chingo247.structureapi.persistence.repository.StructureNode;
import com.chingo247.structureapi.persistence.repository.StructureRepository;
import com.chingo247.structureapi.platforms.bukkit.IConfigProvider;
import com.chingo247.structureapi.structure.plan.placement.event.PlacementHandlerRegisterEvent;
import com.chingo247.structureapi.structure.plan.exception.PlacementException;
import com.chingo247.structureapi.structure.plan.placement.Placement;
import com.chingo247.structureapi.structure.plan.placement.handlers.PlacementHandler;
import com.chingo247.structureapi.structure.plan.placement.handlers.SchematicPlacementHandler;
import com.chingo247.structureapi.structure.plan.StructurePlan;
import com.chingo247.structureapi.structure.plan.StructurePlanManager;
import com.chingo247.structureapi.structure.plan.document.PlacementElement;
import com.chingo247.structureapi.structure.construction.asyncworldedit.AsyncPlacement;
import com.chingo247.structureapi.structure.construction.options.Options;
import com.chingo247.structureapi.structure.event.StructureCreateEvent;
import com.chingo247.structureapi.structure.plan.GlobalPlanManager;
import com.chingo247.structureapi.structure.plan.SubStructuredPlan;
import com.chingo247.structureapi.structure.plan.placement.PlaceOptions;
import com.chingo247.structureapi.structure.plan.placement.SchematicPlacement;
import com.chingo247.structureapi.util.PlacementUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.DocumentException;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;

/**
 *
 * @author Chingo
 */
public class StructureAPI {

    public static final String STRUCTURE_PLAN_FILE_NAME = "structureplan.xml";
    public static final String PLUGIN_NAME = "SettlerCraft";
    public static final String PLANS_DIRECTORY = "plans";
    private static final Map<String, Map<String, PlacementHandler>> handlers = Maps.newHashMap();

    private final StructureRepository structureRepository;
    private final WorldRepository worldRepository;

    private final APlatform platform;
    private IPlugin plugin;
    private IConfigProvider config;

    private final Lock loadLock = new ReentrantLock();
    private StructurePlanManager globalPlanManager;
    private StructurePlanMenuFactory planMenuFactory;
    private final ExecutorService executor;
    private final KeyPool<Long> pool;
    private CategoryMenu planMenu;

    private boolean isLoadingPlans = false;
    private static StructureAPI instance;

    private Map<Long, UUID> taskedBy = Maps.newHashMap();
    private Map<Long, Integer> jobIds = Maps.newHashMap();
    private final Lock jobLock = new ReentrantLock();

    private StructureAPI() {
        this.executor = SettlerCraft.getInstance().getExecutor();
        this.platform = SettlerCraft.getInstance().getPlatform();

        // Register Handlers first...
//        registerHandler(new GeneratedCuboidHandler());
//        registerHandler(new GeneratedCylinderHandler());
//        registerHandler(new GeneratedEllipsoidHandler());
//        registerHandler(new GeneratedPolygonal2DHandler());
        registerHandler(new SchematicPlacementHandler());
        this.pool = new KeyPool<>(executor);

        // Now register the GlobalPlanManager
        this.structureRepository = new StructureRepository();
        this.worldRepository = new WorldRepository();
    }

    public static StructureAPI getInstance() {
        if (instance == null) {
            instance = new StructureAPI();
        }
        return instance;
    }

    public void initialize() throws DocumentException, SettlerCraftException {
        StructurePlanMenuReader reader = new StructurePlanMenuReader();
        planMenu = reader.read(new File(getWorkingDirectory(), "menu.xml"));
        if (globalPlanManager == null) {
            globalPlanManager = new GlobalPlanManager(getPlanDirectory());
        }
        if (loadLock.tryLock()) {
            try {
                loadPlans();
            } finally {
                loadLock.unlock();
            }
        }
    }

    public synchronized void loadPlans() {
        planMenuFactory = new StructurePlanMenuFactory(platform, planMenu);
        isLoadingPlans = true;
        try {
            globalPlanManager.loadPlans();
            for (StructurePlan plan : globalPlanManager.getPlans()) {
                planMenuFactory.load(plan);
            }
        } finally {
            isLoadingPlans = false;
        }
    }

    public boolean isLoadingPlans() {
        return isLoadingPlans;
    }

    public StructurePlan getPlanById(String planId) {
        return globalPlanManager.getPlan(planId);
    }

    /**
     * Creates a structure and sets the given player as MASTER owner
     *
     * @param player The player who will be the MASTER owner
     * @param world The world
     * @param plan The plan
     * @param position The position
     * @param direction The direction
     * @return The created Structure
     * @throws StructureException
     */
    public Structure createStructureWithPlayer(Player player, World world, StructurePlan plan, Vector position, Direction direction) throws StructureException {
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(plan);
        Preconditions.checkNotNull(direction);
        Preconditions.checkNotNull(position);

        if (plan instanceof SubStructuredPlan) {
            System.out.println("Structure is SubstructurePlan!");
            return null;
        } else {
            System.out.println("Structure is StructurePlan!");
            Placement placement = plan.getPlacement();

            checkLocation(world, placement, position);

            Vector min = position;
            Vector max = PlacementUtil.getPoint2Right(min, direction, placement.getDimension().getMaxPosition());

            System.out.println("Min: " + min);
            System.out.println("Max: " + max);
            System.out.println("Direction: " + direction);

            IWorld w = platform.getServer().getWorld(world.getName());
            WorldNode worldNode = worldRepository.findWorldNodeById(w.getUUID());
            if (worldNode == null) {
                worldRepository.addWorld(w.getName(), w.getUUID());
                worldNode = worldRepository.findWorldNodeById(w.getUUID());
                if (worldNode == null) {
                    throw new StructureAPIException("Something went wrong during creation of the 'WorldNode'"); // should't happen but just in case...
                }
            }

            StructureNode node = structureRepository.addStructure(worldNode, plan.getName(), plan.getPlacement().getDimension(), direction); // store and get the ID
            Structure structure = StructureFactory.instance().create(node);
            File structureDir = new File(getStructuresDirectory(world.getName()), String.valueOf(structure.getId()));

            // Overwrite old one if exists...
            if (structureDir.exists()) {
                structureDir.delete();
            }
            structureDir.mkdirs();

            File planFile = plan.getFile();

            try {
                if (placement instanceof SchematicPlacement) {
                    SchematicPlacement schematicPlacement = (SchematicPlacement) placement;
                    File schematicFile = schematicPlacement.getSchematic().getFile();
                    Files.copy(schematicFile, new File(structureDir, schematicFile.getName()));
                }
                Files.copy(planFile, new File(structureDir, STRUCTURE_PLAN_FILE_NAME));

            } catch (IOException ex) {
                structureDir.delete();
                Logger.getLogger(StructureAPI.class.getName()).log(Level.SEVERE, null, ex);
                structureRepository.deleteStructure(structure.getId());
                structure = null;
            }

            if (structure != null) {
                EventManager.getInstance().getEventBus().post(new StructureCreateEvent(structure));
            }

            return structure;
        }
    }

    /**
     * Creates a structure without any owners
     *
     * @param world The world
     * @param plan The plan
     * @param position The position
     * @param direction The direction
     * @return The created Structure
     * @throws StructureException
     */
    public Structure createStructure(World world, StructurePlan plan, Vector position, Direction direction) throws StructureException {
        return createStructureWithPlayer(null, world, plan, position, direction);
    }

    /**
     * Creates a structure with the given placement
     *
     * @param world The world
     * @param placement The placement
     * @param position The position
     * @param direction The direction
     * @return The created Structure
     * @throws StructureException
     */
    public Structure createStructure(World world, Placement placement, Vector position, Direction direction) throws StructureException {
        throw new UnsupportedOperationException();
    }

    /**
     * Builds the structure
     *
     * @param structure The structure that has to be build
     * @param uuid The player uuid, The UUID will be used to register this
     * build-operation. This method does NOT check if the player is allowed to
     * build
     * @param session The session to use
     * @param options The options, use {@link Options#defaultOptions() } to get
     * the default options
     * @param force whether the current construction state should be ignored.
     * Therefore forcefully stops and starts a build operation
     */
    public void build(final Structure structure, final UUID uuid, final EditSession session, final PlaceOptions options, boolean force) {
        Preconditions.checkNotNull(structure, "Structure may not be null");
        Preconditions.checkNotNull(uuid, "UUID may not be null");
        Preconditions.checkNotNull(session, "EditSession may not be null");
        Preconditions.checkNotNull(options, "Options may not be null");

        pool.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                PlayerEntry playerEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(uuid);
                AsyncPlacement placement = new AsyncPlacement(playerEntry, structure.getPlan().getPlacement());
                placement.rotate(structure.getDirection());
                placement.place(session, structure.getDimension().getMinPosition(), options);
            }
        });
    }

    public void demolish(final Structure structure, final UUID player, final EditSession session, final PlaceOptions options, boolean force) {
        pool.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                PlayerEntry playerEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(player);
                AsyncPlacement placement = new AsyncPlacement(playerEntry, structure.getPlan().getPlacement());
                placement.rotate(structure.getDirection());

                // Set Negative MASK
                // Set Negative not natural MASK
                placement.place(session, structure.getDimension().getMinPosition(), options);
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }

    /**
     * Stops a structure Build/Demolish operation
     * @param player The player, which will only be used for feedback. May be null
     * @param structure The structure
     */
    public void stop(final Player player, final Structure structure) {
        Preconditions.checkNotNull(structure, "Structure may not be null");
        pool.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                UUID uuid = null;
                Integer jobId = null;

                jobLock.lock();
                try {
                    uuid = taskedBy.get(structure.getId());
                    jobId = jobIds.get(structure.getId());
                } finally {
                    jobLock.unlock();
                }

                if (uuid != null && jobId != null) {
                    PlayerEntry entry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(uuid);
                    if (entry != null) {
                        BlockPlacer blockPlacer = AsyncWorldEditMain.getInstance().getBlockPlacer();
                        blockPlacer.cancelJob(entry, jobId);
                        if(player != null) {
                            player.print("Stopping structure #" + structure.getId());
                        }
                    }
                }
            }
        });
    }

    public void stop(final Structure structure) {
        stop(null, structure);
    }

    private void checkLocation(World world, Placement p, Vector position) throws StructureException {
        // TODO check extra restrictions based on world

        IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(world.getName());
        ILocation l = w.getSpawn();
        Vector spawnPos = new Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ());

        CuboidDimension placementDimension = new CuboidDimension(p.getDimension().getMinPosition().add(position), p.getDimension().getMaxPosition().add(position));

        if (placementDimension.getMinY() <= 1) {
            throw new StructureException("Structure must be placed at a minimum height of 1");
        }

        if (placementDimension.getMaxY() > w.getMaxHeight()) {
            throw new StructureException("Structure will reach above the world's max height (" + w.getMaxHeight() + ")");
        }

        if (CuboidDimension.isPositionWithin(placementDimension, spawnPos)) {
            throw new StructureException("Structure overlaps the world's spawn...");
        }

        if (!structureRepository.getStructuresWithin(world, p.getDimension(), 1).isEmpty()) {
            throw new StructureException("Structure overlaps another structure...");
        }
    }

    protected final File getStructuresDirectory(String world) {
        File f = new File(SettlerCraft.getInstance().getWorkingDirectory(), "worlds//" + world + "//structures");
        f.mkdirs(); // creates if not exists..
        return f;
    }

    public final File getPlanDirectory() {
        return new File(getWorkingDirectory(), PLANS_DIRECTORY);
    }

    public CategoryMenu createPlanMenu() {
        return planMenuFactory.createPlanMenu();
    }

    public static boolean isSupported(File structurePlan) {
        // Placement Check
        // SubPlacements Check
        // SubPlansCheck
        throw new UnsupportedOperationException("Not supported yet...");
    }

    public static Placement handle(PlacementElement placementElement) {
        String type = placementElement.getType();

        if (type.contains(".")) {
            String[] pluginPlacement = type.split(".");

            if (pluginPlacement.length == 2) {
                return handle(pluginPlacement[0], pluginPlacement[1], placementElement);
            } else {
                throw new PlacementException("Invalid format for placment element '" + placementElement.getElementName() + "' on line " + placementElement.getLine() + "! Format should be: SomePluginName.SomeTypeName");
            }
        } else {
            return handle(PLUGIN_NAME, type, placementElement);
        }
    }

    private static Placement handle(String plugin, String type, PlacementElement placemeElement) {
        Map<String, PlacementHandler> handlerMap;
        synchronized (handlers) {
            handlerMap = handlers.get(plugin);
            if (handlerMap == null) {
                throw new AssertionError("No handlers registered for plugin: " + plugin);
            }
        }
        PlacementHandler placementHandler;
        synchronized (handlerMap) {
            placementHandler = handlerMap.get(type);
            if (placementHandler == null) {
                throw new AssertionError("Not handler found for : " + type);
            }
        }

        return placementHandler.handle(placemeElement);
    }

    public static boolean canHandle(PlacementElement placementElement) {
        String[] pluginPlacement = placementElement.getType().split(".");

        if (pluginPlacement.length == 0) {
            return canHandle("SettlerCraft", placementElement.getType());
        } else if (pluginPlacement.length == 2) {
            return canHandle(pluginPlacement[0], pluginPlacement[1]);
        } else {
            throw new ElementValueException("Invalid format for placment element '" + placementElement.getElementName() + "'"
                    + " on line " + placementElement.getLine() + " of '" + placementElement.getFile().getAbsolutePath() + "'"
                    + " !\n Format should be: SomePluginName.SomeTypeName");
        }
    }

    private static boolean canHandle(String plugin, String type) {
        Map<String, PlacementHandler> handlerMap;
        synchronized (handlers) {
            handlerMap = handlers.get(plugin);
            if (handlerMap == null) {
                return false;
            }
        }
        synchronized (handlerMap) {
            return handlerMap.get(type) != null;
        }
    }

    public static void registerHandler(PlacementHandler handler) {
        Map<String, PlacementHandler> pluginHandlerMap;
        String plugin = handler.getPlugin();
        synchronized (handlers) {
            pluginHandlerMap = handlers.get(plugin);
            if (pluginHandlerMap == null) {
                pluginHandlerMap = Maps.newHashMap();
                handlers.put(plugin, pluginHandlerMap);
            }
        }

        synchronized (pluginHandlerMap) {
            if (pluginHandlerMap.get(handler.getType()) != null) {
                throw new RuntimeException("Already registered a handler for plugin '" + plugin + "' "
                        + "and type '" + handler.getType() + "'! Current registered handler: " + pluginHandlerMap.get(handler.getType()).getClass());
            }
            pluginHandlerMap.put(handler.getType(), handler);
            EventManager.getInstance().getEventBus().register(new PlacementHandlerRegisterEvent(handler));
        }
    }

    public void registerStructureAPIPlugin(IPlugin plugin) throws StructureAPIException {
        if (this.plugin != null) {
            throw new StructureAPIException("Already registered a Plugin for the StructureAPI, NOTE that this method should only be used by SettlerCraft-APIs!");
        }
        this.plugin = plugin;
    }

    public APlatform getPlatform() {
        return platform;
    }

    public List<StructurePlan> getStructurePlans() {
        return globalPlanManager.getPlans();
    }

    protected File getWorkingDirectory() {
        return plugin.getDataFolder();
    }

    public void registerConfigProvider(IConfigProvider configProvider) {
        this.config = configProvider;
    }

    public IConfigProvider getConfig() {
        return config;
    }

}
