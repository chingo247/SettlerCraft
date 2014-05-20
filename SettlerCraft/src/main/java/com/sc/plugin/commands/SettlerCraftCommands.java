/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.plugin.commands;

import com.sc.api.menu.plugin.shop.ItemShopCategoryMenu;
import com.sc.api.menu.plugin.shop.MenuManager;
import com.sc.plugin.SettlerCraft;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class SettlerCraftCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        if(args.length == 0) {
            return false;
        } else {
            switch(args[0]) {
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

    
    private boolean openShopMenu(Player player) {
        ItemShopCategoryMenu menu = (ItemShopCategoryMenu) MenuManager.getInstance().getMenu(SettlerCraft.PLANSHOP);
        menu.onEnter(player);
        return true;
    }
}
