/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cc.plugin.scitems;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class SettlerCraftItem {
    
    private int data;
    private int type;
    private String name;
    private String description;
    private double value;
    private HashMap<String, Object> requirements;
    private HashMap<String, Integer> modifiers;
    
    public SettlerCraftItem(ItemStack stack) {
        this(stack.getItemMeta().getDisplayName(), stack);
    }
    
    public SettlerCraftItem(String name, ItemStack stack) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(stack);
    }
    
}
