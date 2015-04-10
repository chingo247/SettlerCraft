/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.structure;

import com.chingo247.proxyplatform.core.APlatform;
import com.chingo247.proxyplatform.core.ILocation;
import com.chingo247.proxyplatform.core.IPlugin;
import com.chingo247.proxyplatform.core.IWorld;
import com.chingo247.structureapi.SettlerCraft;
import com.chingo247.structureapi.world.Direction;
import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.settlercraft.core.regions.CuboidDimension;
import com.chingo247.structureapi.structure.event.StructureCreateEvent;
import com.chingo247.structureapi.structure.exception.ElementValueException;
import com.chingo247.structureapi.structure.exception.StructureException;
import com.chingo247.structureapi.structure.placement.event.PlacementHandlerRegisterEvent;
import com.chingo247.structureapi.structure.plan.exception.PlacementException;
import com.chingo247.structureapi.structure.placement.Placement;
import com.chingo247.structureapi.structure.placement.SchematicPlacement;
import com.chingo247.structureapi.structure.placement.handlers.PlacementHandler;
import com.chingo247.structureapi.structure.placement.handlers.SchematicPlacementHandler;
import com.chingo247.structureapi.structure.plan.GlobalPlanManager;
import com.chingo247.structureapi.structure.plan.StructurePlan;
import com.chingo247.structureapi.structure.plan.StructurePlanManager;
import com.chingo247.structureapi.structure.plan.SubStructuredPlan;
import com.chingo247.structureapi.structure.plan.document.PlacementElement;
import com.chingo247.structureapi.util.PlacementUtil;
import com.chingo247.structureapi.world.SettlerCraftWorld;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
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

/**
 *
 * @author Chingo
 */
public abstract class StructureAPI {

    public static final String STRUCTURE_PLAN_FILE_NAME = "structureplan.xml";
    public static final String PLUGIN_NAME = "SettlerCraft";
    public static final String PLANS_DIRECTORY = "plans";
    private static final Map<String, Map<String, PlacementHandler>> handlers = Maps.newHashMap();

    private final StructurePlanManager globalPlanManager;
    private final APlatform platform;
    private final Lock loadLock = new ReentrantLock();
    private final IPlugin plugin;
    private final ExecutorService executor;
    private final StructureRepository structureRepository;

    private boolean isLoadingPlans = false;

    protected StructureAPI(APlatform platform, IPlugin plugin, ExecutorService service) {
        Preconditions.checkNotNull(platform);
        Preconditions.checkNotNull(plugin);
        Preconditions.checkNotNull(service);
        this.executor = service;
        this.plugin = plugin;
        this.platform = platform;
        // Register Handlers first...
//        registerHandler(new GeneratedCuboidHandler());
//        registerHandler(new GeneratedCylinderHandler());
//        registerHandler(new GeneratedEllipsoidHandler());
//        registerHandler(new GeneratedPolygonal2DHandler());
        registerHandler(new SchematicPlacementHandler());

        // Now register the GlobalPlanManager
        this.globalPlanManager = new GlobalPlanManager(getPlanDirectory());
        this.structureRepository = new StructureRepository();
    }

    public void load() {
        if (loadLock.tryLock()) {
            try {
                loadPlans();
            } finally {
                loadLock.unlock();
            }
        }
    }

    public synchronized void loadPlans() {
        isLoadingPlans = true;
        try {
            globalPlanManager.loadPlans();
        } finally {
            isLoadingPlans = false;
        }
    }

    public boolean isLoadingPlans() {
        return isLoadingPlans;
    }

    public StructurePlan getPlan(String planId) {
        return globalPlanManager.getPlan(planId);
    }

    public Structure createUninitizalizedStructure(SettlerCraftWorld world, Placement placement, Direction direction, Vector position) {
        //putStructure(null);
        throw new UnsupportedOperationException();
    }

    public Structure createStructure(SettlerCraftWorld world, StructurePlan plan, Vector position, Direction direction) throws StructureException {
        Structure structure = createUninitizalizedStructure(world, plan, direction, position);
        if (structure != null) {
            structure.setState(State.CREATED);
            EventManager.getInstance().getEventBus().post(new StructureCreateEvent(structure));
        }

        return structure;
    }

    public Structure createStructure(SettlerCraftWorld world, Placement placement, Vector position, Direction direction) throws StructureException {
        Structure structure = createUninitizalizedStructure(world, placement, direction, position);
        if (structure != null) {
            structure.setState(State.CREATED);
            EventManager.getInstance().getEventBus().post(new StructureCreateEvent(structure));

        }

        return structure;
    }

    public Structure createUninitizalizedStructure(SettlerCraftWorld world, StructurePlan plan, Direction direction, Vector position) throws StructureException {
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

            Structure structure = new SimpleStructure(plan.getName(), world, direction, new CuboidDimension(min, max));

            structure = structureRepository.save(structure); // store and get the ID
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
                return null;
            }

            return structure;
        }
    }

    private void checkLocation(SettlerCraftWorld world, Placement p, Vector position) throws StructureException {
        IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(world.getUUID());
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

        if (structureRepository.overlaps(world, p.getDimension())) {
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

    public APlatform getPlatform() {
        return platform;
    }

    public List<StructurePlan> getStructurePlans() {
        return globalPlanManager.getPlans();
    }

    protected File getWorkingDirectory() {
        return plugin.getDataFolder();
    }

    public abstract Player getPlayer(UUID player);
}
