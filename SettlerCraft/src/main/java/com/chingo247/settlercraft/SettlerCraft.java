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

import com.chingo247.settlercraft.entities.WorldEntity;
import com.chingo247.settlercraft.structure.persistence.service.WorldDAO;
import com.chingo247.settlercraft.structure.plan.StructurePlanManager;
import com.chingo247.settlercraft.world.World;
import com.chingo247.xcore.core.APlatform;
import com.chingo247.xcore.core.IWorld;
import com.chingo247.xcore.platforms.PlatformFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

/**
 *
 * @author Chingo
 */
public class SettlerCraft {

    private static SettlerCraft instance;
    private final Logger LOG = Logger.getLogger(SettlerCraft.class);
    private final APlatform PLATFORM;
    private final SettlerCraftContext CONTEXT;
    private final WorldDAO WORLD_DAO;
    private final Map<UUID, SCWorld> WORLDS;
    private final ExecutorService SERVICE;
    private final String MSG_PREFIX;
    private final StructurePlanManager PLAN_STORAGE;

    private SettlerCraft() {
        this.WORLD_DAO = new WorldDAO();
        this.SERVICE = Executors.newCachedThreadPool();
        this.WORLDS = new HashMap<>();
        this.CONTEXT = SettlerCraftContext.getContext();
        this.PLATFORM = PlatformFactory.createPlatform(CONTEXT.getPlatform());
        this.MSG_PREFIX = "["+CONTEXT.getPluginName()+"]: ";
        this.PLAN_STORAGE = StructurePlanManager.getInstance();
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
        reloadPlans();
    }

    private void loadWorlds() {
        LOG.info(MSG_PREFIX + " Initializing worlds...");
        List<IWorld> ws = PLATFORM.getServer().getWorlds();
        for(IWorld world : ws) {
            getWorld(world.getUUID());
        }
    }
    
    public void reloadPlans() {
        PLAN_STORAGE.loadPlans();
    }

    public World getWorld(UUID world) {
        World w = null;
        synchronized (WORLDS) {
            WORLDS.get(world);
        }

        if (w == null) {
            IWorld iWorld = PLATFORM.getServer().getWorld(world);
            if (iWorld == null) {
                return null;
            }
            synchronized (WORLDS) {
                WorldEntity we = WORLD_DAO.find(world);
                if (we == null) {
                    we = new WorldEntity(iWorld.getName(), iWorld.getUUID());
                }
                SCWorld scw = new SCWorld(SERVICE, we);
                WORLDS.put(scw.getUniqueId(), scw);
                w = scw;
            }
        }
        return w;
    }

}
