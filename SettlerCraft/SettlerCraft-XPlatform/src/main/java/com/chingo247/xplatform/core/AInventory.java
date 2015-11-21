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
package com.chingo247.xplatform.core;

/**
 * Abstraction of inventory
 * @author Chingo
 */
public abstract class AInventory {
    
    /**
     * Checks if the inventory has the item
     * @param stack The item
     * @return True if inventory has the item
     */
    public abstract boolean hasItem(AItemStack stack);
    
    /**
     * Removes an item from the inventory
     * @param item The item to remove
     */
    public abstract void removeItem(AItemStack item);
    
    /**
     * Adds adds an item to this inventory
     * @param itemStack The item to add
     */
    public abstract void addItem(AItemStack itemStack);
    
    /**
     * Sets an item at a certain index
     * @param index The item to set
     * @param itemstack The ItemStack
     */
    public abstract void setItem(int index, AItemStack itemstack);          
    
    /**
     * Gets all the items in this inventory
     * @return The items
     */
    public abstract AItemStack[] getItems();
    
    /**
     * Clears the inventory
     */
    public abstract void clear();
    
    
}
