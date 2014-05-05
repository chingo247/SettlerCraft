/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.menu.plugin.shop;

import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Chingo
 */
public abstract class CategoryMenu extends Menu {

    /**
     * Constructor
     *
     * @param title The title of this menu
     * @param wontDeplete Wheter or not the items in this menu will deplete when clicked on it
     */
    public CategoryMenu(String title, boolean wontDeplete) {
        this(UUID.randomUUID(), title, wontDeplete);
    }

    public CategoryMenu(UUID id, String title, boolean wontDeplete) {
        super(id, title, wontDeplete);
    }

    public boolean addCategory(int slot, ItemStack icon, String categoryName, String... aliases) {
        if (!isLocked(slot) && !hasCategory(categoryName)) {
            ItemMeta meta = icon.getItemMeta();
            meta.setDisplayName(categoryName);
            icon.setItemMeta(meta);
            put(slot, new MenuCategorySlot(id, icon, categoryName));
            return true;
        } 
        return false;
    }
    
    public boolean hasCategory(String category) {
        for(MenuSlot s : getSlots().values()) {
            if(s.getName().equalsIgnoreCase(category) && (s instanceof MenuCategorySlot)) {
                return true;
            } 
            if(s instanceof MenuCategorySlot && ((MenuCategorySlot)s).hasAlias(category)){
                return true;
            }
        }
        return false;
    }
    
    public String getCategoryName(String alias) {
        for(MenuSlot s : getSlots().values()) {
            if(s instanceof MenuCategorySlot && ((s.getName().equalsIgnoreCase(alias) || ((MenuCategorySlot)s).hasAlias(alias)) )){
                return s.getName();
            }
        }
        return null;
    }
    
    public abstract void onCategoryClicked(MenuCategorySlot slot, Player whoClicked);

}
