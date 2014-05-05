/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.menu.plugin.shop;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;

/**
 * @author Chingo
 */
public class MenuManager {
    
    private final Map<UUID,Menu> menus = Maps.newHashMap();
    private final Map<UUID,ItemShopCategoryMenu> shops = Maps.newHashMap();
    private static MenuManager instance;
    
    private MenuManager() {
    }
    
    public static MenuManager getInstance() {
        if(instance == null) {
            instance = new MenuManager();
        }
        return instance;
    }
    
    public boolean register(Menu menu) {
        if(menus.containsKey(menu.getId())) {
            return false;
        } else {
            if(menu instanceof ItemShopCategoryMenu) {
                shops.put(menu.getId(), (ItemShopCategoryMenu) menu);
            }
            menus.put(menu.getId(), menu);
            return true;
        }
    }
    
    public boolean removeVisitorFromShop(Player player) {
        for(ItemShopCategoryMenu iscm : shops.values()) {
            if(iscm.hasVisitor(player)) {
                iscm.onLeave(player);
                return true;
            }
        }
        return false;
    }
    
    public boolean contains(Menu shop) {
        return menus.containsKey(shop.getId());
    }
    
    public Menu getMenu(UUID shop) {
        return menus.get(shop);
    }
}
