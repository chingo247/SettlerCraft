/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi;

import com.chingo247.menuapi.menu.CategoryMenu;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IPlugin;
import com.chingo247.xplatform.core.IWorld;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.Direction;
import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.settlercraft.core.event.async.AsyncEventManager;
import com.chingo247.settlercraft.core.exception.SettlerCraftException;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldDAO;
import com.chingo247.settlercraft.core.util.KeyPool;
import com.chingo247.structureapi.exception.StructureAPIException;
import com.chingo247.structureapi.exception.StructureException;
import com.chingo247.structureapi.menu.StructurePlanMenuFactory;
import com.chingo247.structureapi.menu.StructurePlanMenuReader;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerDAO;
import com.chingo247.settlercraft.core.persistence.dao.world.DefaultWorldFactory;
import com.chingo247.settlercraft.core.persistence.neo4j.Neo4jHelper;
import com.chingo247.structureapi.persistence.dao.placement.PlacementDataNode;
import com.chingo247.structureapi.persistence.dao.placement.PlacementRelTypes;
import com.chingo247.structureapi.persistence.dao.structure.StructureDAO;
import com.chingo247.structureapi.persistence.dao.structure.StructureNode;
import com.chingo247.structureapi.persistence.dao.structure.StructureOwnerType;
import com.chingo247.structureapi.persistence.dao.structure.StructureWorldNode;
import com.chingo247.structureapi.platforms.bukkit.IConfigProvider;
import com.chingo247.structureapi.construction.asyncworldedit.AsyncDemolishingPlacement;
import com.chingo247.structureapi.plan.placement.Placement;
import com.chingo247.structureapi.plan.StructurePlan;
import com.chingo247.structureapi.construction.asyncworldedit.AsyncPlacement;
import com.chingo247.structureapi.construction.asyncworldedit.AsyncPlacementCallback;
import com.chingo247.structureapi.construction.asyncworldedit.SCJobEntry;
import com.chingo247.structureapi.construction.options.Options;
import com.chingo247.structureapi.event.StructureCreateEvent;
import com.chingo247.structureapi.event.async.StructureJobAddedEvent;
import com.chingo247.structureapi.event.async.StructureJobCanceledEvent;
import com.chingo247.structureapi.event.async.StructureJobCompleteEvent;
import com.chingo247.structureapi.event.async.StructureJobStartedEvent;
import com.chingo247.structureapi.plan.placement.FilePlacement;
import com.chingo247.structureapi.plan.StructurePlanManager;
import com.chingo247.structureapi.plan.SubStructuredPlan;
import com.chingo247.structureapi.event.StructurePlansLoadedEvent;
import com.chingo247.structureapi.event.StructurePlansReloadEvent;
import com.chingo247.structureapi.plan.placement.options.PlaceOptions;
import com.chingo247.structureapi.plan.placement.options.DemolishingOptions;
import com.chingo247.structureapi.plan.placement.DemolishingPlacement;
import com.chingo247.structureapi.plan.placement.node.NodePlacementHandler;
import com.chingo247.structureapi.plan.placement.node.NodePlacementHandlerFactory;
import com.chingo247.structureapi.util.PlacementUtil;
import com.chingo247.xplatform.core.IColor;
import com.chingo247.xplatform.core.ILocation;
import com.chingo247.xplatform.core.IPlayer;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.Files;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.util.com.google.common.collect.Lists;
import org.dom4j.DocumentException;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;
import org.primesoft.asyncworldedit.PlayerEntry;
import org.primesoft.asyncworldedit.blockPlacer.BlockPlacer;
import org.primesoft.asyncworldedit.blockPlacer.IBlockPlacerListener;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;

/**
 *
 * @author Chingo
 */
public class StructureAPI {

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
    private final Lock claimLock = new ReentrantLock();
    private StructurePlanMenuFactory planMenuFactory;
    private final ExecutorService executor;
    private final KeyPool<Long> pool;
    private CategoryMenu planMenu;
    private final GraphDatabaseService graph;

    private boolean isLoadingPlans = false;
    private static StructureAPI instance;

    private Map<Long, StructureEntry> tasks;
    private final Lock jobLock = new ReentrantLock();
    private final IColor COLOR;

