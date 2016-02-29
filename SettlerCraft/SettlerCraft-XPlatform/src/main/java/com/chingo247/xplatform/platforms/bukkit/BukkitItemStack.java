/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.xplatform.platforms.bukkit;

import com.chingo247.xplatform.core.AItemStack;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_8_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

/**
 *
 * @author Chingo
 */
public class BukkitItemStack extends AItemStack {
    
    private final ItemStack stack;

    BukkitItemStack(ItemStack stack) {
        this.stack = stack;
    }
    
    @Override
    public String getName() {
        return stack.getItemMeta().getDisplayName();
    }
    
    public void set() {
       
        
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
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(lore);
        stack.setItemMeta(meta);
    }

    @Override
    public void setAmount(int amount) {
        stack.setAmount(amount);
    }

    public ItemStack getStack() {
        return stack;
    }
    
    @Override
    public AItemStack clone() {
        AItemStack clone = new BukkitItemStack(stack.clone());
        return clone;
    }

    @Override
    public void setMaterial(int material) {
        stack.setTypeId(material);
    }
    
    
    
}
