/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.menu.plugin.shop;

import com.google.common.collect.Maps;
import java.util.Map;

/**
 * @author Chingo
 */
public class MenuManager {
    
    private final Map<String,Menu> menus = Maps.newHashMap(); //NOTE TO MYSELF: NO PLAYER SHOPS WILL BE EVER PLACED HERE
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
        if(menus.containsKey(menu.getTitle())) {
            return false;
        } else {
            if(menu instanceof ItemShopCategoryMenu) {
                menus.put(menu.getTitle(), (ItemShopCategoryMenu) menu);
            }
            menus.put(menu.getTitle(), menu);
            return true;
        }
    }
    
    public boolean contains(String menuTitle) {
        return menus.containsKey(menuTitle);
    }
    
    public Menu getMenu(String menuTitle) {
        return menus.get(menuTitle);
    }
}
