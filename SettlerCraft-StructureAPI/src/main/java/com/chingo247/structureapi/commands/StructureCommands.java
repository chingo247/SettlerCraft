/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.structureapi.commands;

import com.chingo247.menuapi.menu.CategoryMenu;
import com.chingo247.menuapi.menu.MenuAPI;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.exception.CommandException;
import com.chingo247.settlercraft.core.platforms.IPermissionManager;
import com.chingo247.settlercraft.core.util.CubicIterator;
import com.chingo247.settlercraft.core.util.CubicIteratorReversed;
import com.chingo247.structureapi.platforms.util.Permissions;
import com.chingo247.structureapi.structure.StructureAPI;
import com.chingo247.xplatform.core.ICommandSender;
import com.chingo247.xplatform.core.ILocation;
import com.chingo247.xplatform.core.IPlayer;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.world.World;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chingo
 */
public class StructureCommands {

    private final StructureAPI structureAPI;
    private final IPermissionManager permissionManager;

    public StructureCommands(StructureAPI structureAPI, IPermissionManager permissionManager) {
        this.structureAPI = structureAPI;
        this.permissionManager = permissionManager;
    }

    public boolean handle(ICommandSender sender, String command, String[] args) throws CommandException {
        argumentsInRange(1, 1, args);

        String commandArg = args[0];
        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

        switch (commandArg) {
            case "cuboidr":
                checkIsPlayer(sender);
                return cuboidReversed((IPlayer) sender, commandArgs);
            case "cuboid":
                checkIsPlayer(sender);
                return cuboid((IPlayer) sender, commandArgs);
            case "info":
                return info(sender, commandArgs);
            case "build":
                return build(sender, commandArgs);
            case "demolish":
                return demolish(sender, commandArgs);
            case "stop":
                return stop(sender, commandArgs);
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

    private boolean build(ICommandSender sender, String[] commandArgs) {
        System.out.println("Build Command");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean demolish(ICommandSender sender, String[] commandArgs) {
        System.out.println("Demolish Command");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean stop(ICommandSender sender, String[] commandArgs) {
        System.out.println("Stop Command");
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    private boolean cuboid(IPlayer player, String[] commandArgs) {
        ILocation l = player.getLocation();

        Vector v = new BlockVector(l.getBlockX(), l.getBlockY(), l.getBlockZ()).add(new Vector(3, 3, 3));
        System.out.println("position: " + v);

        final CubicIterator iterator = new CubicIterator(v, new Vector(10, 10, 10), 5, 5, 5);
        final World w = SettlerCraft.getInstance().getWorld(player.getWorld().getName());
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (iterator.hasNext()) {
                        Vector newPos = iterator.next();
//                        System.out.println("new pos: " + newPos);
                        w.setBlock(newPos, new BaseBlock(1));
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(StructureCommands.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (WorldEditException ex) {
                    Logger.getLogger(StructureCommands.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();

        return true;
    }

    private boolean cuboidReversed(IPlayer player, String[] commandArgs) {
        ILocation l = player.getLocation();

        Vector v = new BlockVector(l.getBlockX(), l.getBlockY(), l.getBlockZ()).add(new Vector(3, 3, 3));
        System.out.println("position: " + v);

        final CubicIteratorReversed iterator = new CubicIteratorReversed(v, new Vector(10, 10, 10), 5, 5, 5);
        final World w = SettlerCraft.getInstance().getWorld(player.getWorld().getName());
        
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (iterator.hasNext()) {
                        Vector newPos = iterator.next();
//                        System.out.println("new pos: " + newPos);
                        w.setBlock(newPos, new BaseBlock(5));
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(StructureCommands.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (WorldEditException ex) {
                    Logger.getLogger(StructureCommands.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();

        return true;
    }

}
