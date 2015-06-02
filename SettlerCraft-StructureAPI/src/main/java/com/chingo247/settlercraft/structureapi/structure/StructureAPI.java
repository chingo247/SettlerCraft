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
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import com.chingo247.settlercraft.core.persistence.neo4j.Neo4jHelper;
import com.chingo247.settlercraft.structureapi.persistence.dao.StructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.platforms.IConfigProvider;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.event.StructureCreateEvent;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlanManager;
import com.chingo247.settlercraft.structureapi.event.StructurePlansLoadedEvent;
import com.chingo247.settlercraft.structureapi.event.StructurePlansReloadEvent;
import com.chingo247.settlercraft.structureapi.persistence.dao.IStructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureOwnerType;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureWorldNode;
import com.chingo247.settlercraft.structureapi.platforms.services.AsyncEditSessionFactoryProvider;
import com.chingo247.settlercraft.structureapi.structure.construction.asyncworldedit.AsyncPlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.DefaultStructurePlan;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.FilePlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.SchematicPlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.schematic.Schematic;
import com.chingo247.settlercraft.structureapi.structure.plan.schematic.SchematicManager;
import com.chingo247.settlercraft.structureapi.structure.restriction.StructureRestriction;
import com.chingo247.settlercraft.structureapi.util.PlacementUtil;
import com.chingo247.xplatform.core.IColors;
import com.chingo247.xplatform.core.ILocation;
import com.chingo247.xplatform.core.IWorld;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.Files;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
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
import org.apache.commons.io.FilenameUtils;
import org.dom4j.DocumentException;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
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

    private final IStructureDAO structureDAO;
    private final WorldDAO worldDAO;
    private final SettlerDAO settlerDAO;

    private final APlatform platform;
    private IPlugin plugin;
    private IConfigProvider config;

    private final Lock loadLock = new ReentrantLock();
    private final Lock structureLock = new ReentrantLock();
    private StructurePlanMenuFactory planMenuFactory;
    private AsyncEditSessionFactoryProvider sessionFactoryProvider;

    private CategoryMenu planMenu;
    private final GraphDatabaseService graph;

    private boolean isLoadingPlans = false;
    private static StructureAPI instance;

    private final Set<StructureRestriction> restrictions;

    private final Logger LOG = Logger.getLogger(getClass().getName());

    private final IColors COLORS;
