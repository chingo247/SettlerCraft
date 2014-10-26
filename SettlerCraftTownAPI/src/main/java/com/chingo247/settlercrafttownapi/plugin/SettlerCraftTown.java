/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercrafttownapi.plugin;

import com.chingo247.settlercrafttownapi.tools.ToolListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraftTown extends JavaPlugin {
    
    private static SettlerCraftTown instance;
    
    public static SettlerCraftTown getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new ToolListener(), this);
    }
    
    
    
   
    
    
    
}