    private StructureAPI() {
        this.executor = SettlerCraft.getInstance().getExecutor();
        this.platform = SettlerCraft.getInstance().getPlatform();
        this.graph = SettlerCraft.getInstance().getNeo4j();

        this.pool = new KeyPool<>(executor);

        // Now register the GlobalPlanManager
        this.structureDAO = new StructureDAO(graph);
        this.worldDAO = new WorldDAO(graph);
        this.settlerDAO = new SettlerDAO(graph);
        this.COLOR = platform.getChatColors();
        this.tasks = new HashMap<>();

        EventManager.getInstance().getEventBus().register(new StructurePlanManagerHandler());

        AsyncEventManager.getInstance().register(new StructureEventHandler());

        setupSchema();
        AsyncWorldEditMain.getInstance().getBlockPlacer().addListener(new IBlockPlacerListener() {

            @Override
            public void jobAdded(JobEntry je) {
                // DO NOTHING
            }

            @Override
            public void jobRemoved(JobEntry je) {
                if (je instanceof SCJobEntry) {
                    // I FIRED THIS JOB!
                    SCJobEntry jobEntry = (SCJobEntry) je;
                    boolean isCanceled = false;
                    jobLock.lock();
                    try {
                        StructureEntry entry = tasks.get(jobEntry.getTaskID());
                        if (entry != null) {
                            isCanceled = entry.isCanceled();
                            if (isCanceled) {
                                if (entry.isWasChecked()) {
                                    isCanceled = false; // dont fire it again...
                                } else {
                                    entry.setWasChecked(true);
                                }
                            }
                        }
                    } finally {
                        jobLock.unlock();
                    }

                    if (isCanceled) {
                        AsyncEventManager.getInstance().post(new StructureJobCanceledEvent(jobEntry.getTaskID(), jobEntry.getJobId()));
                    }
                }
            }
        });

    }

    private void setupSchema() {
        try (Transaction tx = graph.beginTx()) {
            Neo4jHelper.createIndexIfNotExist(graph, StructureNode.LABEL, StructureNode.DELETED_AT_PROPERTY);

            tx.success();
        }
    }

    public static StructureAPI getInstance() {
        if (instance == null) {
            instance = new StructureAPI();
        }
        return instance;
    }

    public void initialize() throws DocumentException, SettlerCraftException {
        StructurePlanMenuReader reader = new StructurePlanMenuReader();
        planMenu = reader.read(new File(getWorkingDirectory(), "menu.xml"));
        planMenuFactory = new StructurePlanMenuFactory(platform, planMenu);
        loadPlans();
    }

    public void loadPlans() {
        if (loadLock.tryLock()) {
            try {
                StructurePlanManager.getInstance().loadPlans();
            } finally {
                loadLock.unlock();
            }

        }

    }

    public boolean isLoadingPlans() {
        return isLoadingPlans;
    }

    public StructurePlan getPlanById(String planId) {
        return StructurePlanManager.getInstance().getPlan(planId);
    }

    /**
     * Creates a structure and sets the given player as MASTER owner
     *
     * @param world The world
     * @param plan The plan
     * @param position The position
     * @param direction The direction
     * @param owner The owner of the structure
     * @return The created Structure
     * @throws StructureException
     */
    public Structure createStructure(World world, StructurePlan plan, Vector position, Direction direction, Player owner) throws StructureException {
        Preconditions.checkNotNull(world);
        Preconditions.checkNotNull(plan);
        Preconditions.checkNotNull(direction);
        Preconditions.checkNotNull(position);

        if (plan instanceof SubStructuredPlan) {
            //TODO Add Structure Restrictions HERE! 
            System.out.println("Structure is SubstructurePlan!");
            return createSubstructured(world, (SubStructuredPlan) plan, position, direction, owner);
        } else {
            return createSimpleStructure(world, plan, position, direction, owner);
        }
    }
    
