/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.plugin.menu;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.entity.Player;

/**
 * @author Chingo
 */
public class MenuManager {
    
    private final Map<String,Menu> menus = Maps.newHashMap(); 
    private final ConcurrentMap<UUID, Player> visiting;
    private static MenuManager instance;
    
    private MenuManager() {
        this.visiting = new ConcurrentHashMap<>();
    }
    
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
