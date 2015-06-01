/*
 * Copyright (C) 2015 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.settlercraft.core.platforms.bukkit;

import com.chingo247.xplatform.platforms.bukkit.BukkitPlatform;
import com.chingo247.xplatform.platforms.bukkit.BukkitPlugin;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.exception.SettlerCraftException;
import com.chingo247.settlercraft.core.platforms.bukkit.services.BKVaultEconomyProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class BKSettlerCraftCorePlugin extends JavaPlugin {
    
    private final String HOST = "localhost";
    private final int PORT = 9001;
    private final String MAIN_DATABASE = "SettlerCraft";
    private final String PATH = "plugins//SettlerCraft//database//";
    private final String MAIN_PATH = PATH + MAIN_DATABASE;

    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            System.out.println("World Edit not found! Disabling SettlerCraft-Core");
            this.setEnabled(false);
            return;
        }
        
        SettlerCraft.getInstance().registerPlatform(new BukkitPlatform(Bukkit.getServer()));
        SettlerCraft.getInstance().registerPlugin(new BukkitPlugin(this));
        SettlerCraft.getInstance().registerPlayerProvider(new BKPlayerProvider());
        
         // Init HSQL Server
//        
//        if (!HSQLServer.isRunning(HOST, PORT)) { 
//            Bukkit.getConsoleSender().sendMessage("SettlerCraft: Starting HSQL Server...");
//            HSQLServer hSQLServer = new HSQLServer(PORT, HOST, new File(getDataFolder(), "databases//HSQL//SettlerCraft"), HOST);
//            hSQLServer.start();
//        }
        
//        SettlerCraft.getInstance().getExecutor().submit(new Runnable() {
//
//            @Override
//            public void run() {
//                 HibernateUtil.getSession().close();
//            }
//        });
        
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                SettlerCraft.getInstance().getNeo4j().shutdown();
            }
        }));
        
        if(Bukkit.getPluginManager().getPlugin("Vault") != null) {
            BKVaultEconomyProvider economyProvider = new BKVaultEconomyProvider();
            if(economyProvider.isEnabled()) {
                try {
                    SettlerCraft.getInstance().registerEconomyService(economyProvider);
                } catch (SettlerCraftException ex) {
                    Logger.getLogger(BKSettlerCraftCorePlugin.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        Bukkit.getPluginManager().registerEvents(new BKEventListener(), this);
        
    }

    @Override
    public void onDisable() {
         SettlerCraft.getInstance().getNeo4j().shutdown();
         SettlerCraft.getInstance().getExecutor().shutdown();
    }
    
    
    
    
    
    
}
