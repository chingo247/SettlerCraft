package com.chingo247.settlercraft.structureapi.platforms.bukkit;

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
import com.chingo247.settlercraft.core.exception.SettlerCraftException;
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
public class BKConfigProvider implements IConfigProvider {

    private boolean menuEnabled = false;
    private boolean shopEnabled = false;
    private double refundPercentage;
    private int buildingMode = 0;
    private int demolisionMode = 0;
    private boolean useHolograms = false;
    private boolean defaultHolograms = false;
    private HashMap<Flag, Object> defaultFlags;
    private int port;

    private final File file = new File(BKStructureAPIPlugin.getInstance().getDataFolder(), "config.yml");

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
                        Bukkit.getConsoleSender().sendMessage("Error in: " + file.getAbsolutePath());
                        Logger.getLogger(BKConfigProvider.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        }

        return df;
    }

    @Override
    public boolean isPlanMenuEnabled() {
        return menuEnabled;
    }

    @Override
    public boolean isPlanShopEnabled() {
        return shopEnabled;
    }

    public HashMap<Flag, Object> getDefaultRegionFlags() {
        return defaultFlags;
    }

    @Override
    public int getBuildMode() {
        return buildingMode;
    }

    @Override
    public int getDemolisionMode() {
        return demolisionMode;
    }

    public boolean useHolograms() {
        return useHolograms;
    }

    @Override
    public double getRefundPercentage() {
        return refundPercentage;
    }

    public boolean useDefaultHolograms() {
        return defaultHolograms;
    }

}