     private Structure createSimpleStructure(World world, StructurePlan plan, Vector position, Direction direction, Player owner) throws StructureException {

         
        claimLock.lock();
        Structure structure = null;
        try {
            //TODO Add Structure Restrictions HERE! 
            Placement placement = plan.getPlacement();
            checkLocation(world, placement, position);
            IWorld w = platform.getServer().getWorld(world.getName());
            try (Transaction tx = graph.beginTx()) {

                // Perform area check!
                Vector min = position;
                Vector max = PlacementUtil.getPoint2Right(min, direction, placement.getCuboidRegion().getMaximumPoint());
                CuboidRegion structureRegion = new CuboidRegion(min, max);

                // World exists?
                WorldNode worldNode = worldDAO.find(w.getUUID());
                if (worldNode == null) {
                    worldDAO.addWorld(w.getName(), w.getUUID());
                    worldNode = worldDAO.find(w.getUUID());
                    if (worldNode == null) {
                        tx.success(); // End here
                        throw new StructureAPIException("Something went wrong during creation of the 'WorldNode'"); // SHOULD NEVER HAPPEN
                    }
                }
                com.chingo247.settlercraft.core.World scWorld = DefaultWorldFactory.instance().createWorld(worldNode);
                if (structureDAO.hasStructuresWithin(scWorld, structureRegion)) {
                    tx.success(); // End here
                    throw new StructureException("Structure overlaps another structure...");
                }
                ArrayList<StructureNode> created = new ArrayList<>(1);
                place(tx, plan.getName(), plan.getPrice(), placement, worldNode, position, direction, owner, created);

                if(created.isEmpty()) {
                    tx.failure();
                } else {
                    StructureNode structureNode = created.get(0);
                    structure = DefaultStructureFactory.instance().makeStructure(structureNode);
                    tx.success();
                }
            }
        } finally {
            claimLock.unlock();
        }

        if (structure != null) {
            EventManager.getInstance().getEventBus().post(new StructureCreateEvent(structure));
        }

        return structure;
    }

    private Structure createSubstructured(World world, SubStructuredPlan plan, Vector position, Direction direction, Player owner) throws StructureException {
        Structure mainstructure = null;

        claimLock.lock();
        try {
            System.out.println("Checklocation recursively");
            checkLocationRecursive(plan, world, position, direction, owner);

            IWorld w = platform.getServer().getWorld(world.getName());
            try (Transaction tx = graph.beginTx()) {

                // World exists?
                WorldNode worldNode = worldDAO.find(w.getUUID());
                if (worldNode == null) {
                    worldDAO.addWorld(w.getName(), w.getUUID());
                    worldNode = worldDAO.find(w.getUUID());
                    if (worldNode == null) {
                        tx.success(); // End here
                        throw new StructureAPIException("Something went wrong during creation of the 'WorldNode'"); // SHOULD NEVER HAPPEN
                    }
                }

                List<StructureNode> created = Lists.newArrayList();
                placeRecursively(tx, plan, worldNode, position, direction, owner, created);

                if (!created.isEmpty()) {
                    mainstructure = DefaultStructureFactory.instance().makeStructure(created.get(0));
                }
                tx.success();
            }

        } finally {
            claimLock.unlock();
        }
        return mainstructure;
    }

    private void placeRecursively(Transaction tx, SubStructuredPlan plan, WorldNode worldNode, Vector position, Direction direction, Player owner, List<StructureNode> created) throws StructureException {
        Placement mainPlacement = plan.getPlacement();
        place(tx, plan.getName(), 0, mainPlacement, worldNode, position, direction, owner, created);
        int sub = 0;
        for (Placement p : plan.getSubPlacements()) {
            place(tx, plan.getName() + "-" + sub++, 0, p, worldNode, position, direction, owner, created);
        }
        for (StructurePlan p : plan.getSubStructurePlans()) {
            if (p instanceof SubStructuredPlan) {
                placeRecursively(tx, (SubStructuredPlan) p, worldNode, position, direction, owner, created);
            } else {
                place(tx, p.getName(), 0,p.getPlacement(), worldNode, position, direction, owner, created);
            }
        }

    }

