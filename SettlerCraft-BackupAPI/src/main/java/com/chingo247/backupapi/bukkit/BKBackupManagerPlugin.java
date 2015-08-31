/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.backupapi.bukkit;

import com.chingo247.backupapi.core.BackupAPI;
import com.chingo247.backupapi.core.BackupManager;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.structureapi.construction.backup.IChunkManager;
import com.chingo247.structureapi.exception.StructureAPIException;
import com.chingo247.structureapi.structure.StructureAPI;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.platforms.bukkit.BukkitPlugin;
import com.sk89q.worldedit.extension.platform.Platform;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
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
        this.backupAPI.registerBackupManager(new BackupManager(chunkManager, platform.getServer().getScheduler(new BukkitPlugin(this)), platform));
        StructureAPI structureAPI = (StructureAPI) StructureAPI.getInstance();
        try {
            structureAPI.registerBackupAPI(backupAPI);
            structureAPI.registerChunkManager(chunkManager);
        } catch (StructureAPIException ex) {
            Logger.getLogger(BKBackupManagerPlugin.class.getName()).log(Level.SEVERE, null, ex);
            this.setEnabled(false);
        }
        
    }
    
    
    
}
