/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.structureapi.menu.item;

import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public interface TradeItem {
    
    public double getPrice();
    
    public String getName();
    
    public ItemStack getItemStack();
    
    
    
}
