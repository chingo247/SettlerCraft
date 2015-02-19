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

import com.chingo247.settlercraft.bukkit.BKWorldHandler;
import com.chingo247.settlercraft.entities.WorldEntity;
import com.chingo247.settlercraft.structure.persistence.service.WorldDAO;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.plan.processing.StructurePlanReader;
import com.chingo247.settlercraft.world.World;
import com.chingo247.xcore.core.APlatform;
import com.chingo247.xcore.core.IWorld;
import com.chingo247.xcore.platforms.PlatformFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author Chingo
 */
public class SettlerCraft {

    private static SettlerCraft instance;
    private final Logger LOG = Logger.getLogger(SettlerCraft.class);
    private final APlatform PLATFORM;
    private final SCGlobalContext CONTEXT;
    private final WorldDAO WORLD_DAO;
    private final ExecutorService SERVICE;
    private final String MSG_PREFIX;
    
    private final Map<UUID, SCWorld> WORLDS;
    private final Map<UUID, Object> WORLDS_MUTEXES;
    private final WorldHandler WORLD_HANDLER;
    private final Map<String, StructurePlan> PLANS;
    
    private final Lock PLANS_LOCK;

    private SettlerCraft() {
        this.WORLD_DAO = new WorldDAO();
        this.SERVICE = Executors.newCachedThreadPool();
        this.CONTEXT = SCGlobalContext.getContext();
        this.PLATFORM = PlatformFactory.createPlatform(CONTEXT.getPlatform());
        this.MSG_PREFIX = "["+CONTEXT.getPluginName()+"]: ";
        this.PLANS = new HashMap<>();
        this.PLANS_LOCK = new ReentrantLock();
        this.WORLDS = new HashMap<>();
        this.WORLDS_MUTEXES = new HashMap<>();
        this.WORLD_HANDLER = getWorldHandler();
    }

    public static SettlerCraft getInstance() {
        if (instance == null) {
            instance = new SettlerCraft();
            instance.load();
        }
        return instance;
    }
    
    private void load() {
        loadWorlds();
        loadPlans();
    }

    private void loadWorlds() {
        LOG.info(MSG_PREFIX + " Initializing worlds...");
        List<IWorld> ws = PLATFORM.getServer().getWorlds();
        List<Future> wTasks = new ArrayList<>(ws.size());
        
        for(final IWorld world : ws) {
            Future<?> f = SERVICE.submit(new Runnable() {

                @Override
                public void run() {
                     getWorld(world.getUUID());
                }
            });
           wTasks.add(f);
        }
        for(Future f : wTasks) {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException ex) {
                java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void loadPlans() {
        if(PLANS_LOCK.tryLock()) {
            try {
                synchronized(PLANS) {
                    PLANS.clear();
                    StructurePlanReader reader = new StructurePlanReader();
                    List<StructurePlan> plans = reader.readDirectory(CONTEXT.getPlanDirectory());
                    for(StructurePlan plan : plans) {
                        PLANS.put(plan.getRelativePath(), plan);
                    }
                }
            } finally {
                PLANS_LOCK.unlock();
            }
        }
    }
    
    public World getWorld(String world) {
        IWorld w = PLATFORM.getServer().getWorld(world);
        if(w == null) {
            return null;
        }
        return getWorld(w.getUUID());
    }

    public World getWorld(UUID world) {
        World w;
        synchronized (WORLDS) {
            w = WORLDS.get(world);
        }

        if (w == null) {
            IWorld iWorld = PLATFORM.getServer().getWorld(world);
            if (iWorld == null) {
                return null;
            }
            
            // Check for null as we can't lock a null object
            Object mutex;
            synchronized(WORLDS_MUTEXES) {
                mutex = WORLDS_MUTEXES.get(iWorld.getUUID());
                if(mutex == null) {
                    mutex = new Object();
                    WORLDS_MUTEXES.put(iWorld.getUUID(), mutex);
                }
            }
            
            synchronized (mutex) {
                WorldEntity we = WORLD_DAO.find(world);
                if (we == null) {
                    we = new WorldEntity(iWorld.getName(), iWorld.getUUID());
                }
                SCWorld scw = WORLD_HANDLER.handle(we);
                scw._load();
                synchronized(WORLDS) {
                    WORLDS.put(scw.getUniqueId(), scw);
                }
                w = scw;
            }
        }
        return w;
    }
    
    private WorldHandler getWorldHandler() {
        switch(CONTEXT.getPlatform().toLowerCase()) {
            case "bukkit": return new BKWorldHandler(SERVICE);
            default: throw new AssertionError("Platform not supported: " + CONTEXT.getPlatform());
        }
    }

}
