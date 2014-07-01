/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.perks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SCPerks extends JavaPlugin {

    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("SettlerCraft") == null) {
            System.out.println("SettlerCraft not found!");
            this.setEnabled(false);
            return;
        }
        
        Bukkit.getPluginManager().registerEvents(new PerkListener(), this);
    }
    
    
    
}
