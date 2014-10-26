/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercrafttownapi;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class ToolCommandExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String label, String[] args) {
        if(!(cs instanceof Player)) {
            cs.sendMessage("You are not a player");
            return true;
        }
        
        Player player = (Player) cs;
        
        if(args.length == 0) {
            player.sendMessage(ChatColor.RED + "Too few arguments");
        }
        
        
        return true;
    }
    
}
