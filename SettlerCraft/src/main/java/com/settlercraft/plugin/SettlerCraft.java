/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose SettlerCraftTools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.plugin;

import com.sc.api.structure.SCStructureAPI;
import com.settlercraft.core.SCCore;
import com.settlercraft.core.SettlerCraftModule;
import com.settlercraft.plugin.exception.DuplicateAPIException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraft extends JavaPlugin {
    
    private SCStructureAPI structureModule;
    private Set<SettlerCraftModule> modules;
    public static final String name = "SettlerCraft";

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("Citizens") == null || getServer().getPluginManager().getPlugin("Citizens").isEnabled() == false) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        SCCore.initDB();
        modules = new HashSet<>();
        structureModule = new SCStructureAPI();
        
        try {

            addModule(structureModule);
        } catch (DuplicateAPIException ex) {
            Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void onLoad() {

    }
    
    public void addModule(SettlerCraftModule api) throws DuplicateAPIException {
        if(modules.add(api)) {
           api.init(this);
        } else {
            throw new DuplicateAPIException(api); // Self check
        }
    }
    
    

}
