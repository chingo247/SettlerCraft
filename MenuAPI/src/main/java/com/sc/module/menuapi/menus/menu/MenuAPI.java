/*
 * Copyright (C) 2014 Chingo
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
    
    private final HashMap<Plugin, Map<UUID, CategoryMenu>> menus = new HashMap<>();
    
    private static MenuAPI instance;
    
    /**
     * Private Constructor.
     */
    private MenuAPI() {}
    
    public static CategoryMenu createMenu(Plugin plugin, String name, int size) {
        if(plugin == null) {
            throw new AssertionError("Null plugin is null");
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
