/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.worldguard.plugin;

import com.chingo247.settlercraft.core.exception.SettlerCraftException;
import com.chingo247.settlercraft.structureapi.platforms.bukkit.BKConfigProvider;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Chingo
 */
public class ConfigProvider {
    
    private HashMap<Flag, Object> defaultFlags;
    private final File configFile;

    ConfigProvider(File f) throws SettlerCraftException {
        this.configFile = f;
        reload();
    }
    
    public final void reload() throws SettlerCraftException {
        final FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
         this.defaultFlags = getDefaultFlags(config);
    }
    
    public HashMap<Flag, Object> getDefaultRegionFlags() {
        return defaultFlags;
    }
    
    private HashMap<Flag, Object> getDefaultFlags(FileConfiguration config) throws SettlerCraftException {
        HashMap<Flag, Object> df = new HashMap<>();
        if (config.getConfigurationSection("structure.default-flags") != null) {
            Map<String, Object> flags = config.getConfigurationSection("structure.default-flags").getValues(false);
            for (Map.Entry<String, Object> entry : flags.entrySet()) {
                Flag foundFlag = DefaultFlag.fuzzyMatchFlag(entry.getKey());
                if (foundFlag == null) {
                    throw new SettlerCraftException("Error in SettlerCraft config.yml: Flag '" + entry.getKey() + "' doesn't exist!");
                } else {
                    try {
                        df.put(foundFlag, foundFlag.parseInput(WorldGuardPlugin.inst(), Bukkit.getConsoleSender(), String.valueOf(entry.getValue())));
                    } catch (InvalidFlagFormat ex) {
                        Bukkit.getConsoleSender().sendMessage("Error in: " + configFile.getAbsolutePath());
                        Logger.getLogger(BKConfigProvider.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }

        return df;
    }
    
    public static ConfigProvider load(File f) throws SettlerCraftException {
        return new ConfigProvider(f);
    }
    
    
}
