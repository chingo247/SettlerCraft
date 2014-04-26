/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.plugin.shop;

import com.google.common.collect.Maps;
import java.util.Map;

/**
 * TODO REMOVE UGLY Singleton
 * TODO HSQLDB SUPPORT for BOTH SHOPS AS VISITORS
 * @author Chingo
 */
public class ShopManager {
    
    private final Map<String,Shop> shops = Maps.newHashMap();
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
        if(shops.containsKey(shop.getTitle())) {
            return false;
        } else {
            shops.put(shop.getTitle(), shop);
            return true;
        }
    }
    
    public boolean contains(String shop) {
        return shops.containsKey(shop);
    }
    
    public Shop getShop(String shop) {
        return shops.get(shop);
    }
}
