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

import com.chingo247.settlercraft.persistence.entities.structure.StructureEntity;
import com.chingo247.settlercraft.persistence.service.StructureDAO;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structure.plan.processing.StructurePlanReader;
import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.world.World;
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
    private final Map<UUID, SCWorld> worlds;
    private final Map<UUID, Object> worldMutexes;
    private final Map<String, StructurePlan> strucuturePlans;
    private final StructureDAO structureDAO;
    
    private final Lock PLANS_LOCK;
    private boolean isLoadingPlans;
    protected ExecutorService EXECUTOR;
    
    private APlatform platform;
    
    protected SettlerCraft(ExecutorService executor, APlatform platform) {
        this.isLoadingPlans = false;
        this.EXECUTOR = executor;
        this.strucuturePlans = new HashMap<>();
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
                synchronized(strucuturePlans) {
                    strucuturePlans.clear();
                    StructurePlanReader reader = new StructurePlanReader();
                    List<StructurePlan> plans = reader.readDirectory(getPlanDirectory());
                    for(StructurePlan plan : plans) {
                        strucuturePlans.put(plan.getRelativePath(), plan);
                    }
                }
            } finally {
                isLoadingPlans = false;
                PLANS_LOCK.unlock();
            }
        }
    }
    
    public Structure getStructure(long id) {
        StructureEntity entity = structureDAO.find(id);
        if(entity == null) {
            return null;
        }
        UUID world = entity.getWorldUUID();
        World w = getWorld(world); 
        if(w == null) { 
            return null;
        }
        return w.getStructure(id);
    }
    
    public World getWorld(String world) {
        IWorld w = getPlatform().getServer().getWorld(world);
        if(w == null) {
            return null;
        }
        return getWorld(w.getUUID());
    }

    public World getWorld(UUID world) {
        World w;
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
                SCWorld scw = handle(iWorld);
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
        synchronized(strucuturePlans) {
            return strucuturePlans.get(path);
        }
    }
    
    public List<StructurePlan> getStructurePlans() {
        synchronized(strucuturePlans) {
            return new ArrayList<>(strucuturePlans.values());
        }
    }

    protected abstract SCWorld handle(IWorld world);

    protected abstract Player getPlayer(UUID player);        
    
    
    

}
