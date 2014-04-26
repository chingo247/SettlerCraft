/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin.shop;

import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public abstract class Shop {

    private final String title;
    public static final int SHOPSIZE = 54;
    private final boolean infinite;
    protected final HashMap<Integer, String> reserved = Maps.newHashMap();

    /**
     * Constructor.
     *
     * @param title The title of this shop, must be unique
     */
    public Shop(String title) {
        this(title, false);
    }

    /**
     * Constructor.
     *
     * @param title The title of this shop, must be unique
     * @param infinite If infinite items in this shop will never deplete
     */
    public Shop(String title, boolean infinite) {
        this.infinite = infinite;
        this.title = title;
    }

    /**
     * Gets the title of this shop
     *
     * @return The title of this shop
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set a slot reserved, items won't be placed here
     *
     * @param slot The index of the slot, needs to be within range of min 0 and max 53
     * @param reserve true to reserve, false to unlock
     */
    public abstract void setSlot(int slot, boolean reserve);

    /**
     * Reserves a column, items won't be within this column
     *
     * @param column The column to reserve
     * @param reserve true to reserve, false to unlock
     */
    public abstract void setColumn(int column, final boolean reserve);

    /**
     * Reserves a row, items won't be placed within this row
     *
     * @param row The row to reserve, row must be within range of min 0 and max 6
     * @param reserve true to reserve, false to unlock
     */
    public abstract void setRow(int row, boolean reserve);

    /**
     * Returns a copy of the reserved slots
     *
     * @return the reserved slots
     */
    public Set<Integer> getReserved() {
        return new HashSet<>(reserved.keySet());
    }

    /**
     * Adds the item to the shop.
     * @param item The item to add
     * @return true if item was succesfully added
     */
    public abstract boolean addItem(ItemStack item);

    /**
     * Determines that no items can be placed in this shop
     * @return true if this shop is full
     */
    public abstract boolean isFull();

    /**
     * Determines if this is an infinite store. Unlike regular stores the items in infinite stores
     * won't deplete. The regular pick action will be cancelled for this stores inventory
     * @return True if this store's items won't deplete
     */
    public boolean isInfinite() {
        return infinite;
    }

    /**
     * Checks wheter this slot is a reserved slot
     * @param slot The slot
     * @return True if this slot is marked as reserved
     */
    public boolean isReserved(int slot) {
        return reserved.containsKey(slot);
    }

    /**
     * The template inventory of this store
     * @param inventory Sets the given inventory back to template inventory
     */
    public abstract void setTemplateInventory(Inventory inventory);

    public abstract Inventory getTemplateInventory();
    
    public abstract void visit(Player player);
    
    public abstract void leave(Player player);

}
