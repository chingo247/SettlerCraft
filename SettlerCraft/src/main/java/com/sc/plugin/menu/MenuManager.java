/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.plugin.menu;

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
