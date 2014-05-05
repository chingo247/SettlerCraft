/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.menu.plugin.shop;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Chingo
 */
public class MenuSlot {

    public enum MenuSlotType {
        ITEM,
        SKILL,
        CATEGORY,
        ACTION,
    }
    
    private final ItemStack icon;
    private final MenuSlotType type;
    private final String[] aliasses;
    private List<String> lore;

    MenuSlot(ItemStack icon, String displayName, MenuSlotType type, String... aliasses) {
        this.icon = icon;
        ItemMeta meta = icon.getItemMeta();
        meta.setDisplayName(displayName);
        lore = new ArrayList<>();
        icon.setItemMeta(meta);
        this.type = type;
        this.aliasses = aliasses;
    }
    
    
    public void setData(String key, Object data) {
        ItemMeta meta = icon.getItemMeta();
        for(int i = 0; i < lore.size(); i++) {
            if(lore.get(i).startsWith("["+key+"]")) {
                lore.set(i, "["+key+"]" + String.valueOf(data));
                meta.setLore(lore);
                icon.setItemMeta(meta);
                return;
            }
        }
        lore.add("["+key+"]: " + String.valueOf(data));
        meta.setLore(lore);
        icon.setItemMeta(meta);
    }
    
    

    public String getName() {
        return icon.getItemMeta().getDisplayName();
    }

    public MenuSlotType getType() {
        return type;
    }
    
    public ItemStack getItemStack() {
        return icon;
    }
    
    public boolean hasAlias(String alias) {
        for(String s : this.aliasses) {
            if(s.equalsIgnoreCase(alias)) return true;
        }
        return false;
    }
    
}
