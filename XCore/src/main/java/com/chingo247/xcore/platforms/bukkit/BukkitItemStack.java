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

import com.chingo247.xcore.core.AItemStack;
import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Chingo
 */
public class BukkitItemStack extends AItemStack {
    
    private final ItemStack stack;

    public BukkitItemStack(ItemStack stack) {
        this.stack = stack;
    }
    
    @Override
    public String getName() {
        return stack.getItemMeta().getDisplayName();
    }

    @Override
    public List<String> getLore() {
        return stack.getItemMeta().getLore();
    }
    
    @Override
    public int getAmount() {
        return stack.getAmount();
    }
    
    @Override
    public int getMaterial() {
        return stack.getType().getId();
    }
    
    @Override
    public int getData() {
        Byte b = stack.getData().getData();
        return b.intValue();
    }

    @Override
    public void setName(String name) {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
    }

    @Override
    public void setLore(List<String> lore) {
        stack.getItemMeta().setLore(lore);
    }

    @Override
    public void setAmount(int amount) {
        stack.setAmount(amount);
    }

    @Override
    public AItemStack clone() {
        AItemStack clone = new BukkitItemStack(stack.clone());
        return clone;
    }
    
    
    
}
