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
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerDAO;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import com.chingo247.settlercraft.core.util.KeyPool;
import com.chingo247.settlercraft.structureapi.event.StructureAddOwnerEvent;
import com.chingo247.settlercraft.structureapi.event.StructureRemoveOwnerEvent;
import com.chingo247.settlercraft.structureapi.exception.ConstructionException;
import com.chingo247.settlercraft.structureapi.persistence.dao.StructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureOwnerNode;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureOwnerType;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureRelTypes;
import com.chingo247.settlercraft.structureapi.platforms.services.PermissionManager;
import com.chingo247.settlercraft.structureapi.structure.ConstructionStatus;
import com.chingo247.settlercraft.structureapi.structure.DefaultStructureFactory;
import com.chingo247.settlercraft.structureapi.structure.IStructureAPI;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.settlercraft.structureapi.structure.StructureAPI;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlan;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.SchematicPlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.BuildOptions;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.DemolishingOptions;
import com.chingo247.settlercraft.structureapi.structure.plan.util.PlanGenerator;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IColors;
import com.chingo247.xplatform.core.ICommandSender;
import com.chingo247.xplatform.core.ILocation;
import com.chingo247.xplatform.core.IPlayer;
import com.chingo247.xplatform.core.IWorld;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    private static final int MAX_LINES = 10;
    private static final Comparator<String> ALPHABETICAL_ORDER = new Comparator<String>() {

        @Override
        public int compare(String o1, String o2) {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    };
    private final IStructureAPI structureAPI;
    private final PermissionManager permissionManager;
    private final IColors COLOR;
    private final ExecutorService executorService;
    private final StructureDAO structureDAO;
    private final SettlerDAO settlerDAO;
    private final GraphDatabaseService graph;
    private final UUID console;
    private final KeyPool<UUID> playerPool;
    private final APlatform platform;
    private CommandHelper commandHelper;

    public StructureCommands(IStructureAPI structureAPI, ExecutorService executorService, GraphDatabaseService graph) {
        this.structureAPI = structureAPI;
        this.permissionManager = PermissionManager.getInstance();
        this.COLOR = structureAPI.getPlatform().getChatColors();
        this.executorService = executorService;
        this.structureDAO = new StructureDAO(graph);
        this.settlerDAO = new SettlerDAO(graph);
        this.graph = graph;
        this.console = UUID.randomUUID();
        this.playerPool = new KeyPool<>(executorService);
        this.platform = structureAPI.getPlatform();
        this.commandHelper = new CommandHelper(platform);
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
                    switch (commandArg) {
                        case "me":
                            checkIsPlayer(sender);
                            IPlayer ply = (IPlayer) sender;
                            try (Transaction tx = graph.beginTx()) {
                                SettlerNode node = settlerDAO.find(ply.getUniqueId()); // NEVER NULL
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
                            owner((IPlayer)sender, commandArgs, StructureOwnerType.MASTER);
                            break;
                        case "owners":
                            checkIsPlayer(sender);
                            owner((IPlayer)sender, commandArgs, StructureOwnerType.OWNER);
                            break;
                        case "members":
                            checkIsPlayer(sender);
                            owner((IPlayer)sender, commandArgs, StructureOwnerType.MEMBER);
                            break;
                        case "schematic":
                            schematic(sender, commandArgs);
                            
                            
                            break;
                        case "menu":
                            checkIsPlayer(sender);
                            if(!structureAPI.getConfig().isPlanMenuEnabled()) {
                                sender.sendMessage(COLOR.red() + "Plan menu is not enabled");
                                return;
                            }
                            openMenu((IPlayer) sender, commandArgs, true);
                            break;
                        case "shop":
                            checkIsPlayer(sender);
                            if(!structureAPI.getConfig().isPlanMenuEnabled()) {
                                sender.sendMessage(COLOR.red() + "Plan shop is not enabled");
                                return;
                            }
                            openMenu((IPlayer) sender, commandArgs, false);
                            break;
                        default:
                            throw new CommandException("No action known for '/" + command + " " + commandArg);
                    }
                } catch (CommandException ex) {
                    String[] error = ex.getPlayerErrorMessage();
                    for(int i = 0; i < error.length; i++) {
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
    
    private void schematic(ICommandSender sender, String[] commandArgs) throws CommandException {
        if(sender instanceof IPlayer) {
            IPlayer player = (IPlayer) sender;
            if(!PermissionManager.getInstance().isAllowed(player, PermissionManager.Perms.ROTATE_SCHEMATIC)) {
                sender.sendMessage(COLOR.red() + "You have no permission to do this!");
                return;
            }
        }
        
        // /stt schematic [structureid] rotate [degrees]
        String usage = "/stt rschematic [structure-id][degrees]";
        commandHelper.argumentsInRange(3, 3, commandArgs, usage);
        commandHelper.isLong(commandArgs[0], "Expected a number for [structure-id]but got '" + commandArgs[0] + "'", usage);
        commandHelper.isInt(commandArgs[1], "Expected a number for [degrees] but got '" + commandArgs[1] + "'", usage);
        
        Long structureId = Long.parseLong(commandArgs[0]);
        Integer degrees = Integer.parseInt(commandArgs[1]);
        
        commandHelper.isTrue(degrees % 90 == 0, "Argument [degrees] must be a multiple of 90");
        Structure s = null;
        try (Transaction tx = graph.beginTx()){
            
            StructureNode n = structureDAO.find(structureId);
            if(n != null) {
            s = DefaultStructureFactory.getInstance().makeStructure(n);
            }
            tx.success();
        }
        
        commandHelper.isFalse(s == null, "Couldn't find structure with id #" + structureId);
        
        StructurePlan plan = s.getStructurePlan();
        Placement p = plan.getPlacement();
        
        commandHelper.isTrue(p instanceof SchematicPlacement, "Placement of structure #" + structureId + " is not a schematic");
        
        SchematicPlacement schematicPlacement = (SchematicPlacement) p;
        
        Long hash = schematicPlacement.getSchematic().getHash();
        
        
        
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

            Structure s = null;
            try (Transaction tx = graph.beginTx()) {
                StructureNode node = structureDAO.find(id);
                if (node != null) {
                    s = DefaultStructureFactory.getInstance().makeStructure(node);
                }
                tx.success();
            }

            if (s == null) {
                player.sendMessage(COLOR.red() + "Couldn't find structure for id #" + id);
                return true;
            }

            World w = SettlerCraft.getInstance().getWorld(s.getWorld());
            if (!w.getName().equals(player.getWorld().getName())) {
                player.sendMessage(COLOR.red() + "Structure must be in the same world...");
                return true;
            }

            ILocation loc = player.getLocation();
            Vector rel = s.getRelativePosition(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

            player.sendMessage("Your relative position is " + COLOR.yellow() + "x: " + COLOR.reset() + rel.getBlockX() + COLOR.yellow() + " y: " + COLOR.reset() + rel.getBlockY() + COLOR.yellow() + " z: " + COLOR.reset() + rel.getBlockZ());

        } else {
            IPlayer ply = (IPlayer) player;
            ILocation loc = ply.getLocation();

            Vector pos = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            try (Transaction tx = graph.beginTx()) {
                StructureNode sn = getSmallesStructure(ply.getWorld(), pos);

                if (sn == null) {
                    ply.sendMessage(COLOR.red() + " Not within a structure...");
                    return true;
                }

                Structure s = DefaultStructureFactory.getInstance().makeStructure(sn);

                World w = SettlerCraft.getInstance().getWorld(s.getWorld());
                if (!w.getName().equals(player.getWorld().getName())) {
                    player.sendMessage(COLOR.red() + "Structure must be in the same world...");
                    return true;
                }

                Vector rel = s.getRelativePosition(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

                player.sendMessage("Your relative position is " + COLOR.yellow() + "x: " + COLOR.reset() + rel.getBlockX() + COLOR.yellow() + " y: " + COLOR.reset() + rel.getBlockY() + COLOR.yellow() + " z: " + COLOR.reset() + rel.getBlockZ());
                tx.success();
            }
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

            try (Transaction tx = graph.beginTx()) {
                StructureNode node = structureDAO.find(id);

                if (node == null) {
                    sender.sendMessage(COLOR.red() + "Couldn't find structure for id #" + id);
                    tx.success();
                    return true;
                }

                sender.sendMessage(getInfo(node));
                tx.success();
            }

        } else if (sender instanceof IPlayer) {
            IPlayer ply = (IPlayer) sender;
            ILocation loc = ply.getLocation();

            Vector pos = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
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

        } else {
            sender.sendMessage(COLOR.red() + " too few arguments", "/stt info [id]");
        }

        return true;
    }

    private String getInfo(StructureNode structure) {
        TreeSet<String> owners = Sets.newTreeSet(ALPHABETICAL_ORDER);
        try (Transaction tx = graph.beginTx()) {
            List<StructureOwnerNode> mastersNode = structure.getOwners(StructureOwnerType.MASTER);

            for (StructureOwnerNode master : mastersNode) {
                owners.add(master.getName());
            }

            tx.success();

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
                    + COLOR.reset()+ "World: " + COLOR.yellow()+ structure.getWorld().getName() + "\n";

            Vector position = structure.getPosition();
            line += COLOR.reset() + "Location: " + COLOR.yellow() + "X: " + COLOR.reset() + position.getX()
                    + " " + COLOR.yellow() + "Y: " + COLOR.reset() + position.getY()
                    + " " + COLOR.yellow() + "Z: " + COLOR.reset() + position.getZ() + "\n";

            line += COLOR.reset()+ "Status: " + COLOR.reset() + getStatusString(structure) + "\n";

            if (structure.getPrice() > 0) {
                line += COLOR.reset()+ "Value: " + COLOR.yellow()+ structure.getPrice() + "\n";
            }

            if (!owners.isEmpty()) {
                if(owners.size() == 1) {
                    line += COLOR.reset()+ "Owners(MASTER): " + COLOR.yellow()+ ownershipString + "\n";
                } else {
                    line += COLOR.reset()+ "Owners(MASTER): \n" + COLOR.yellow()+ ownershipString + "\n";
                }
            }

            if (structure.getRawNode().hasProperty("WGRegion")) {
                line += COLOR.reset()+ "WorldGuard-Region: " + COLOR.yellow()+ structure.getRawNode().getProperty("WGRegion");
            }
            return line;
        }

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
        try (Transaction tx = graph.beginTx()) {
            StructureNode structureNode = structureDAO.find(id);

            if (structureNode == null) {
                tx.success();
                throw new CommandException("Couldn't find a structure for #" + structureIdArg);
            }

            if (!structureNode.isOwner(player.getUniqueId()) && !player.isOP()) {
                tx.success();
                throw new CommandException("You don't own this structure...");
            }

            structure = DefaultStructureFactory.getInstance().makeStructure(structureNode);
            tx.success();
        }

        String force = commandArgs.length == 2 ? commandArgs[1] : null;
        if (force != null && !(force.equals("force") && force.equals("f"))) {
            throw new CommandException("Unknown second argument '" + force + "' ");
        }
        final boolean useForce = force != null && (force.equals("f") || force.equals("force"));

        executorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    structure.build(SettlerCraft.getInstance().getPlayer(player.getUniqueId()), new BuildOptions(), useForce);
                } catch (ConstructionException ex) {
                    player.sendMessage(COLOR.red() + ex.getMessage());
                }
            }
        });

        return true;
    }

    private boolean demolish(final IPlayer player, String[] commandArgs) throws CommandException {
        argumentsInRange(1, 2, commandArgs);

        final Structure structure;

        String structureIdArg = commandArgs[0];
        if (!NumberUtils.isNumber(structureIdArg)) {
            throw new CommandException("Expected a number but got '" + structureIdArg + "'");
        }

        long id = Long.parseLong(structureIdArg);
        try (Transaction tx = graph.beginTx()) {
            StructureNode structureNode = structureDAO.find(id);

            if (structureNode == null) {
                tx.success();
                throw new CommandException("Couldn't find a structure for #" + structureIdArg);
            }

            if (!structureNode.isOwner(player.getUniqueId()) && !player.isOP()) {
                tx.success();
                throw new CommandException("You don't own this structure...");
            }

            structure = DefaultStructureFactory.getInstance().makeStructure(structureNode);
            tx.success();
        }

        String force = commandArgs.length == 2 ? commandArgs[1] : null;
        if (force != null && !(force.equals("force") && force.equals("f"))) {
            throw new CommandException("Unknown second argument '" + force + "' ");
        }
        final boolean useForce = force != null && (force.equals("f") || force.equals("force"));

        executorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    structure.demolish(SettlerCraft.getInstance().getPlayer(player.getUniqueId()), new DemolishingOptions(), useForce);
                } catch (ConstructionException ex) {
                    player.sendMessage(COLOR.red() + ex.getMessage());
                } catch (Exception ex) { // Catch everything or disappear it will dissappear in the abyss!
                    Logger.getLogger(StructureCommands.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        });

        return true;
    }

    private boolean stop(final IPlayer player, String[] commandArgs) throws CommandException {
        argumentsInRange(1, 2, commandArgs);

        final Structure structure;

        String structureIdArg = commandArgs[0];
        if (!NumberUtils.isNumber(structureIdArg)) {
            throw new CommandException("Expected a number but got '" + structureIdArg + "'");
        }

        long id = Long.parseLong(structureIdArg);
        try (Transaction tx = graph.beginTx()) {
            StructureNode structureNode = structureDAO.find(id);

            if (structureNode == null) {
                tx.success();
                throw new CommandException("Couldn't find a structure for #" + structureIdArg);
            }

            if (!structureNode.isOwner(player.getUniqueId()) && !player.isOP()) {
                tx.success();
                throw new CommandException("You don't own this structure...");
            }

            structure = DefaultStructureFactory.getInstance().makeStructure(structureNode);
            tx.success();
        }

        String force = commandArgs.length == 2 ? commandArgs[1] : null;
        if (force != null && !(force.equals("force") && force.equals("f"))) {
            throw new CommandException("Unknown second argument '" + force + "' ");
        }
        final boolean useForce = force != null && (force.equals("f") || force.equals("force"));
        executorService.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    structure.stop(useForce);
                } catch (ConstructionException ex) {
                    player.sendMessage(COLOR.red() + ex.getMessage());
                } catch (Exception ex) { // Catch everything or disappear it will dissappear in the abyss!
                    Logger.getLogger(StructureCommands.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        });

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
            if(!permissionManager.isAllowed(player, PermissionManager.Perms.OPEN_PLAN_MENU)) {
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

        try (Transaction tx = graph.beginTx()) {
            long totalStructures = structureDAO.getStructureCountForSettler(playerId);
            long totalPages = Math.round(Math.ceil(totalStructures / (MAX_LINES - 1)));
            List<StructureNode> structures = structureDAO.getStructuresForSettler(playerId, skip, limit);
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
                + " MATCH (world)<-[:" + StructureRelTypes.RELATION_WITHIN + "]-(s:" + StructureNode.LABEL.name() + ")"
                + " WHERE NOT s." + StructureNode.CONSTRUCTION_STATUS_PROPERTY + " = " + ConstructionStatus.REMOVED.getStatusId()
                + " AND s." + StructureNode.MAX_X_PROPERTY + " >= " + position.getBlockX() + " AND s." + StructureNode.MIN_X_PROPERTY + " <= " + position.getBlockX()
                + " AND s." + StructureNode.MAX_Y_PROPERTY + " >= " + position.getBlockY() + " AND s." + StructureNode.MIN_Y_PROPERTY + " <= " + position.getBlockY()
                + " AND s." + StructureNode.MAX_Z_PROPERTY + " >= " + position.getBlockZ() + " AND s." + StructureNode.MIN_Z_PROPERTY + " <= " + position.getBlockZ()
                + " RETURN s as structure"
                + " ORDER BY s." + StructureNode.SIZE_PROPERTY + " ASC "
                + " LIMIT 1";

        Result result = graph.execute(query, params);
        while (result.hasNext()) {
            Map<String, Object> map = result.next();
            Node n = (Node) map.get("structure");
            structure = new StructureNode(n);
        }

        return structure;
    }

    /**
     * Sends the status of this structure to given player
     *
     * @param structure The structure
     * @param player The player to tell
     */
    private String getStatusString(Structure structure) {
        String statusString;
        ConstructionStatus status = structure.getConstructionStatus();
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

    /**
     * Sends the status of this structure to given player
     *
     * @param structure The structure
     * @param player The player to tell
     */
    private String getStatusString(StructureNode structure) {
        String statusString;
        ConstructionStatus status = structure.getConstructionStatus();
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

        if (commandArgs.length == 1) {
            TreeSet<String> ownerships = Sets.newTreeSet(ALPHABETICAL_ORDER);
            String structureName = null;
            try (Transaction tx = graph.beginTx()) {
                StructureNode node = structureDAO.find(structureId);
                if (node == null) {
                    tx.success();
                    throw new CommandException("Couldn't find structure for id #" + structureId);
                }

                structureName = node.getName();
                for (StructureOwnerNode member : node.getOwners(requestedType)) {
                    ownerships.add(member.getName());
                }

                tx.success();
            }
            String ownershipString = "";
            int size = ownerships.size();
            
            if(size != 0) {
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

            if(size == 0) {
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

        try (Transaction tx = graph.beginTx()) {
            StructureNode structureNode = structureDAO.find(structureId);
            if (structureNode == null) {
                tx.success();
                throw new CommandException("Couldn't find structure for id #" + structureId);
            }

            StructureOwnerNode senderOwner = structureNode.findOwner(senderPlayer.getUniqueId());
            if (senderOwner == null) {
                tx.success();
                throw new CommandException("You don't own this structure");
            }

            if (senderOwner.getType().getTypeId() < requestedType.getTypeId()) {
                tx.success();
                throw new CommandException("You don't have enough privileges to " + method + " players of type '" + requestedType.name() + "'");
            }

            if (requestedType == StructureOwnerType.MASTER && senderOwner.getType() == requestedType && method.equalsIgnoreCase("remove")) {
                tx.success();
                throw new CommandException("Players of type '" + StructureOwnerType.MASTER + "' can't remove each other");
            }

            IPlayer ply;
            if(!player.startsWith("#")) {
                if(!isUniquePlayerName(player)) {
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
                    SettlerNode sn = settlerDAO.find(id);
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
            Structure structure = DefaultStructureFactory.getInstance().makeStructure(structureNode);
            if (method.equalsIgnoreCase("add")) {
                SettlerNode settler = settlerDAO.find(ply.getUniqueId());
                
                StructureOwnerNode owner = structureNode.findOwner(settler.getUUID());
                if(owner == null) {
                    structureNode.addOwner(settler, requestedType);
                    EventManager.getInstance().getEventBus().post(new StructureAddOwnerEvent(uuid, structure, requestedType));
                    senderPlayer.sendMessage("Successfully added '" + COLOR.green() + ply.getName() + COLOR.reset() + "' to #" + COLOR.gold() + structureId + " " + COLOR.blue() + structureNode.getName() + COLOR.reset() + " as " + COLOR.yellow() + requestedType.name());
                } else if (owner.getType().getTypeId() < requestedType.getTypeId()) {
                    structureNode.removeOwner(settler.getUUID());
                    structureNode.addOwner(settler, requestedType);
                    EventManager.getInstance().getEventBus().post(new StructureAddOwnerEvent(uuid, structure, requestedType));
                        senderPlayer.sendMessage("Upgraded ownership of '" + COLOR.green() + ply.getName() + COLOR.reset() + "' to " + COLOR.yellow() + requestedType.name() + COLOR.reset() + " for structure ",
                                "#" + COLOR.gold() + structure.getId() + " " + COLOR.blue() + structure.getName());
                } else {
                    throw new CommandException(ply.getName() + " is already an owner of this structure and his ownership couldn't be upgraded");
                }
            } else { // remove
                boolean isOwner = structureNode.isOwner(uuid);
                if (isOwner) {
                    senderPlayer.sendMessage(ply.getName() + " does not own this structure...");
                    return true;
                }

                structureNode.removeOwner(uuid);
                EventManager.getInstance().getEventBus().post(new StructureRemoveOwnerEvent(uuid, structure, requestedType));
                senderPlayer.sendMessage("Successfully removed '" + COLOR.green() + ply.getName() + COLOR.reset() + "' from #" + COLOR.gold() + structureId + " " + COLOR.blue() + structureNode.getName() + " as " + COLOR.yellow() + requestedType.name());

            }

            tx.success();
        }
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
