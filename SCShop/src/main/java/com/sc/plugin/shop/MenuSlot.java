/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.plugin.shop;

import com.google.common.base.Preconditions;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
abstract class MenuSlot extends ItemStack {

    private final UUID menuId;

    MenuSlot(UUID menuId, ItemStack stack) {
        super(stack);
        Preconditions.checkArgument(stack != null);
        Preconditions.checkArgument(menuId != null);
        this.menuId = menuId;
    }

    public String getName() {
        return getItemMeta().getDisplayName();
    }
    
    public void setName(String name) {
        this.getItemMeta().setDisplayName(name);
    }
    
    public UUID getMenuId() {
        return menuId;
    }

    
    
}