//    private final SubstructureHandler substructureHandler;

    private StructureAPI() {
        this.platform = SettlerCraft.getInstance().getPlatform();
        this.graph = SettlerCraft.getInstance().getNeo4j();
        this.restrictions = Sets.newHashSet();

        // Now register the GlobalPlanManager
        this.structureDAO = new StructureDAO(graph);
        this.worldDAO = new WorldDAO(graph);
        this.settlerDAO = new SettlerDAO(graph);
        this.COLORS = platform.getChatColors();

        EventManager.getInstance().getEventBus().register(new StructurePlanManagerHandler());

        setupSchema();

//        this.substructureHandler = new SubstructureHandler(graph, worldDAO, structureDAO, settlerDAO, this);
    }

    private void setupSchema() {
        try (Transaction tx = graph.beginTx()) {
            Neo4jHelper.createIndexIfNotExist(graph, StructureNode.LABEL, StructureNode.DELETED_AT_PROPERTY);
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

    private void resetStates() {
        try (Transaction tx = graph.beginTx()) {

            Map<String, Object> params = Maps.newHashMap();
            // Enforce integers
            params.put("completed", (Integer) ConstructionStatus.COMPLETED.getStatusId());
            params.put("removed", (Integer) ConstructionStatus.REMOVED.getStatusId());
            params.put("stopped", (Integer) ConstructionStatus.COMPLETED.getStatusId());

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
    public Structure createStructure(StructurePlan plan, World world, Vector position, Direction direction, Player owner) throws StructureException {
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(plan);
        Preconditions.checkNotNull(direction);
        Preconditions.checkNotNull(position);

        Structure structure;
        structureLock.lock();
        try {
//            // Check the default restrictions first
            Placement placement = plan.getPlacement();

            checkDefaultRestrictions(placement, world, position, direction);
            
            Vector min = position;
            Vector max = PlacementUtil.getPoint2Right(min, direction, placement.getCuboidRegion().getMaximumPoint());
            CuboidRegion structureRegion = new CuboidRegion(min, max);
            checkStructureRestrictions(owner, world, structureRegion);

            WorldNode worldNode = findWorld(world);

            try (Transaction tx = graph.beginTx()) {

                // Check for overlap with other structures
                if (structureDAO.hasStructuresWithin(world, structureRegion)) {
                    tx.success(); // End here
                    throw new StructureException("Structure overlaps another structure...");
                }

                // Create the StructureNode - Where it all starts...
                StructureNode structureNode = structureDAO.addStructure(plan.getName(), position, structureRegion, direction, plan.getPrice());
                StructureWorldNode structureWorldNode = new StructureWorldNode(worldNode);
                structureWorldNode.addStructure(structureNode);

                // Add owner!
                if (owner != null) {
                    SettlerNode settler = settlerDAO.find(owner.getUniqueId());

                    if (settler == null) {
                        tx.failure();
                        throw new RuntimeException("Settler was null!"); // SHOULD NEVER HAPPEN AS SETTLERS ARE ADDED AT MOMENT OF FIRST LOGIN
                    }
                    structureNode.addOwner(settler, StructureOwnerType.MASTER);
                }

                try {
                    moveResources(worldNode, structureNode, plan);
                } catch (IOException ex) {
                    // rollback...
                    File structureDir = getDirectoryForStructure(worldNode, structureNode);
                    structureDir.delete();
                    tx.failure();
                    Logger.getLogger(StructureAPI.class.getName()).log(Level.SEVERE, "Error occured during structure creation... rolling back changes made", ex);
                }

                tx.success();

                structure = DefaultStructureFactory.getInstance().makeStructure(structureNode);
            }
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
    public Structure createStructure(Placement placement, World world, Vector position, Direction direction) throws StructureException {
        return createStructure(placement, world, position, direction, null);
    }

    @Override
    public Structure createStructure(Placement placement, World world, Vector position, Direction direction, Player owner) throws StructureException {
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(placement);
        Preconditions.checkNotNull(direction);
        Preconditions.checkNotNull(position);

        Structure structure;
        structureLock.lock();
        try {
//            // Check the default restrictions first
            checkDefaultRestrictions(placement, world, position, direction);
            Vector min = position;
            Vector max = PlacementUtil.getPoint2Right(min, direction, placement.getCuboidRegion().getMaximumPoint());
            CuboidRegion structureRegion = new CuboidRegion(min, max);
            checkStructureRestrictions(owner, world, structureRegion);
            
            
            WorldNode worldNode = findWorld(world);

            try (Transaction tx = graph.beginTx()) {

                // Check for overlap with other structures
                if (structureDAO.hasStructuresWithin(world, structureRegion)) {
                    tx.success(); // End here
                    throw new StructureException("Structure overlaps another structure...");
                }

                // Create the StructureNode - Where it all starts...
                StructureNode structureNode = structureDAO.addStructure(placement.getClass().getSimpleName(), position, structureRegion, direction, 0.0);
                StructureWorldNode structureWorldNode = new StructureWorldNode(worldNode);
                structureWorldNode.addStructure(structureNode);

                // Add owner!
                if (owner != null) {
                    SettlerNode settler = settlerDAO.find(owner.getUniqueId());
                    if (settler == null) {
                        tx.failure();
                        throw new RuntimeException("Settler was null!"); // SHOULD NEVER HAPPEN AS SETTLERS ARE ADDED AT MOMENT OF FIRST LOGIN
                    }
                    structureNode.addOwner(settler, StructureOwnerType.MASTER);
                }
                File structureDir = getDirectoryForStructure(worldNode, structureNode);
                try {
                    File placementPlanFile = new File(structureDir, "structureplan.xml");
                    PlacementPlan plan = new PlacementPlan(UUID.randomUUID().toString(), placementPlanFile, placement);
                    moveResources(worldNode, structureNode, plan);
                } catch (IOException ex) {
                    // rollback...
                    structureDir.delete();
                    tx.failure();
                    Logger.getLogger(StructureAPI.class.getName()).log(Level.SEVERE, "Error occured during structure creation... rolling back changes made", ex);
                }

                tx.success();

                structure = DefaultStructureFactory.getInstance().makeStructure(structureNode);
            }
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
    public Structure createSubstructure(Structure parentStructure, StructurePlan plan, World world, Vector position, Direction direction, Player owner) throws StructureException {
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(plan);
        Preconditions.checkNotNull(direction);
        Preconditions.checkNotNull(position);
        Preconditions.checkArgument(parentStructure.getWorld().equals(world.getName()), "Structure must be in the same world...");

        Structure substructure;
        structureLock.lock();
        try {
//            // Check the default restrictions first
            Placement placement = plan.getPlacement();
            checkDefaultRestrictions(placement, world, position, direction);
            
            Vector min = position;
            Vector max = PlacementUtil.getPoint2Right(min, direction, placement.getCuboidRegion().getMaximumPoint());
            CuboidRegion structureRegion = new CuboidRegion(min, max);
            checkStructureRestrictions(owner, world, structureRegion);
            
            
            WorldNode worldNode = findWorld(world);

            CuboidRegion parentRegion = parentStructure.getCuboidRegion();


            if (!(parentRegion.contains(min) && parentRegion.contains(max))) {
                throw new StructureException("Structure overlaps structure #" + parentStructure.getId() + ", but does not fit within it's boundaries");
            }

            boolean hasWithin;
            try (Transaction tx = graph.beginTx()) {
                hasWithin = structureDAO.hasSubstructuresWithin(parentStructure, world, structureRegion);
                if (hasWithin) {
                    tx.success(); // End here
                    throw new StructureException("Structure overlaps another structure...");
                }
            

            // Deep check overlap
//            ThreadSafeEditSession editSession = AsyncWorldEditUtil.getAsyncSessionFactory().getThreadSafeEditSession(world, -1);
//            
//            Vector min = structureRegion.getMinimumPoint();
//            Vector max = structureRegion.getMaximumPoint();
//            
//            for (int y = min.getBlockY() + 1; y < max.getBlockY(); y++) {
//                for (int x = min.getBlockX(); x < max.getBlockX(); x++) {
//                    for (int z = min.getBlockZ(); z < max.getBlockZ(); z++) {
//                        Vector pos = new Vector(x,y,z);
//                        BaseBlock b = editSession.getBlock(pos);
//                        if (b == null || b.getId() == 0) {
//                            continue;
//                        }
//                        throw new StructureException("Can't substructure overlaps blocks of #" + parentStructure.getId() + " " + parentStructure.getName());
//                    }
//                }
//            }

                // Create the StructureNode - Where it all starts...
                StructureNode substructureNode = structureDAO.addStructure(plan.getName(), position, structureRegion, direction, plan.getPrice());
                StructureWorldNode structureWorldNode = new StructureWorldNode(worldNode);
                structureWorldNode.addStructure(substructureNode);

                StructureNode parentStructureNode = structureDAO.find(parentStructure.getId());
                parentStructureNode.addSubstructure(substructureNode);

                // Add owner!
                if (owner != null) {
                    SettlerNode settler = settlerDAO.find(owner.getUniqueId());
                    if (settler == null) {
                        tx.failure();
                        throw new RuntimeException("Settler was null!"); // SHOULD NEVER HAPPEN AS SETTLERS ARE ADDED AT MOMENT OF FIRST LOGIN
                    }
                    substructureNode.addOwner(settler, StructureOwnerType.MASTER);
                }
                
                // Inherit ownership
                Node rawNode = substructureNode.getRawNode();
                for (Relationship rel : rawNode.getRelationships(DynamicRelationshipType.withName("OwnedBy"))) {
                    Node n = rel.getOtherNode(rawNode);
                    if(!n.hasLabel(SettlerNode.LABEL)) {
                        continue;
                    }
                    
                    SettlerNode ownerNode = new SettlerNode(n);
                    Integer typeId = (Integer) rel.getProperty("Type");
                    StructureOwnerType type = StructureOwnerType.match(typeId);
                    substructureNode.addOwner(ownerNode, type);
                }

                try {
                    moveResources(worldNode, substructureNode, plan);
                } catch (IOException ex) {
                    // rollback...
                    File structureDir = getDirectoryForStructure(worldNode, substructureNode);
                    structureDir.delete();
                    tx.failure();
                    Logger.getLogger(StructureAPI.class.getName()).log(Level.SEVERE, "Error occured during structure creation... rolling back changes made", ex);
                }

                tx.success();

                substructure = DefaultStructureFactory.getInstance().makeStructure(substructureNode);
            }
        } finally {
            structureLock.unlock();
        }

        // If not null.. A structre has been created!
        if (substructure != null) {
            EventManager.getInstance().getEventBus().post(new StructureCreateEvent(substructure));
        }

        return substructure;
    }

    @Override
    public Structure createSubstructure(Structure parentStructure, StructurePlan plan, World world, Vector position, Direction direction) throws StructureException {
        return createSubstructure(parentStructure, plan, world, position, direction, null);
    }

    @Override
    public Structure createSubstructure(Structure parentStructure, Placement placement, World world, Vector position, Direction direction, Player owner) throws StructureException {
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(placement);
        Preconditions.checkNotNull(direction);
        Preconditions.checkNotNull(position);

        Structure substructure;
        structureLock.lock();
        try {
//            // Check the default restrictions first
            checkDefaultRestrictions(placement, world, position, direction);
            
            Vector min = position;
            Vector max = PlacementUtil.getPoint2Right(min, direction, placement.getCuboidRegion().getMaximumPoint());
            CuboidRegion structureRegion = new CuboidRegion(min, max);
            checkStructureRestrictions(owner, world, structureRegion);
            
            WorldNode worldNode = findWorld(world);
            CuboidRegion parentRegion = parentStructure.getCuboidRegion();

            if (!(parentRegion.contains(min) && parentRegion.contains(max))) {
                throw new StructureException("Structure overlaps structure #" + parentStructure.getId() + ", but does not fit within it's boundaries");
            }

            boolean hasWithin;
            try (Transaction tx = graph.beginTx()) {
                hasWithin = structureDAO.hasSubstructuresWithin(parentStructure, world, structureRegion);
                if (hasWithin) {
                    tx.success(); // End here
                    throw new StructureException("Structure overlaps another structure...");
                }
                
           


                // Create the StructureNode - Where it all starts...
                StructureNode substructureNode = structureDAO.addStructure(placement.getClass().getSimpleName(), position, structureRegion, direction, 0.0);
                StructureWorldNode structureWorldNode = new StructureWorldNode(worldNode);
                structureWorldNode.addStructure(substructureNode);

                StructureNode parentStructureNode = structureDAO.find(parentStructure.getId());
                parentStructureNode.addSubstructure(substructureNode);

                // Add owner!
                if (owner != null) {
                    SettlerNode settler = settlerDAO.find(owner.getUniqueId());
                    if (settler == null) {
                        tx.failure();
                        throw new RuntimeException("Settler was null!"); // SHOULD NEVER HAPPEN AS SETTLERS ARE ADDED AT MOMENT OF FIRST LOGIN
                    }
                    substructureNode.addOwner(settler, StructureOwnerType.MASTER);
                }
                
                // Inherit ownership
                Node rawNode = substructureNode.getRawNode();
                for (Relationship rel : rawNode.getRelationships(DynamicRelationshipType.withName("OwnedBy"))) {
                    Node n = rel.getOtherNode(rawNode);
                    if(!n.hasLabel(SettlerNode.LABEL)) {
                        continue;
                    }
                    
                    SettlerNode ownerNode = new SettlerNode(n);
                    Integer typeId = (Integer) rel.getProperty("Type");
                    StructureOwnerType type = StructureOwnerType.match(typeId);
                    substructureNode.addOwner(ownerNode, type);
                }
                

                File structureDir = getDirectoryForStructure(worldNode, substructureNode);
                try {
                    File placementPlanFile = new File(structureDir, "structureplan.xml");
                    PlacementPlan plan = new PlacementPlan(UUID.randomUUID().toString(), placementPlanFile, placement);
                    moveResources(worldNode, substructureNode, plan);
                } catch (IOException ex) {
                    // rollback...
                    structureDir.delete();
                    tx.failure();
                    Logger.getLogger(StructureAPI.class.getName()).log(Level.SEVERE, "Error occured during structure creation... rolling back changes made", ex);
                }

                tx.success();

                substructure = DefaultStructureFactory.getInstance().makeStructure(substructureNode);
            }
        } finally {
            structureLock.unlock();
        }

        // If not null.. A structre has been created!
        if (substructure != null) {
            EventManager.getInstance().getEventBus().post(new StructureCreateEvent(substructure));
        }
        return substructure;
    }

    @Override
    public Structure createSubstructure(Structure structure, Placement placement, World world, Vector position, Direction direction) throws StructureException {
        return createSubstructure(structure, placement, world, position, direction, null);
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

    /**
     * Checks if the placement is allowed to be placed. Placement should not
     * overlap the world's spawn and should also be placed at a height > 1 and
     * the top may not by higher than the world's max height
     *
     * @param p The placement
     * @param world The world
     * @param position The position of the placement
     * @param direction The direction
     */
    protected void checkDefaultRestrictions(Placement p, World world, Vector position, Direction direction) throws StructureException {
        Vector min = p.getCuboidRegion().getMinimumPoint().add(position);
        Vector max = min.add(p.getCuboidRegion().getMaximumPoint());
        CuboidRegion placementDimension = new CuboidRegion(min, max);
        

        // Below the world?s
        if (placementDimension.getMinimumPoint().getBlockY() <= 1) {
            throw new StructureException("Structure must be placed at a minimum height of 1");
        }

        // Exceeds world height limit?
        if (placementDimension.getMaximumPoint().getBlockY() > world.getMaxY()) {
            throw new StructureException("Structure will reach above the world's max height (" + world.getMaxY() + ")");
        }

        // Check for overlap on the world's 'SPAWN'
        IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(world.getName());
        ILocation l = w.getSpawn();
        Vector spawnPos = new Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        if (placementDimension.contains(spawnPos)) {
            throw new StructureException("Structure overlaps the world's spawn...");
        }
        
        
        
    }
    
    public void checkStructureRestrictions(Player player, World world, CuboidRegion region) throws StructureException {
        for(StructureRestriction structureRestriction : restrictions) {
            structureRestriction.check(player, world, region);
        }
    }

    protected final void moveResources(WorldNode worldNode, StructureNode structureNode, StructurePlan plan) throws IOException {
        // Give this structure a directory!
        File structureDir = getDirectoryForStructure(worldNode, structureNode);
        structureDir.mkdirs();

        Files.copy(plan.getFile(), new File(structureDir, "structureplan.xml"));
        Placement placement = plan.getPlacement();

        // Move the resources if applicable!
        if (placement instanceof FilePlacement) {
            FilePlacement filePlacement = (FilePlacement) placement;
            File[] files = filePlacement.getFiles();
            for (File f : files) {
                Files.copy(f, new File(structureDir, f.getName()));
            }
        }
    }

    protected final File getDirectoryForStructure(WorldNode worldNode, StructureNode structureNode) {
        File structuresDirectory = getStructuresDirectory(worldNode.getName());
        File structureDir = new File(structuresDirectory, String.valueOf(structureNode.getId()));
        return structureDir;
    }

    protected final WorldNode findWorld(World world) {
        IWorld w = getPlatform().getServer().getWorld(world.getName());
        if (w == null) {
            throw new RuntimeException("World was null");
        }

        try (Transaction tx = graph.beginTx()) {
            WorldNode worldNode = worldDAO.find(w.getUUID());
            if (worldNode == null) {
                worldDAO.addWorld(w.getName(), w.getUUID());
                worldNode = worldDAO.find(w.getUUID());
                if (worldNode == null) {
                    throw new StructureAPIException("Something went wrong during creation of the 'WorldNode'"); // SHOULD NEVER HAPPEN
                }
            }
            tx.success();
            return worldNode;
        }
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

    private class PlacementPlan extends DefaultStructurePlan {

        PlacementPlan(String id, File planFile, Placement placement) {
            super(id, planFile, placement);
            setCategory("Other");
            setDescription("None");
            setPrice(0.0);
            setName(FilenameUtils.getBaseName(planFile.getName()));
        }

    }
    
    public void registerAsyncEditSesionFactoryProvider(AsyncEditSessionFactoryProvider provider) {
        Preconditions.checkNotNull(provider, "Provider was null");
        Preconditions.checkArgument(sessionFactoryProvider == null, "Already registered a AsyncEditSessionFactoryProvider");
        this.sessionFactoryProvider = provider;
    }
    
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
