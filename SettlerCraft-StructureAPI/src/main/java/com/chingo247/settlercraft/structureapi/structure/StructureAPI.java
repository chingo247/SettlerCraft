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

import com.chingo247.settlercraft.structureapi.model.structure.StructureStatus;
import com.chingo247.settlercraft.structureapi.model.structure.StructureNode;
import com.chingo247.menuapi.menu.CategoryMenu;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IPlugin;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.settlercraft.core.exception.SettlerCraftException;
import com.chingo247.settlercraft.structureapi.exception.StructureAPIException;
import com.chingo247.settlercraft.structureapi.exception.StructureException;
import com.chingo247.settlercraft.structureapi.menu.StructurePlanMenuFactory;
import com.chingo247.settlercraft.structureapi.menu.StructurePlanMenuReader;
import com.chingo247.settlercraft.core.model.WorldNode;
import com.chingo247.settlercraft.core.persistence.neo4j.Neo4jHelper;
import com.chingo247.settlercraft.structureapi.platforms.IConfigProvider;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structureapi.structure.plan.IStructurePlan;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlanManager;
import com.chingo247.settlercraft.structureapi.event.StructurePlansLoadedEvent;
import com.chingo247.settlercraft.structureapi.event.StructurePlansReloadEvent;
import com.chingo247.settlercraft.structureapi.exception.StructureRestrictionViolationException;
import com.chingo247.settlercraft.structureapi.model.structure.Structure;
import com.chingo247.settlercraft.structureapi.platforms.services.AsyncEditSessionFactoryProvider;
import com.chingo247.settlercraft.structureapi.structure.construction.asyncworldedit.AsyncPlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.SchematicPlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.schematic.Schematic;
import com.chingo247.settlercraft.structureapi.structure.plan.schematic.SchematicManager;
import com.chingo247.settlercraft.structureapi.structure.restriction.StructureRestriction;
import com.chingo247.xplatform.core.IColors;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.dom4j.DocumentException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.playerManager.PlayerEntry;
import org.primesoft.asyncworldedit.worldedit.AsyncEditSessionFactory;

/**
 *
 * @author Chingo
 */
public class StructureAPI implements IStructureAPI {

    public static final String STRUCTURE_PLAN_FILE_NAME = "plan.xml";
    public static final String PLUGIN_NAME = "SettlerCraft";
    public static final String PLANS_DIRECTORY = "plans";

    private final APlatform platform;
    private IPlugin plugin;
    private IConfigProvider config;

    private final Lock loadLock = new ReentrantLock();
    private StructurePlanMenuFactory planMenuFactory;
    private AsyncEditSessionFactoryProvider sessionFactoryProvider;

    private CategoryMenu planMenu;
    private final GraphDatabaseService graph;

    private boolean isLoadingPlans = false;
    private static StructureAPI instance;

    private final Set<StructureRestriction> restrictions;

    private final Logger LOG = Logger.getLogger(getClass().getName());

    private final IColors COLORS;
    private Level logLevel;
    private final Map<String,StructureManager> structureManagers;
//    private final SubstructureHandler substructureHandler;

    private StructureAPI() {
        this.platform = SettlerCraft.getInstance().getPlatform();
        this.graph = SettlerCraft.getInstance().getNeo4j();
        this.restrictions = Sets.newHashSet();
        this.structureManagers = Maps.newHashMap();

        // Now register the GlobalPlanManager
        this.COLORS = platform.getChatColors();

        EventManager.getInstance().getEventBus().register(new StructurePlanManagerHandler());

        setupSchema();
        this.logLevel = Level.SEVERE;
//        this.substructureHandler = new SubstructureHandler(graph, worldDAO, structureDAO, settlerDAO, this);
    }
    
    /**
     * Gets the {@link StructureManager} for a world, never returns null
     * @param w The world
     * @return The StructureManager
     */
    private synchronized StructureManager getStructureManager(World w) {
        StructureManager structureManager = structureManagers.get(w.getName());
        if(structureManager == null) {
            structureManager = new StructureManager(w, instance);
            structureManagers.put(w.getName(), structureManager);
        }
        return structureManager;
    }

