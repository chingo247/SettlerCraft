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
public final class MenuCategorySlot extends MenuSlot {

    private String name;

    MenuCategorySlot(UUID menuId, ItemStack stack, String title) {
        super(menuId, stack);

    }



    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MenuCategorySlot) {
            MenuCategorySlot slot = (MenuCategorySlot) obj;
            return getItemMeta().getDisplayName().equals(slot.getItemMeta().getDisplayName());
        } else {
            return false;
        }
    }
}
