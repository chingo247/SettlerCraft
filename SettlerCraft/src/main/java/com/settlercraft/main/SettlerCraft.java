/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.main;

import com.settlercraft.listener.StructurePlanListener;
import com.settlercraft.model.recipe.DefaultBuildingRecipes;
import com.settlercraft.model.structure.Structure;
import java.io.File;
import java.util.logging.Level;
import javax.persistence.PersistenceException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraft extends JavaPlugin {

    private StructureChestRegister structureChestRegister;

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        structureChestRegister = new StructureChestRegister(this);
        registerBuildings();

//        registerCustomBuildings(buildingRegister);
        StructurePlanRegister.printStructures(new File(this.getDataFolder() + "/buildings.txt"));
        registerRecipes();
        Bukkit.getPluginManager().registerEvents(new StructurePlanListener(this), this);
        setupDatabase();
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

//    public StructurePlanRegister getStructurePlanRegister() {
//        return this.structurePlanRegister;
//    }

    public StructureChestRegister getStructureChestRegister() {
        return this.structureChestRegister;
    }

    private void registerRecipes() {
        DefaultBuildingRecipes.load(this);
    }

    private void setupDatabase() {
        try {
            getDatabase().find(Structure.class).findRowCount();
        } catch (PersistenceException pe) {
            System.out.println("Installing database for " + getDescription().getName() + " due to first time usage");
            installDDL();
        }
    }

}
