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

import com.chingo247.xplatform.core.AInventory;
import com.chingo247.xplatform.core.AItemStack;
import com.google.common.base.Preconditions;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

/**
 *
 * @author Chingo
 */
public class BukkitInventory extends AInventory {

    private final Inventory inventory;

    public BukkitInventory(Inventory inventory) {
        Preconditions.checkNotNull(inventory, "Inventory was null!");
        this.inventory = inventory;
    }

    @Override
    public AItemStack[] getItems() {
        ItemStack[] items = inventory.getContents();
        AItemStack[] copy = new AItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
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
        if (itemstack != null) {
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

    @Override
    public void addItem(AItemStack itemStack) {
        inventory.addItem(((BukkitItemStack) itemStack).getStack());
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public boolean hasItem(AItemStack stack) {
        BukkitItemStack bkis = (BukkitItemStack) stack;
        return inventory.contains(bkis.getStack());
    }

    @Override
    public void removeItem(AItemStack item) {
//        BukkitItemStack bkis = (BukkitItemStack) item;
        
        for(int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if(stack != null && stack.getType().getId() == item.getMaterial() 
                    && stack.getItemMeta() != null 
                    && stack.getItemMeta().getDisplayName().equals(item.getName())
                    && stack.getItemMeta().getLore().equals(item.getLore())
                    ) {
                int amountToRemove = item.getAmount();
                stack.setAmount(Math.max(0, stack.getAmount() - amountToRemove));
                inventory.setItem(i, stack);
            }
        }
        
//        inventory.remove(bkis.getStack().clone());
    }
    
}
