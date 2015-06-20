/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.towny.plugin;

import com.chingo247.settlercraft.structureapi.structure.StructureAPI;
import com.chingo247.settlercraft.towny.restriction.TownyRestriction;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraftTowny extends JavaPlugin {

    public SettlerCraftTowny() {
        
        
        
        
    }

    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("Towny") == null) {
            System.out.println("[SettlerCraft]: Couldn't find towny!");
            System.out.println("[SettlerCraft]: Disabling");
            return;
        }
        
        if(Bukkit.getPluginManager().getPlugin("SettlerCraft-StructureAPI") == null) {
            System.out.println("[SettlerCraft]: Couldn't find SettlerCraft-StructureAPI!");
            System.out.println("[SettlerCraft]: Disabling");
            return;
        }
        
        // Towny Restriction
        StructureAPI.getInstance().addRestriction(new TownyRestriction());
        
    }
    
    
    
    
    
}
