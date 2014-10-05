/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
