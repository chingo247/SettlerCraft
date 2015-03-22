package com.chingo247.settlercraft.bukkit;

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
import com.chingo247.menu.CategoryMenu;
import com.chingo247.settlercraft.SettlerCraft;
import com.chingo247.settlercraft.bukkit.commands.SettlerCraftCommandExecutor;
import com.chingo247.settlercraft.bukkit.listener.PlanListener;
import com.chingo247.settlercraft.exception.SettlerCraftException;
import com.chingo247.settlercraft.model.persistence.HSQLServer;
import com.chingo247.settlercraft.bukkit.menu.PlanMenuManager;
import com.chingo247.settlercraft.model.persistence.hibernate.HibernateUtil;
import com.chingo247.xcore.core.IPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.dom4j.DocumentException;

/**
 *
 * @author Chingo
 */
public class SettlerCraftPlugin extends JavaPlugin implements IPlugin {

    public static final String MSG_PREFIX = "[SettlerCraft]: ";
    private static final Logger LOGGER = Logger.getLogger(SettlerCraftPlugin.class);
    private static SettlerCraftPlugin instance;
    private BKSettlerCraft settlerCraft;
    private BKEconomyProvider provider;

//    private final ThreadPoolExecutor GLOBAL_THREADPOOL = new ThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors(), 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private ExecutorService service;
    private PlanMenuManager planMenuManager;
    private BKConfigProvider configProvider;
    

    @Override
    public void onEnable() {
        instance = this;

        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            System.out.println(MSG_PREFIX + " WorldEdit NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("AsyncWorldEdit") == null) {
            System.out.println(MSG_PREFIX + " AsyncWorldEdit NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            System.out.println(MSG_PREFIX + " WorldGuard NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            System.out.println(MSG_PREFIX + " Vault NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }

        service = Executors.newCachedThreadPool();
        settlerCraft = new BKSettlerCraft(service, this);
        planMenuManager = new PlanMenuManager(settlerCraft);
        provider = new BKEconomyProvider();

        try {
            planMenuManager.initialize();
        } catch (DocumentException | SettlerCraftException ex) {
            java.util.logging.Logger.getLogger(SettlerCraftPlugin.class.getName()).log(Level.SEVERE, null, ex);
            setEnabled(false);
            return;
        }

        configProvider = new BKConfigProvider();
        try {
            configProvider.load();
        } catch (SettlerCraftException ex) {
            java.util.logging.Logger.getLogger(SettlerCraftPlugin.class.getName()).log(Level.SEVERE, null, ex);
            setEnabled(false);
            return;
        }

        // Init HSQL Server
        HSQLServer hSQLServer = HSQLServer.getInstance();
        if (!hSQLServer.isRunning()) {
            Bukkit.getConsoleSender().sendMessage(MSG_PREFIX + "Starting HSQL Server");
            hSQLServer.start();
        }

        // ValidationService restoreService = new BukkitValidationService(structureAPI);
        // restoreService.validate();
        // resetStates();
        settlerCraft.load();
        planMenuManager.load();

        Bukkit.getPluginManager().registerEvents(new PlanListener(settlerCraft, service, provider), this);
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
        HibernateUtil.shutdown();
    }


    public BKConfigProvider getConfigProvider() {
        return configProvider;
    }
    
    public static SettlerCraftPlugin getInstance() {
        return instance;
    }

    public CategoryMenu getPlanMenu() {
        return planMenuManager.getPlanMenu();
    }

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
