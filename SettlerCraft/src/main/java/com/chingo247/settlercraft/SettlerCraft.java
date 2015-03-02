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

import com.chingo247.settlercraft.model.entities.structure.StructureEntity;
import com.chingo247.settlercraft.model.service.StructureDAO;
import com.chingo247.structureapi.structure.placement.handler.PlacementHandler;
import com.chingo247.structureapi.structure.plan.StructurePlan;
import com.chingo247.structureapi.structure.plan.processing.StructurePlanReader;
import com.chingo247.xcore.core.APlatform;
import com.chingo247.xcore.core.IWorld;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author Chingo
 */
public abstract class SettlerCraft {

    public static final String MSG_PREFIX = "[SettlerCraft]: ";
    private final Logger LOG = Logger.getLogger(SettlerCraft.class);
    private final Map<UUID, SettlerCraftWorld> worlds;
    private final Map<UUID, Object> worldMutexes;
    private final Map<String, StructurePlan> structurePlans;
    private final StructureDAO structureDAO;
    
    private final Lock PLANS_LOCK;
    private boolean isLoadingPlans;
    protected ExecutorService EXECUTOR;
    
    private APlatform platform;
    
    protected SettlerCraft(ExecutorService executor, APlatform platform) {
        this.isLoadingPlans = false;
        this.EXECUTOR = executor;
        this.structurePlans = new HashMap<>();
        this.PLANS_LOCK = new ReentrantLock();
        this.worlds = new HashMap<>();
        this.worldMutexes = new HashMap<>();
        this.platform = platform;
        this.structureDAO = new StructureDAO();
    }

    
    
    public boolean isLoadingPlans() {
        return isLoadingPlans;
    }
    
    protected synchronized void load() {
        loadPlans();
        loadWorlds();
    }

    private void loadWorlds() {
        LOG.info(MSG_PREFIX + " Initializing worlds...");
        List<IWorld> ws = getPlatform().getServer().getWorlds();
        List<Future> wTasks = new ArrayList<>(ws.size());
        
        for(final IWorld world : ws) {
            Future<?> f = EXECUTOR.submit(new Runnable() {

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
            isLoadingPlans = true;
            try {
                synchronized(structurePlans) {
                    structurePlans.clear();
                    StructurePlanReader reader = new StructurePlanReader();
                    List<StructurePlan> plans = reader.readDirectory(getPlanDirectory());
                    for(StructurePlan plan : plans) {
                        structurePlans.put(plan.getRelativePath(), plan);
                    }
                }
            } finally {
                isLoadingPlans = false;
                PLANS_LOCK.unlock();
            }
        }
    }
    
    public SettlerCraftStructure getStructure(long id) {
        StructureEntity entity = structureDAO.find(id);
        if(entity == null) {
            return null;
        }
        UUID world = entity.getWorldUUID();
        SettlerCraftWorld w = getWorld(world); 
        if(w == null) { 
            return null;
        }
        return w.getStructure(id);
    }
    
    public SettlerCraftWorld getWorld(String world) {
        IWorld w = getPlatform().getServer().getWorld(world);
        if(w == null) {
            return null;
        }
        return getWorld(w.getUUID());
    }

    public SettlerCraftWorld getWorld(UUID world) {
        SettlerCraftWorld w;
        synchronized (worlds) {
            w = worlds.get(world);
        }

        if (w == null) {
            IWorld iWorld = getPlatform().getServer().getWorld(world);
            if (iWorld == null) {
                return null;
            }
            
            // Check for null as we can't lock a null object
            Object mutex;
            synchronized(worldMutexes) {
                mutex = worldMutexes.get(iWorld.getUUID());
                if(mutex == null) {
                    mutex = new Object();
                    worldMutexes.put(iWorld.getUUID(), mutex);
                }
            }
            
            synchronized (mutex) {
                SettlerCraftWorld scw = handle(iWorld);
                scw._load();
                synchronized(worlds) {
                    worlds.put(scw.getUniqueId(), scw);
                }
                w = scw;
            }
        }
        return w;
    }

    public APlatform getPlatform() {
        return platform;
    }
    
    
    
    
    public abstract File getStructureDirectory();
    
    public abstract File getPlanDirectory();
    
    public abstract File getPluginDirectory();
    
    public abstract File getSchematicToPlanDirectory();
    
    public StructurePlan getStructurePlan(String path) {
        synchronized(structurePlans) {
            return structurePlans.get(path);
        }
    }
    
    public List<StructurePlan> getStructurePlans() {
        synchronized(structurePlans) {
            return new ArrayList<>(structurePlans.values());
        }
    }
    
    private static final Map<String,Map<String,PlacementHandler>> handlers = Maps.newHashMap();
    
    public static void registerPlacementHandler(PlacementHandler handler) {
        Map<String, PlacementHandler> map;
        String p = handler.getPlugin();
        String t = handler.getType();
        synchronized(handlers) {
            map = handlers.get(p);
            if(map == null) {
                map = Maps.newHashMap();
                handlers.put(p, map);
            }
        }
        synchronized(handlers.get(p)) {
            if(handlers.get(p).get(t) != null) {
                throw new RuntimeException("Handler with pluginName '"+p+"' and type '"+t+"' already exists!");
            } 
            map.put(handler.getType(), handler);
        }
    }
    
    public static PlacementHandler getPlacementHandler(String pluginName, String typeName) {
        Preconditions.checkNotNull(typeName);
        Preconditions.checkNotNull(pluginName);
        
        Map<String, PlacementHandler> map;
        synchronized(handlers) {
            map = handlers.get(pluginName);
            if(map == null) {
                return null;
            }
        }
        synchronized(handlers.get(pluginName)) {
            return map.get(typeName);
        }
    }

    protected abstract SettlerCraftWorld handle(IWorld world);

    protected abstract Player getPlayer(UUID player);        
    
    
    

}
