/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft;

import com.settlercraft.listener.StructureChestListener;
import com.settlercraft.listener.StructurePlanListener;
import com.settlercraft.model.recipe.DefaultBuildingRecipes;
import com.settlercraft.model.structure.Structure;
import com.settlercraft.model.structure.StructureChest;
import com.settlercraft.model.structure.StructureSign;
import com.settlercraft.util.HibernateUtil;
import java.io.File;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraft extends JavaPlugin {

    public static final String name = "SettlerCraft";

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerBuildings();

//        registerCustomBuildings(buildingRegister);
        StructurePlanRegister.printStructures(new File(this.getDataFolder() + "/buildings.txt"));
        registerRecipes();
        Bukkit.getPluginManager().registerEvents(new StructurePlanListener(this), this);
        Bukkit.getPluginManager().registerEvents(new StructureChestListener(this), this);

        initDB();
    }

    @Override
    public void onLoad() {
//        // Register commands
//        CommandManager manager = new CommandManager(this);
//        manager.registerCommands(new BuildCommands());
//        manager.registerHelp();
//        System.out.println("[" + this.getName() + "]" + " registered commands!");
    }

    private void registerBuildings() {
        File buildingFolder = new File(getDataFolder().getAbsolutePath());
        if (!buildingFolder.exists()) {
            buildingFolder.mkdir();
        }
        StructurePlanRegister.registerStructures(buildingFolder);
    }

    private void registerRecipes() {
        DefaultBuildingRecipes.load(this);
    }

    private void initDB() {
        HibernateUtil.addAnnotatedClasses(
                Structure.class,
                StructureChest.class,
                StructureSign.class
        );
    }



}
