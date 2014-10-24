/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercrafttownapi.tools;

import java.util.ArrayList;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Chingo
 */
public class Tool {
    
    private final String name;

    public Tool(String name) {
        this.name = name;
    }
    
    public ItemStack asItemStack(Material item) {
        ItemStack stack = new ItemStack(item);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(new ArrayList<>(Arrays.asList("Type: " + ChatColor.GOLD + "SC-TOOL")));
        stack.setItemMeta(meta);
        return stack;
    }
    
    public static boolean isStool(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        for(String s : meta.getLore()){
            if(s.contains("Type:") && s.contains("SC-TOOL")) {
                return true;
            }
        }
        return false;
    }
    
    
    
}
