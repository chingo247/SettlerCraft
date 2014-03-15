/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.main;

import com.not2excel.api.command.CommandManager;
import com.settlercraft.commands.BuildCommands;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraft extends JavaPlugin {

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register Event Listeners!
    }

    @Override
    public void onLoad() {
        // Register commands
        CommandManager manager = new CommandManager(this);
        manager.registerCommands(new BuildCommands());
        manager.registerHelp();
        System.out.println("[" + this.getName() + "]" + " registered commands!");
    }

}
