/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin.shop;

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

    public boolean addCategory(ItemStack icon, String categoryName, int slot) {
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
        }
        return false;
    }
    
    public abstract void onCategoryClicked(MenuCategorySlot slot, Player whoClicked);

}
