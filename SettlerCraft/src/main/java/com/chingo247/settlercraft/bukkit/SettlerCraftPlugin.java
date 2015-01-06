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
package com.chingo247.settlercraft.bukkit;

import com.chingo247.menu.CategoryMenu;
import com.chingo247.settlercraft.bukkit.PermissionManager.Perms;
import com.chingo247.settlercraft.bukkit.commands.ConstructionCommandExecutor;
import com.chingo247.settlercraft.bukkit.commands.SettlerCraftCommandExecutor;
import com.chingo247.settlercraft.bukkit.commands.StructureCommandExecutor;
import com.chingo247.settlercraft.bukkit.listener.FenceListener;
import com.chingo247.settlercraft.bukkit.listener.PlanListener;
import com.chingo247.settlercraft.bukkit.listener.PluginListener;
import com.chingo247.settlercraft.structureapi.structure.old.NopeStructure;
import com.chingo247.settlercraft.structureapi.construction.prism.DimensionRollback;
import com.chingo247.settlercraft.structureapi.exception.SettlerCraftException;
import com.chingo247.settlercraft.structureapi.exception.StructureAPIException;
import com.chingo247.settlercraft.structureapi.persistence.HSQLServer;
import com.chingo247.settlercraft.structureapi.persistence.hibernate.HibernateUtil;
import com.chingo247.settlercraft.structureapi.persistence.hibernate.ValidationService;
import com.chingo247.settlercraft.structureapi.structure.plan.menu.PlanMenuManager;
import com.chingo247.settlercraft.structureapi.structure.QStructure;
import com.chingo247.settlercraft.structureapi.structure.regions.CuboidDimension;
import com.chingo247.xcore.platforms.bukkit.BukkitPlatform;
import com.mysema.query.jpa.hibernate.HibernateUpdateClause;
import com.sk89q.worldedit.Vector;
import java.io.File;
import java.io.IOException;
import java.util.Date;
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
        
        DimensionRollback rollback = new DimensionRollback(new CuboidDimension(Vector.ZERO, new Vector(100, 30, 100)), new Date(), 50000);
        rollback.lookup("myWorld", new CuboidDimension(Vector.ZERO, Vector.ONE), new Date());
        
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

        new HibernateUpdateClause(session, qs).where(qs.state.ne(NopeStructure.State.COMPLETE).and(qs.state.ne(NopeStructure.State.REMOVED)))
                .set(qs.state, NopeStructure.State.STOPPED)
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
