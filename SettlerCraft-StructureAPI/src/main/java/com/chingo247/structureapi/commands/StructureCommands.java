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

import com.chingo247.menuapi.menu.CategoryMenu;
import com.chingo247.menuapi.menu.MenuAPI;
import com.chingo247.menuapi.menu.util.ShopUtil;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.exception.CommandException;
import com.chingo247.settlercraft.core.platforms.IPermissionManager;
import com.chingo247.structureapi.persistence.dao.StructureDAO;
import com.chingo247.structureapi.persistence.entities.structure.StructureNode;
import com.chingo247.structureapi.platforms.util.Permissions;
import com.chingo247.structureapi.structure.DefaultStructureFactory;
import com.chingo247.structureapi.structure.Structure;
import com.chingo247.structureapi.structure.StructureAPI;
import com.chingo247.structureapi.structure.plan.placement.options.PlaceOptions;
import com.chingo247.structureapi.structure.plan.placement.options.DemolishingOptions;
import com.chingo247.xplatform.core.IColors;
import com.chingo247.xplatform.core.ICommandSender;
import com.chingo247.xplatform.core.IPlayer;
import com.sk89q.worldedit.Vector;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import org.apache.commons.lang.math.NumberUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author Chingo
 */
public class StructureCommands {

    private static final int MAX_LINES = 10;
    private final StructureAPI structureAPI;
    private final IPermissionManager permissionManager;
    private final IColors COLOR;
    private final ExecutorService executorService;
    private final StructureDAO structureDAO;
    private final GraphDatabaseService graph;

    public StructureCommands(StructureAPI structureAPI, IPermissionManager permissionManager, ExecutorService executorService, GraphDatabaseService graph) {
        this.structureAPI = structureAPI;
        this.permissionManager = permissionManager;
        this.COLOR = structureAPI.getPlatform().getChatColors();
        this.executorService = executorService;
        this.structureDAO = new StructureDAO(graph);
        this.graph = graph;
    }

    public boolean handle(ICommandSender sender, String command, String[] args) throws CommandException {
        if (args.length == 0) {
            throw new CommandException("Too few arguments!");
        }

        String commandArg = args[0];
        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

        System.out.println("issued: " + commandArg + " " + Arrays.toString(commandArgs));

        switch (commandArg) {
            case "info":
                return info(sender, commandArgs);
            case "build":
                checkIsPlayer(sender);
                return build((IPlayer)sender, commandArgs);
            case "demolish":
                checkIsPlayer(sender);
                return demolish((IPlayer)sender, commandArgs);
            case "halt":
                checkIsPlayer(sender);
                return stop((IPlayer)sender, commandArgs);
            case "list":
                checkIsPlayer(sender);
                return list((IPlayer) sender, commandArgs);
            case "location":
                checkIsPlayer(sender);
                return location((IPlayer) sender, commandArgs);
            case "menu":
                checkIsPlayer(sender);
                return openMenu((IPlayer) sender, commandArgs, true);
            case "shop":
                checkIsPlayer(sender);
                return openMenu((IPlayer) sender, commandArgs, false);
            default:
                throw new CommandException("No action known for '/" + command + " " + commandArg);
        }
    }

