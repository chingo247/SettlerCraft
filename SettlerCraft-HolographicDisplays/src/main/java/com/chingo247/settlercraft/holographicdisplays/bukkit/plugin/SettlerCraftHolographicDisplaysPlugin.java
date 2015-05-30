/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.holographicdisplays.bukkit.plugin;

import com.chingo247.settlercraft.structureapi.platforms.services.holograms.StructureHologramManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraftHolographicDisplaysPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null) {
            StructureHologramManager.getInstance().setHologramProvider(new HolographicDisplaysHologramProvider());
        } else {
            System.out.println("[SettlerCraft-HolographicDisplays]: Couldn't find HolographicDisplays, Disabling SettlerCraft-HolographicDisplays");
            this.setEnabled(false);
        }
    }
    
    
    
}
