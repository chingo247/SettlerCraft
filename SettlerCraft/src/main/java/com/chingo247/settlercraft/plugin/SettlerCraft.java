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
package com.chingo247.settlercraft.plugin;

import com.chingo247.settlercraft.bukkit.commands.ConstructionCommandExecutor;
import com.chingo247.settlercraft.bukkit.commands.SettlerCraftCommandExecutor;
import com.chingo247.settlercraft.bukkit.commands.StructureCommandExecutor;
import com.chingo247.settlercraft.bukkit.listener.FenceListener;
import com.chingo247.settlercraft.bukkit.listener.PlanListener;
import com.chingo247.settlercraft.bukkit.listener.PluginListener;
import com.chingo247.settlercraft.exception.SettlerCraftException;
import com.chingo247.settlercraft.exception.StructureAPIException;
import com.chingo247.settlercraft.persistence.HSQLServer;
import com.chingo247.settlercraft.persistence.HibernateUtil;
import com.chingo247.settlercraft.persistence.RestoreService;
import com.chingo247.settlercraft.plugin.PermissionManager.Perms;
import com.chingo247.settlercraft.structure.entities.structure.QStructure;
import com.chingo247.settlercraft.structure.entities.structure.Structure;
import com.chingo247.settlercraft.structure.plan.PlanMenuManager;
import com.chingo247.settlercraft.structure.plan.SettlerCraftManager;
import com.chingo247.settlercraft.structure.plan.SettlerCraftPlanManager;
import com.chingo247.settlercraft.structure.plan.data.holograms.StructureHologramManager;
import com.chingo247.settlercraft.structure.plan.data.overview.StructureOverviewManager;
import com.chingo247.settlercraft.structure.plan.document.PlanDocumentManager;
import com.mysema.query.jpa.hibernate.HibernateUpdateClause;
import com.sc.module.menuapi.menus.menu.CategoryMenu;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
public class SettlerCraft extends JavaPlugin {

    private static final Logger LOGGER = Logger.getLogger(SettlerCraft.class);
    private static SettlerCraft instance;
    private final ThreadPoolExecutor GLOBAL_THREADPOOL = new ThreadPoolExecutor(0, Runtime.getRuntime().availableProcessors(), 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
   

    public static final String MSG_PREFIX = ChatColor.YELLOW + "[SettlerCraft]: " + ChatColor.RESET;

    @Override
    public void onEnable() {
        instance = this;
        
        
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


        
        // Init HSQL Server
        HSQLServer hsqls = HSQLServer.getInstance();
        if (!hsqls.isRunning()) {
            Bukkit.getConsoleSender().sendMessage(MSG_PREFIX + "Starting HSQL Server");
            hsqls.start();
            new RestoreService().restore();
        }

        
        resetStates();
        
        // Load plan menu from XML
        try {
            PlanMenuManager.getInstance().init();
        } catch (DocumentException | StructureAPIException ex) {
            java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
        }


        // Init SettlerCraftPlanManager
        SettlerCraftPlanManager.getInstance().init();
//        SettlerCraftPlanManager.getInstance().generate();
        print("Loading plans");
        PlanDocumentManager.getInstance().load();
        print("Initializing...");
        SettlerCraftManager.getInstance().initialize();
        print("Done!");
        


        try {
            ConfigProvider.getInstance().load();
        } catch (SettlerCraftException ex) {
            java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
        }

        Bukkit.getPluginManager().registerEvents(new PluginListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlanListener(), this);
        Bukkit.getPluginManager().registerEvents(new FenceListener(), this);

        boolean useHolograms = getConfig().getBoolean("structure.holograms.enabled");
        if (useHolograms && Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null) {
            StructureOverviewManager overviewManager = StructureOverviewManager.getInstance();
            StructureHologramManager hologramManager = StructureHologramManager.getInstance();

            Bukkit.getPluginManager().registerEvents(overviewManager, instance);
            Bukkit.getPluginManager().registerEvents(hologramManager, instance);

            overviewManager.init();
            hologramManager.init();
        }

        getCommand("sc").setExecutor(new SettlerCraftCommandExecutor(this));
        getCommand("cst").setExecutor(new ConstructionCommandExecutor(this));
        getCommand("stt").setExecutor(new StructureCommandExecutor());

        printPerms();
    }
    
    public ExecutorService getExecutorService() {
        return GLOBAL_THREADPOOL;
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
        File printedFile = new File(SettlerCraft.getInstance().getDataFolder(), "permissions.yml");
        if (!printedFile.exists()) {
            try {
                printedFile.createNewFile();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
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
            java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static SettlerCraft getInstance() {
        return instance;
    }



    private void write(InputStream inputStream, File file) {
        OutputStream outputStream = null;

        try {

            // write the inputStream to a FileOutputStream
            outputStream = new FileOutputStream(file);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

        } catch (IOException e) {
            Logger.getLogger(SettlerCraft.class).error(e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Logger.getLogger(SettlerCraft.class).error(e.getMessage());
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    Logger.getLogger(SettlerCraft.class).error(e.getMessage());
                }

            }
        }
    }

    public CategoryMenu getPlanMenu() {
        return PlanMenuManager.getInstance().getMenu();
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