    private boolean location(IPlayer player, String[] commandArgs) {
        System.out.println("Location Command");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean info(ICommandSender sender, String[] commandArgs) {
        System.out.println("Info Command");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean build(IPlayer player, String[] commandArgs) throws CommandException {
        System.out.println("Build Command");
        argumentsInRange(1, 2, commandArgs);
        
        String structureIdArg = commandArgs[0];
        if(!NumberUtils.isNumber(structureIdArg)) {
            throw new CommandException("Expected a number but got '"+structureIdArg+"'");
        }
        
        String force = commandArgs.length == 2 ? commandArgs[1] : null;
        if(force != null && !(force.equals("force") && force.equals("f"))) {
            throw new CommandException("Unknown second argument '"+force+"' ");
        } 
        boolean useForce = force != null && (force.equals("f") || force.equals("force"));
        
        long id = Long.parseLong(structureIdArg);
        Structure structure;
        try(Transaction tx = graph.beginTx()) {
            StructureNode structureNode = structureDAO.find(id);
            if(!structureNode.isOwner(player.getUniqueId())) {
                if( !(player.isOP() && useForce)) {
                    throw new CommandException("You don't own this structure...");
                }
            }
            structure = DefaultStructureFactory.instance().makeStructure(structureNode);
            tx.success();
        }
        structure.build(SettlerCraft.getInstance().getPlayer(player.getUniqueId()), new PlaceOptions(), useForce);
        return true;
    }

    private boolean demolish(IPlayer player, String[] commandArgs) throws CommandException {
        System.out.println("Demolish Command");
        argumentsInRange(1, 2, commandArgs);
        
        String structureIdArg = commandArgs[0];
        if(!NumberUtils.isNumber(structureIdArg)) {
            throw new CommandException("Expected a number but got '"+structureIdArg+"'");
        }
        
        String force = commandArgs.length == 2 ? commandArgs[1] : null;
        if(force != null && !(force.equals("force") && force.equals("f"))) {
            throw new CommandException("Unknown second argument '"+force+"' ");
        } 
        boolean useForce = force != null && (force.equals("f") || force.equals("force"));
        
        long id = Long.parseLong(structureIdArg);
        Structure structure;
        try(Transaction tx = graph.beginTx()) {
            StructureNode structureNode = structureDAO.find(id);
            if(!structureNode.isOwner(player.getUniqueId())) {
                if( !(player.isOP() && useForce)) {
                    throw new CommandException("You don't own this structure...");
                }
            }
            structure = DefaultStructureFactory.instance().makeStructure(structureNode);
            tx.success();
        }
        
        structure.demolish(SettlerCraft.getInstance().getPlayer(player.getUniqueId()), new DemolishingOptions(), useForce);
        return true;
    }

    private boolean stop(IPlayer player, String[] commandArgs) throws CommandException {
        System.out.println("Stop Command");
        argumentsInRange(1, 2, commandArgs);
        
        String structureIdArg = commandArgs[0];
        if(!NumberUtils.isNumber(structureIdArg)) {
            throw new CommandException("Expected a number but got '"+structureIdArg+"'");
        }
        
        String force = commandArgs.length == 2 ? commandArgs[1] : null;
        if(force != null && !(force.equals("force") && force.equals("f"))) {
            throw new CommandException("Unknown second argument '"+force+"' ");
        } 
        boolean useForce = force != null && (force.equals("f") || force.equals("force"));
        
        long id = Long.parseLong(structureIdArg);
        Structure structure;
        try(Transaction tx = graph.beginTx()) {
            StructureNode structureNode = structureDAO.find(id);
            if(!structureNode.isOwner(player.getUniqueId())) {
                if( !(player.isOP() && useForce)) {
                    throw new CommandException("You don't own this structure...");
                }
            }
            structure = DefaultStructureFactory.instance().makeStructure(structureNode);
            tx.success();
        }
        
        structure.stop(useForce);
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
        if (!isFree && MenuAPI.getInstance().getEconomyProvider() == null) {
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
            throw new org.bukkit.command.CommandException("Planmenu is initialized yet, please wait...");
        }

        if (!planmenu.isEnabled()) {
            throw new org.bukkit.command.CommandException("Planmenu is not ready yet, please wait");
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
        executorService.submit(new Runnable() {

            @Override
            public void run() {
                String[] message = new String[MAX_LINES];
                int skip = p * MAX_LINES;
                int limit = skip + MAX_LINES;

                try (Transaction tx = graph.beginTx()) {
                    long totalStructures = structureDAO.getStructureCountForSettler(playerId);
                    long totalPages = Math.round(Math.ceil(totalStructures / (MAX_LINES - 1)));
                    List<StructureNode> structures = structureDAO.getStructuresForSettler(playerId, skip, limit);
                    if (p > totalPages || p < 0) {
                        iPlayer.sendMessage(COLOR.red() + "Page " + p + " out of " + totalPages + "...");
                        return;
                    }
                    
                    int lineNumber = 0;
                    message[0] = "-----------(Page: " + p + "/" + totalPages + ", Structures: " + totalStructures + ")-----------";
                    for (StructureNode structure : structures) {
                        lineNumber++;
                        Vector position = structure.getCuboidRegion().getMinimumPoint();
                        String line = "#" + COLOR.gold() + structure.getId() + " " + COLOR.blue() + structure.getName()
                                + " " + COLOR.yellow() + "X: " + COLOR.reset() + position.getX()
                                + " " + COLOR.yellow() + "Y: " + COLOR.reset() + position.getY()
                                + " " + COLOR.yellow() + "Z: " + COLOR.reset() + position.getZ();
                                if(structure.getPrice() > 0.0d) {
                                    line += " " + COLOR.reset() + COLOR.yellow() + "Value: " + ShopUtil.valueString(structure.getPrice());
                                }
                        message[lineNumber] = line;
                    }
                    tx.success();
                }
                iPlayer.sendMessage(message);
            }
        });

        return true;

    }
    
  

}
