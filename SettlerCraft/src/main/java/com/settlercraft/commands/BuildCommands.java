/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.commands;

import com.not2excel.api.command.CommandHandler;
import com.not2excel.api.command.CommandListener;
import com.not2excel.api.command.objects.CommandInfo;

/**
 *
 * @author Chingo
 */
public class BuildCommands implements CommandListener {
    
    
    @CommandHandler(command = "build.road.wood")
    public void BuildRoadCommand(CommandInfo info) {
        info.getPlayer().sendMessage("BUILD WOOD ROAD");
    }
}
