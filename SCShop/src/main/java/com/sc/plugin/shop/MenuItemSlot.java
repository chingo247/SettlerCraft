/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.plugin.shop;

import java.util.UUID;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class MenuItemSlot extends MenuSlot {
    
    private double price;

    public MenuItemSlot(UUID menuId, ItemStack stack, double price) {
        super(menuId, stack);
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}
