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
package com.chingo247.structureapi.commands;

import com.chingo247.menuapi.menu.util.ShopUtil;
import com.chingo247.settlercraft.core.commands.util.CommandExtras;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.settlercraft.core.model.interfaces.IBaseSettler;
import com.chingo247.structureapi.IStructureAPI;
import com.chingo247.settlercraft.core.commands.util.CommandSenderType;
import com.chingo247.settlercraft.core.platforms.services.permission.Permission;
import com.chingo247.structureapi.construction.options.BuildOptions;
import com.chingo247.structureapi.construction.options.DemolitionOptions;
import com.chingo247.structureapi.event.StructureAddOwnerEvent;
import com.chingo247.structureapi.event.StructureRemoveOwnerEvent;
import com.chingo247.structureapi.construction.ConstructionException;
import com.chingo247.structureapi.model.owner.IOwnership;
import com.chingo247.structureapi.model.owner.OwnerDomain;
import com.chingo247.structureapi.model.owner.OwnerType;
import com.chingo247.structureapi.model.settler.ISettler;
import com.chingo247.structureapi.model.settler.ISettlerRepository;
import com.chingo247.structureapi.model.settler.SettlerRepositiory;
import com.chingo247.structureapi.model.structure.ConstructionStatus;
import com.chingo247.structureapi.model.structure.IStructureRepository;
import com.chingo247.structureapi.model.structure.Structure;
import com.chingo247.structureapi.model.structure.StructureNode;
import com.chingo247.structureapi.model.structure.StructureRepository;
import com.chingo247.structureapi.model.world.StructureWorld;
import com.chingo247.structureapi.platform.permission.Permissions;
import com.chingo247.xplatform.core.IColors;
import com.chingo247.xplatform.core.ICommandSender;
import com.chingo247.xplatform.core.ILocation;
import com.chingo247.xplatform.core.IPlayer;
import com.google.common.collect.Sets;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.World;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.math.NumberUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class StructureCommands {

    private static final UUID CONSOLE = UUID.randomUUID();
    private static final Logger LOG = Logger.getLogger(StructureCommands.class.getSimpleName());
    private static final int MAX_LINES = 10;

    private static final Comparator<String> ALPHABETICAL_ORDER = new Comparator<String>() {

        @Override
        public int compare(String o1, String o2) {
            return o1.toLowerCase().compareTo(o2.toLowerCase());
        }
    };

    private static UUID getUUID(ICommandSender sender) {
        if (sender instanceof IPlayer) {
            return ((IPlayer) sender).getUniqueId();
        }
        return CONSOLE;
    }

    private static boolean isConsole(ICommandSender sender) {
        return !(isPlayer(sender));
    }

    private static boolean isPlayer(ICommandSender sender) {
        return (sender instanceof IPlayer);
    }

    private static boolean isOP(ICommandSender sender) {
        return isConsole(sender) || ((IPlayer) sender).isOP();
    }

    private static boolean isUniquePlayerName(String playerName, IStructureAPI structureAPI) {
        int count = 0;
        for (IPlayer player : structureAPI.getPlatform().getServer().getPlayers()) {
            if (player.getName().equals(playerName)) {
                count++;
                if (count > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    private static String getInfo(StructureNode structure, IColors colors) {
        TreeSet<String> owners = Sets.newTreeSet(ALPHABETICAL_ORDER);

        List<? extends ISettler> mastersNode = structure.getOwnerDomain().getOwners(OwnerType.MASTER);
        for (ISettler master : mastersNode) {
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

        String line = "#" + colors.gold() + structure.getId() + " " + colors.blue() + structure.getName() + "\n"
                + colors.reset() + "World: " + colors.yellow() + structure.getWorld().getName() + "\n";

        Vector position = structure.getOrigin();
        line += colors.reset() + "Location: " + colors.yellow() + "X: " + colors.reset() + position.getX()
                + " " + colors.yellow() + "Y: " + colors.reset() + position.getY()
                + " " + colors.yellow() + "Z: " + colors.reset() + position.getZ() + "\n";

        line += colors.reset() + "Status: " + colors.reset() + getStatusString(structure, colors) + "\n";

        if (structure.getPrice() > 0) {
            line += colors.reset() + "Value: " + colors.yellow() + structure.getPrice() + "\n";
        }

        if (!owners.isEmpty()) {
            if (owners.size() == 1) {
                line += colors.reset() + "Owners(MASTER): " + colors.yellow() + ownershipString + "\n";
            } else {
                line += colors.reset() + "Owners(MASTER): \n" + colors.yellow() + ownershipString + "\n";
            }
        }

        if (structure.getNode().hasProperty("WGRegion")) {
            line += colors.reset() + "WorldGuard-Region: " + colors.yellow() + structure.getNode().getProperty("WGRegion");
        }
        return line;

    }

    private static String getStatusString(StructureNode structure, IColors colors) {
        String statusString;
        ConstructionStatus status = structure.getStatus();
        switch (status) {
            case BUILDING:
                statusString = colors.yellow() + "BUILDING";
                break;
            case DEMOLISHING:
                statusString = colors.yellow() + "DEMOLISHING";
                break;
            case COMPLETED:
                statusString = colors.green() + "COMPLETE";
                break;
            case ON_HOLD:
                statusString = colors.red() + "ON HOLD";
                break;
            case QUEUED:
                statusString = colors.yellow() + "QUEUED";
                break;
            case REMOVED:
                statusString = colors.red() + "REMOVED";
                break;
            case STOPPED:
                statusString = colors.red() + "STOPPED";
                break;
            default:
                statusString = status.name();
        }
        return statusString;
    }

    @CommandPermissions(Permissions.STRUCTURE_INFO)
    @CommandExtras(async = true)
    @Command(aliases = {"structure:info", "stt:info"}, desc = "Display info about the structure you are in or with the given id", max = 1)
    public static void info(final CommandContext args, final ICommandSender sender, IStructureAPI structureAPI) throws Exception {
        final GraphDatabaseService graph = SettlerCraft.getInstance().getNeo4j();
        final IColors color = structureAPI.getPlatform().getChatColors();
        final StructureRepository structureRepository = new StructureRepository(graph);

        if (args.argsLength() == 1) {
            // Find by ID
            Long id;
            try {
                id = Long.parseLong(args.getString(0));
            } catch (NumberFormatException nfe) {
                throw new CommandException("Expected a number but got '" + args.getString(0) + "'");
            }
            long start = System.currentTimeMillis();

            try (Transaction tx = graph.beginTx()) {
                StructureNode structure = structureRepository.findById(id);

                if (structure == null) {
                    tx.success();
                    throw new CommandException("Couldn't find structure for id #" + id);
                }

                sender.sendMessage(getInfo(structure, color));
                tx.success();
            }

            LOG.log(Level.INFO, "info in {0} ms", (System.currentTimeMillis() - start));
        } else if (sender instanceof IPlayer) {
            // Find by position
            IPlayer ply = (IPlayer) sender;
            ILocation loc = ply.getLocation();
            Vector pos = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            long start = System.currentTimeMillis();

            try (Transaction tx = graph.beginTx()) {
                StructureNode s = structureRepository.findStructureOnPosition(ply.getWorld().getUUID(), pos);

                if (s == null) {
                    tx.success();
                    throw new CommandException(" Not within a structure...");
                }

                String info = getInfo(s, color);
                sender.sendMessage(info);
                tx.success();
            }

            LOG.log(Level.INFO, "info in {0} ms", (System.currentTimeMillis() - start));
        } else {
            throw new CommandException("Too few arguments \n" + "/structure:info [id]");
        }

    }

    @CommandPermissions(Permissions.STRUCTURE_CONSTRUCTION)
    @CommandExtras(async = true)
    @Command(aliases = {"structure:build", "stt:build"}, desc = "Builds a structure", min = 1, max = 1, flags = "f")
    public static void build(final CommandContext args, final ICommandSender sender, final IStructureAPI structureAPI) throws Exception {
        final UUID uuid = getUUID(sender);
        final GraphDatabaseService graph = SettlerCraft.getInstance().getNeo4j();
        final StructureRepository structureRepository = new StructureRepository(graph);

        final Structure structure;
        String structureIdArg = args.getString(0);
        if (!NumberUtils.isNumber(structureIdArg)) {
            throw new CommandException("Expected a number but got '" + structureIdArg + "' \n" + "/structure:build [id]");
        }
        long id = Long.parseLong(structureIdArg);
        long start = System.currentTimeMillis();
        try (Transaction tx = graph.beginTx()) {
            StructureNode sn = structureRepository.findById(id);

            if (sn == null) {
                tx.success();
                throw new CommandException("Couldn't find a structure for #" + structureIdArg);
            }

            if (isPlayer(sender) && !isOP(sender) && sender instanceof IPlayer && !sn.getOwnerDomain().isOwnerOfType(uuid, OwnerType.MASTER)) {
                tx.success();
                throw new CommandException("You are not the 'MASTER' owner of this structure...");
            }
            structure = new Structure(sn);
            tx.success();
        }
        LOG.log(Level.INFO, "build in {0} ms", (System.currentTimeMillis() - start));

        String force = args.hasFlag('f') ? args.getFlag('f') : null;
        final boolean useForce = force != null && (force.equals("t") || force.equals("true"));
        try {
            BuildOptions options = new BuildOptions();
            options.setUseForce(useForce);
            structureAPI.build(uuid, structure, options);
        } catch (ConstructionException ex) {
            throw new CommandException(ex.getMessage());
        }

    }

    @CommandPermissions(Permissions.STRUCTURE_CONSTRUCTION)
    @CommandExtras(async = true)
    @Command(aliases = {"structure:demolish", "stt:demolish"}, desc = "Demolishes a structure", min = 1, max = 1, flags = "f")
    public static void demolish(final CommandContext args, ICommandSender sender, IStructureAPI structureAPI) throws Exception {
        final GraphDatabaseService graph = SettlerCraft.getInstance().getNeo4j();
        final StructureRepository structureRepository = new StructureRepository(graph);
        final Structure structure;
        final UUID uuid = getUUID(sender);

        String structureIdArg = args.getString(0);
        if (!NumberUtils.isNumber(structureIdArg)) {
            throw new CommandException("Expected a number but got '" + structureIdArg + "' \n" + "/structure:demolish [id]");
        }

        // Check structure
        long id = Long.parseLong(structureIdArg);
        long start = System.currentTimeMillis();
        try (Transaction tx = graph.beginTx()) {
            StructureNode sn = structureRepository.findById(id);

            // Structure not found!
            if (sn == null) {
                tx.success();
                throw new com.chingo247.settlercraft.core.exception.CommandException("Couldn't find a structure for #" + structureIdArg);
            }

            // Player is not the owner!
            if (isPlayer(sender) && !isOP(sender) && !sn.getOwnerDomain().isOwnerOfType(uuid, OwnerType.MASTER)) {
                tx.success();
                throw new CommandException("You are not the 'MASTER' owner of this structure...");
            }
            structure = new Structure(sn);

            tx.success();
        }
        LOG.log(Level.INFO, "demolish in {0} ms", (System.currentTimeMillis() - start));

        // Use force?
        String force = args.hasFlag('f') ? args.getFlag('f') : null;
        final boolean useForce = force != null && (force.equals("t") || force.equals("true"));

        // Start demolition
        try {
            DemolitionOptions options = new DemolitionOptions();
            options.setUseForce(useForce);
            structureAPI.demolish(uuid, structure, options);
        } catch (ConstructionException ex) {
            throw new CommandException(ex.getMessage());
        }

    }

    @CommandPermissions(Permissions.STRUCTURE_CONSTRUCTION)
    @CommandExtras(async = true)
    @Command(aliases = {"structure:halt", "stt:halt"}, desc = "Stop building or demolishing of a structure", min = 1, max = 1, flags = "f")
    public static void halt(final CommandContext args, ICommandSender sender, IStructureAPI structureAPI) throws Exception {
        final GraphDatabaseService graph = SettlerCraft.getInstance().getNeo4j();
        final StructureRepository structureRepository = new StructureRepository(graph);
        final Structure structure;
        final UUID uuid = getUUID(sender);
        final IColors colors = structureAPI.getPlatform().getChatColors();
        
        String structureIdArg = args.getString(0);
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

            if (isPlayer(sender) && !isOP(sender) && !sn.getOwnerDomain().isOwner(uuid)) {
                tx.success();
                throw new CommandException("You don't own this structure...");
            }
            structure = new Structure(sn);

            tx.success();
        }
        LOG.log(Level.INFO, "stop in {0} ms", (System.currentTimeMillis() - start));

        // Use force?
        String force = args.hasFlag('f') ? args.getFlag('f') : null;
        final boolean useForce = force != null && (force.equals("t") || force.equals("true"));

        // Stop current action
        String structureInfo = colors.reset() + ": #" + colors.gold() + structure.getId() + colors.blue() + " " + structure.getName();
        sender.sendMessage(colors.red()+ "STOPPING" + structureInfo);
        try {
            structureAPI.stop(structure, useForce);
        } catch (ConstructionException ex) {
            throw new CommandException(ex.getMessage());
        }

    }

    @CommandExtras(async = true)
    @Command(aliases = {"structure:masters", "stt:masters"}, desc = "")
    public static void masters(final CommandContext args, ICommandSender sender, IStructureAPI structureAPI) throws Exception {
        masterOwnerMember(sender, args, structureAPI, OwnerType.MASTER, args.hasFlag('#') ? args.getFlag('#') : null);
    }

    @CommandExtras(async = true)
    @Command(aliases = {"structure:owners", "stt:owners"}, desc = "")
    public static void owners(final CommandContext args, ICommandSender sender, IStructureAPI structureAPI) throws Exception {
        masterOwnerMember(sender, args, structureAPI, OwnerType.OWNER, args.hasFlag('#') ? args.getFlag('#') : null);
    }

    @CommandExtras(async = true)
    @Command(aliases = {"structure:members", "stt:members"}, desc = "")
    public static void members(final CommandContext args, ICommandSender sender, IStructureAPI structureAPI) throws Exception {
        masterOwnerMember(sender, args, structureAPI, OwnerType.MEMBER, args.hasFlag('#') ? args.getFlag('#') : null);
    }

    private static boolean masterOwnerMember(ICommandSender sender, CommandContext args, IStructureAPI structureAPI, OwnerType requestedType, String idFlag) throws CommandException {
        final GraphDatabaseService graph = SettlerCraft.getInstance().getNeo4j();
        final IStructureRepository structureRepository = new StructureRepository(graph);
        final ISettlerRepository settlerRepository = new SettlerRepositiory(graph);
        final IColors COLOR = structureAPI.getPlatform().getChatColors();

        // /stt owner [structureId] <add|remove> [playerName|playerId]
        String help;
        if (requestedType == OwnerType.MASTER) {
            help = "/structure:masters [structureId] <add|remove> [playerName| -# playerId]";
        } else if (requestedType == OwnerType.OWNER) {
            help = "/structure:owners [structureId] <add|remove> [playerName| -# playerId]";
        } else {
            help = "/structure:members [structureId] <add|remove> [playerName| -# playerId]";
        }

        Long structureId = null;
        if (args.argsLength() >= 1) {
            String structureIdArg = args.getString(0);
            if (!NumberUtils.isNumber(structureIdArg)) {
                throw new CommandException("Expected a number but got '" + structureIdArg + "'");
            }
            structureId = Long.parseLong(structureIdArg);
        }

        // /stt: [members|owners|masters]
        if (args.argsLength() == 1) {
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
                for (ISettler member : structure.getOwnerDomain().getOwners(requestedType)) {
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
            if (requestedType == OwnerType.MASTER) {
                ownersString = "Masters: ";
            } else if (requestedType == OwnerType.OWNER) {
                ownersString = "Owners: ";
            } else {
                ownersString = "Members: ";
            }

            if (size == 0) {
                sender.sendMessage("#" + COLOR.gold() + structureId + " - " + COLOR.blue() + structureName, COLOR.reset() + ownersString + COLOR.red() + ownershipString);
            } else {
                sender.sendMessage("#" + COLOR.gold() + structureId + " - " + COLOR.blue() + structureName, COLOR.reset() + ownersString, ownershipString);
            }

            return true;
        }

        if (args.argsLength() < 3) {
            throw new CommandException("Too few arguments" + "\n" + help);
        } else if (args.argsLength() > 3) {
            throw new CommandException("Too many arguments" + "\n" + help);
        }

        String method = args.getString(1);
        String playerArg = args.getString(2);

        if (!method.equalsIgnoreCase("add") && !method.equalsIgnoreCase("remove")) {
            throw new CommandException("Unknown method '" + method + "', expected 'add' or 'remove'" + "\n" + help);
        }

        long start = System.currentTimeMillis();
        try (Transaction tx = graph.beginTx()) {
            StructureNode structurenode = structureRepository.findById(structureId);
            if (structurenode == null) {
                tx.success();
                throw new CommandException("Couldn't find structure for id #" + structureId);
            }

            if (!isOP(sender)) {
                IPlayer player = (IPlayer) sender;
                IOwnership ownership = structurenode.getOwnerDomain().getOwnership(player.getUniqueId());

                if (ownership == null) {
                    tx.success();
                    throw new CommandException("You don't own this structure");
                }

                if (ownership.getOwnerType().getTypeId() < requestedType.getTypeId()) {
                    tx.success();
                    throw new CommandException("You don't have enough privileges to " + method + " players of type '" + requestedType.name() + "'");
                }

                if (requestedType == OwnerType.MASTER && ownership.getOwnerType() == requestedType && method.equalsIgnoreCase("remove")) {
                    tx.success();
                    throw new CommandException("Players of type '" + OwnerType.MASTER + "' can't remove each other");
                }

            }

            IPlayer ply;
            if (idFlag == null) {
                if (!isUniquePlayerName(playerArg, structureAPI)) {
                    throw new CommandException("Player name '" + playerArg + "' is not unique \n"
                            + "Use player id instead of name \n"
                            + "Usage: /structure:owners [structureId] " + method + " -# [playerId]"
                            + "The other player can get it's player id by using the '/settler:me' command"
                    );
                }

                ply = structureAPI.getPlatform().getPlayer(playerArg);
                if (ply == null) {
                    tx.success();
                    throw new CommandException("Couldn't find a player for '" + playerArg + "'");
                }
            } else {
                Long id = null;
                try {
                    id = Long.parseLong(idFlag);
                    IBaseSettler sn = settlerRepository.findById(id);
                    if (sn == null) {
                        tx.success();
                        throw new CommandException("Couldn't find a player for id'" + idFlag + "'");
                    }
                    ply = structureAPI.getPlatform().getPlayer(sn.getUniqueIndentifier());

                } catch (NumberFormatException nfe) {
                    tx.success();
                    throw new CommandException("Expected a number after -# but got'" + idFlag + "'");
                }
            }

            UUID uuid = ply.getUniqueId();
            if (method.equalsIgnoreCase("add")) {
                IBaseSettler settler = settlerRepository.findByUUID(ply.getUniqueId());
                OwnerDomain ownerDomain = structurenode.getOwnerDomain();
                IOwnership ownershipToAdd = ownerDomain.getOwnership(settler.getUniqueIndentifier());

                if (ownershipToAdd == null) {
                    ownerDomain.updateOwnership(settler, requestedType);
                    EventManager.getInstance().getEventBus().post(new StructureAddOwnerEvent(uuid, new Structure(structurenode), requestedType));
                    sender.sendMessage("Successfully added '" + COLOR.green() + ply.getName() + COLOR.reset() + "' to #" + COLOR.gold() + structureId + " " + COLOR.blue() + structurenode.getName() + COLOR.reset() + " as " + COLOR.yellow() + requestedType.name());
                } else {
                    ownerDomain.updateOwnership(settler, requestedType);
                    EventManager.getInstance().getEventBus().post(new StructureAddOwnerEvent(uuid, new Structure(structurenode), requestedType));
                    sender.sendMessage("Updated ownership of '" + COLOR.green() + ply.getName() + COLOR.reset() + "' to " + COLOR.yellow() + requestedType.name() + COLOR.reset() + " for structure ",
                            "#" + COLOR.gold() + structurenode.getId() + " " + COLOR.blue() + structurenode.getName());
                }
            } else { // remove
                OwnerDomain ownerDomain = structurenode.getOwnerDomain();
                if (!ownerDomain.removeOwnership(uuid)) {
                    sender.sendMessage(ply.getName() + " does not own this structure...");
                    return true;
                }
                EventManager.getInstance().getEventBus().post(new StructureRemoveOwnerEvent(uuid, new Structure(structurenode), requestedType));
                sender.sendMessage("Successfully removed '" + COLOR.green() + ply.getName() + COLOR.reset() + "' from #" + COLOR.gold() + structureId + " " + COLOR.blue() + structurenode.getName() + " as " + COLOR.yellow() + requestedType.name());
            }

            tx.success();
        }
        LOG.log(Level.INFO, "owners add/remove in {0} ms", (System.currentTimeMillis() - start));
        return true;
    }

    @CommandPermissions(Permissions.STRUCTURE_LIST)
    @CommandExtras(async = true)
    @Command(aliases = {"structure:list", "stt:list"}, desc = "")
    public static void list(final CommandContext args, ICommandSender sender, IStructureAPI structureAPI) throws Exception {
        final GraphDatabaseService graph = SettlerCraft.getInstance().getNeo4j();
        final ISettlerRepository structureOwnerRepository = new SettlerRepositiory(graph);
        final IColors colors = structureAPI.getPlatform().getChatColors();
        
        int page = 0;
        final UUID playerId;
        if (args.argsLength() == 0) {
            if(!isPlayer(sender)) {
                throw new CommandUsageException("Too few arguments!", "/structure:list - Is for players only");
            }
            
            playerId = getUUID(sender);

        } else if (args.argsLength() == 1) {
            if(!isPlayer(sender)) {
                throw new CommandUsageException("Too few arguments!", "/structure:list [page] - Is for players only");
            }
            
            playerId = getUUID(sender);
            String pageArg = args.getString(0);
            if (NumberUtils.isNumber(pageArg)) {
                page = Integer.parseInt(pageArg);
            } else {
                throw new com.chingo247.settlercraft.core.exception.CommandException("Expected a number but got '" + pageArg + "'");
            }
        } else { // 2 arguments

            String plyName = args.getString(0);
            IPlayer ply = SettlerCraft.getInstance().getPlatform().getServer().getPlayer(plyName);
            if (ply == null) {
                throw new CommandException("Player '" + plyName + "' not found");
            }
            playerId = ply.getUniqueId();
            String pageString = args.getString(1);
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
            ISettler structureOwner = structureOwnerRepository.findByUUID(playerId);

            long countStart = System.currentTimeMillis();
            long totalStructures = structureOwner.getStructureCount();
            LOG.log(Level.INFO, "list count in {0} ms", (System.currentTimeMillis() - countStart));
            long totalPages = Math.round(Math.ceil(totalStructures / (MAX_LINES - 1)));
            List<StructureNode> structures = structureOwner.getStructures(skip, limit);
            if (p > totalPages || p < 0) {
                throw new CommandException("Page " + p + " out of " + totalPages + "...");
            }

            int lineNumber = 0;
            message[0] = "-----------(Page: " + p + "/" + totalPages + ", Structures: " + totalStructures + ")---------------";
            lineNumber++;
            for (StructureNode structureNode : structures) {
//                

                String line;
                double price = structureNode.getPrice();
                if (price > 0.0d) {
                    line = String.format("#%-1s%-10d%-3s%-40s%-15s%-1s%-5s", colors.gold(), structureNode.getId(), colors.blue(), structureNode.getName(), getStatusString(structureNode, colors), colors.yellow(), ShopUtil.valueString(price));
                } else {
                    line = String.format("#%-1s%-10d%-3s%-40s%-15s", colors.gold(), structureNode.getId(), colors.blue(), structureNode.getName(), getStatusString(structureNode, colors));
                }

                message[lineNumber] = line;
                lineNumber++;
            }
            tx.success();
        }
        LOG.log(Level.INFO, "list structures in {0} ms", (System.currentTimeMillis() - start));
        sender.sendMessage(message);
    }
    
    @CommandPermissions(Permissions.STRUCTURE_LOCATION)
    @CommandExtras(async = true, senderType = CommandSenderType.PLAYER)
    @Command(aliases = {"structure:location", "stt:location"}, desc = "")
    public static void location(final CommandContext args, ICommandSender sender, IStructureAPI structureAPI) throws Exception {
        final GraphDatabaseService graph = SettlerCraft.getInstance().getNeo4j();
        final IStructureRepository structureRepository = new StructureRepository(graph);
        final IColors colors = structureAPI.getPlatform().getChatColors();
        final IPlayer player = (IPlayer) sender;
        
        if (args.argsLength() == 1) {
            Long id;
            String idArg = args.getString(0);
            try {
                id = Long.parseLong(idArg);
            } catch (NumberFormatException nfe) {
                throw new CommandException("Expected a number but got '" + idArg + "'");
            }

            ILocation loc = player.getLocation();
            long start = System.currentTimeMillis();
            try (Transaction tx = graph.beginTx()) {
                StructureNode structure = structureRepository.findById(id);
                if (structure == null) {
                    tx.success();
                    throw new CommandException("Couldn't find structure for id #" + id);
                }
                if (structure.getStatus() == ConstructionStatus.REMOVED) {
                    tx.success();
                    throw new CommandException("Can't get relative location of a removed structure");
                }

                World w = SettlerCraft.getInstance().getWorld(structure.getWorld().getName());
                if (!w.getName().equals(player.getWorld().getName())) {
                    tx.success();
                    throw new CommandException("Structure must be in the same world...");
                }
                Vector rel = structure.getRelativePosition(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
                player.sendMessage("Your relative position is " + colors.yellow() + "x: " + colors.reset() + rel.getBlockX() + colors.yellow() + " y: " + colors.reset() + rel.getBlockY() + colors.yellow() + " z: " + colors.reset() + rel.getBlockZ());

                tx.success();
            }
            LOG.log(Level.INFO, "relative location in {0} ms", (System.currentTimeMillis() - start));

        } else {
            IPlayer ply = (IPlayer) player;
            ILocation loc = ply.getLocation();

            Vector pos = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            long start = System.currentTimeMillis();
            try (Transaction tx = graph.beginTx()) {
                StructureNode structure = structureRepository.findStructureOnPosition(ply.getWorld().getUUID(), pos);

                if (structure == null) {
                    throw new CommandException("Not within a structure...");
                }

                StructureWorld w = structure.getWorld();
                if (!w.getName().equals(player.getWorld().getName())) {
                    throw new CommandException("Structure must be in the same world...");
                }

                Vector rel = structure.getRelativePosition(new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));

                player.sendMessage("Your relative position is " + colors.yellow() + "x: " + colors.reset() + rel.getBlockX() + colors.yellow() + " y: " + colors.reset() + rel.getBlockY() + colors.yellow() + " z: " + colors.reset() + rel.getBlockZ());
                tx.success();
            }
            LOG.log(Level.INFO, "relative location in {0} ms", (System.currentTimeMillis() - start));
        }
        
    }

}
