package com.cc.plugin.api.menu;

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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

/**
 * @author Chingo
 */
public class MenuManager {
    
    private final Map<String,Menu> menus = Collections.synchronizedMap(new HashMap<String,Menu>()); 
    private final Map<UUID, Player> visiting = Collections.synchronizedMap(new HashMap<UUID, Player>());
    
    private static MenuManager instance;
    
    private MenuManager() {} 
    public void putVisitor(Player player) {
        visiting.put(player.getUniqueId(), player);
    }
    
    public void removeVisitor(Player player) {
        visiting.remove(player.getUniqueId());
    }
    
    
    
    public static MenuManager getInstance() {
        if(instance == null) {
            instance = new MenuManager();
        }
        return instance;
    }
    
    public boolean register(Menu menu) {
        if(menus.containsKey(menu.getTitle())) {
            return false;
        } else {
            if(menu instanceof ShopCategoryMenu) {
                menus.put(menu.getTitle(), (ShopCategoryMenu) menu);
            }
            menus.put(menu.getTitle(), menu);
            return true;
        }
    }
    
    public boolean hasMenu(String menuTitle) {
        return menus.containsKey(menuTitle);
    }
    
    public Menu getMenu(String menuTitle) {
        return menus.get(menuTitle);
    }
    
    public void clearVisitors() {
        for(Player player : visiting.values()) {
            player.closeInventory();
        }
        visiting.clear();
    }
}
