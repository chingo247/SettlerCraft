/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.menu.plugin.shop;

import com.google.common.base.Preconditions;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Chingo
 */
public abstract class MenuSlot extends ItemStack {

    private final UUID menuId;
    private ItemMeta meta;

    MenuSlot(UUID menuId, ItemStack icon, String displayName) {
        Preconditions.checkArgument(menuId != null);
        this.setType(icon.getType());
        this.meta = Bukkit.getItemFactory().getItemMeta(icon.getType());
        
        meta.setDisplayName(displayName);
        this.setItemMeta(meta);
        this.menuId = menuId;
    }

    public String getName() {
        return meta.getDisplayName();
    }
    
    public void setName(String name) {
        meta.setDisplayName(name);
    }

    @Override
    public ItemMeta getItemMeta() {
        return meta;
    }

    public UUID getMenuId() {
        return menuId;
    }
    
}