    private void setupSchema() {
        // Create indexes, each index creation needs to be executed in a seperate transaction!
        try (Transaction tx = graph.beginTx()) {
            Neo4jHelper.createIndexIfNotExist(graph, StructureNode.LABEL, StructureNode.DELETED_AT_PROPERTY);
            tx.success();
        }
        try (Transaction tx = graph.beginTx()) {
            Neo4jHelper.createUniqueIndexIfNotExist(graph, StructureNode.LABEL, StructureNode.ID_PROPERTY);
            tx.success();
        }
    }

    /**
     * Gets the StructureAPI instance
     *
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
     *
     * @throws DocumentException
     * @throws SettlerCraftException
     */
    public void initialize() throws DocumentException, SettlerCraftException {
        // Set states back to stopped... if not completed or removed
        resetStates();
        // Load StructurePlans
        StructurePlanMenuReader reader = new StructurePlanMenuReader();
        
        planMenu = reader.read(new File(getWorkingDirectory(), "menu.xml"));
        planMenuFactory = new StructurePlanMenuFactory(platform, planMenu);
        
        reload();
    }
    
    @Override
    public void checkRestrictions(Player player, World world, CuboidRegion region) throws StructureRestrictionViolationException {
        for(StructureRestriction restriction : restrictions) {
            restriction.check(player, world, region);
        }
    }

