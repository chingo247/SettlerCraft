/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.main;

import com.not2excel.api.command.CommandManager;
import com.settlercraft.commands.BuildCommands;
import com.settlercraft.listener.StructurePlanListener;
import com.settlercraft.model.recipe.DefaultBuildingRecipes;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import org.bukkit.Bukkit;

import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraft extends JavaPlugin {

    private StructurePlanRegister buildingRegister;

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        buildingRegister = new StructurePlanRegister();
        registerDefaultStructurePlan(buildingRegister);
//        registerCustomBuildings(buildingRegister);
        registerRecipes();
        Bukkit.getPluginManager().registerEvents(new StructurePlanListener(), this);
    }

    @Override
    public void onLoad() {
        // Register commands
        CommandManager manager = new CommandManager(this);
        manager.registerCommands(new BuildCommands());
        manager.registerHelp();
        System.out.println("[" + this.getName() + "]" + " registered commands!");
    }

//    private void registerCustomStructurePlanRegisterngRegister buildingRegister) {
//        File buildingFolder = new File(getDataFolder().getAbsolutePath() + "\\Buildings");
//        if (!buildingFolder.exists()) {
//            buildingFolder.mkdir();
//        }
//        buildingRegister.registerCustomBuildings(buildingFolder);
//    }

    private void registerDefaultStructurePlan(StructurePlanRegister structureRegister) {
      SettlerCraft.class.getResource("buildings\\default").getPath();
    }
    
    private void registerRecipes() {
        DefaultBuildingRecipes.load(this);
    }

//    public static void main(String... args) {
//        
//                
//    }
    
    
    
}
