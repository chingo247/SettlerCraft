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
public class MenuButtonSlot extends MenuSlot {

    public MenuButtonSlot(UUID menuId, ItemStack stack, String action) {
        super(menuId, stack);
        getItemMeta().setDisplayName(action);
    }
    
}
