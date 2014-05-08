/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.plugin.commands;

import com.sc.api.menu.plugin.shop.MenuManager;
import com.sc.api.structure.SCStructureAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class SetterCraftCommands implements CommandExecutor {

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
                default: return false;
            }
        }
    }

    private boolean openPlanMenu(Player player) {
        MenuManager.getInstance().getMenu(SCStructureAPI.PLAN_SHOP_NAME).onEnter(player);
        return true;
    }
    
}
