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
package com.chingo247.settlercraft.structureapi.structure;

import com.chingo247.menuapi.menu.CategoryMenu;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IPlugin;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.settlercraft.core.exception.SettlerCraftException;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldDAO;
import com.chingo247.settlercraft.structureapi.exception.StructureAPIException;
import com.chingo247.settlercraft.structureapi.exception.StructureException;
import com.chingo247.settlercraft.structureapi.menu.StructurePlanMenuFactory;
import com.chingo247.settlercraft.structureapi.menu.StructurePlanMenuReader;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerDAO;
import com.chingo247.settlercraft.core.persistence.neo4j.Neo4jHelper;
import com.chingo247.settlercraft.structureapi.persistence.dao.StructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.platforms.bukkit.IConfigProvider;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.event.StructureCreateEvent;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlanManager;
import com.chingo247.settlercraft.structureapi.event.StructurePlansLoadedEvent;
import com.chingo247.settlercraft.structureapi.event.StructurePlansReloadEvent;
import com.chingo247.settlercraft.structureapi.structure.session.PlayerSessionManager;
import com.chingo247.xplatform.core.IColors;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import org.dom4j.DocumentException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class StructureAPI implements IStructureAPI {

    public static final String STRUCTURE_PLAN_FILE_NAME = "plan.xml";
    public static final String PLUGIN_NAME = "SettlerCraft";
    public static final String PLANS_DIRECTORY = "plans";

    private final StructureDAO structureDAO;
    private final WorldDAO worldDAO;
    private final SettlerDAO settlerDAO;

    private final APlatform platform;
    private IPlugin plugin;
    private IConfigProvider config;

    private final Lock loadLock = new ReentrantLock();
    private final Lock structureLock = new ReentrantLock();
    private StructurePlanMenuFactory planMenuFactory;
    private final ExecutorService executor;
   
    private CategoryMenu planMenu;
    private final GraphDatabaseService graph;

    private boolean isLoadingPlans = false;
    private static StructureAPI instance;

    
    private final Logger LOG = Logger.getLogger(getClass().getName());
    
    private final IColors COLORS;
    private final StructureHandler structureHandler;
//    private final SubstructureHandler substructureHandler;

    private StructureAPI() {
        this.executor = SettlerCraft.getInstance().getExecutor();
        this.platform = SettlerCraft.getInstance().getPlatform();
        this.graph = SettlerCraft.getInstance().getNeo4j();

       

        // Now register the GlobalPlanManager
        this.structureDAO = new StructureDAO(graph);
        this.worldDAO = new WorldDAO(graph);
        this.settlerDAO = new SettlerDAO(graph);
        this.COLORS = platform.getChatColors();

       

        EventManager.getInstance().getEventBus().register(new StructurePlanManagerHandler());

        setupSchema();
        
        this.structureHandler = new StructureHandler(graph, worldDAO, structureDAO, settlerDAO, this);
//        this.substructureHandler = new SubstructureHandler(graph, worldDAO, structureDAO, settlerDAO, this);
        
        
    }

    private void setupSchema() {
        try (Transaction tx = graph.beginTx()) {
            Neo4jHelper.createIndexIfNotExist(graph, StructureNode.LABEL, StructureNode.DELETED_AT_PROPERTY);
            tx.success();
        }
    }

    /**
     * Gets the StructureAPI instance
     * @return 
     */
    public static IStructureAPI getInstance() {
        if (instance == null) {
            instance = new StructureAPI();
        }
        return instance;
    }

    /**
     * Fires all process that need to start at the startup of the StructureAPI
     * @throws DocumentException
     * @throws SettlerCraftException 
     */
    public void initialize() throws DocumentException, SettlerCraftException {
        StructurePlanMenuReader reader = new StructurePlanMenuReader();
        planMenu = reader.read(new File(getWorkingDirectory(), "menu.xml"));
        planMenuFactory = new StructurePlanMenuFactory(platform, planMenu);
        reload();
    }

    /**
     * Reloads all plans from the StructurePlan directory
     */
    @Override
    public void reload() {
        if (loadLock.tryLock()) {
            try {
                StructurePlanManager.getInstance().loadPlans();
            } finally {
                loadLock.unlock();
            }

        }

    }

   
    @Override
    public boolean isLoading() {
        return isLoadingPlans;
    }
    
    @Override
    public ConstructionManager getConstructionManager() {
        return ConstructionManager.getInstance();
    }
    
    @Override
    public PlayerSessionManager getPlayerSessionManager() {
        return PlayerSessionManager.getInstance();
    }

    @Override
    public StructurePlanManager getStructurePlanManager() {
        return StructurePlanManager.getInstance();
    }
   
    @Override
    public Structure createStructure(StructurePlan plan, World world, Vector position, Direction direction, Player owner) throws StructureException {
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(plan);
        Preconditions.checkNotNull(direction);
        Preconditions.checkNotNull(position);

        Structure structure;
        structureLock.lock();
        try {
//            if (plan instanceof SubStructuresPlan) {
////                System.out.println("Handling substructures plan!");
////                structure = substructureHandler.handleStructure((SubStructuresPlan) plan, world, position, direction, owner);
////                throw new StructureException(PLUGIN_NAME)
//                
//                
//            } else {
                System.out.println("Handling simple plan!");
                structure = structureHandler.handleStructure(plan, world, position, direction, owner);
//            }
        } finally {
            structureLock.unlock();
        }

        // If not null.. A structre has been created!
        if (structure != null) {
            EventManager.getInstance().getEventBus().post(new StructureCreateEvent(structure));
        }

        return structure;
    }
    
    @Override
    public Structure createStructure(StructurePlan plan, World world, Vector position, Direction direction) throws StructureException {
        return createStructure(plan, world, position, direction, null);
    }

    @Override
    public Structure createStructure(Placement placement, World world, Vector position, Direction direction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Structure createStructure(Placement placement, World world, Vector position, Direction direction, Player owner) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Structure createSubstructure(Structure structure, StructurePlan plan, World world, Vector position, Direction direction, Player owner) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Structure createSubstructure(Structure structure, StructurePlan plan, World world, Vector position, Direction direction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Structure createSubstructure(Structure structure, Placement placement, World world, Vector position, Direction direction, Player owner) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Structure createSubstructure(Structure structure, Placement placement, World world, Vector position, Direction direction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public final File getStructuresDirectory(String world) {
        File f = new File(getWorkingDirectory().getAbsolutePath() + "//worlds//" + world + "//structures");
        f.mkdirs(); // creates if not exists..
        return f;
    }

    @Override
    public final File getPlanDirectory() {
        return new File(getWorkingDirectory(), PLANS_DIRECTORY);
    }

    @Override
    public CategoryMenu createPlanMenu() {
        return planMenuFactory.createPlanMenu();
    }

    public void registerStructureAPIPlugin(IPlugin plugin) throws StructureAPIException {
        if (this.plugin != null) {
            throw new StructureAPIException("Already registered a Plugin for the StructureAPI, NOTE that this method should only be used by StructureAPI Plugin itself!");
        }
        this.plugin = plugin;
    }

    @Override
    public APlatform getPlatform() {
        return platform;
    }

    protected File getWorkingDirectory() {
        return plugin.getDataFolder();
    }

    public void registerConfigProvider(IConfigProvider configProvider) {
        this.config = configProvider;
    }

    @Override
    public IConfigProvider getConfig() {
        return config;
    }

    

    

    

    

    

    private class StructurePlanManagerHandler {

        @Subscribe
        @AllowConcurrentEvents
        public void onLoadingStructurePlans(StructurePlansReloadEvent event) {
            isLoadingPlans = true;
            platform.getConsole().printMessage(COLORS.yellow() + "[SettlerCraft]: " + COLORS.reset() + "Loading StructurePlans");
        }

        @Subscribe
        @AllowConcurrentEvents
        public void onStructurePlansLoaded(StructurePlansLoadedEvent event) {
            for (StructurePlan plan : StructurePlanManager.getInstance().getPlans()) {
                planMenuFactory.load(plan);
            }
            isLoadingPlans = false;
            platform.getConsole().printMessage(COLORS.yellow() + "[SettlerCraft]: " + COLORS.reset() + "Plans are loaded!");
        }
    }

}
