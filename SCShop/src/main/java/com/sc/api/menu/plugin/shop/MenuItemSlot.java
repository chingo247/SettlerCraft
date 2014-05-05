/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.menu.plugin.shop;

import java.util.UUID;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public final class MenuItemSlot extends MenuSlot {

    private double price;

    public MenuItemSlot(UUID menuId, ItemStack stack, double price, String itemName) {
        super(menuId, stack, itemName);
        this.price = price;
        
    }

    public double getPrice() {
        return price;
    }
    
    public boolean isFree() {
        return price == 0;
    }

    public boolean setPrice(double price) {
        if (price >= 0) {
            this.price = price;
            return true;
        } else {
            return false;
        }
    }

}
