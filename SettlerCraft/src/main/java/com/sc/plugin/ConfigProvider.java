/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Chingo
 */
public class ConfigProvider {

    private static ConfigProvider instance;
    private double refundPercentage;
    private boolean menuEnabled = false;
    private boolean shopEnabled = false;
    private int buildingMode = 0;
    private int demolisionMode = 0;
    private boolean useHolograms = false;
    private boolean overrideModes = false;
    private HashMap<Flag<?>, Object> defaultFlags;

    private ConfigProvider() {}

    public static ConfigProvider getInstance() {
        if (instance == null) {
            instance = new ConfigProvider();
        }
        return instance;
    }

    public void load() throws SettlerCraftException {
        final FileConfiguration config = SettlerCraft.getSettlerCraft().getConfig();
        this.menuEnabled = config.getBoolean("menus.planmenu");
            this.shopEnabled = config.getBoolean("menus.planshop");
            this.refundPercentage = config.getDouble("structure.refund");
            if (refundPercentage < 0) {
                throw new SettlerCraftException("refund node in config was negative");
            }
            this.buildingMode = config.getInt("structure.mode.building");
            this.demolisionMode = config.getInt("structure.mode.building");
            if(buildingMode < 0 || buildingMode > 2) {
                throw new SettlerCraftException("Invalid building node in config");
            }
            if(demolisionMode < 0 || demolisionMode > 2) {
                throw new SettlerCraftException("Invalid demolision node in config");
            }
            this.useHolograms = config.getBoolean("use-holograms");
            this.defaultFlags = getDefaultFlags(config);
            
    }
    
    private HashMap<Flag<?>, Object> getDefaultFlags(FileConfiguration config) throws SettlerCraftException {
        Map<String, Object> flags = config.getConfigurationSection("structure.default-flags").getValues(false);
        HashMap<Flag<?>, Object> df = new HashMap<>();
        for(Entry<String,Object> entry : flags.entrySet()) {
            Flag<?> foundFlag = DefaultFlag.fuzzyMatchFlag(entry.getKey());
            if(foundFlag == null) {
                throw new SettlerCraftException("Flag '"+entry.getKey()+"' doesn't exist!");
            } else {
                try {
                    df.put(foundFlag, foundFlag.parseInput(WorldGuardPlugin.inst(), null, String.valueOf(entry.getValue())));
                } catch (InvalidFlagFormat ex) {
                    Logger.getLogger(ConfigProvider.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }
        
        return df;
    }
    
         /**
     * WorldGuard Utility method to set a flag.
     * 
     * @param region the region
     * @param flag the flag
     * @param sender the sender
     * @param value the value
     * @throws InvalidFlagFormat thrown if the value is invalid
     */
    private static <V> void setFlag(ProtectedRegion region,
            Flag<V> flag, CommandSender sender, String value)
                    throws InvalidFlagFormat {
        region.setFlag(flag, flag.parseInput(WorldGuardPlugin.inst(), sender, value));
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

    public boolean isOverridingModes() {
        return overrideModes;
    }

    public boolean useHolograms() {
        if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") == null) {
            return false;
        }
        return useHolograms;
    }
    
     public boolean isPlanMenuEnabled() {
        return menuEnabled;
    }

    public boolean isPlanShopEnabled() {
        return shopEnabled;
    }

    public double getRefundPercentage() {
        return refundPercentage;
    }

}
