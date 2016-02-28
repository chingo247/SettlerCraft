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
import com.chingo247.settlercraft.core.event.EventDispatcher;
import com.chingo247.settlercraft.core.exception.SettlerCraftException;
import com.chingo247.settlercraft.core.persistence.neo4j.Neo4jDatabase;
import com.chingo247.settlercraft.core.persistence.neo4j.Neo4jHelper;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IPlugin;
import com.chingo247.xplatform.core.IWorld;
import com.chingo247.settlercraft.core.model.settler.SettlerRepository;
import com.chingo247.settlercraft.core.model.settler.SettlerNode;
import com.chingo247.settlercraft.core.model.world.WorldNode;
import com.chingo247.settlercraft.core.platforms.services.IEconomyProvider;
import com.chingo247.settlercraft.core.platforms.services.IPlayerProvider;
import com.chingo247.settlercraft.core.util.yaml.YAMLFormat;
import com.chingo247.settlercraft.core.util.yaml.YAMLProcessor;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public static final String LAST_NEO4J_VERSION = "2.3.2";

    private static SettlerCraft instance;
    private final ExecutorService executor;

    private APlatform platform;
    private IPlugin plugin;
    private IPlayerProvider playerProvider;
    private Neo4jDatabase database;
    private SettlerRepository settlerRepo;
    private IEconomyProvider economyProvider;
    private EventBus eventBus, asyncEventBus;
    private EventDispatcher eventDispatcher;

    private SettlerCraft() {
        this.executor = new ThreadPoolFactory().newCachedThreadPool(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors());
        this.eventBus = new EventBus(new DefaultSubscriberExceptionHandler());
        this.asyncEventBus = new AsyncEventBus(executor, new DefaultSubscriberExceptionHandler());
        this.eventDispatcher = new EventDispatcher();
        this.eventDispatcher.register(eventBus);
        this.eventDispatcher.register(asyncEventBus);
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public EventBus getAsyncEventBus() {
        return asyncEventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void setupNeo4j(IPlugin plugin) {
        File databaseDir = new File(plugin.getDataFolder().getAbsolutePath() + "//databases//Neo4J");

        boolean freshDatabase = !databaseDir.exists();

        if (freshDatabase) {
            System.out.println(MSG_PREFIX + "Setting up Neo4j Database for the first time, this might take a while");
        }

        databaseDir.mkdirs();

        File updateFile = new File(databaseDir, "updated.yml");
        if (!updateFile.exists()) {
            try {
                updateFile.createNewFile();
            } catch (IOException ex) {
                throw new RuntimeException("Error occured during creation of a file: " + updateFile.getName(), ex);
            }
        }

        YAMLProcessor processor = new YAMLProcessor(updateFile, true, YAMLFormat.EXTENDED);
        try {
            processor.load();
        } catch (IOException ex) {
            throw new RuntimeException("Error occured while loading file: " + updateFile.getName(), ex);
        }
        
        boolean updated = processor.getBoolean("neo4j.updated", false);

        boolean shouldUpdate = !freshDatabase && !updated;

        this.database = new Neo4jDatabase(databaseDir, "SettlerCraft", 1024, shouldUpdate);

        processor.setProperty("neo4j.updated", true);
        processor.setProperty("neo4j.version", LAST_NEO4J_VERSION);
        processor.save();

        boolean setup = processor.getBoolean("settler.repository.setup", false);

        GraphDatabaseService graph = database.getGraph();
        this.settlerRepo = new SettlerRepository(graph);

        if (!setup) {
            try (Transaction tx = graph.beginTx()) {
                if (!Neo4jHelper.hasUniqueConstraint(graph, WorldNode.label(), WorldNode.UUID_PROPERTY)) {
                    graph.schema().constraintFor(WorldNode.label())
                            .assertPropertyIsUnique(WorldNode.UUID_PROPERTY)
                            .create();
                    tx.success();
                }
            }
            try (Transaction tx = graph.beginTx()) {
                Neo4jHelper.createUniqueIndexIfNotExist(graph, SettlerNode.label(), SettlerNode.UUID_PROPERTY);
                tx.success();
            }
            try (Transaction tx = graph.beginTx()) {
                Neo4jHelper.createUniqueIndexIfNotExist(graph, SettlerNode.label(), SettlerNode.ID_PROPERTY);
                tx.success();
            }
            try (Transaction tx = graph.beginTx()) {
                Neo4jHelper.createUniqueIndexIfNotExist(graph, DynamicLabel.label("ID_GENERATOR"), "name");
                tx.success();
            }
            setupIdGenerator("SETTLER_ID");
            processor.setProperty("settler.repository.setup", true);
            processor.save();
        }

        SettlerRegister settlerRegister = new SettlerRegister(settlerRepo, executor, graph);
        eventBus.register(settlerRegister);
    }

    private void setupIdGenerator(String generatorName) {
        try (Transaction tx = database.getGraph().beginTx()) {
            Result r = database.getGraph().execute("MATCH (sid: ID_GENERATOR {name:'" + generatorName + "'}) "
                    + "RETURN sid "
                    + "LIMIT 1");
            if (!r.hasNext()) {
                database.getGraph().execute("CREATE (sid: ID_GENERATOR {name:'" + generatorName + "', nextId: 0})");
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
        if (economyProvider != null) {
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

        if (w != null) {
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

        return database.getGraph();
    }

}
