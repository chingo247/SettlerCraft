/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.menu.plugin.shop;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
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
        LOCKED
    }
    
    private final ItemStack icon;
    private final MenuSlotType type;
    private final String[] aliasses;
    private List<String> lore;
    private double price;

    MenuSlot(ItemStack icon, String displayName, MenuSlotType type, String... aliasses) {
        this.icon = icon;
        if(icon != null) {
        ItemMeta meta = icon.getItemMeta();
        lore = new ArrayList<>();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        icon.setItemMeta(meta);
        }
        this.type = type;
        this.aliasses = aliasses;
    }
    
    public void setPrice(double price) {
        if(type == MenuSlotType.ITEM) {
            this.price = price;
            this.setData("Price", price, ChatColor.GOLD);
        } else {
            throw new IllegalArgumentException("Slot needs to be of type item to have a price");
        }
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setData(String key, Object data, ChatColor dataColor) {
        ItemMeta meta = icon.getItemMeta();
        for(int i = 0; i < lore.size(); i++) {
            if(lore.get(i).contains("["+ key + "]")) {
                lore.set(i,  "["+key+"]: " + dataColor + String.valueOf(data));
                meta.setLore(lore);
                icon.setItemMeta(meta);
                return;
            }
        }
        lore.add("["+key+"]: " + dataColor + String.valueOf(data));
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
        if(this.aliasses == null) return false;
        for(String s : this.aliasses) {
            if(s.equalsIgnoreCase(alias)) return true;
        }
        return false;
    }
    
    
    
}
