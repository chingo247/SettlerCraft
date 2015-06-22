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
package com.chingo247.settlercraft.structureapi.commands;

import com.chingo247.menuapi.menu.CategoryMenu;
import com.chingo247.menuapi.menu.util.ShopUtil;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.settlercraft.core.exception.CommandException;
import com.chingo247.settlercraft.core.model.BaseSettlerRepository;
import com.chingo247.settlercraft.core.model.WorldNode;
import com.chingo247.settlercraft.core.model.interfaces.IBaseSettler;
import com.chingo247.settlercraft.core.model.interfaces.IBaseSettlerRepository;
import com.chingo247.settlercraft.core.util.KeyPool;
import com.chingo247.settlercraft.structureapi.event.StructureAddOwnerEvent;
import com.chingo247.settlercraft.structureapi.event.StructureRemoveOwnerEvent;
import com.chingo247.settlercraft.structureapi.exception.ConstructionException;
import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureOwner;
import com.chingo247.settlercraft.structureapi.model.owner.StructureOwnerRepository;
import com.chingo247.settlercraft.structureapi.model.owner.StructureOwnerType;
import com.chingo247.settlercraft.structureapi.model.structure.StructureRepository;
import com.chingo247.settlercraft.structureapi.platforms.services.PermissionManager;
import com.chingo247.settlercraft.structureapi.model.world.StructureWorldRepository;
import com.chingo247.settlercraft.structureapi.structure.IStructureAPI;
import com.chingo247.settlercraft.structureapi.model.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureOwnerRepository;
import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureOwnership;
import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureRepository;
import com.chingo247.settlercraft.structureapi.model.interfaces.IStructureWorldRepository;
import com.chingo247.settlercraft.structureapi.model.structure.Structure;
import com.chingo247.settlercraft.structureapi.model.structure.StructureStatus;
import static com.chingo247.settlercraft.structureapi.model.structure.StructureStatus.COMPLETED;
import static com.chingo247.settlercraft.structureapi.model.structure.StructureStatus.ON_HOLD;
import com.chingo247.settlercraft.structureapi.model.util.StructureRelations;
import com.chingo247.settlercraft.structureapi.model.world.StructureWorldNode;
import com.chingo247.settlercraft.structureapi.structure.StructureAPI;
import com.chingo247.settlercraft.structureapi.structure.plan.IStructurePlan;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlanManager;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.SchematicPlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.BuildOptions;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.DemolishingOptions;
import com.chingo247.settlercraft.structureapi.structure.plan.schematic.FastClipboard;
import com.chingo247.settlercraft.structureapi.structure.plan.util.PlanGenerator;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IColors;
import com.chingo247.xplatform.core.ICommandSender;
import com.chingo247.xplatform.core.ILocation;
import com.chingo247.xplatform.core.IPlayer;
import com.chingo247.xplatform.core.IWorld;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class StructureCommands {

    private static final Logger LOG = Logger.getLogger(StructureCommands.class.getSimpleName());
    private static final int MAX_LINES = 10;
    private static final Comparator<String> ALPHABETICAL_ORDER = new Comparator<String>() {

        @Override
        public int compare(String o1, String o2) {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    };

    private final IStructureAPI structureAPI;
    private final IStructureRepository structureRepository;
    private final IStructureWorldRepository worldRepository;
    private final IStructureOwnerRepository structureOwnerRepository;

    private final PermissionManager permissionManager;
    private final IColors COLOR;

    private final IBaseSettlerRepository settlerRepository;
    private final GraphDatabaseService graph;
    private final UUID console;
    private final KeyPool<UUID> playerPool;
    private final APlatform platform;
    private final CommandHelper commandHelper;

    public StructureCommands(IStructureAPI structureAPI, ExecutorService executorService, GraphDatabaseService graph) {
        this.structureAPI = structureAPI;
        this.permissionManager = PermissionManager.getInstance();
        this.COLOR = structureAPI.getPlatform().getChatColors();
        this.structureRepository = new StructureRepository(graph);
        this.worldRepository = new StructureWorldRepository(graph);
        this.settlerRepository = new BaseSettlerRepository(graph);
        this.structureOwnerRepository = new StructureOwnerRepository(graph);
        this.graph = graph;
        this.console = UUID.randomUUID();
        this.playerPool = new KeyPool<>(executorService);
        this.platform = structureAPI.getPlatform();
        this.commandHelper = new CommandHelper(platform);
        LOG.setLevel(((StructureAPI) structureAPI).getLogLevel());
    }

    public boolean handle(final ICommandSender sender, final String command, String[] args) throws CommandException {
        if (args.length == 0) {
            throw new CommandException("Too few arguments!");
        }

        final String commandArg = args[0];
        final String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

        UUID uuid;
        if (sender instanceof IPlayer) {
            IPlayer ply = (IPlayer) sender;
            uuid = ply.getUniqueId();
        } else {
            uuid = console;
        }

        playerPool.execute(uuid, new Runnable() {

            @Override
            public void run() {
                try {
                    long start = System.currentTimeMillis();
                    switch (commandArg) {
                        // /stt me 
                        case "me":
                            checkIsPlayer(sender);
                            IPlayer ply = (IPlayer) sender;
                            try (Transaction tx = graph.beginTx()) {
                                IBaseSettler node = settlerRepository.findByUUID(ply.getUniqueId()); // NEVER NULL
                                sender.sendMessage("Your unique id is #" + COLOR.gold() + node.getId());

                                tx.success();
                            }
                            break;
                        case "generate":
                            if (sender instanceof IPlayer) {
                                IPlayer ply2 = (IPlayer) sender;
                                if (!ply2.isOP()) {
                                    ply2.sendMessage("You're not allowed to use this command, OP only");
                                    return;
                                }
                            }
                            generate(commandArgs);
                            break;
                        case "reload":
                            reload(sender, commandArgs);
                            break;
                        case "info":
                            info(sender, commandArgs);
                            break;
//                        case "mask":
//                            checkIsPlayer(sender);
//                            mask((IPlayer) sender, commandArgs);
//                            break;
                        case "build":
                            checkIsPlayer(sender);
                            build((IPlayer) sender, commandArgs);
                            break;
                        case "demolish":
                            checkIsPlayer(sender);
                            demolish((IPlayer) sender, commandArgs);
                            break;
                        case "halt":
                            checkIsPlayer(sender);
                            stop((IPlayer) sender, commandArgs);
                            break;
                        case "list":
                            checkIsPlayer(sender);
                            list((IPlayer) sender, commandArgs);
                            break;
                        case "location":
                            checkIsPlayer(sender);
                            location((IPlayer) sender, commandArgs);
                            break;
                        case "masters":
                            checkIsPlayer(sender);
                            owner((IPlayer) sender, commandArgs, StructureOwnerType.MASTER);
                            break;
                        case "owners":
                            checkIsPlayer(sender);
                            owner((IPlayer) sender, commandArgs, StructureOwnerType.OWNER);
                            break;
                        case "members":
                            checkIsPlayer(sender);
                            owner((IPlayer) sender, commandArgs, StructureOwnerType.MEMBER);
                            break;
                        case "rotate":
                            schematic(sender, commandArgs);
                            break;
                        case "menu":
                            checkIsPlayer(sender);
                            if (!structureAPI.getConfig().isPlanMenuEnabled()) {
                                sender.sendMessage(COLOR.red() + "Plan menu is not enabled");
                                return;
                            }
                            openMenu((IPlayer) sender, commandArgs, true);
                            break;
                        case "shop":
                            checkIsPlayer(sender);
                            if (!structureAPI.getConfig().isPlanMenuEnabled()) {
                                sender.sendMessage(COLOR.red() + "Plan shop is not enabled");
                                return;
                            }
                            openMenu((IPlayer) sender, commandArgs, false);
                            break;
                        default:
                            throw new CommandException("No action known for '/" + command + " " + commandArg);
                    }

                    LOG.log(Level.INFO, "Executed /stt {0} in {1} ms", new Object[]{commandArg, System.currentTimeMillis() - start});

                } catch (CommandException ex) {
                    String[] error = ex.getPlayerErrorMessage();
                    for (int i = 0; i < error.length; i++) {
                        error[i] = COLOR.red() + error[i];
                    }

                    sender.sendMessage(error);
                } catch (Exception ex) { // Catch everything or disappear it will dissappear in the abyss!
                    Logger.getLogger(StructureCommands.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }

        });
        return true;
    }

    private void reload(ICommandSender sender, String[] commandArgs) throws CommandException {
        String help = "Usage: /stt reload plans";
        commandHelper.argumentsInRange(1, 1, commandArgs, "Usage: /stt reload plans");
        
        String reloadArgument = commandArgs[0];
        // reload plans
        if(reloadArgument.equals("plans")) {
            if(!structureAPI.isLoading()) {
                structureAPI.getStructurePlanManager().loadPlans(false);
            } else {
                sender.sendMessage(COLOR.red() + "Already reloading!");
            }
        } else {
            sender.sendMessage(help);
        }
//        else if (reloadArgument.equals("config")) {
//            
//        }
        
    }

    private void schematic(ICommandSender sender, String[] commandArgs) throws CommandException {
        if (sender instanceof IPlayer) {
            IPlayer player = (IPlayer) sender;
            if (!PermissionManager.getInstance().isAllowed(player, PermissionManager.Perms.ROTATE_SCHEMATIC)) {
                sender.sendMessage(COLOR.red() + "You have no permission to do this!");
                return;
            }
        }

        // /stt schematic [structureid] rotate [degrees]
        String usage = "/stt rotate [structure-id][degrees]";
        commandHelper.argumentsInRange(2, 2, commandArgs, usage);
        commandHelper.isLong(commandArgs[0], "Expected a number for [structure-id]but got '" + commandArgs[0] + "'", usage);
        commandHelper.isInt(commandArgs[1], "Expected a number for [degrees] but got '" + commandArgs[1] + "'", usage);

        Long structureId = Long.parseLong(commandArgs[0]);
        Integer degrees = Integer.parseInt(commandArgs[1]);

        commandHelper.isTrue(degrees % 90 == 0, "Argument [degrees] must be a multiple of 90");
        Structure structure = null;
        try (Transaction tx = graph.beginTx()) {

            StructureNode n = structureRepository.findById(structureId);
            if (n == null) {
                sender.sendMessage(COLOR.red() + "unable to find structure with id #" + structureId);
                tx.success();
                return;
            }
            structure = new Structure(n);
            tx.success();
        }

        IStructurePlan plan = structure.getStructurePlan();
        Placement placement = plan.getPlacement();

        commandHelper.isTrue(placement instanceof SchematicPlacement, "Placement type of structure #" + structureId + " is not a schematic");

        SchematicPlacement schematicPlacement = (SchematicPlacement) placement;

        Iterator<File> it = FileUtils.iterateFiles(structureAPI.getPlanDirectory(), new String[]{"schematic"}, true);

        StructurePlanManager spm = StructurePlanManager.getInstance();
        List<IStructurePlan> plans = spm.getPlans();
        List<File> matching = Lists.newArrayList();
        Set<String> done = Sets.newHashSet();
        long hash = schematicPlacement.getSchematic().getHash();
        for (IStructurePlan p : plans) {
            if (p.getPlacement() instanceof SchematicPlacement) {
                SchematicPlacement sp = (SchematicPlacement) p.getPlacement();
                File nextSchematicFile = sp.getSchematic().getFile();
                if (sp.getSchematic().getHash() == hash) {
                    if (!done.contains(nextSchematicFile.getAbsolutePath())) {
                        matching.add(nextSchematicFile);
                        try {
                            FastClipboard.rotateAndWrite(nextSchematicFile, degrees);
                        } catch (IOException ex) {
                            if (sender instanceof Player) {
                                sender.sendMessage(COLOR.red() + "Something went wrong during rotation...");
                            }
                            Logger.getLogger(StructureCommands.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    spm.reload(p.getId()); // Reload placement
                }

            }
        }

        if (matching.isEmpty()) {
            sender.sendMessage(COLOR.red() + "Couldn't find plan for structure #" + structureId);
        } else if (matching.size() == 1) {
            sender.sendMessage(COLOR.white() + "Rotated '" + COLOR.blue() + matching.get(0).getName() + COLOR.reset() + "' by " + degrees + " degrees");
        } else {
            String[] rotatedPlans = new String[matching.size() + 1];
            for (int i = 0; i < rotatedPlans.length; i++) {
                if (i == 0) {
                    rotatedPlans[i] = "The schematics of the following plans have been rotated:";
                } else {
                    rotatedPlans[i] = COLOR.blue() + matching.get(i - 1).getName() + COLOR.reset();
                }
            }
            sender.sendMessage(rotatedPlans);
        }

    }

    private void generate(String[] commandArgs) throws CommandException {
        if (commandArgs.length < 1) {
            throw new CommandException(COLOR.red() + "Too few arguments", COLOR.red() + "/stt generate plans");
        } else if (commandArgs.length > 1) {
            throw new CommandException(COLOR.red() + "Too many arguments", COLOR.red() + "/stt generate plans");
        } else if (!commandArgs[0].equals("plans")) {
            throw new CommandException("Unknown argument '" + commandArgs[0] + "'");
        } else {
            File generationDirectory = StructureAPI.getInstance().getGenerationDirectory();
            PlanGenerator.generate(generationDirectory);
        }

    }

//    private void mask(IPlayer iPlayer, String[] commandArgs) throws CommandException {
//        if (commandArgs.length == 0) {
//            throw new CommandException("Too few arguments!");
//        }
//        String method = commandArgs[0];
//
//
//        switch (method.toLowerCase()) {
//            case "replace":
//                // /stt mask replace [currentMaterial][currentData][newMaterial][newData]
//
//                String helpReplace = "/stt mask replace [currentMaterial][currentData][newMaterial][newData]|<ignoreMaterial>";
//
//                if (commandArgs.length < 5) {
//                    throw new CommandException("Too few arguments!", helpReplace);
//                }
//
//                final int currentMat = getInt(commandArgs[1], "currentMaterial", helpReplace);
//                final int currentData = getInt(commandArgs[2], "currentData", helpReplace);
//                final int newMaterial = getInt(commandArgs[3], "newMaterial", helpReplace);
//                final int newData = getInt(commandArgs[4], "newData", helpReplace);
//                
//                final int ignore = commandArgs.length == 6 ? getInt(commandArgs[5], "<ignoreMaterial>", helpReplace) : -1;
//                
//
//                BuildOptions options = session.getPlaceOptions();
//                options.addBlockMask(new BlockMask() {
//
//                    @Override
//                    public BaseBlock apply(Vector relativePosition, Vector worldPosition, BaseBlock block) {
//                        if(ignore != -1 && block.getId() == ignore) {
//                            return block;
//                        }
//                        
//                        
//                        if ((currentMat < 0 || currentMat == block.getId()) && (currentData < 0 || currentData == block.getData())) {
//
//                                block.setId(newMaterial);
//                                block.setData(newData);
//
//                        }
//                        return block;
//                    }
//                });
//
//                break;
//            case "ignore":
//
//                // /stt mask replace [currentMaterial][currentData][newMaterial][newData]
//                String helpIgnore = "/stt mask ignore [material][data]";
//
//                if (commandArgs.length < 5) {
//                    throw new CommandException("Too few arguments!", helpIgnore);
//                }
//
//                final int matIgnore = getInt(commandArgs[1], "material", helpIgnore);
//                final int datIgnore = getInt(commandArgs[2], "data", helpIgnore);
//
//                BuildOptions options2 = session.getPlaceOptions();
//                options2.addIgnore(new BlockPredicate() {
//
//                    @Override
//                    public boolean evaluate(Vector position, Vector worldPosition, BaseBlock block) {
//                        return ((matIgnore < 0 || matIgnore == block.getId()) && (datIgnore < -1 || datIgnore == block.getData()));
//                    }
//                });
//
//                break;
//        }
//
//    }
//    private int getInt(String number, String argumentName, String help) throws CommandException {
//        int num;
//        try {
//            num = Integer.parseInt(number);
//        } catch (NumberFormatException nfe) {
//            throw new CommandException("Expected a number for " + argumentName + " but got " + number, help);
//        }
//        return num;
//    }
    private boolean location(IPlayer player, String[] commandArgs) throws CommandException {
        argumentsInRange(0, 1, commandArgs);

        if (commandArgs.length == 1) {
            Long id;

            try {
                id = Long.parseLong(commandArgs[0]);
            } catch (NumberFormatException nfe) {
                player.sendMessage("Expected a number but got '" + commandArgs[0] + "'");
                return true;
            }

            ILocation loc = player.getLocation();
            long start = System.currentTimeMillis();
            try (Transaction tx = graph.beginTx()) {
                StructureNode structure = structureRepository.findById(id);
                if (structure == null) {
                    tx.success();
                    player.sendMessage(COLOR.red() + "Couldn't find structure for id #" + id);
                    return true;
                }
                if (structure.getStatus() == StructureStatus.REMOVED) {
                    tx.success();
                    player.sendMessage(COLOR.red() + "Can't get relative location of a removed structure");
                    return true;
                }

                World w = SettlerCraft.getInstance().getWorld(structure.getWorld().getName());
                if (!w.getName().equals(player.getWorld().getName())) {
                    player.sendMessage(COLOR.red() + "Structure must be in the same world...");
                    tx.success();
                    return true;
                }
                Vector rel = structure.getRelativePosition(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                player.sendMessage("Your relative position is " + COLOR.yellow() + "x: " + COLOR.reset() + rel.getBlockX() + COLOR.yellow() + " y: " + COLOR.reset() + rel.getBlockY() + COLOR.yellow() + " z: " + COLOR.reset() + rel.getBlockZ());

                tx.success();
            }
            LOG.log(Level.INFO, "relative location in {0} ms", (System.currentTimeMillis() - start));

        } else {
            IPlayer ply = (IPlayer) player;
            ILocation loc = ply.getLocation();

            Vector pos = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            long start = System.currentTimeMillis();
            try (Transaction tx = graph.beginTx()) {
                StructureNode structure = getSmallesStructure(ply.getWorld(), pos);

                if (structure == null) {
                    ply.sendMessage(COLOR.red() + " Not within a structure...");
                    return true;
                }

                StructureWorldNode w = structure.getWorld();
                if (!w.getName().equals(player.getWorld().getName())) {
                    player.sendMessage(COLOR.red() + "Structure must be in the same world...");
                    return true;
                }

                Vector rel = structure.getRelativePosition(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

                player.sendMessage("Your relative position is " + COLOR.yellow() + "x: " + COLOR.reset() + rel.getBlockX() + COLOR.yellow() + " y: " + COLOR.reset() + rel.getBlockY() + COLOR.yellow() + " z: " + COLOR.reset() + rel.getBlockZ());
                tx.success();
            }
            LOG.log(Level.INFO, "relative location in {0} ms", (System.currentTimeMillis() - start));
        }

        return true;

    }

    private boolean info(ICommandSender sender, String[] commandArgs) throws CommandException {
        argumentsInRange(0, 1, commandArgs);

        if (commandArgs.length == 1) {
            Long id;

            try {
                id = Long.parseLong(commandArgs[0]);
            } catch (NumberFormatException nfe) {
                sender.sendMessage("Expected a number but got '" + commandArgs[0] + "'");
                return true;
            }
            long start = System.currentTimeMillis();
            try (Transaction tx = graph.beginTx()) {
                StructureNode structure = structureRepository.findById(id);

                if (structure == null) {
                    sender.sendMessage(COLOR.red() + "Couldn't find structure for id #" + id);
                    tx.success();
                    return true;
                }

                sender.sendMessage(getInfo(structure));
                tx.success();
            }
            LOG.log(Level.INFO, "info in {0} ms", (System.currentTimeMillis() - start));

        } else if (sender instanceof IPlayer) {
            IPlayer ply = (IPlayer) sender;
            ILocation loc = ply.getLocation();

            Vector pos = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

            long start = System.currentTimeMillis();
            try (Transaction tx = graph.beginTx()) {
                StructureNode s = getSmallesStructure(ply.getWorld(), pos);

                if (s == null) {
                    ply.sendMessage(COLOR.red() + " Not within a structure...");
                    tx.success();
                    return true;
                }

                String info = getInfo(s);
                sender.sendMessage(info);
                tx.success();
            }
            LOG.log(Level.INFO, "info in {0} ms", (System.currentTimeMillis() - start));
        } else {
            sender.sendMessage(COLOR.red() + " too few arguments", "/stt info [id]");
        }

        return true;
    }

    private String getInfo(StructureNode structure) {
        TreeSet<String> owners = Sets.newTreeSet(ALPHABETICAL_ORDER);

        List<? extends IStructureOwner> mastersNode = structure.getOwners(StructureOwnerType.MASTER);
        for (IStructureOwner master : mastersNode) {
            owners.add(master.getName());
        }

        String ownershipString = "";
        int size = owners.size();
        int count = 0;

        for (String ownership : owners) {
            ownershipString += ownership;
            count++;
            if (count != size) {
                ownershipString += ", ";
            }

        }

        String line = "#" + COLOR.gold() + structure.getId() + " " + COLOR.blue() + structure.getName() + "\n"
                + COLOR.reset() + "World: " + COLOR.yellow() + structure.getWorld().getName() + "\n";

        Vector position = structure.getOrigin();
        line += COLOR.reset() + "Location: " + COLOR.yellow() + "X: " + COLOR.reset() + position.getX()
                + " " + COLOR.yellow() + "Y: " + COLOR.reset() + position.getY()
                + " " + COLOR.yellow() + "Z: " + COLOR.reset() + position.getZ() + "\n";

        line += COLOR.reset() + "Status: " + COLOR.reset() + getStatusString(structure) + "\n";

        if (structure.getPrice() > 0) {
            line += COLOR.reset() + "Value: " + COLOR.yellow() + structure.getPrice() + "\n";
        }

        if (!owners.isEmpty()) {
            if (owners.size() == 1) {
                line += COLOR.reset() + "Owners(MASTER): " + COLOR.yellow() + ownershipString + "\n";
            } else {
                line += COLOR.reset() + "Owners(MASTER): \n" + COLOR.yellow() + ownershipString + "\n";
            }
        }

        if (structure.getNode().hasProperty("WGRegion")) {
            line += COLOR.reset() + "WorldGuard-Region: " + COLOR.yellow() + structure.getNode().getProperty("WGRegion");
        }
        return line;

    }

    private boolean build(final IPlayer player, String[] commandArgs) throws CommandException {
        argumentsInRange(1, 2, commandArgs);

        final Structure structure;

        // /stt build [structureId][force]
        String structureIdArg = commandArgs[0];
        if (!NumberUtils.isNumber(structureIdArg)) {
            throw new CommandException("Expected a number but got '" + structureIdArg + "'");
        }

        long id = Long.parseLong(structureIdArg);
        long start = System.currentTimeMillis();
        try (Transaction tx = graph.beginTx()) {
            StructureNode sn = structureRepository.findById(id);

            if (sn == null) {
                tx.success();
                throw new CommandException("Couldn't find a structure for #" + structureIdArg);
            }

            if (!sn.isOwner(player.getUniqueId(), StructureOwnerType.MASTER) && !player.isOP()) {
                tx.success();
                throw new CommandException("You are not the 'MASTER' owner of this structure...");
            }
            structure = new Structure(sn);
            tx.success();
        }
        LOG.log(Level.INFO, "build in {0} ms", (System.currentTimeMillis() - start));

        String force = commandArgs.length == 2 ? commandArgs[1] : null;
        if (force != null && !(force.equals("force") || force.equals("f"))) {
            throw new CommandException("Unknown second argument '" + force + "' ");
        }
        final boolean useForce = force != null && (force.equals("f") || force.equals("force"));

        Player ply = SettlerCraft.getInstance().getPlayer(player.getUniqueId());
        try {
            structure.build(ply, new BuildOptions(), useForce);
        } catch (ConstructionException ex) {
            player.sendMessage(COLOR.red() + ex.getMessage());
        }

        return true;
    }

    private boolean demolish(final IPlayer player, String[] commandArgs) throws CommandException {
        argumentsInRange(1, 2, commandArgs);

        final Structure structure;

        String structureIdArg = commandArgs[0];
        if (!NumberUtils.isNumber(structureIdArg)) {
            throw new CommandException("Expected a number but got '" + structureIdArg + "'");
        }

        // Check structure
        long id = Long.parseLong(structureIdArg);
        long start = System.currentTimeMillis();
        try (Transaction tx = graph.beginTx()) {
            StructureNode sn = structureRepository.findById(id);

            // Structure not found!
            if (sn == null) {
                tx.success();
                throw new CommandException("Couldn't find a structure for #" + structureIdArg);
            }

            // Player is not the owner!
            if (!sn.isOwner(player.getUniqueId(), StructureOwnerType.MASTER) && !player.isOP()) {
                tx.success();
                throw new CommandException("You are not the 'MASTER' owner of this structure...");
            }
            structure = new Structure(sn);

            tx.success();
        }
        LOG.log(Level.INFO, "demolish in {0} ms", (System.currentTimeMillis() - start));

        // Use force?
        String force = commandArgs.length == 2 ? commandArgs[1] : null;
        if (force != null && !(force.equals("force") || force.equals("f"))) {
            throw new CommandException("Unknown second argument '" + force + "' ");
        }
        final boolean useForce = force != null && (force.equals("f") || force.equals("force"));

        // Start demolition
        try {
            structure.demolish(SettlerCraft.getInstance().getPlayer(player.getUniqueId()), new DemolishingOptions(), useForce);
        } catch (ConstructionException ex) {
            player.sendMessage(COLOR.red() + ex.getMessage());
        }
        return true;
    }

    private boolean stop(final IPlayer player, String[] commandArgs) throws CommandException {
        argumentsInRange(1, 2, commandArgs);

        final Structure structure;

        String structureIdArg = commandArgs[0];
        if (!NumberUtils.isNumber(structureIdArg)) {
            throw new CommandException("Expected a number but got '" + structureIdArg + "'");
        }

        // Retrieve structure and perform checks
        long id = Long.parseLong(structureIdArg);
        long start = System.currentTimeMillis();
        try (Transaction tx = graph.beginTx()) {
            StructureNode sn = structureRepository.findById(id);

            if (sn == null) {
                tx.success();
                throw new CommandException("Couldn't find a structure for #" + structureIdArg);
            }

            if (!sn.isOwner(player.getUniqueId()) && !player.isOP()) {
                tx.success();
                throw new CommandException("You don't own this structure...");
            }
            structure = new Structure(sn);

            tx.success();
        }
        LOG.log(Level.INFO, "stop in {0} ms", (System.currentTimeMillis() - start));

        // Use force?
        String force = commandArgs.length == 2 ? commandArgs[1] : null;
        if (force != null && !(force.equals("force") && force.equals("f"))) {
            throw new CommandException("Unknown second argument '" + force + "' ");
        }
        final boolean useForce = force != null && (force.equals("f") || force.equals("force"));

        // Stop current action
        try {
            structure.stop(useForce);
        } catch (ConstructionException ex) {
            player.sendMessage(COLOR.red() + ex.getMessage());
        }

        return true;
    }

    /**
     *
     * @param player The player
     * @param commandArgs could specify a structure
     * @param isFree Determine if this menu is 'free' meaning all items are free
     * @return
     * @throws CommandException
     */
    private boolean openMenu(IPlayer player, String[] commandArgs, boolean isFree) throws CommandException {
        if (!isFree && SettlerCraft.getInstance().getEconomyProvider() == null) {
            throw new CommandException("Plan shop is not available (no economy plugin)");
        }

        if (structureAPI.isLoading()) {
            player.sendMessage(COLOR.red() + "Plans are not loaded yet... please wait...");
            return true;
        }

        if (isFree) {
            if (!permissionManager.isAllowed(player, PermissionManager.Perms.OPEN_PLAN_MENU)) {
                throw new CommandException("You have no permission to open the plan menu");
            }
        } else {
            if (!permissionManager.isAllowed(player, PermissionManager.Perms.OPEN_SHOP_MENU)) {
                throw new CommandException("You have no permission to open the plan shop");
            }
        }

        CategoryMenu planmenu = StructureAPI.getInstance().createPlanMenu();
        if (planmenu == null) {
            throw new CommandException("Planmenu is initialized yet, please wait...");
        }

        if (!planmenu.isEnabled()) {
            throw new CommandException("Planmenu is not ready yet, please wait");
        }
        planmenu.setNoCosts(isFree);
        planmenu.openMenu(player);
        return true;
    }

    private void checkIsPlayer(ICommandSender sender) throws CommandException {
        if (!(sender instanceof IPlayer)) {
            throw new CommandException("You need to be a player!");
        }
    }

    private void argumentsInRange(int min, int max, String[] args) throws CommandException {
        if (args.length < min) {
            throw new CommandException("Too few arguments!");
        } else if (args.length > max) {
            throw new CommandException("Too many arguments!");
        }
    }

    private boolean list(final IPlayer iPlayer, String[] commandArgs) throws CommandException {
        // /stt list [player][page]
        // /stt list [page]
        argumentsInRange(0, 2, commandArgs);

        int page = 0;
        final UUID playerId;
        if (commandArgs.length == 0) {
            playerId = iPlayer.getUniqueId();

        } else if (commandArgs.length == 1) {
            playerId = iPlayer.getUniqueId();
            String arg1 = commandArgs[0];
            if (NumberUtils.isNumber(arg1)) {
                page = Integer.parseInt(arg1);
            } else {
                throw new CommandException("Expected a number but got '" + arg1 + "'");
            }
        } else { // 2 arguments

            String plyName = commandArgs[0];
            IPlayer ply = SettlerCraft.getInstance().getPlatform().getServer().getPlayer(plyName);
            if (ply == null) {
                throw new CommandException("Player '" + plyName + "' not found");
            }
            playerId = ply.getUniqueId();
            String pageString = commandArgs[1];
            if (NumberUtils.isNumber(pageString)) {
                page = Integer.parseInt(pageString);
            } else {
                throw new CommandException("Expected a number but got '" + pageString + "'");
            }
        }

        final int p = page;

        String[] message = new String[MAX_LINES];
        int skip = p * (MAX_LINES - 1);
        int limit = (MAX_LINES - 1);

        long start = System.currentTimeMillis();
        try (Transaction tx = graph.beginTx()) {
            IStructureOwner structureOwner = structureOwnerRepository.findByUUID(playerId);

            long countStart = System.currentTimeMillis();
            long totalStructures = structureOwner.getStructureCount();
            LOG.log(Level.INFO, "list count in {0} ms", (System.currentTimeMillis() - countStart));
            long totalPages = Math.round(Math.ceil(totalStructures / (MAX_LINES - 1)));
            List<StructureNode> structures = structureOwner.getStructures(skip, limit);
            if (p > totalPages || p < 0) {
                iPlayer.sendMessage(COLOR.red() + "Page " + p + " out of " + totalPages + "...");
                return true;
            }

            int lineNumber = 0;
            message[0] = "-----------(Page: " + p + "/" + totalPages + ", Structures: " + totalStructures + ")---------------";
            lineNumber++;
            for (StructureNode structureNode : structures) {
//                

                String line;
                double price = structureNode.getPrice();
                if (price > 0.0d) {
                    line = String.format("#%-1s%-10d%-3s%-40s%-15s%-1s%-5s", COLOR.gold(), structureNode.getId(), COLOR.blue(), structureNode.getName(), getStatusString(structureNode), COLOR.yellow(), ShopUtil.valueString(price));
                } else {
                    line = String.format("#%-1s%-10d%-3s%-40s%-15s", COLOR.gold(), structureNode.getId(), COLOR.blue(), structureNode.getName(), getStatusString(structureNode));
                }

                message[lineNumber] = line;
                lineNumber++;
            }
            tx.success();
        }
        LOG.log(Level.INFO, "list structures in {0} ms", (System.currentTimeMillis() - start));
        iPlayer.sendMessage(message);

        return true;

    }

    private StructureNode getSmallesStructure(IWorld world, Vector position) {
        StructureNode structure = null;
        IWorld w = SettlerCraft.getInstance().getPlatform().getServer().getWorld(world.getName());
        Map<String, Object> params = Maps.newHashMap();
        params.put("worldId", w.getUUID().toString());

        String query
                = "MATCH (world:" + WorldNode.LABEL.name() + " { " + WorldNode.ID_PROPERTY + ": {worldId} })"
                + " WITH world "
                + " MATCH (world)<-[:" + StructureRelations.RELATION_WITHIN + "]-(s:" + StructureNode.LABEL.name() + ")"
                + " WHERE NOT s." + StructureNode.CONSTRUCTION_STATUS_PROPERTY + " = " + StructureStatus.REMOVED.getStatusId()
                + " AND s." + StructureNode.MAX_X_PROPERTY + " >= " + position.getBlockX() + " AND s." + StructureNode.MIN_X_PROPERTY + " <= " + position.getBlockX()
                + " AND s." + StructureNode.MAX_Y_PROPERTY + " >= " + position.getBlockY() + " AND s." + StructureNode.MIN_Y_PROPERTY + " <= " + position.getBlockY()
                + " AND s." + StructureNode.MAX_Z_PROPERTY + " >= " + position.getBlockZ() + " AND s." + StructureNode.MIN_Z_PROPERTY + " <= " + position.getBlockZ()
                + " RETURN s as structure"
                + " ORDER BY s." + StructureNode.SIZE_PROPERTY + " ASC "
                + " LIMIT 1";
        long start = System.currentTimeMillis();
        Result result = graph.execute(query, params);
        while (result.hasNext()) {
            Map<String, Object> map = result.next();
            Node n = (Node) map.get("structure");
            structure = new StructureNode(n);
        }
        LOG.log(Level.INFO, "Smallest structure in {0} ms", (System.currentTimeMillis() - start));
        return structure;
    }

    /**
     * Sends the status of this structure to given player
     *
     * @param structure The structure
     * @param player The player to tell
     */
    private String getStatusString(StructureNode structure) {
        String statusString;
        StructureStatus status = structure.getStatus();
        switch (status) {
            case BUILDING:
                statusString = COLOR.yellow() + "BUILDING";
                break;
            case DEMOLISHING:
                statusString = COLOR.yellow() + "DEMOLISHING";
                break;
            case COMPLETED:
                statusString = COLOR.green() + "COMPLETE";
                break;
            case ON_HOLD:
                statusString = COLOR.red() + "ON HOLD";
                break;
            case QUEUED:
                statusString = COLOR.yellow() + "QUEUED";
                break;
            case REMOVED:
                statusString = COLOR.red() + "REMOVED";
                break;
            case STOPPED:
                statusString = COLOR.red() + "STOPPED";
                break;
            default:
                statusString = status.name();
        }
        return statusString;
    }

    private boolean owner(IPlayer senderPlayer, String[] commandArgs, StructureOwnerType requestedType) throws CommandException {

        // /stt owner [structureId] <add|remove> [playerName|playerId]
        String help;
        if (requestedType == StructureOwnerType.MASTER) {
            help = "/stt masters [structureId] <add|remove> [playerName|playerId]";
        } else if (requestedType == StructureOwnerType.OWNER) {
            help = "/stt owners [structureId] <add|remove> [playerName|playerId]";
        } else {
            help = "/stt members [structureId] <add|remove> [playerName|playerId]";
        }

        Long structureId = null;
        if (commandArgs.length >= 1) {
            String structureIdArg = commandArgs[0];
            if (!NumberUtils.isNumber(structureIdArg)) {
                throw new CommandException("Expected a number but got '" + structureIdArg + "'");
            }
            structureId = Long.parseLong(structureIdArg);
        }

        // /stt [members|owners|masters]
        if (commandArgs.length == 1) {
            TreeSet<String> ownerships = Sets.newTreeSet(ALPHABETICAL_ORDER);
            String structureName = null;
            long start = System.currentTimeMillis();
            try (Transaction tx = graph.beginTx()) {
                StructureNode structure = structureRepository.findById(structureId);
                if (structure == null) {
                    tx.success();
                    throw new CommandException("Couldn't find structure for id #" + structureId);
                }

                structureName = structure.getName();
                for (IStructureOwner member : structure.getOwners(requestedType)) {
                    ownerships.add(member.getName());
                }

                tx.success();
            }
            LOG.log(Level.INFO, "owners in {0} ms", (System.currentTimeMillis() - start));
            String ownershipString = "";
            int size = ownerships.size();

            if (size != 0) {
                int count = 0;

                for (String ownership : ownerships) {
                    ownershipString += ownership;
                    count++;
                    if (count != size) {
                        ownershipString += ", ";
                    }

                }
            } else {
                ownershipString = "None";
            }

            String ownersString;
            if (requestedType == StructureOwnerType.MASTER) {
                ownersString = "Masters: ";
            } else if (requestedType == StructureOwnerType.OWNER) {
                ownersString = "Owners: ";
            } else {
                ownersString = "Members: ";
            }

            if (size == 0) {
                senderPlayer.sendMessage("#" + COLOR.gold() + structureId + " - " + COLOR.blue() + structureName, COLOR.reset() + ownersString + COLOR.red() + ownershipString);
            } else {
                senderPlayer.sendMessage("#" + COLOR.gold() + structureId + " - " + COLOR.blue() + structureName, COLOR.reset() + ownersString, ownershipString);
            }

            return true;
        }

        if (commandArgs.length < 3) {
            throw new CommandException("Too few arguments", help);
        } else if (commandArgs.length > 3) {
            throw new CommandException("Too many arguments", help);
        }

        String method = commandArgs[1];
        String player = commandArgs[2];

        if (!method.equalsIgnoreCase("add") && !method.equalsIgnoreCase("remove")) {
            throw new CommandException("Unknown method '" + method + "', expected 'add' or 'remove'", help);
        }

        long start = System.currentTimeMillis();
        try (Transaction tx = graph.beginTx()) {
            StructureNode structure = structureRepository.findById(structureId);
            if (structure == null) {
                tx.success();
                throw new CommandException("Couldn't find structure for id #" + structureId);
            }

            IStructureOwnership ownership = structure.findOwnership(senderPlayer.getUniqueId());

            if (ownership == null) {
                tx.success();
                throw new CommandException("You don't own this structure");
            }

            if (ownership.getOwnerType().getTypeId() < requestedType.getTypeId()) {
                tx.success();
                throw new CommandException("You don't have enough privileges to " + method + " players of type '" + requestedType.name() + "'");
            }

            if (requestedType == StructureOwnerType.MASTER && ownership.getOwnerType() == requestedType && method.equalsIgnoreCase("remove")) {
                tx.success();
                throw new CommandException("Players of type '" + StructureOwnerType.MASTER + "' can't remove each other");
            }

            IPlayer ply;
            if (!player.startsWith("#")) {
                if (!isUniquePlayerName(player)) {
                    throw new CommandException("Player name '" + player + "' is not unique",
                            "Use /stt members [structureId] <add|remove> [playerId]", "Note that the player id argument needs to start with '#'",
                            "The other player can get it's player id by using the '/stt me' command"
                    );
                }

                ply = platform.getPlayer(player);
                if (ply == null) {
                    tx.success();
                    throw new CommandException("Couldn't find a player for '" + player + "'");
                }
            } else {
                String number = player.substring(1);
                Long id = null;
                try {
                    id = Long.parseLong(number);
                    IBaseSettler sn = settlerRepository.findById(id);
                    if (sn == null) {
                        tx.success();
                        throw new CommandException("Couldn't find a player for id'" + number + "'");
                    }
                    ply = platform.getPlayer(sn.getUUID());

                } catch (NumberFormatException nfe) {
                    tx.success();
                    throw new CommandException("Expected a number after # but got'" + number + "'");
                }
            }

            UUID uuid = ply.getUniqueId();
            if (method.equalsIgnoreCase("add")) {
                IBaseSettler settler = settlerRepository.findByUUID(ply.getUniqueId());
                IStructureOwnership ownershipToAdd = structure.findOwnership(settler.getUUID());

                if (ownershipToAdd == null) {
                    structure.addOwner(settler, requestedType);
                    EventManager.getInstance().getEventBus().post(new StructureAddOwnerEvent(uuid, new Structure(structure), requestedType));
                    senderPlayer.sendMessage("Successfully added '" + COLOR.green() + ply.getName() + COLOR.reset() + "' to #" + COLOR.gold() + structureId + " " + COLOR.blue() + structure.getName() + COLOR.reset() + " as " + COLOR.yellow() + requestedType.name());
                } else if (ownershipToAdd.getOwnerType().getTypeId() < requestedType.getTypeId()) {
                    structure.removeOwner(settler.getUUID());
                    structure.addOwner(settler, requestedType);
                    EventManager.getInstance().getEventBus().post(new StructureAddOwnerEvent(uuid, new Structure(structure), requestedType));
                    senderPlayer.sendMessage("Upgraded ownership of '" + COLOR.green() + ply.getName() + COLOR.reset() + "' to " + COLOR.yellow() + requestedType.name() + COLOR.reset() + " for structure ",
                            "#" + COLOR.gold() + ownershipToAdd.getStructure().getId() + " " + COLOR.blue() + ownershipToAdd.getStructure().getName());
                } else {
                    throw new CommandException(ply.getName() + " is already an owner of this structure and his ownership couldn't be upgraded");
                }
            } else { // remove
                boolean isOwner = structure.isOwner(uuid);
                if (isOwner) {
                    senderPlayer.sendMessage(ply.getName() + " does not own this structure...");
                    return true;
                }

                structure.removeOwner(uuid);
                EventManager.getInstance().getEventBus().post(new StructureRemoveOwnerEvent(uuid, new Structure(structure), requestedType));
                senderPlayer.sendMessage("Successfully removed '" + COLOR.green() + ply.getName() + COLOR.reset() + "' from #" + COLOR.gold() + structureId + " " + COLOR.blue() + structure.getName() + " as " + COLOR.yellow() + requestedType.name());

            }

            tx.success();
        }
        LOG.log(Level.INFO, "owners add/remove in {0} ms", (System.currentTimeMillis() - start));
        return true;
    }

    private boolean isUniquePlayerName(String playerName) {
        int count = 0;
        for (IPlayer player : platform.getServer().getPlayers()) {
            if (player.getName().equals(playerName)) {
                count++;
                if (count > 1) {
                    return false;
                }
            }
        }
        return true;
    }

}
