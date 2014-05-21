/*
 * Copyright (C) 2014 Chingo
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

package com.sc.api.structure.commands;

import com.sc.api.menu.plugin.shop.ItemShopCategoryMenu;
import com.sc.api.menu.plugin.shop.MenuManager;
import com.sc.api.structure.SCStructureAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class StructureCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if(args.length == 0) {
            return false;
        } else {
            switch(args[0]) {
                case "menu":
                    if(args.length == 1) {
                        return openPlanMenu((Player) cs);
                    } else {
                        return false;
                    }
                case "shop":
                    if(args.length == 1) {
                        return openShopMenu((Player) cs);
                    } else {
                        return false;
                    }
                default: return false;
            }
        }
    }

    private boolean openPlanMenu(Player player) {
        ItemShopCategoryMenu menu = (ItemShopCategoryMenu) MenuManager.getInstance().getMenu(SCStructureAPI.PLAN_MENU_NAME);
        menu.onEnter(player, true);
        return true;
    }
    
    private boolean openShopMenu(Player player) {
        if(Bukkit.getPluginManager().getPlugin("Vault") == null) {
            player.sendMessage(ChatColor.RED + " Planshop requires Vault to work");
            return false;
        }
        ItemShopCategoryMenu menu = (ItemShopCategoryMenu) MenuManager.getInstance().getMenu(SCStructureAPI.PLANSHOP);
        menu.onEnter(player);
        return true;
    }
    
}
