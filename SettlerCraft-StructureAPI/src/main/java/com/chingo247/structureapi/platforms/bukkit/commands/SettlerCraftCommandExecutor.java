package com.chingo247.structureapi.platforms.bukkit.commands;

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

import com.chingo247.menuapi.menu.CategoryMenu;
import com.chingo247.xplatform.platforms.bukkit.BukkitPlayer;
import com.chingo247.structureapi.platforms.bukkit.BKPermissionManager;
import com.chingo247.structureapi.platforms.bukkit.BKStructureAPIPlugin;
import com.chingo247.structureapi.StructureAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class SettlerCraftCommandExecutor implements CommandExecutor {

    private static final int MAX_LINES = 10;
    private final BKStructureAPIPlugin structureAPIPlugin;
//    private final StructureDAO structureDAO = new StructureDAO();

    public SettlerCraftCommandExecutor(BKStructureAPIPlugin structureAPIPlugin) {
        this.structureAPIPlugin = structureAPIPlugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {

        if (args.length == 0) {
            cs.sendMessage(ChatColor.RED + "Too few arguments");
            return true;
        }

        try {
            
            switch (args[0]) {
                case "menu":
                    checkIsPlayer(cs);
                    openMenu(cs, args);
                    break;
                case "shop":
                    checkIsPlayer(cs);
                    openShop(cs, args);
                    break;
                case "refund":
//                    refund(cs, args);
                    break;
                case "reload":
                    reload(cs, args);
                    break;
                default:
                    String actionLast = "";
                    for (String s : args) {
                        actionLast += s + " ";
                    }
                    cs.sendMessage(ChatColor.RED + "No actions known for: " + actionLast);
                    return true;
            }
        } catch (CommandException e) {
            cs.sendMessage(e.getMessage());
        }
        return true;
    }
    
    private void checkIsPlayer(CommandSender sender) throws CommandException {
        if(!(sender instanceof Player)) {
           throw new CommandException("This command is for player's only...");
        }
    } 

    private void openMenu(CommandSender sender, String[] args) throws CommandException {
       if (args.length > 1) {
            throw new CommandException("Too many arguments");
        }

        if (!(sender instanceof Player)) {
            throw new CommandException("You are not a player!");
        }
    }

    private void openShop(CommandSender sender, String[] args) throws CommandException {
        if (args.length > 1) {
            throw new CommandException("Too many arguments");
        }

        if (!(sender instanceof Player)) {
            throw new CommandException("You are not a player!");
        }

        if (!structureAPIPlugin.getConfigProvider().isPlanShopEnabled()) {
            throw new CommandException("Planshop is disabled");
        }

        Player player = (Player) sender;
        if (!BKPermissionManager.getInstance().isAllowed(player, BKPermissionManager.Perms.OPEN_PLAN_MENU)) {
            throw new CommandException("You have no permission to open the plan shop");
        }

        CategoryMenu planmenu = StructureAPI.getInstance().createPlanMenu();
        if (planmenu == null) {
            throw new CommandException("Planmenu is initialized yet, please wait...");
        }

        if (!planmenu.isEnabled()) {
            throw new CommandException("Planmenu is not ready yet, please wait");
        }
        planmenu.openMenu(new BukkitPlayer(player));
    }

    private void reload(CommandSender cs, String[] args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }




   

}
