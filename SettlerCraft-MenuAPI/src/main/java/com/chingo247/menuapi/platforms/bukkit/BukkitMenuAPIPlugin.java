/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.menuapi.platforms.bukkit;

import com.chingo247.menuapi.menu.BKMenuAPIListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class BukkitMenuAPIPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("SettlerCraft-Core") == null) {
            System.out.println("SettlerCraft-Core not found! Disabling MenuAPI");
            this.setEnabled(false);
            return;
        }
        
        Bukkit.getPluginManager().registerEvents(new BKMenuAPIListener(), this);

    }
    
    
    
}