    private void resetStates() {
        try (Transaction tx = graph.beginTx()) {

            Map<String, Object> params = Maps.newHashMap();
            // Enforce integers
            params.put("completed", (Integer) StructureStatus.COMPLETED.getStatusId());
            params.put("removed", (Integer) StructureStatus.REMOVED.getStatusId());
            params.put("stopped", (Integer) StructureStatus.COMPLETED.getStatusId());

            String query = "MATCH (s:" + StructureNode.LABEL.name() + ") "
                    + "WHERE NOT s." + StructureNode.CONSTRUCTION_STATUS_PROPERTY + " =  {completed} "
                    + "AND NOT s." + StructureNode.CONSTRUCTION_STATUS_PROPERTY + " =  {removed}"
                    + "SET s." + StructureNode.CONSTRUCTION_STATUS_PROPERTY + " =  {stopped}";
            graph.execute(query, params);

            tx.success();
        }
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
    public StructurePlanManager getStructurePlanManager() {
        return StructurePlanManager.getInstance();
    }

    @Override
    public Structure createStructure(IStructurePlan plan, World world, Vector position, Direction direction, Player owner) throws StructureException {
        StructureManager structureManager = getStructureManager(world);
        return structureManager.createStructure(null, plan, position, direction, owner);
    }

    @Override
    public Structure createStructure(IStructurePlan plan, World world, Vector position, Direction direction) throws StructureException {
        StructureManager structureManager = getStructureManager(world);
        return structureManager.createStructure(null, plan, position, direction, null);
    }

    @Override
    public Structure createStructure(Placement placement, World world, Vector position, Direction direction) throws StructureException {
        StructureManager structureManager = getStructureManager(world);
        return structureManager.createStructure(null, placement, position, direction, null);
    }

    @Override
    public Structure createStructure(Placement placement, World world, Vector position, Direction direction, Player owner) throws StructureException {
       StructureManager structureManager = getStructureManager(world);
       return structureManager.createStructure(null, placement, position, direction, owner);
    }

    @Override
    public Structure createSubstructure(Structure parent, IStructurePlan plan, World world, Vector position, Direction direction, Player owner) throws StructureException {
        StructureManager structureManager = getStructureManager(world);
        return structureManager.createStructure(parent, plan, position, direction, owner);
    }

    @Override
    public Structure createSubstructure(Structure parent, IStructurePlan plan, World world, Vector position, Direction direction) throws StructureException {
        StructureManager structureManager = getStructureManager(world);
        return structureManager.createStructure(parent, plan, position, direction, null);
    }

    @Override
    public Structure createSubstructure(Structure parent, Placement placement, World world, Vector position, Direction direction, Player owner) throws StructureException {
        StructureManager structureManager = getStructureManager(world);
        return structureManager.createStructure(parent, placement, position, direction, owner);
    }

    @Override
    public Structure createSubstructure(Structure parent, Placement placement, World world, Vector position, Direction direction) throws StructureException {
        StructureManager structureManager = getStructureManager(world);
        return structureManager.createStructure(parent, placement, position, direction, null);
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

    @Override
    public File getWorkingDirectory() {
        return plugin.getDataFolder();
    }

    public void registerConfigProvider(IConfigProvider configProvider) {
        this.config = configProvider;
    }
    
    public void setLogLevel(Level level) {
        this.logLevel = level;
    }
    
    public Level getLogLevel() {
        return logLevel;
    }

    public List<StructureRestriction> getRestrictions() {
        return new ArrayList<>(restrictions);
    }
    
    @Override
    public boolean isQueueLocked(UUID player) {
        BlockPlacer bp = (BlockPlacer) AsyncWorldEditMain.getInstance().getBlockPlacer();
        
        Class bpClass = bp.getClass();
        
        for(Field f : bpClass.getFields()) {
            System.out.println("Field: " + f.getName());
        }
        
        Field f;
        try {
            f = bpClass.getDeclaredField("m_lockedQueues");
            f.setAccessible(true);
            
            HashSet s = (HashSet) f.get(bp);
            
            PlayerEntry playerEntry = new PlayerEntry(null,player);
            return s.contains(playerEntry);
            
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(StructureAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    

    

    final File getDirectoryForStructure(WorldNode worldNode, StructureNode structureNode) {
        File structuresDirectory = getStructuresDirectory(worldNode.getName());
        File structureDir = new File(structuresDirectory, String.valueOf(structureNode.getId()));
        return structureDir;
    }

    

    @Override
    public IConfigProvider getConfig() {
        return config;
    }

    @Override
    public File getGenerationDirectory() {
        return new File(getWorkingDirectory(), "generate");
    }

    @Override
    public void addRestriction(StructureRestriction structureRestriction) {
        synchronized (restrictions) {
            this.restrictions.add(structureRestriction);
        }
    }

    @Override
    public void removeRestriction(StructureRestriction structureRestriction) {
        synchronized (restrictions) {
            this.restrictions.remove(structureRestriction);
        }
    }

    private class StructurePlanManagerHandler {

        @Subscribe
        @AllowConcurrentEvents
        public void onLoadingStructurePlans(StructurePlansReloadEvent event) {
            isLoadingPlans = true;
            platform.getServer().broadcast(COLORS.yellow() + "[SettlerCraft]: "+COLORS.reset()+"Plans are being reloaded...");
//            platform.getConsole().printMessage(COLORS.yellow() + "[SettlerCraft]: " + COLORS.reset() + "Loading StructurePlans");
        }

        @Subscribe
        @AllowConcurrentEvents
        public void onStructurePlansLoaded(StructurePlansLoadedEvent event) {
            planMenuFactory = new StructurePlanMenuFactory(platform, planMenu);
            planMenuFactory.clearAll();
            for (IStructurePlan plan : StructurePlanManager.getInstance().getPlans()) {
                planMenuFactory.load(plan);
            }
            isLoadingPlans = false;
            platform.getServer().broadcast(COLORS.yellow() + "[SettlerCraft]: "+COLORS.reset()+"Reload plans complete!");
//            platform.getConsole().printMessage(COLORS.yellow() + "[SettlerCraft]: " + COLORS.reset() + "Plans are loaded!");
        }
    }

    
    
    public void registerAsyncEditSesionFactoryProvider(AsyncEditSessionFactoryProvider provider) {
        Preconditions.checkNotNull(provider, "Provider was null");
        Preconditions.checkArgument(sessionFactoryProvider == null, "Already registered a AsyncEditSessionFactoryProvider");
        this.sessionFactoryProvider = provider;
    }
    
    @Override
    public AsyncEditSessionFactory getSessionFactory() {
        return sessionFactoryProvider.getFactory();
    }

    @Override
    public SchematicPlacement loadSchematic(File schematicFile) throws IOException {
        Schematic schematic = SchematicManager.getInstance().getOrLoadSchematic(schematicFile);
        return new SchematicPlacement(schematic);
    }
    
    
    @Override
    public AsyncPlacement makeAsync(UUID player, Placement placement) {
        if(player == null) {
            return new AsyncPlacement(PlayerEntry.CONSOLE, placement);
        }
        return new AsyncPlacement(AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(player), placement);
    }
    
    

}
