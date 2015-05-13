/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.core.platforms.bukkit;

import com.chingo247.xplatform.platforms.bukkit.BukkitPlatform;
import com.chingo247.xplatform.platforms.bukkit.BukkitPlugin;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.settlercraft.core.event.EventManager;
import com.chingo247.settlercraft.core.event.PlayerSubscriber;
import com.chingo247.settlercraft.core.exception.SettlerCraftException;
import com.chingo247.settlercraft.core.persistence.hibernate.HibernateUtil;
import com.chingo247.settlercraft.core.platforms.bukkit.services.BKVaultEconomyProvider;
import com.google.common.eventbus.EventBus;
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
        
        SettlerCraft.getInstance().getExecutor().submit(new Runnable() {

            @Override
            public void run() {
                 HibernateUtil.getSession().close();
            }
        });
        
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                SettlerCraft.getInstance().getNeo4j().shutdown();
            }
        }));
        
        
        BKVaultEconomyProvider economyProvider = new BKVaultEconomyProvider();
        try {
            SettlerCraft.getInstance().registerEconomyService(economyProvider);
        } catch (SettlerCraftException ex) {
            Logger.getLogger(BKSettlerCraftCorePlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
        Bukkit.getPluginManager().registerEvents(new BKEventListener(), this);
        EventBus eventBus  = EventManager.getInstance().getEventBus();
        eventBus.register(new PlayerSubscriber());
        
    }

    @Override
    public void onDisable() {
         SettlerCraft.getInstance().getNeo4j().shutdown();
    }
    
    
    
    
    
    
}
