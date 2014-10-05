/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.settlercraft.plugin;

import com.sc.module.settlercraft.commands.ConstructionCommandExecutor;
import com.sc.module.settlercraft.commands.SettlerCraftCommandExecutor;
import com.sc.module.settlercraft.commands.StructureCommandExecutor;
import com.sc.module.settlercraft.plugin.PermissionManager.Perms;
import com.sc.plugin.ConfigProvider;
import com.sc.plugin.SettlerCraftException;
import com.sc.structureapi.structure.StructureAPIModule;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class SettlerCraft extends JavaPlugin {

    private static final int INFINITE_BLOCKS = -1;
    private static final Logger LOGGER = Logger.getLogger(SettlerCraft.class);
    private static SettlerCraft instance;
    private boolean plansLoaded = false;

    public static final String MSG_PREFIX = ChatColor.GOLD + "[SettlerCraft]: ";


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

        if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") == null) {
            System.out.println("[SettlerCraft]: HolographicDisplays NOT FOUND!!! Disabling...");
            this.setEnabled(false);
            return;
        }

        // Write Changelog & Config if not exist!
        writeResources();
        
        

        try {
            ConfigProvider.getInstance().load();
        } catch (SettlerCraftException ex) {
            java.util.logging.Logger.getLogger(SettlerCraft.class.getName()).log(Level.SEVERE, null, ex);
        }


        // Init StructureAPI
        StructureAPIModule.getInstance().initialize();

        getCommand("sc").setExecutor(new SettlerCraftCommandExecutor(this));
        getCommand("cst").setExecutor(new ConstructionCommandExecutor(this));
        getCommand("stt").setExecutor(new StructureCommandExecutor());


        printPerms();
    }

    private void printPerms() {
        File printedFile = new File(SettlerCraft.getInstance().getDataFolder(), "SettlerCraftPermissions.yml");
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

    public boolean isPlansLoaded() {
        return plansLoaded;
    }



    private void writeResources() {
        File config = new File(getDataFolder(), "config.yml");
        File changelog = new File(getDataFolder(), "changelog.txt");
        
        
        if (!config.exists()) {
            
            InputStream i = this.getClassLoader().getResourceAsStream("settlercraft/config.yml");
            System.out.println(i);
            write(i, config);
        }
        if (!changelog.exists()) {
            InputStream i = this.getClassLoader().getResourceAsStream("settlercraft/changelog.txt");
            System.out.println(i);
            write(i, changelog);
        }
    }

    private void write(InputStream inputStream, File file) {
        OutputStream outputStream = null;

        try {

            // write the inputStream to a FileOutputStream
            outputStream  = new FileOutputStream(file);

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
    


}
