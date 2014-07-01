/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin;

import com.sc.commands.ConstructionCommandExecutor;
import com.sc.commands.SettlerCraftCommandExecutor;
import com.sc.commands.StructureCommandExecutor;
import com.sc.construction.feedback.SelectionManager;
import com.sc.construction.plan.StructurePlanManager;
import com.sc.construction.structure.StructureConstructionManager;
import com.sc.listener.PlayerListener;
import com.sc.listener.PluginListener;
import com.sc.listener.ShopListener;
import com.sc.listener.StructureListener;
import com.sc.menu.MenuManager;
import com.sc.persistence.HSQLServer;
import com.sc.persistence.RestoreService;
import com.sc.plugin.PermissionManager.Perms;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraft extends JavaPlugin {

    private static final int INFINITE_BLOCKS = -1;
    private static final Logger LOGGER = Logger.getLogger(SettlerCraft.class);
    private Plugin plugin;
    private static SettlerCraft instance;
    private boolean plansLoaded = false;

    @Override
    public void onEnable() {

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

        if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") == null) {
            System.out.println("[SettlerCraft]: HolographicDisplays NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }

        if (!HSQLServer.getInstance().isRunning()) {
            HSQLServer.getInstance().start();
            System.out.println("[SettlerCraft]: Checking for invalid structures");
            new RestoreService().restore(); // only execute on server start, not on reload!
        }

        try {
            ConfigProvider.getInstance().load();
        } catch (SettlerCraftException ex) {
            java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
        }

        loadStructures(FileUtils.getFile(getDataFolder(), "Structures"));

        Bukkit.broadcastMessage(ChatColor.GOLD + "[SettlerCraft]: " + ChatColor.RESET + "Structure plans loaded");
        StructureConstructionManager.getInstance().init();

        Bukkit.getPluginManager().registerEvents(new StructureListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new ShopListener(), this);
        Bukkit.getPluginManager().registerEvents(new PluginListener(), this);

        getCommand("sc").setExecutor(new SettlerCraftCommandExecutor(this));
        getCommand("cst").setExecutor(new ConstructionCommandExecutor(this));
        getCommand("stt").setExecutor(new StructureCommandExecutor());

        printPerms();
    }

    private void printPerms() {
        File printedFile = new File(SettlerCraft.getSettlerCraft().getDataFolder(), "SettlerCraftPermissions.yml");
        if (!printedFile.exists()) {
            try {
                printedFile.createNewFile();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(printedFile);

        for (Perms perm : Perms.values()) {
            Permission p = perm.PERM;
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

    public static SettlerCraft getSettlerCraft() {
        return (SettlerCraft) Bukkit.getPluginManager().getPlugin("SettlerCraft");
    }

    /**
     * Stops all processes
     */
    public void stop() {
        StructureConstructionManager.getInstance().shutdown();
        MenuManager.getInstance().clearVisitors();
        SelectionManager.getInstance().clearAll();
    }

    public boolean isPlansLoaded() {
        return plansLoaded;
    }

    /**
     * Loads structures from a directory
     *
     * @param structureDirectory The directory to search
     */
    private static void loadStructures(File structureDirectory) {
        File structureFolder = new File(structureDirectory.getAbsolutePath());
        if (!structureFolder.exists()) {
            structureFolder.mkdirs();
        }
        StructurePlanManager.getInstance().load(structureFolder);
    }

}
