/*
 * Copyright (C) 2014 Chingo247
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


package com.sc.module.menuapi.menus.menu.item;

import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public interface TradeItem extends Cloneable{
    
    public double getPrice();
    
    public void setPrice(double price);
    
    public String getName();
    
    public ItemStack getItemStack();
    
    public abstract TradeItem clone();
    
}
