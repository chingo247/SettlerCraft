/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure;

import com.sc.api.structure.exception.InvalidStructurePlanException;
import com.sc.api.structure.listeners.PlayerListener;
import com.sc.api.structure.listeners.StructurePlanListener;
import com.settlercraft.core.SettlerCraftAPI;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SCStructureAPI extends SettlerCraftAPI{

    public SCStructureAPI() {
        super("SCStructureAPI");
    }

    

    @Override
    public void setupListeners(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new StructurePlanListener(plugin), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), plugin);
    }

    @Override
    public void setupRecipes(JavaPlugin plugin) {
        // Add recipes to plugin here
    }
    
    /**
     * Read and loads all structures in the datafolder of the plugin
     * @param baseFolder The datafolder of the plugin
     */
    public void loadStructures(File baseFolder) {
        if (!baseFolder.exists()) {
            baseFolder.mkdir();
        }
        try {
            StructurePlanLoader spLoader = new StructurePlanLoader();
            spLoader.load(baseFolder);
        } catch (InvalidStructurePlanException ex) {
            Logger.getLogger(SCStructureAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void init(JavaPlugin plugin) {
        loadStructures(plugin.getDataFolder().getAbsoluteFile());
        setupListeners(plugin);
        setupRecipes(plugin);
    }


}
