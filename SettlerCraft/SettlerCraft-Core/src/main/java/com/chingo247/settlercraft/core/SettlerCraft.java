/*
 * Copyright (C) 2015 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.settlercraft.core;

import com.chingo247.settlercraft.core.concurrent.ThreadPoolFactory;
import com.chingo247.settlercraft.core.event.DefaultSubscriberExceptionHandler;
import com.chingo247.settlercraft.core.event.EventDispatcher;
import com.chingo247.settlercraft.core.event.IEventDispatcher;
import com.chingo247.settlercraft.core.exception.SettlerCraftException;
import com.chingo247.settlercraft.core.persistence.neo4j.Neo4jDatabase;
import com.chingo247.settlercraft.core.persistence.neo4j.Neo4jHelper;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IPlugin;
import com.chingo247.xplatform.core.IWorld;
import com.chingo247.settlercraft.core.model.settler.BaseSettlerRepository;
import com.chingo247.settlercraft.core.model.settler.BaseSettlerNode;
import com.chingo247.settlercraft.core.model.world.WorldNode;
import com.chingo247.settlercraft.core.platforms.services.IEconomyProvider;
import com.chingo247.settlercraft.core.platforms.services.IPlayerProvider;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class SettlerCraft {

    public static final String MSG_PREFIX = "[SettlerCraft]: ";

    private static SettlerCraft instance;
    private final ExecutorService executor;

    private APlatform platform;
    private IPlugin plugin;
    private IPlayerProvider playerProvider;
    private GraphDatabaseService graph;
    private BaseSettlerRepository settlerDAO;
    private IEconomyProvider economyProvider;
    private EventBus eventBus, asyncEventBus;
    private IEventDispatcher eventDispatcher;

    private SettlerCraft() {
        this.executor = new ThreadPoolFactory().newCachedThreadPool(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors());
        this.eventBus = new EventBus(new DefaultSubscriberExceptionHandler());
        this.asyncEventBus = new AsyncEventBus(executor, new DefaultSubscriberExceptionHandler());
        this.eventDispatcher = new EventDispatcher();
        this.eventDispatcher.register(eventBus);
        this.eventDispatcher.register(asyncEventBus);
    }

    public IEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public EventBus getAsyncEventBus() {
        return asyncEventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }
    
    

    private void setupNeo4j() {
        File databaseDir = new File(plugin.getDataFolder().getAbsolutePath() + "//databases//Neo4J");
        if(!databaseDir.exists()) {
            System.out.println(MSG_PREFIX + "Setting up Neo4j Database for the first time, this might take a while");
        }
        
        databaseDir.mkdirs();
        this.graph = new Neo4jDatabase(databaseDir, "SettlerCraft", 1024).getGraph();
        this.settlerDAO = new BaseSettlerRepository(graph);
        
        try (Transaction tx = graph.beginTx()) {
            if (!Neo4jHelper.hasUniqueConstraint(graph, WorldNode.label(), WorldNode.UUID_PROPERTY)) {
                this.graph.schema().constraintFor(WorldNode.label())
                        .assertPropertyIsUnique(WorldNode.UUID_PROPERTY)
                        .create();
                tx.success();
            }
        }
        try (Transaction tx = graph.beginTx()) {
            Neo4jHelper.createUniqueIndexIfNotExist(graph, BaseSettlerNode.label(), BaseSettlerNode.UUID_PROPERTY);
            tx.success();
        }
        try (Transaction tx = graph.beginTx()) {
            Neo4jHelper.createUniqueIndexIfNotExist(graph, BaseSettlerNode.label(), BaseSettlerNode.ID_PROPERTY);
            tx.success();
        }
        try (Transaction tx = graph.beginTx()) {
            Neo4jHelper.createUniqueIndexIfNotExist(graph, DynamicLabel.label("ID_GENERATOR"), "name");
            tx.success();
        }
        setupIdGenerator("SETTLER_ID");
        
        SettlerRegister settlerRegister = new SettlerRegister(settlerDAO, executor, graph);
        eventBus.register(settlerRegister);
    }
    
    private void setupIdGenerator(String generatorName) {
        try(Transaction tx = graph.beginTx()) {
            Result r = graph.execute("MATCH (sid: ID_GENERATOR {name:'"+generatorName+"'}) "
                        + "RETURN sid "
                        + "LIMIT 1");
            if(!r.hasNext()) {
                graph.execute("CREATE (sid: ID_GENERATOR {name:'"+generatorName+"', nextId: 0})");
            }
            tx.success();
        }
    }
    
    public static SettlerCraft getInstance() {
        if (instance == null) {
            instance = new SettlerCraft();
        }
        return instance;
    }

    public IEconomyProvider getEconomyProvider() {
        return economyProvider;
    }
    
    public void registerEconomyService(IEconomyProvider iEconomyProvider) throws SettlerCraftException {
        if(economyProvider != null) {
            throw new SettlerCraftException("Already registered an Economy Provider!");
        }
        this.economyProvider = iEconomyProvider;
    }

    public void registerPlugin(IPlugin plugin) {
        Preconditions.checkNotNull(plugin);
        if (this.plugin != null) {
            throw new RuntimeException("Can't register '" + plugin.getName() + "' already registered a plugin!");
        }
        this.plugin = plugin;
        setupNeo4j();
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
    
    public IPlugin getPlugin() {
        return plugin;
    }

    public File getWorkingDirectory() {
        return plugin.getDataFolder();
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public Player getPlayer(UUID player) {
        return playerProvider.getPlayer(player);
    }

    public World getWorld(UUID world) {
        IWorld w = platform.getServer().getWorld(world);
        
        if(w != null) {
            return getWorld(w.getName());
        }
        
        return null;
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
