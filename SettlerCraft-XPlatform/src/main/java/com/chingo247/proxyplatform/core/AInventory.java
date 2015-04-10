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
package com.chingo247.proxyplatform.core;

/**
 *
 * @author Chingo
 */
public abstract class AInventory {
    
    public boolean hasItem(AItemStack stack) {
        AItemStack[] items = getItems();
        for(AItemStack item : items) {
            if(item != null && item.matches(stack)) {
                return true;
            }
        }
        return false;
    }
    
    public int getAmount(AItemStack stack) {
        AItemStack[] items = getItems();
        int count = 0;
        for(AItemStack item : items) {
            if(item.matches(stack)) {
                count++;
            }
        }
        return count;
    }
    

    
    public void removeItem(AItemStack item) {
        AItemStack[] items = getItems();
        for(int i = 0; i < items.length; i++) {
            if(item.matches(items[i])) {
                setItem(i, null);
            }
        }
    }
    
    public abstract void addItem(AItemStack itemStack);
    
    public abstract void setItem(int index, AItemStack itemstack);          
    
    public abstract AItemStack[] getItems();
    
    public abstract void clear();
    
    
}
