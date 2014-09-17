/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.menuapi.menus.menu.slots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Chingo
 */
public class ActionSlot extends Slot {
    
    private final String action;
    private final List<String> lore;
    private final Material icon;
    
    public ActionSlot(String action, Material icon) {
        this(action, icon, new String[]{});
    }

    public ActionSlot(String action, Material icon, String... lore) {
        this.action = action;
        this.lore = new ArrayList<>(Arrays.asList(lore));
        this.icon = icon;
    }

    public String getAction() {
        return action;
    }
    
    public void setLore(String... lore) {
        this.lore.clear();
        this.lore.addAll(Arrays.asList(lore));
    }
    
    public ItemStack getIcon() {
        ItemStack stack = new ItemStack(icon);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(action);
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }
    
    
}
