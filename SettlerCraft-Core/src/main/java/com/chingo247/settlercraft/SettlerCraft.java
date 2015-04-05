/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft;

import com.chingo247.settlercraft.plugin.IEconomyProvider;
import com.chingo247.settlercraft.util.WorldEditUtil;
import com.chingo247.settlercraft.menu.CategoryMenu;
import com.chingo247.settlercraft.menu.MenuAPI;
import com.chingo247.settlercraft.menu.plan.StructurePlanMenu;
import com.chingo247.settlercraft.plugin.IConfigProvider;
import com.chingo247.settlercraft.structure.StructureAPI;
import com.chingo247.settlercraft.structure.StructureManager;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.world.SettlerCraftWorld;
import com.chingo247.xcore.core.APlatform;
import com.chingo247.xcore.core.IPlugin;
import com.chingo247.xcore.core.IWorld;
import com.google.common.base.Preconditions;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author Chingo
 */
public class SettlerCraft {

    public static final String MSG_PREFIX = "[SettlerCraft]: ";
    private final Logger LOG = Logger.getLogger(SettlerCraft.class);

    private static SettlerCraft instance;
    private final Map<String, SettlerCraftWorldImpl> worlds;
    private final Map<String, Object> worldMutexes;

    private ExecutorService service;

    private APlatform platform;
    private StructureAPI structureAPI;
    private MenuAPI menuAPI;
    private IConfigProvider configProvider;
    private IPlugin plugin;
    private IEconomyProvider economyProvider;
    private boolean initialized = false;
    private StructurePlanMenu planMenu;

    private SettlerCraft() {
        this.worlds = new HashMap<>();
        this.worldMutexes = new HashMap<>();
    }

    public static SettlerCraft getInstance() {
        if (instance == null) {
            instance = new SettlerCraft();
        }
        return instance;
    }

    public void registerExecutor(ExecutorService service) {
        Preconditions.checkNotNull(service);
        if (this.service != null) {
            throw new RuntimeException("Already registered a ExecutorService!");
        }
        this.service = service;
    }

    public void registerPlugin(IPlugin plugin) {
        Preconditions.checkNotNull(plugin);
        if (this.plugin != null) {
            throw new RuntimeException("Can't register '" + plugin.getName() + "' already registered a plugin!");
        }
        this.plugin = plugin;
    }

    public void registerPlatform(APlatform platform) throws RuntimeException {
        Preconditions.checkNotNull(platform);
        if (this.platform != null) {
            throw new RuntimeException("Already registered a platform!");
        }
        this.platform = platform;
    }

    public void registerStructureAPI(StructureAPI structureAPI) throws RuntimeException {
        Preconditions.checkNotNull(structureAPI);
        if (this.structureAPI != null) {
            throw new RuntimeException("Already registered a StructureAPI!");
        }
        this.structureAPI = structureAPI;
    }

    public void registerMenuAPI(MenuAPI menuAPI) throws RuntimeException {
        Preconditions.checkNotNull(menuAPI);
        if (this.menuAPI != null) {
            throw new RuntimeException("Already registered a MenuAPI!");
        }
        this.menuAPI = menuAPI;
    }

    public void registerPlanMenu(CategoryMenu menu) {
        if (planMenu != null) {
            throw new RuntimeException("Can only register one planMenu!");
        }
        if (menuAPI == null) {
            throw new RuntimeException("No Menu API was registered yet!");
        }
        this.planMenu = new StructurePlanMenu(platform, menuAPI, menu);
    }

    public void registerEconomyProvider(IEconomyProvider economyProvider) {
        Preconditions.checkNotNull(economyProvider);
        if (this.economyProvider != null) {
            throw new RuntimeException("Already registered an '" + IEconomyProvider.class.getSimpleName() + "'");
        }
        this.economyProvider = economyProvider;
    }

    public void registerConfigProvider(IConfigProvider configProvider) {
        Preconditions.checkNotNull(configProvider);
        this.configProvider = configProvider;
    }

    public IConfigProvider getConfigProvider() {
        return configProvider;
    }

    public synchronized void initialize() {
        if (!initialized) {
            if (structureAPI == null) {
                throw new RuntimeException("No StructureAPI was registered!");
            }
            if (menuAPI == null) {
                throw new RuntimeException("No MenuAPI was registered!");
            }
            if (planMenu == null) {
                throw new RuntimeException("No PlanMenu was registered!");
            }

            if (platform == null) {
                throw new RuntimeException("No platform was registered!");
            }
            if (plugin == null) {
                throw new RuntimeException("No plugin was registered!");
            }
            if (service == null) {
                throw new RuntimeException("No ExecutorService was registered!");
            }
            if (configProvider == null) {
                throw new RuntimeException("No ConfigProvider was registered!");
            }

            // Load the StructureAPI first
            structureAPI.load();

            for (StructurePlan plan : structureAPI.getStructurePlans()) {
                planMenu.load(plan);
            }

            loadWorlds();
            initialized = true;
        }
    }

    private void loadWorlds() {
        LOG.info(MSG_PREFIX + " Initializing worlds...");
        List<IWorld> ws = getPlatform().getServer().getWorlds();
        List<Future> wTasks = new ArrayList<>(ws.size());

        for (final IWorld world : ws) {
            Future<?> f = service.submit(new Runnable() {

                @Override
                public void run() {
                    getWorld(world.getName());
                }
            });
            wTasks.add(f);
        }
        for (Future f : wTasks) {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException ex) {
                java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public SettlerCraftWorld getWorld(String world) {
        SettlerCraftWorldImpl swi;
        synchronized (worlds) {
            swi = worlds.get(world);
        }

        if (swi == null) {
            IWorld iWorld = getPlatform().getServer().getWorld(world);
            if (iWorld == null) {
                return null;
            }

            // Check for null as we can't lock a null object
            Object mutex;
            synchronized (worldMutexes) {
                mutex = worldMutexes.get(iWorld.getName());
                if (mutex == null) {
                    mutex = new Object();
                    worldMutexes.put(iWorld.getName(), mutex);
                }
            }
            // Loads the world
            synchronized (mutex) {
                File worldsDirectory = new File(getWorkingDirectory(), "worlds");

                File worldDirectory = new File(worldsDirectory, world);
                worldDirectory.mkdirs();

                File worldFile = new File(worldDirectory, "config.xml");
                if (worlds.get(world) == null) {
                    StructureManager structureManager = structureAPI.getStructureManager(world);
                    swi = new SettlerCraftWorldImpl(structureManager, iWorld.getUUID(), WorldEditUtil.getWorld(world), worldFile);
                    swi.load();
                    worlds.put(swi.getName(), swi);
                } 
            }
        }
        return swi;
    }

    public APlatform getPlatform() {
        return platform;
    }

    public File getWorkingDirectory() {
        return plugin.getDataFolder();
    }

    public StructureAPI getStructureAPI() {
        return structureAPI;
    }

    public IEconomyProvider getEconomyProvider() {
        return economyProvider;
    }

    public MenuAPI getMenuAPI() {
        return menuAPI;
    }

    public CategoryMenu createPlanMenu() {
        return planMenu.createPlanMenu();
    }

}
