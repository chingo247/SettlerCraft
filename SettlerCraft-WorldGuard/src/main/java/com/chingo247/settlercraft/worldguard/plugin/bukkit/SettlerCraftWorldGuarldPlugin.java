/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.worldguard.plugin.bukkit;

import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.structureapi.persistence.dao.StructureDAO;
import com.chingo247.settlercraft.structureapi.structure.IStructureAPI;
import com.chingo247.settlercraft.structureapi.structure.StructureAPI;
import com.chingo247.settlercraft.worldguard.protecttion.WorldGuardHelper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.neo4j.graphdb.GraphDatabaseService;

/**
 *
 * @author Chingo
 */
public class SettlerCraftWorldGuarldPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("SettlerCraft-Core") == null) {
           System.out.println("[SettlerCraft-WorldGuard]: SettlerCraft-Core NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return; 
        }
        
        if(Bukkit.getPluginManager().getPlugin("SettlerCraft-StructureAPI") == null) {
           System.out.println("[SettlerCraft-WorldGuard]: SettlerCraft-StructureAPI NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return; 
        }
        
        
        // Enable WorldGuard
        GraphDatabaseService graph = SettlerCraft.getInstance().getNeo4j();
        IStructureAPI structureAPI = StructureAPI.getInstance();
        
        
        
        if(Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            WorldGuardHelper worldGuardHelper = new WorldGuardHelper(graph, new StructureDAO(graph), structureAPI);
            worldGuardHelper.initialize();
        } else {
            System.out.println("[SettlerCraft-WorldGuard]: Couldn't find WorldGuard! Disabling SettlerCraft-WorldGuard");
            this.setEnabled(false);
        }
    }
    
    
    
    
}
