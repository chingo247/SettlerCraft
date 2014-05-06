/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose SettlerCraftTools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.plugin;

import com.settlercraft.core.SCCore;
import com.settlercraft.core.SCVaultEconomyUtil;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraft extends JavaPlugin {

    public static final String name = "SettlerCraft";

    @Override
    public void onEnable() {
        System.out.println(getServer().getPluginManager().getPlugin("SCStructureAPI"));
        if(getServer().getPluginManager().getPlugin("SCStructureAPI") == null) {
            
        }
        if(SCVaultEconomyUtil.getInstance().getEconomy() == null) {
            
        }
        SCCore.getInstance().initDB();
    }

    



}
