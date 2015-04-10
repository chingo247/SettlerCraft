package com.chingo247.structureapi.plugin.bukkit;

/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
import com.chingo247.proxyplatform.core.IPlugin;
import com.chingo247.proxyplatform.platforms.bukkit.BukkitPlatform;
import com.chingo247.proxyplatform.platforms.bukkit.BukkitPlugin;
import com.chingo247.settlercraft.core.exception.SettlerCraftException;
import com.chingo247.settlercraft.core.persistence.HSQLServer;
import com.chingo247.structureapi.plugin.bukkit.commands.SettlerCraftCommandExecutor;
import com.chingo247.structureapi.plugin.bukkit.listener.PlanListener;
import com.chingo247.structureapi.SettlerCraft;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraftPlugin extends JavaPlugin implements IPlugin {

    public static final String MSG_PREFIX = "[SettlerCraft]: ";
    private static final Logger LOGGER = Logger.getLogger(SettlerCraftPlugin.class);
    private static SettlerCraftPlugin instance;
    private BKEconomyProvider economyProvider;
    private final SettlerCraft settlerCraft = SettlerCraft.getInstance();

    private final ExecutorService service = new ThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors(), 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
//    private PlanMenuManager planMenuManager;
    private BKConfigProvider configProvider;
    

    @Override
    public void onEnable() {
        instance = this;
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            System.out.println(MSG_PREFIX + " WorldEdit NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            service.shutdown();
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("AsyncWorldEdit") == null) {
            System.out.println(MSG_PREFIX + " AsyncWorldEdit NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            service.shutdown();
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            System.out.println(MSG_PREFIX + " WorldGuard NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            service.shutdown();
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            System.out.println(MSG_PREFIX + " Vault NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            service.shutdown();
            return;
        }
        // Register platform
        settlerCraft.registerPlatform(new BukkitPlatform(Bukkit.getServer()));
        settlerCraft.registerPlugin(new BukkitPlugin(this));
        settlerCraft.registerExecutor(service);

        // Register Economy
        economyProvider = new BKEconomyProvider();
        settlerCraft.registerEconomyProvider(new BKEconomyProvider());
        
        // Register Config
        configProvider = new BKConfigProvider();
        try {
            configProvider.load();
        } catch (SettlerCraftException ex) {
            java.util.logging.Logger.getLogger(SettlerCraftPlugin.class.getName()).log(Level.SEVERE, null, ex);
            setEnabled(false);
            return;
        }
        settlerCraft.registerConfigProvider(configProvider);
        
//        // Register menu API
//        BKMenuAPI menuAPI = new BKMenuAPI();
//        settlerCraft.registerMenuAPI(menuAPI);
//        Bukkit.getPluginManager().registerEvents(menuAPI, this);
//        
//        
//        StructurePlanMenuReader reader = new StructurePlanMenuReader();
//        CategoryMenu menu;
//        try {
//            menu = reader.read(menuAPI, new File(getDataFolder(), "menu.xml"));
//        } catch (DocumentException | SettlerCraftException ex) {
//            java.util.logging.Logger.getLogger(SettlerCraftPlugin.class.getName()).log(Level.SEVERE, null, ex);
//            System.out.println("Disabling SettlerCraft!");
//            this.setEnabled(false);
//            return;
//        }
        
        
//        
//        
//        settlerCraft.registerStructureAPI(new BKStructureAPI(this, service));
//        settlerCraft.registerPlanMenu(menu);
        
        
        

        // Init HSQL Server
        HSQLServer hSQLServer = HSQLServer.getInstance();
        if (!hSQLServer.isRunning()) {
            Bukkit.getConsoleSender().sendMessage(MSG_PREFIX + "Starting HSQL Server");
            hSQLServer.start();
        }

        settlerCraft.initialize();

        Bukkit.getPluginManager().registerEvents(new PlanListener(settlerCraft, service, economyProvider), this);
//
        getCommand("sc").setExecutor(new SettlerCraftCommandExecutor(this));
//        getCommand("cst").setExecutor(new ConstructionCommandExecutor(structureAPI));
//        getCommand("stt").setExecutor(new StructureCommandExecutor(structureAPI));

    }

    public SettlerCraft getSettlerCraft() {
        return settlerCraft;
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(SettlerCraftPlugin.MSG_PREFIX + " Shutting down...");
        service.shutdown();
//        HibernateUtil.shutdown();
    }


    public BKConfigProvider getConfigProvider() {
        return configProvider;
    }
    
    public static SettlerCraftPlugin getInstance() {
        return instance;
    }

//    public CategoryMenu getPlanMenu() {
//        return planMenuManager.getPlanMenu();
//    }

//    private void resetStates() {
//        Session session = HibernateUtil.getSession();
//        QStructure qs = QStructure.structure;
//
//        new HibernateUpdateClause(session, qs).where(qs.state.ne(Structure.State.COMPLETE).and(qs.state.ne(Structure.State.REMOVED)))
//                .set(qs.state, Structure.State.STOPPED)
//                .execute();
//        session.close();
//    }
    public static void print(String[] messages) {
        for (String s : messages) {
            print(s);
        }
    }

    public static void print(String message) {
        Bukkit.getConsoleSender().sendMessage(MSG_PREFIX + message);
    }

}
