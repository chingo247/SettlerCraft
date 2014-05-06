/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.menu.plugin;

import com.sc.api.menu.plugin.shop.ShopListener;
import com.settlercraft.core.SCVaultEconomyUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SCMenu extends JavaPlugin {
    
    

    @Override
    public void onEnable() {
        if(SCVaultEconomyUtil.getInstance().getEconomy() == null) {
            System.out.println("[" + getName() + "]: WARNING! NO ECONOMY FOUND");
        }
        Bukkit.getServer().getPluginManager().registerEvents(new ShopListener(), this);
        super.onEnable(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    
}
