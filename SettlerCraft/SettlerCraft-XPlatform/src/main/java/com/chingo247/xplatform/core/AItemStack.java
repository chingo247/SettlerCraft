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

import java.util.List;

/**
 * Abstraction of ItemStack
 * @author Chingo
 */
public abstract class AItemStack {

    /**
     * Gets the name of the item
     * @return The name
     */
    public abstract String getName();
    
    /**
     * Sets the name of the item
     * @param name The name
     */
    public abstract void setName(String name);
    
    /**
     * Sets the lore of this item
     * @param lore The lore
     */
    public abstract void setLore(List<String> lore);

    /**
     * Gets the lore of this Item
     * @return The lore
     */
    public abstract List<String> getLore();

    /*
    * Gets the amount of items on this stack
    * @return the Amount
    */
    public abstract int getAmount();
    
    /**
     * Sets the amount of items on this stack
     * @param amount The amount
     */
    public abstract void setAmount(int amount);
    
    /**
     * Sets the material of this item
     * @param material The material
     */
    public abstract void setMaterial(int material);

    /**
     * Gets the material of this item
     * @return The material
     */
    public abstract int getMaterial();

    /**
     * Gets the byte data of this item
     * @return The data
     */
    public abstract int getData();
    
    /**
     * Checks if this item matches another
     * @param other The other item
     * @return True if item matches material, data and lore
     */
    public boolean matches(AItemStack other) {
        return getMaterial() == other.getMaterial() 
                    && getData() == other.getData()
                    && other.getLore().equals(getLore());
    }

    /**
     * Creates a clone of this item
     * @return The clone
     */
    public abstract AItemStack clone();
}
