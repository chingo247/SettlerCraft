/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.menu.plugin.shop;

import com.sc.api.menu.plugin.shop.MenuSlot.MenuSlotType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Chingo
 */
public abstract class CategoryMenu extends Menu {
    
    private int categories = 0;

    public CategoryMenu(String title, boolean wontDeplete) {
        super(title, wontDeplete);
    }

    public boolean addCategory(int slot, ItemStack icon, String categoryName, String... aliases) {
        if (!isLocked(slot) && !hasCategory(categoryName)) {
            ItemMeta meta = icon.getItemMeta();
            meta.setDisplayName(categoryName);
            icon.setItemMeta(meta);
            put(slot, new MenuSlot(icon, categoryName, MenuSlotType.CATEGORY, aliases));
            categories++;
            return true;
        } 
        return false;
    }
    
    public boolean addActionSlot(int slot, ItemStack icon, String actionName, String... aliases) {
        if (!isLocked(slot)) {
            ItemMeta meta = icon.getItemMeta();
            meta.setDisplayName(actionName);
            icon.setItemMeta(meta);
            put(slot, new MenuSlot(icon, actionName, MenuSlotType.ACTION, aliases));
            return true;
        } 
        return false;
    }
    
    public int getAmountOfCategories() {
        return categories;
    }
    
    public boolean hasCategory(String category) {
        for(MenuSlot s : getSlots().values()) {
            if(s.getName().equalsIgnoreCase(category) ||s.hasAlias(category)){
                return true;
            }
        }
        return false;
    }
    
    public String getCategoryName(String alias) {
        for(MenuSlot s : getSlots().values()) {
            if(s.getType() == MenuSlotType.CATEGORY && ((s.getName().equalsIgnoreCase(alias) || s.hasAlias(alias)))){
                return s.getName();
            }
        }
        return null;
    }
    
    public abstract void onCategoryClicked(String categoryName, Player whoClicked);

}
