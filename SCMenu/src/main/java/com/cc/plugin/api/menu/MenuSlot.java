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
package com.cc.plugin.api.menu;

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

    public MenuSlot(ItemStack icon, String displayName, MenuSlotType type, String... aliasses) {
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
