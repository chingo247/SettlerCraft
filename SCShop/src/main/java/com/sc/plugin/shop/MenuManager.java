/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.plugin.shop;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;

/**
 * @author Chingo
 */
public class MenuManager {
    
    private final Map<UUID,Menu> shops = Maps.newHashMap();
    private static MenuManager instance;
    
    private MenuManager() {
    }
    
    public static MenuManager getInstance() {
        if(instance == null) {
            instance = new MenuManager();
        }
        return instance;
    }
    
    public boolean register(Menu shop) {
        if(shops.containsKey(shop.getId())) {
            return false;
        } else {
            shops.put(shop.getId(), shop);
            return true;
        }
    }
    
    public boolean contains(Menu shop) {
        return shops.containsKey(shop.getId());
    }
    
    public Menu getMenu(UUID shop) {
        return shops.get(shop);
    }
}
