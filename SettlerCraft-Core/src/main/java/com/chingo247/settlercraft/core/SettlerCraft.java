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
package com.chingo247.settlercraft.core;

import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.settlercraft.core.persistence.neo4j.Neo4jDatabase;
import com.chingo247.settlercraft.core.persistence.neo4j.Neo4jHelper;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IPlugin;
import com.chingo247.xplatform.core.IWorld;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerDAO;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import com.chingo247.settlercraft.core.platforms.IPlayerProvider;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class SettlerCraft {

    public static final String MSG_PREFIX = "[SettlerCraft]: ";

    private static SettlerCraft instance;
    private final ExecutorService service;

    private APlatform platform;
    private IPlugin plugin;
    private IPlayerProvider playerProvider;
    private GraphDatabaseService graph;
    private SettlerDAO settlerDAO;

    private SettlerCraft() {
        this.service = new ThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors(), 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    private void setupDatabase() {
        File databaseDir = new File(plugin.getDataFolder().getAbsolutePath() + "//databases//Neo4J");
        databaseDir.mkdirs();
        this.graph = new Neo4jDatabase(databaseDir, "SettlerCraft").getGraph();
        this.settlerDAO = new SettlerDAO(graph);
        
        try (Transaction tx = graph.beginTx()) {
            if (!Neo4jHelper.hasUniqueConstraint(graph, WorldNode.LABEL, WorldNode.ID_PROPERTY)) {
                this.graph.schema().constraintFor(WorldNode.LABEL)
                        .assertPropertyIsUnique(WorldNode.ID_PROPERTY)
                        .create();
                tx.success();
            }
        }
        SettlerRegister structureOwnerRegister = new SettlerRegister(settlerDAO, service, graph);
        EventManager.getInstance().getEventBus().register(structureOwnerRegister);
    }

    public static SettlerCraft getInstance() {
        if (instance == null) {
            instance = new SettlerCraft();
        }
        return instance;
    }

    public void registerPlugin(IPlugin plugin) {
        Preconditions.checkNotNull(plugin);
        if (this.plugin != null) {
            throw new RuntimeException("Can't register '" + plugin.getName() + "' already registered a plugin!");
        }
        this.plugin = plugin;
        setupDatabase();
    }

    public void registerPlatform(APlatform platform) throws RuntimeException {
        Preconditions.checkNotNull(platform);
        if (this.platform != null) {
            throw new RuntimeException("Already registered a platform!");
        }
        this.platform = platform;
    }

    public void registerPlayerProvider(IPlayerProvider playerProvider) {
        Preconditions.checkNotNull(playerProvider);
        if (this.playerProvider != null) {
            throw new RuntimeException("Already registered a PlayerProvider!");
        }
        this.playerProvider = playerProvider;
    }

    public APlatform getPlatform() {
        return platform;
    }

    public File getWorkingDirectory() {
        return plugin.getDataFolder();
    }

    public ExecutorService getExecutor() {
        return service;
    }

    public Player getPlayer(UUID player) {
        return playerProvider.getPlayer(player);
    }

    public World getWorld(UUID world) {
        IWorld w = platform.getServer().getWorld(world);
        return w == null ? null : getWorld(w.getName());
    }

    public World getWorld(String world) {
        List<? extends World> worlds = WorldEdit.getInstance().getServer().getWorlds();
        for (World w : worlds) {
            if (w.getName().equals(world)) {
                return w;
            }
        }
        return null;
    }

    public synchronized final GraphDatabaseService getNeo4j() {
        Preconditions.checkNotNull(plugin);

        return graph;
    }

}
