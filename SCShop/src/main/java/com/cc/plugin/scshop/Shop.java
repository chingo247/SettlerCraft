/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cc.plugin.scshop;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class Shop {

    protected final boolean infinite;
    protected String title;
    protected static final int SHOPSIZE = 54;
    protected Inventory inventory;
    private InventoryHolder hold;
    private final Set<Integer> reserved = Sets.newHashSet();
    
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
        this.inventory = Bukkit.createInventory(null, SHOPSIZE, title);
        System.out.println(inventory.getName());
        System.out.println(inventory.getTitle());
    }

    /**
     * Set a slot reserved, items won't be placed here
     *
     * @param slot The index of the slot, needs to be within range of min 0 and max 53
     * @param reserve true to reserve, false to unlock
     */
    public void setSlot(int slot, boolean reserve) {
        if (reserve) {
            reserved.add(slot);
        } else {
            reserved.remove(slot);
        }
    }

    /**
     * Reserves a column, items won't be within this column
     *
     * @param column The column to reserve
     * @param reserve true to reserve, false to unlock
     */
    public void setColumn(int column, final boolean reserve) {
        Preconditions.checkArgument(column >= 0 && column <= 8);
        for (int i = column; i < 54; i += 9) {
            setSlot(i, reserve);
        }
    }

    /**
     * Reserves a row, items won't be placed within this row
     *
     * @param row The row to reserve, row must be within range of min 0 and max 6
     * @param reserve true to reserve, false to unlock
     */
    public void setRow(int row, boolean reserve) {
        Preconditions.checkArgument(row >= 0 && row <= 6);
        int rowSize = 9;
        for (int i = row + rowSize; i < row + rowSize; i++) {
            setSlot(i, reserve);
        }
    }
    
    /**
     * Checks if the slot is reserved
     * @param slot The slot
     * @return true if the slot was reserved
     */
    public boolean isReserved(int slot) {
        return reserved.contains(slot) || slot == 0;
    }
    
    /**
     * Returns a copy of the reserved slots
     * @return the reserved slots
     */
    public Set<Integer> getReserved() {
        return new HashSet<>(reserved);
    }

    public boolean addItem(ItemStack item) {
        if (!isFull()) {
            if (infinite) {
                item.setAmount(1); // Won't deplete!
            }
            for (int i = 0; i < SHOPSIZE; i++) {
                if (!reserved.contains(i)) {
                    if (inventory.getItem(i) == null) {
                        inventory.setItem(i, item);
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public String getTitle() {
        return title;
    }
    
    /**
     * The amount of items that can be placed within this shop
     * @return The amount
     */
    public int storage() {
        return SHOPSIZE - reserved.size();
    }

    public boolean isFull() {
        return (inventory.getContents().length == SHOPSIZE
                || inventory.getContents().length + reserved.size() == SHOPSIZE);
    }

    public boolean isInfinite() {
        return infinite;
    }
    protected void visit(Player player) {
        player.openInventory(inventory);
    }

}
