/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.structureapi.bukkit;

import com.sc.structureapi.exception.StructureAPIException;
import com.sc.structureapi.structure.StructureAPIModule;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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

    private static ConfigProvider instance;
    private double refundPercentage;
    private int buildingMode = 0;
    private int demolisionMode = 0;
    private boolean useHolograms = false;
    private boolean defaultHolograms = false;
    private HashMap<Flag<?>, Object> defaultFlags;
    private File file;

    private ConfigProvider() {
    }

    public static ConfigProvider getInstance() {
        if (instance == null) {
            instance = new ConfigProvider();
        }
        return instance;
    }

    public boolean hasDefaultHologramEnabled() {
        return defaultHolograms;
    }

    
    
    public void load() throws StructureAPIException {
        file = new File(StructureAPIModule.getInstance().getModuleFolder(), "config.yml");
        final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.refundPercentage = config.getDouble("structure.refund");
        if (refundPercentage < 0) {
            throw new StructureAPIException("refund node in config was negative");
        }
        this.buildingMode = config.getInt("structure.mode.building");
        this.demolisionMode = config.getInt("structure.mode.building");
        if (buildingMode < 0 || buildingMode > 2) {
            throw new StructureAPIException("Invalid building node in config");
        }
        if (demolisionMode < 0 || demolisionMode > 2) {
            throw new StructureAPIException("Invalid demolision node in config");
        }
        this.useHolograms = config.getBoolean("structure.holograms.enabled");
        this.defaultHolograms = config.getBoolean("structure.holograms.defaultHologram");
        this.defaultFlags = getDefaultFlags(config);

    }

    private HashMap<Flag<?>, Object> getDefaultFlags(FileConfiguration config) throws StructureAPIException {
        Map<String, Object> flags = config.getConfigurationSection("structure.default-flags").getValues(false);
        HashMap<Flag<?>, Object> df = new HashMap<>();
        for (Entry<String, Object> entry : flags.entrySet()) {
            Flag<?> foundFlag = DefaultFlag.fuzzyMatchFlag(entry.getKey());
            if (foundFlag == null) {
                throw new StructureAPIException("Error in SettlerCraft config.yml: Flag '" + entry.getKey() + "' doesn't exist!");
            } else {
                try {
                    df.put(foundFlag, foundFlag.parseInput(WorldGuardPlugin.inst(), Bukkit.getConsoleSender(), String.valueOf(entry.getValue())));
                } catch (InvalidFlagFormat ex) {
                    Bukkit.getConsoleSender().sendMessage("Error in: " + file.getAbsolutePath());
                    Logger.getLogger(ConfigProvider.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

        return df;
    }

    public HashMap<Flag<?>, Object> getDefaultFlags() {
        return defaultFlags;
    }

    public int getBuildMode() {
        return buildingMode;
    }

    public int getDemolisionMode() {
        return demolisionMode;
    }

    public boolean useHolograms() {
        if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") == null) {
            return false;
        }
        return useHolograms;
    }


    public double getRefundPercentage() {
        return refundPercentage;
    }

}
