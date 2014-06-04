/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.plugin.menu;

import com.sc.plugin.listener.ShopListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SCMenu extends JavaPlugin {

    
    @Override
    public void onEnable() {
         Bukkit.getServer().getPluginManager().registerEvents(new ShopListener(), this);
    }
    

    
}
