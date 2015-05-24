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
import com.chingo247.settlercraft.core.Settler;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.exception.CommandException;
import com.chingo247.settlercraft.core.persistence.dao.settler.DefaultSettlerFactory;
import com.chingo247.settlercraft.core.persistence.dao.settler.SettlerNode;
import com.chingo247.settlercraft.core.persistence.dao.world.WorldNode;
import com.chingo247.settlercraft.core.platforms.services.IPermissionManager;
import com.chingo247.settlercraft.core.util.KeyPool;
import com.chingo247.settlercraft.structureapi.exception.ConstructionException;
import com.chingo247.settlercraft.structureapi.persistence.dao.StructureDAO;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.settlercraft.structureapi.persistence.entities.structure.StructureRelTypes;
import com.chingo247.settlercraft.structureapi.platforms.bukkit.services.worldguard.WorldGuardHelper;
import com.chingo247.settlercraft.structureapi.platforms.util.Permissions;
import com.chingo247.settlercraft.structureapi.structure.ConstructionStatus;
import com.chingo247.settlercraft.structureapi.structure.DefaultStructureFactory;
import com.chingo247.settlercraft.structureapi.structure.IStructureAPI;
import com.chingo247.settlercraft.structureapi.structure.Structure;
import com.chingo247.settlercraft.structureapi.structure.StructureAPI;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.BuildOptions;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.DemolishingOptions;
import com.chingo247.settlercraft.structureapi.structure.plan.util.PlanGenerator;
import com.chingo247.xplatform.core.IColors;
import com.chingo247.xplatform.core.ICommandSender;
import com.chingo247.xplatform.core.ILocation;
import com.chingo247.xplatform.core.IPlayer;
import com.chingo247.xplatform.core.IWorld;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.util.com.google.common.collect.Maps;
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
    private final IStructureAPI structureAPI;
    private final IPermissionManager permissionManager;
    private final IColors COLOR;
    private final ExecutorService executorService;
    private final StructureDAO structureDAO;
    private final GraphDatabaseService graph;
    private final UUID console;
    private final KeyPool<UUID> playerPool;

    public StructureCommands(IStructureAPI structureAPI, IPermissionManager permissionManager, ExecutorService executorService, GraphDatabaseService graph) {
        this.structureAPI = structureAPI;
        this.permissionManager = permissionManager;
        this.COLOR = structureAPI.getPlatform().getChatColors();
        this.executorService = executorService;
        this.structureDAO = new StructureDAO(graph);
        this.graph = graph;
        this.console = UUID.randomUUID();
        this.playerPool = new KeyPool<>(executorService);
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
                        case "gplans":
                            if (sender instanceof IPlayer) {
                                IPlayer ply = (IPlayer) sender;
                                if (!ply.isOP()) {
                                    ply.sendMessage("You're not allowed to use this command, OP only");
                                    return;
                                }
                            }
                            File generationDirectory = StructureAPI.getInstance().getGenerationDirectory();
                            PlanGenerator.generate(generationDirectory);
                            break;
                        case "info":
                            info(sender, commandArgs);
                            break;
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
                        case "menu":
                            checkIsPlayer(sender);
                            openMenu((IPlayer) sender, commandArgs, true);
                            break;
                        case "shop":
                            checkIsPlayer(sender);
                            openMenu((IPlayer) sender, commandArgs, false);
                            break;
                        default:
                            throw new CommandException("No action known for '/" + command + " " + commandArg);
                    }
                } catch (CommandException ex) {
                    sender.sendMessage(ex.getMessage());
                } catch (Exception ex) { // Catch everything or disappear it will dissappear in the abyss!
                    Logger.getLogger(StructureCommands.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        });
        return true;
    }

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
        Settler master = null;
        try (Transaction tx = graph.beginTx()) {
            SettlerNode setn = structureDAO.getMasterOwnerForStructure(structure.getId());
            master = DefaultSettlerFactory.getInstance().makeStructureOwner(setn);
            tx.success();
        }

        String line = "#" + COLOR.gold() + structure.getId() + " " + COLOR.blue() + structure.getName() + "\n"
                + COLOR.yellow() + "World: " + COLOR.reset() + structure.getWorld().getName() + "\n";

        Vector position = structure.getPosition();
        line += "Location: " + COLOR.yellow() + "X: " + COLOR.reset() + position.getX()
                + " " + COLOR.yellow() + "Y: " + COLOR.reset() + position.getY()
                + " " + COLOR.yellow() + "Z: " + COLOR.reset() + position.getZ() + "\n";

        line += COLOR.yellow() + "Status: " + COLOR.reset() + structure.getConstructionStatus().name() + "\n";

        if (structure.getPrice() > 0) {
            line += COLOR.yellow() + "Value: " + COLOR.reset() + structure.getPrice() + "\n";
        }

        if (master != null) {
            line += COLOR.yellow() + "Owner(master): " + COLOR.reset() + master.getName() + "\n";
        }

        if (structure.getRawNode().hasProperty(WorldGuardHelper.WORLD_GUARD_REGION_PROPERTY)) {
            line += COLOR.yellow() + "WorldGuard-Region: " + COLOR.reset() + structure.getRawNode().getProperty(WorldGuardHelper.WORLD_GUARD_REGION_PROPERTY);
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
            throw new CommandException("No economy plugin available");
        }

        if (!structureAPI.getConfig().isPlanShopEnabled()) {
            throw new CommandException("Planshop is disabled");
        }

        if (isFree && !permissionManager.isAllowed(player, Permissions.OPEN_PLAN_MENU) && !player.isOP()) {
            throw new CommandException("You have no permission to open the plan menu");
        }

        if (!isFree && !permissionManager.isAllowed(player, Permissions.OPEN_PLAN_SHOP) && !player.isOP()) {
            throw new CommandException("You have no permission to open the plan shop");
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

}