    private void place(Transaction tx, String name, double price, Placement placement, WorldNode worldNode, Vector position, Direction direction, Player owner, List<StructureNode> created) throws StructureException {
        // Perform area check!
        Vector min = position.add(placement.getPosition());
        Vector max = PlacementUtil.getPoint2Right(min, direction, placement.getCuboidRegion().getMaximumPoint());
        CuboidRegion structureRegion = new CuboidRegion(min, max);

        platform.getConsole().printMessage(COLOR.red() + " overlap is ignored! (commented out)");
//        com.chingo247.settlercraft.core.World scWorld = DefaultWorldFactory.instance().createWorld(worldNode);
//        if (structureDAO.hasStructuresWithin(scWorld, structureRegion)) {
//            tx.failure();// End here
//            throw new StructureException("Structure overlaps another structure...");
//        }
        platform.getConsole().printMessage(COLOR.red() + " default price still 0, fix before release!");
        // Create the StructureNode - Where it all starts...
        StructureNode structureNode = structureDAO.addStructure(name, structureRegion, direction, price);
        StructureWorldNode structureWorldNode = new StructureWorldNode(worldNode);
        structureWorldNode.addStructure(structureNode);

        // Add the placement!
        Node placementNode = graph.createNode(PlacementDataNode.LABEL);
        
        NodePlacementHandler handler = NodePlacementHandlerFactory.getInstance().getHandler(placement.getTypeName());
        placementNode.setProperty(PlacementDataNode.WIDTH_PROPERTY, placement.getWidth());
        placementNode.setProperty(PlacementDataNode.HEIGHT_PROPERTY, placement.getHeight());
        placementNode.setProperty(PlacementDataNode.LENGTH_PROPERTY, placement.getLength());
        placementNode.setProperty(PlacementDataNode.TYPE_PROPERTY, placement.getTypeName());
        handler.setNodeProperties(placement, placementNode);
        
        
        structureNode.getRawNode().createRelationshipTo(placementNode, DynamicRelationshipType.withName(PlacementRelTypes.USES));

        // Add owner!
        if (owner != null) {
            SettlerNode settler = settlerDAO.find(owner.getUniqueId());
            if (settler == null) {
                throw new RuntimeException("Settler was null!"); // SHOULD NEVER HAPPEN AS SETTLERS ARE ADDED AT MOMENT OF FIRST LOGIN
            }
            structureNode.addOwner(settler, StructureOwnerType.MASTER);
        }

        Structure structure = DefaultStructureFactory.instance().makeStructure(structureNode);
        File structureDir = new File(getStructuresDirectory(worldNode.getName()), String.valueOf(structure.getId()));

        // Copy resources
        if (placement instanceof FilePlacement) {
            FilePlacement filePlacement = (FilePlacement) placement;
            File[] files = filePlacement.getFiles();
            try {
                for (File f : files) {
                    Files.copy(f, new File(structureDir, f.getName()));
                }
            } catch (IOException ex) {
                tx.failure();
                Logger.getLogger(StructureAPI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("created structure #" + structureNode.getId());

        created.add(structureNode);

    }

    private void checkLocationRecursive(SubStructuredPlan plan, World world, Vector position, Direction direction, Player owner) throws StructureException {
        // Check Self
        checkLocation(world, plan.getPlacement(), position);

        // Check SubPlacement
        for (Placement p : plan.getSubPlacements()) {
            checkLocation(world, p, position);
        }

        for (StructurePlan p : plan.getSubStructurePlans()) {
            if (p instanceof SubStructuredPlan) {
                checkLocationRecursive((SubStructuredPlan) p, world, position, direction, owner);
            } else {
                checkLocation(world, p.getPlacement(), position);
            }
        }

    }

    private void checkLocation(World world, Placement p, Vector position) throws StructureException {
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

    /**
     * Creates a structure without any owners
     *
     * @param world The world
     * @param plan The plan
     * @param position The position
     * @param direction The direction
     * @return The created Structure
     * @throws StructureException
     */
    public Structure createStructure(World world, StructurePlan plan, Vector position, Direction direction) throws StructureException {
        return createStructure(world, plan, position, direction, null);
    }

    /**
     * Creates a structure with the given placement
     *
     * @param world The world
     * @param placement The placement
     * @param position The position
     * @param direction The direction
     * @return The created Structure
     * @throws StructureException
     */
    public Structure createStructure(World world, Placement placement, Vector position, Direction direction) throws StructureException {
        throw new UnsupportedOperationException();
    }

    /**
     * Builds the structure
     *
     * @param structure The structure that has to be build
     * @param player The player uuid, The UUID will be used to register this
     * build-operation. This method does NOT check if the player is allowed to
     * build
     * @param session The session to use
     * @param options The options, use {@link Options#defaultOptions() } to get
     * the default options
     * @param force whether the current construction state should be ignored.
     * Therefore forcefully stops and starts a build operation
     */
    public void build(final Structure structure, final UUID player, final EditSession session, final PlaceOptions options, boolean force) {
        Preconditions.checkNotNull(structure, "Structure may not be null");
        Preconditions.checkNotNull(player, "UUID may not be null");
        Preconditions.checkNotNull(session, "EditSession may not be null");
        Preconditions.checkNotNull(options, "Options may not be null");

        pool.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                PlayerEntry playerEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(player);
                tasks.put(structure.getId(), new StructureEntry(-1, false, player));

                AsyncPlacement placement = new AsyncPlacement(playerEntry, structure.getPlan().getPlacement(), new AsyncPlacementCallback() {

                    @Override
                    public void onJobAdded(int jobId) {
                        AsyncEventManager.getInstance().post(new StructureJobAddedEvent(structure.getId(), jobId, false));
                    }
                }, structure);
                placement.rotate(structure.getDirection());
                placement.place(session, structure.getCuboidRegion().getMinimumPoint(), options);
            }
        });
    }

    public void demolish(final Structure structure, final UUID player, final EditSession session, final DemolishingOptions options, boolean force) {
        pool.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                PlayerEntry playerEntry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(player);
                Placement p = structure.getPlan().getPlacement();
                p.rotate(structure.getDirection());
                CuboidRegion region = p.getCuboidRegion();

                DemolishingPlacement dp = new DemolishingPlacement(region.getMaximumPoint());
                tasks.put(structure.getId(), new StructureEntry(-1, true, player));
                AsyncDemolishingPlacement placement = new AsyncDemolishingPlacement(playerEntry, dp, new AsyncPlacementCallback() {

                    @Override
                    public void onJobAdded(int jobId) {
                        AsyncEventManager.getInstance().post(new StructureJobAddedEvent(structure.getId(), jobId, true));
                    }
                }, structure);

                // Set Negative MASK
                // Set Negative not natural MASK
                placement.place(session, structure.getCuboidRegion().getMinimumPoint(), options);
            }
        });
    }

    /**
     * Stops a structure Build/Demolish operation
     *
     * @param player The player, which will only be used for feedback. May be
     * null
     * @param structure The structure
     * @param force
     */
    public void stop(final Player player, final Structure structure, boolean force) {
        Preconditions.checkNotNull(structure, "Structure may not be null");
        pool.execute(structure.getId(), new Runnable() {

            @Override
            public void run() {
                UUID uuid = null;
                Integer jobId = null;

                jobLock.lock();
                try {
                    StructureEntry entry = tasks.get(structure.getId());
                    if (entry == null) {
                        return;
                    } else {
                        entry.setIsCanceled(true);
                    }

                    uuid = entry.getWhoStarted();
                    jobId = entry.getJobId();
                } finally {
                    jobLock.unlock();
                }

                if (uuid != null && jobId != null) {
                    PlayerEntry entry = AsyncWorldEditMain.getInstance().getPlayerManager().getPlayer(uuid);
                    if (entry != null) {
                        BlockPlacer blockPlacer = AsyncWorldEditMain.getInstance().getBlockPlacer();
                        blockPlacer.cancelJob(entry, jobId);
                        if (player != null) {
                            player.print("Stopping structure #" + structure.getId());
                        }
                    }
                }
            }
        });
    }

    /**
     * Stops a structure Build/Demolish
     *
     * @param structure
     * @param force
     */
    public void stop(final Structure structure, boolean force) {
        stop(null, structure, force);
    }

    protected final File getStructuresDirectory(String world) {
        File f = new File(getWorkingDirectory().getAbsolutePath() + "//worlds//" + world + "//structures");
        f.mkdirs(); // creates if not exists..
        return f;
    }

    public final File getPlanDirectory() {
        return new File(getWorkingDirectory(), PLANS_DIRECTORY);
    }

    public CategoryMenu createPlanMenu() {
        return planMenuFactory.createPlanMenu();
    }

    public void registerStructureAPIPlugin(IPlugin plugin) throws StructureAPIException {
        if (this.plugin != null) {
            throw new StructureAPIException("Already registered a Plugin for the StructureAPI, NOTE that this method should only be used by StructureAPI Plugin itself!");
        }
        this.plugin = plugin;
    }

    public APlatform getPlatform() {
        return platform;
    }

    public List<StructurePlan> getStructurePlans() {
        return StructurePlanManager.getInstance().getPlans();
    }

    protected File getWorkingDirectory() {
        return plugin.getDataFolder();
    }

    public void registerConfigProvider(IConfigProvider configProvider) {
        this.config = configProvider;
    }

    public IConfigProvider getConfig() {
        return config;
    }

    /**
     *
     * @author Chingo
     */
    private class StructureEventHandler {

        StructureEventHandler() {
        }

        @Subscribe
        @AllowConcurrentEvents
        public void onJobAddedEvent(StructureJobAddedEvent jobAddedEvent) {
            long structureId = jobAddedEvent.getStructure();
            UUID uuid;
            jobLock.lock();
            try {
                int jobId = jobAddedEvent.getJobId();
                StructureEntry entry = tasks.get(structureId);
                entry.setJobId(jobId);
                uuid = entry.getWhoStarted();
            } finally {
                jobLock.unlock();
            }

            Structure structure;
            try (Transaction tx = graph.beginTx()) {
                StructureNode structureNode = structureDAO.find(structureId);
                structureNode.setConstructionStatus(ConstructionStatus.QUEUED);
                structure = DefaultStructureFactory.instance().makeStructure(structureNode);
                tx.success();
            }

            if (uuid != null) {
                IPlayer player = platform.getServer().getPlayer(uuid);
                if (player != null) {
                    String status = getStatusString(structure);
                    player.sendMessage(status);
                }
            }

        }

        @Subscribe
        @AllowConcurrentEvents
        public void onJobCanceledEvent(StructureJobCanceledEvent jobCanceledEvent) {
            long structureId = jobCanceledEvent.getStructure();
            jobLock.lock();
            try {
                tasks.remove(structureId);
            } finally {
                jobLock.unlock();
            }

            Structure structure;
            List<IPlayer> owners = new ArrayList<>();
            try (Transaction tx = graph.beginTx()) {
                StructureNode structureNode = structureDAO.find(structureId);
                structureNode.setConstructionStatus(ConstructionStatus.STOPPED);
                structure = DefaultStructureFactory.instance().makeStructure(structureNode);
                List<SettlerNode> settlers = structureNode.getOwners();
                for (SettlerNode settlerNode : settlers) {
                    IPlayer player = platform.getPlayer(settlerNode.getId());
                    owners.add(player);
                }
                tx.success();
            }

            String status = getStatusString(structure);

            // Tell the starter
            for (IPlayer p : owners) {
                p.sendMessage(status);
            }
        }

        @Subscribe
        @AllowConcurrentEvents
        public void onJobCompleteEvent(StructureJobCompleteEvent jobCompleteEvent) {
            boolean isDemolishing = false;
            long structureId = jobCompleteEvent.getStructure();

            jobLock.lock();
            try {
                tasks.remove(structureId);
            } finally {
                jobLock.unlock();
            }

            // Set the status
            Structure structure;
            List<IPlayer> owners = new ArrayList<>();
            try (Transaction tx = graph.beginTx()) {
                StructureNode structureNode = structureDAO.find(structureId);
                if (isDemolishing) {
                    structureNode.setConstructionStatus(ConstructionStatus.REMOVED);
                } else {
                    structureNode.setConstructionStatus(ConstructionStatus.COMPLETED);
                }
                structure = DefaultStructureFactory.instance().makeStructure(structureNode);
                List<SettlerNode> settlers = structureNode.getOwners();
                for (SettlerNode settlerNode : settlers) {
                    IPlayer player = platform.getPlayer(settlerNode.getId());
                    owners.add(player);
                }
                tx.success();
            }

            String status = getStatusString(structure);

            // Tell the starter
            for (IPlayer p : owners) {
                p.sendMessage(status);
            }

        }

        @Subscribe
        @AllowConcurrentEvents
        public void onJobStartedEvent(StructureJobStartedEvent jobStartedEvent) {
            long structureId = jobStartedEvent.getStructure();
            boolean isDemolishing = false;
            jobLock.lock();
            try {
                StructureEntry entry = tasks.get(structureId);
                isDemolishing = entry.isDemolishing();
            } finally {
                jobLock.unlock();
            }

            // Set the status!
            Structure structure;
            List<IPlayer> owners = new ArrayList<>();
            try (Transaction tx = graph.beginTx()) {
                StructureNode structureNode = structureDAO.find(structureId);
                if (isDemolishing) {
                    structureNode.setConstructionStatus(ConstructionStatus.DEMOLISHING);
                } else {
                    structureNode.setConstructionStatus(ConstructionStatus.BUILDING);
                }
                structure = DefaultStructureFactory.instance().makeStructure(structureNode);

                List<SettlerNode> settlers = structureNode.getOwners();
                for (SettlerNode settlerNode : settlers) {
                    IPlayer ply = platform.getPlayer(settlerNode.getId());
                    owners.add(ply);
                }
                tx.success();
            }

            String status = getStatusString(structure);
            // Tell the new status!
            for (IPlayer p : owners) {
                p.sendMessage(status);
            }
        }

        /**
         * Sends the status of this structure to given player
         *
         * @param structure The structure
         * @param player The player to tell
         */
        public String getStatusString(Structure structure) {
            String statusString;
            ConstructionStatus status = structure.getConstructionStatus();
            switch (status) {
                case BUILDING:
                    statusString = COLOR.yellow() + "BUILDING " + COLOR.reset() + "#" + COLOR.gold() + structure.getId() + COLOR.blue() + " " + structure.getName();
                    break;
                case DEMOLISHING:
                    statusString = COLOR.yellow() + "DEMOLISHING " + COLOR.reset() + "#" + COLOR.gold() + structure.getId() + COLOR.blue() + " " + structure.getName();
                    break;
                case COMPLETED:
                    statusString = COLOR.green() + "COMPLETE " + COLOR.reset() + "#" + COLOR.gold() + structure.getId() + COLOR.blue() + " " + structure.getName();
                    break;
                case ON_HOLD:
                    statusString = COLOR.red() + "ON HOLD " + COLOR.reset() + "#" + COLOR.gold() + structure.getId() + COLOR.blue() + " " + structure.getName();
                    break;
                case QUEUED:
                    statusString = COLOR.yellow() + "QUEUED " + COLOR.reset() + "#" + COLOR.gold() + structure.getId() + COLOR.blue() + " " + structure.getName();
                    break;
                case REMOVED:
                    statusString = COLOR.red() + "REMOVED " + COLOR.reset() + "#" + COLOR.gold() + structure.getId() + COLOR.blue() + " " + structure.getName();
                    break;
                case STOPPED:
                    statusString = COLOR.red() + "STOPPED " + COLOR.reset() + "#" + COLOR.gold() + structure.getId() + COLOR.blue() + " " + structure.getName();
                    break;
                default:
                    statusString = status.name();
            }
            return statusString;
        }

    }

    private class StructureEntry {

        private int jobId;
        private boolean isDemolishing;
        private UUID whoStarted;
        private boolean isCanceled = false;
        private boolean wasChecked = false;

        public StructureEntry(int jobId, boolean isDemolishing, UUID whoStarted) {
            this.jobId = jobId;
            this.isDemolishing = isDemolishing;
            this.whoStarted = whoStarted;
        }

        public void setIsCanceled(boolean isCanceled) {
            this.isCanceled = isCanceled;
        }

        public boolean isCanceled() {
            return isCanceled;
        }

        public boolean isWasChecked() {
            return wasChecked;
        }

        public void setWasChecked(boolean wasChecked) {
            this.wasChecked = wasChecked;
        }

        public int getJobId() {
            return jobId;
        }

        public UUID getWhoStarted() {
            return whoStarted;
        }

        public boolean isDemolishing() {
            return isDemolishing;
        }

        public void setIsDemolishing(boolean isDemolishing) {
            this.isDemolishing = isDemolishing;
        }

        public void setJobId(int jobId) {
            this.jobId = jobId;
        }

        public void setWhoStarted(UUID whoStarted) {
            this.whoStarted = whoStarted;
        }

    }

    private class StructurePlanManagerHandler {

        @Subscribe
        @AllowConcurrentEvents
        public void onLoadingStructurePlans(StructurePlansReloadEvent event) {
            isLoadingPlans = true;
            platform.getConsole().printMessage(COLOR.yellow() + "[SettlerCraft]: " + COLOR.reset() + "Loading StructurePlans");
        }

        @Subscribe
        @AllowConcurrentEvents
        public void onStructurePlansLoaded(StructurePlansLoadedEvent event) {
            for (StructurePlan plan : StructurePlanManager.getInstance().getPlans()) {
                planMenuFactory.load(plan);
            }
            isLoadingPlans = false;
            platform.getConsole().printMessage(COLOR.yellow() + "[SettlerCraft]: " + COLOR.reset() + "Plans are loaded!");
        }
    }

}
