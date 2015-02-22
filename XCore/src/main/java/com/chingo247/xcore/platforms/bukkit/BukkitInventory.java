/*
 * Copyright (C) 2015 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chingo247.xcore.platforms.bukkit;

import com.chingo247.xcore.core.AInventory;
import com.chingo247.xcore.core.AItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

/**
 *
 * @author Chingo
 */
public class BukkitInventory extends AInventory{
    
    private final Inventory inventory;

    public BukkitInventory(Inventory inventory) {
        this.inventory = inventory;
    }
    
    @Override
    public AItemStack[] getItems() {
        ItemStack[] items = inventory.getContents();
        AItemStack[] copy = new AItemStack[items.length];
        for(int i = 0; i < items.length; i++) {
            copy[i] = new BukkitItemStack(items[i]);
        }
        return copy;
    }

    @Override
    public void setItem(int index, AItemStack itemstack) {
        ItemStack stack = toBukkitItem(itemstack);
        inventory.setItem(index, stack);
    }

    private ItemStack toBukkitItem(AItemStack itemstack) {
        ItemStack stack = null;
        if(itemstack != null) {
            stack = new ItemStack(itemstack.getMaterial());
            stack.setData(new MaterialData(itemstack.getMaterial(), new Integer(itemstack.getData()).byteValue()));
            stack.setAmount(itemstack.getAmount());
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(itemstack.getName());
            meta.setLore(itemstack.getLore());
            stack.setItemMeta(meta);
        }
        return stack;
    }

    

    
    
}
