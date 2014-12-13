/*
 * Copyright (C) 2014 Chingo247
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
package com.chingo247.settlercraft.bukkit;

import com.chingo247.menu.CategoryMenu;
import com.chingo247.settlercraft.bukkit.PermissionManager.Perms;
import com.chingo247.settlercraft.bukkit.commands.ConstructionCommandExecutor;
import com.chingo247.settlercraft.bukkit.commands.SettlerCraftCommandExecutor;
import com.chingo247.settlercraft.bukkit.commands.StructureCommandExecutor;
import com.chingo247.settlercraft.bukkit.listener.FenceListener;
import com.chingo247.settlercraft.bukkit.listener.PlanListener;
import com.chingo247.settlercraft.bukkit.listener.PluginListener;
import com.chingo247.settlercraft.structure.QStructure;
import com.chingo247.settlercraft.structure.Structure;
import com.chingo247.settlercraft.structure.exception.SettlerCraftException;
import com.chingo247.settlercraft.structure.exception.StructureAPIException;
import com.chingo247.settlercraft.structure.persistence.HSQLServer;
import com.chingo247.settlercraft.structure.persistence.hibernate.HibernateUtil;
import com.chingo247.settlercraft.structure.persistence.hibernate.ValidationService;
import com.chingo247.settlercraft.structure.plan.PlanMenuManager;
import com.chingo247.xcore.platforms.bukkit.BukkitPlatform;
import com.mysema.query.jpa.hibernate.HibernateUpdateClause;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.dom4j.DocumentException;
import org.hibernate.Session;

/**
 *
 * @author Chingo
 */
public class SettlerCraftPlugin extends JavaPlugin {
    private static final Logger LOGGER = Logger.getLogger(SettlerCraftPlugin.class);
    private static SettlerCraftPlugin instance;
    private final ThreadPoolExecutor GLOBAL_THREADPOOL = new ThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors(), 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    private ConfigProvider configProvider;
    public static final String MSG_PREFIX = ChatColor.YELLOW + "[SettlerCraft]: " + ChatColor.RESET;
    private BukkitStructureAPI structureAPI;
    private PlanMenuManager planMenuManager;

    @Override
    public void onEnable() {
        instance = this;
        this.planMenuManager = new PlanMenuManager(this);
        this.configProvider = new ConfigProvider();
        this.structureAPI = new BukkitStructureAPI(this, GLOBAL_THREADPOOL, new BukkitPlatform(getServer()));
        if (Bukkit.getPluginManager().getPlugin("WorldEdit") == null) {
            System.out.println("[SettlerCraft]: WorldEdit NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("AsyncWorldEdit") == null) {
            System.out.println("[SettlerCraft]: AsyncWorldEdit NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }

        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) {
            System.out.println("[SettlerCraft]: WorldGuard NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }
        
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            System.out.println("[SettlerCraft]: Vault NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }
        

        
        try {
            configProvider.load();
        } catch (SettlerCraftException ex) {
            java.util.logging.Logger.getLogger(SettlerCraftPlugin.class.getName()).log(Level.SEVERE, null, ex);
            this.setEnabled(false);
            return;
        }
        
        
        
        // Init HSQL Server
        HSQLServer hSQLServer = HSQLServer.getInstance();
        if (!hSQLServer.isRunning()) {
            Bukkit.getConsoleSender().sendMessage(MSG_PREFIX + "Starting HSQL Server");
            hSQLServer.start();
        }
        
        ValidationService restoreService = new BukkitValidationService(structureAPI);
        restoreService.validate();
        resetStates();
        
        structureAPI.initialize();
        
        // Load plan menu from XML
        try {
            planMenuManager.init();
        } catch (DocumentException | StructureAPIException ex) {
            java.util.logging.Logger.getLogger(SettlerCraftPlugin.class.getName()).log(Level.SEVERE, null, ex);
            this.setEnabled(false);
            return;
        }
        
        planMenuManager.loadPlans();

        Bukkit.getPluginManager().registerEvents(new PluginListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlanListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FenceListener(), this);

        getCommand("sc").setExecutor(new SettlerCraftCommandExecutor(this));
        getCommand("cst").setExecutor(new ConstructionCommandExecutor(structureAPI));
        getCommand("stt").setExecutor(new StructureCommandExecutor(structureAPI));

        printPerms();
        structureAPI.print("Done!");
        
    }
    
    public ExecutorService getExecutorService() {
        return GLOBAL_THREADPOOL;
    }
    
    public ConfigProvider getConfigProvider() {
        return configProvider;
    }

    public BukkitStructureAPI getStructureAPI() {
        return structureAPI;
    }
    
   
    


    /**
     * Gets the datafolder for the StructureAPI or creates them if none exists
     *
     * @return The datafolder
     */
    public final File getStructureDataFolder() {
        File structureDirectory = new File(getDataFolder(), "Structures");
        if (!structureDirectory.exists()) {
            structureDirectory.mkdirs();
        }
        return structureDirectory;
    }

    private void printPerms() {
        File printedFile = new File(SettlerCraftPlugin.getInstance().getDataFolder(), "permissions.yml");
        if (!printedFile.exists()) {
            try {
                printedFile.createNewFile();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(SettlerCraftPlugin.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(printedFile);

        for (Perms perm : Perms.values()) {
            Permission p = perm.getPermission();
            config.createSection(p.getName());
            config.set(p.getName() + ".default", p.getDefault().toString());
            config.set(p.getName() + ".description", p.getDescription());
        }

        try {
            config.save(printedFile);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(SettlerCraftPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    

    public static SettlerCraftPlugin getInstance() {
        return instance;
    }

    public CategoryMenu getPlanMenu() {
        return planMenuManager.getPlanMenu();
    }


    private void resetStates() {
        Session session = HibernateUtil.getSession();
        QStructure qs = QStructure.structure;

        new HibernateUpdateClause(session, qs).where(qs.state.ne(Structure.State.COMPLETE).and(qs.state.ne(Structure.State.REMOVED)))
                .set(qs.state, Structure.State.STOPPED)
                .execute();
        session.close();
    }

    public static void print(String[] messages) {
        for (String s : messages) {
            print(s);
        }
    }

    public static void print(String message) {
        Bukkit.getConsoleSender().sendMessage(MSG_PREFIX + message);
    }

}
