/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.plugin;

import java.util.Comparator;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class AlphabeticalItemStackComperator implements Comparator<ItemStack> {

    @Override
    public int compare(ItemStack o1, ItemStack o2) {
        return o1.getItemMeta().getDisplayName().compareToIgnoreCase(o2.getItemMeta().getDisplayName());
    }
    
}
