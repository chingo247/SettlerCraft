/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.backupapi.bukkit;

import com.chingo247.backupapi.core.BackupAPI;
import com.chingo247.backupapi.core.IBackupMaker;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.backupapi.core.IChunkManager;
import com.chingo247.backupapi.core.BackupMaker;
import com.chingo247.backupapi.core.exception.BackupAPIException;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.platforms.bukkit.BukkitPlugin;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chingo
 */
public class BKBackupManagerPlugin extends JavaPlugin {
    
    private static final String MAIN_PLUGIN = "SettlerCraft-StructureAPI";
    private BackupAPI backupAPI;
    private APlatform platform;

    @Override
    public void onEnable() {
        if(Bukkit.getPluginManager().getPlugin(MAIN_PLUGIN) == null) {
            System.out.println("[SettlerCraft-ChunkSaver]: Unable to find SettlerCraft-StructureAPI!");
            System.out.println("[SettlerCraft-ChunkSaver]: Disabling...");
            this.setEnabled(false);
            return;
        }
        
        
        
        IChunkManager chunkManager = new BKChunkManager();
        this.backupAPI = (BackupAPI) BackupAPI.getInstance();
        this.platform = SettlerCraft.getInstance().getPlatform();
        try {
            IBackupMaker bmgr = new BackupMaker(platform.getServer().getScheduler(new BukkitPlugin(this)), chunkManager, platform);
            setupConfig(bmgr);
            this.backupAPI.registerBackupManager(bmgr);
            this.backupAPI.registerChunkManager(chunkManager);
        } catch (BackupAPIException ex) {
            Logger.getLogger(BKBackupManagerPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setupConfig(IBackupMaker bmgr) throws BackupAPIException {
        File configFile = new File(getDataFolder(), "config.yml");
        final FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        int interval = config.getInt("backup.interval");
        int chunks = config.getInt("backup.chunks");
        long time = config.getLong("backup.time");
        if(interval <= 0) {
            throw new BackupAPIException("[BackupAPI]: Invalid value for backup interval, value has to be greater than 0");
        }
        if(chunks <= 0 && chunks != -1) {
            throw new BackupAPIException("[BackupAPI]: Invalid value for chunks in config value has to be greater than 0 or equal to -1");
        }
        if(time <= 0 && time != -1) {
            throw new BackupAPIException("[BackupAPI]: Invalid value for time , value has to be greater than 0 or equal to -1");
        }
        bmgr.setInterval(interval);
        bmgr.setTime(time);
        bmgr.setChunks(chunks);
        System.out.println(" ");
        System.out.println("Interval: " + interval);
        System.out.println("Chunks: " + chunks);
        System.out.println("Time: " + time);
        System.out.println(" ");
    }
    
    
}
