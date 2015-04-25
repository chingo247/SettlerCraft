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
package com.chingo247.xplatform.core;

/**
 *
 * @author Chingo
 */
public abstract class AInventory {
    
    public abstract boolean hasItem(AItemStack stack);
    
    public abstract void removeItem(AItemStack item);
    
    
    
    public abstract void addItem(AItemStack itemStack);
    
    public abstract void setItem(int index, AItemStack itemstack);          
    
    public abstract AItemStack[] getItems();
    
    public abstract void clear();
    
    
}
