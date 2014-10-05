/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin;

import com.sc.module.settlercraft.plugin.SettlerCraft;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Chingo
 */
public class ConfigProvider {

    private static ConfigProvider instance;
    private boolean menuEnabled = false;
    private boolean shopEnabled = false;

    private ConfigProvider() {
    }

    public static ConfigProvider getInstance() {
        if (instance == null) {
            instance = new ConfigProvider();
        }
        return instance;
    }

    public void load() throws SettlerCraftException {
        final FileConfiguration config = SettlerCraft.getInstance().getConfig();
        this.menuEnabled = config.getBoolean("menus.planmenu");
        this.shopEnabled = config.getBoolean("menus.planshop");

    }


    public boolean isPlanMenuEnabled() {
        return menuEnabled;
    }

    public boolean isPlanShopEnabled() {
        return shopEnabled;
    }

 
}
