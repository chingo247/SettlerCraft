/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.plugin.shop;

import com.google.common.base.Preconditions;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class ShopSlot extends ItemStack {
    public enum ShopSlotType {
        ITEM,
        CATEGORY,
        NEXT,
        PREVIOUS
    }
    
    private final ShopSlotType type;
    private final UUID shopId;
    private double cost;

    ShopSlot(UUID shopId, ItemStack stack, ShopSlotType type) {
        super(stack);
        this.cost = 0;
        this.shopId = shopId;
        this.type = type;
    }
    
    /**
     * Constructor, used to for slots of type ITEM
     * @param shopId The shopID
     * @param stack The item
     * @param price Price each
     */
    ShopSlot(UUID shopId, ItemStack stack, double price) {
        super(stack);
        this.cost = 0;
        this.shopId = shopId;
        this.type = ShopSlotType.ITEM;
    }

    public double getPrice() {
        return cost;
    }

    public void setCost(double cost) {
        Preconditions.checkArgument(type == ShopSlotType.ITEM);
        this.cost = cost;
    }
    
    public String getName() {
        return getItemMeta().getDisplayName();
    }
    
    public void setName(String name) {
        this.getItemMeta().setDisplayName(name);
    }
    
    public UUID getShopId() {
        return shopId;
    }

    public ShopSlotType getSlotType() {
        return type;
    }
    
    
    
}
