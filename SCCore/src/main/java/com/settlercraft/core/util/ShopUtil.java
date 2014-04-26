/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.core.util;

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
public class ShopUtil {
    
    public static ItemStack createCategory(String name, ItemStack item, List<String> lore) {
        ItemStack stack = new ItemStack(item);
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(lore);
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
        return stack;
    }
    
    public static ItemStack createArrow(String name, String num) {
        ItemStack stack = new ItemStack(Material.COAL);
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(Arrays.asList(num + ""));
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
        return stack;
    }
    
    public static ItemStack createCategory(String name, ItemStack item) {
        return createCategory(name, item, new ArrayList<String>());
    }
    
}
