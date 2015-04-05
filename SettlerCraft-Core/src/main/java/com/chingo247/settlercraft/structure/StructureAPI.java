/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structure;

import com.chingo247.settlercraft.world.Direction;
import com.chingo247.settlercraft.event.EventManager;
import com.chingo247.settlercraft.structure.exception.ElementValueException;
import com.chingo247.settlercraft.structure.exception.StructureException;
import com.chingo247.settlercraft.structure.placement.event.PlacementHandlerRegisterEvent;
import com.chingo247.settlercraft.structure.plan.exception.PlacementException;
import com.chingo247.settlercraft.structure.placement.Placement;
import com.chingo247.settlercraft.structure.placement.handlers.PlacementHandler;
import com.chingo247.settlercraft.structure.placement.handlers.SchematicPlacementHandler;
import com.chingo247.settlercraft.structure.plan.GlobalPlanManager;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.plan.StructurePlanManager;
import com.chingo247.settlercraft.structure.plan.document.PlacementElement;
import com.chingo247.xcore.core.APlatform;
import com.chingo247.xcore.core.IPlugin;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Chingo
 */
public abstract class StructureAPI {

    public static final String PLUGIN_NAME = "SettlerCraft";
    public static final String PLANS_DIRECTORY = "plans";
    private static final Map<String, Map<String, PlacementHandler>> handlers = Maps.newHashMap();

    private final StructurePlanManager globalPlanManager;
    private final Map<String, StructureManager> structureManagers;
    private final APlatform platform;
    private final Lock loadLock = new ReentrantLock();
    private final IPlugin plugin;
    private final ExecutorService executor;

    private boolean isLoadingPlans = false;

    protected StructureAPI(APlatform platform, IPlugin plugin, ExecutorService service) {
        Preconditions.checkNotNull(platform);
        Preconditions.checkNotNull(plugin);
        Preconditions.checkNotNull(service);
        this.executor = service;
        this.plugin = plugin;
        this.structureManagers = Maps.newHashMap();
        this.platform = platform;
        // Register Handlers first...
//        registerHandler(new GeneratedCuboidHandler());
//        registerHandler(new GeneratedCylinderHandler());
//        registerHandler(new GeneratedEllipsoidHandler());
//        registerHandler(new GeneratedPolygonal2DHandler());
        registerHandler(new SchematicPlacementHandler());

        // Now register the GlobalPlanManager
        this.globalPlanManager = new GlobalPlanManager(getPlanDirectory());
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

    public StructureManager getStructureManager(String world) {
        StructureManager structureManager;
        synchronized (structureManagers) {
            structureManager = structureManagers.get(world);

            if (structureManager == null) {
                structureManager = new StructureManager(world, executor, this);
                structureManager.load();
                structureManagers.put(world, structureManager);
            }
        }
        return structureManager;
    }

    public Structure create(StructurePlan plan, World world, Direction direction, Vector position) throws StructureException {
        StructureManager manager = structureManagers.get(world.getName());
        if (manager == null) {
            throw new NullPointerException("World '" + world + "' + doesn't exist...");
        }
        return manager.createStructure(plan, position, direction);
    }

    public Structure create(Placement placement, World world, Direction direction, Vector position) throws StructureException {
        StructureManager manager = structureManagers.get(world.getName());
        if (manager == null) {
            throw new NullPointerException("World '" + world + "' + doesn't exist...");
        }
        return manager.createStructure(placement, position, direction);
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
