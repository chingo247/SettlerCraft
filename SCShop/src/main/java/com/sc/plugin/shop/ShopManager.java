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
public class ShopManager {
    
    private final Map<UUID,Shop> shops = Maps.newHashMap();
    private static ShopManager instance;
    
    private ShopManager() {
    }
    
    public static ShopManager getInstance() {
        if(instance == null) {
            instance = new ShopManager();
        }
        return instance;
    }
    
    public boolean register(Shop shop) {
        if(shops.containsKey(shop.getId())) {
            return false;
        } else {
            shops.put(shop.getId(), shop);
            return true;
        }
    }
    
    public boolean contains(Shop shop) {
        return shops.containsKey(shop.getId());
    }
    
    public Shop getShop(UUID shop) {
        return shops.get(shop);
    }
}
