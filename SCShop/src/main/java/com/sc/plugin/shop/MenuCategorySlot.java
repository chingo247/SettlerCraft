/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.plugin.shop;

import java.util.UUID;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class MenuCategorySlot extends MenuSlot {
    

    public MenuCategorySlot(UUID menuId, ItemStack stack, String title) {
        super(menuId, stack);
        getItemMeta().setDisplayName(title);
    }
    
}
