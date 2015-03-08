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

import com.chingo247.settlercraft.model.service.StructureDAO;
import com.chingo247.structureapi.StructureAPI;
import com.chingo247.structureapi.StructureWorld;
import com.chingo247.xcore.core.APlatform;
import com.chingo247.xcore.core.IWorld;
import com.sk89q.worldedit.entity.Player;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author Chingo
 */
public abstract class SettlerCraft {

    public static final String MSG_PREFIX = "[SettlerCraft]: ";
    private final Logger LOG = Logger.getLogger(SettlerCraft.class);

    private final Map<String, SettlerCraftWorld> worlds;
    private final Map<String, Object> worldMutexes;

    protected final StructureDAO structureDAO;
    protected ExecutorService EXECUTOR;

    private APlatform platform;
    private StructureAPI structureAPI;

    protected SettlerCraft(ExecutorService executor, APlatform platform, StructureAPI structureAPI) {
        this.EXECUTOR = executor;
        this.worlds = new HashMap<>();
        this.worldMutexes = new HashMap<>();
        this.platform = platform;
        this.structureDAO = new StructureDAO();
        this.structureAPI = structureAPI;
    }

    public boolean isLoadingPlans() {
        return structureAPI.isLoadingPlans();
    }

    protected synchronized void load() {
        // Load the plans first...
        structureAPI.loadPlans();
        loadWorlds();
    }

    private void loadWorlds() {
        LOG.info(MSG_PREFIX + " Initializing worlds...");
        List<IWorld> ws = getPlatform().getServer().getWorlds();
        List<Future> wTasks = new ArrayList<>(ws.size());

        for (final IWorld world : ws) {
            Future<?> f = EXECUTOR.submit(new Runnable() {

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

    public final StructureAPI getStructureAPI() {
        return structureAPI;
    }

    public SettlerCraftWorld getWorld(String world) {
        SettlerCraftWorld settlerCraftWorld;
        synchronized (worlds) {
            settlerCraftWorld = worlds.get(world);
        }

        if (settlerCraftWorld == null) {
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
            StructureWorld structureWorld = structureAPI.getWorld(world);
            synchronized (mutex) {
                settlerCraftWorld = new SettlerCraftWorld(structureWorld);
                settlerCraftWorld.load();
                synchronized (worlds) {
                    worlds.put(settlerCraftWorld.getName(), settlerCraftWorld);
                }
            }
        }
        return settlerCraftWorld;
    }

    public APlatform getPlatform() {
        return platform;
    }
    
    public abstract File getWorkingDirectory();

    protected abstract Player getPlayer(UUID player);

}
