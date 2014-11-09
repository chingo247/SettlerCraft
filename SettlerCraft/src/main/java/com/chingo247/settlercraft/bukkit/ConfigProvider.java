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

import com.chingo247.settlercraft.main.IConfigProvider;
import com.chingo247.settlercraft.main.exception.SettlerCraftException;
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
public class ConfigProvider implements IConfigProvider {

    private boolean menuEnabled = false;
    private boolean shopEnabled = false;
    private double refundPercentage;
    private int buildingMode = 0;
    private int demolisionMode = 0;
    private boolean useHolograms = false;
    private boolean defaultHolograms = false;
    private HashMap<Flag, Object> defaultFlags;
    private int port;
    
    private final File file = new File(SettlerCraftPlugin.getInstance().getDataFolder(), "config.yml");
    

    public void load() throws SettlerCraftException {
        final FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        this.menuEnabled = config.getBoolean("menus.planmenu");
        this.shopEnabled = config.getBoolean("menus.planshop");
       
        this.refundPercentage = config.getDouble("structure.refund");
        if (refundPercentage < 0) {
            throw new SettlerCraftException("refund node in config was negative");
        }
        this.buildingMode = config.getInt("structure.mode.building");
        this.demolisionMode = config.getInt("structure.mode.building");
        if (buildingMode < 0 || buildingMode > 2) {
            throw new SettlerCraftException("Invalid building node in config");
        }
        if (demolisionMode < 0 || demolisionMode > 2) {
            throw new SettlerCraftException("Invalid demolision node in config");
        }
        this.useHolograms = config.getBoolean("structure.holograms.enabled");
        this.defaultHolograms = config.getBoolean("structure.holograms.default-hologram");
        this.defaultFlags = getDefaultFlags(config);
    }

    public boolean isDefaultHolograms() {
        return defaultHolograms;
    }
    
    private HashMap<Flag, Object> getDefaultFlags(FileConfiguration config) throws SettlerCraftException {
        Map<String, Object> flags = config.getConfigurationSection("structure.default-flags").getValues(false);
        HashMap<Flag, Object> df = new HashMap<>();
        for (Map.Entry<String, Object> entry : flags.entrySet()) {
            Flag foundFlag = DefaultFlag.fuzzyMatchFlag(entry.getKey());
            if (foundFlag == null) {
                throw new SettlerCraftException("Error in SettlerCraft config.yml: Flag '" + entry.getKey() + "' doesn't exist!");
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


    public boolean isPlanMenuEnabled() {
        return menuEnabled;
    }

    public boolean isPlanShopEnabled() {
        return shopEnabled;
    }
    
    public HashMap<Flag, Object> getDefaultRegionFlags() {
        return defaultFlags;
    }

    public int getBuildMode() {
        return buildingMode;
    }

    public int getDemolisionMode() {
        return demolisionMode;
    }

    public boolean useHolograms() {
        return useHolograms;
    }


    public double getRefundPercentage() {
        return refundPercentage;
    }

    public boolean useDefaultHolograms() {
        return defaultHolograms;
    }

    
 
}
