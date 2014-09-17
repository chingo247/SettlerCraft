/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.menuapi.menus.menu;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Chingo
 */
public class MenuAPI implements Listener {
    
    private HashMap<Plugin, Map<UUID, CategoryMenu>> menus = new HashMap<>();
    
    private static MenuAPI instance;
    
    private MenuAPI() {
        
    }
    
    public static CategoryMenu createMenu(Plugin plugin, String name, int size) {
        if(plugin == null) {
            throw new AssertionError("Null menu");
        }
        
        CategoryMenu menu = new CategoryMenu(name, size, plugin);
        MenuAPI api = getInstance();
        
        if(api.menus.get(plugin) == null) {
            api.menus.put(plugin, new HashMap<UUID, CategoryMenu>());
        }
        
        api.menus.get(plugin).put(menu.getId(), menu);
        Bukkit.getPluginManager().registerEvents(menu, plugin);
        
        return menu;
    }
    
    public CategoryMenu getMenu(Plugin plugin, UUID menuID) {
        if(getInstance().menus.containsKey(plugin)) {
            return getInstance().menus.get(plugin).get(menuID);
        }
        return null;
    }
    
  
    
    public static MenuAPI getInstance() {
        if(instance == null) {
            instance = new MenuAPI();
        }
        return instance;
    }
    
    
    
    
}
