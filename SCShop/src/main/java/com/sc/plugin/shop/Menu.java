/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin.shop;

import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author Chingo
 */
public abstract class Menu {

    private final UUID id;
    private final String title;
    public static final int MENUSIZE = 54;
    private final boolean wontDeplete;
    protected final Set<Integer> reserved = Sets.newHashSet();

    /**
     * Constructor.
     *
     * @param title The title of this menu, must be unique
     */
    public Menu(String title) {
        this(title, false);
    }

    /**
     * Constructor.
     *
     * @param title The title of this menu, must be unique
     * @param infinite If wontDeplete items in this shop will never deplete
     */
    public Menu(String title, boolean infinite) {
        this(UUID.randomUUID(), title, infinite);
    }

    /**
     * Constructor.
     *
     * @param id The id of the shop
     * @param title The title of this shop, must be unique
     * @param infinite If wontDeplete items in this shop will never deplete
     */
    public Menu(UUID id, String title, boolean infinite) {
        this.wontDeplete = infinite;
        this.title = title;
        this.id = id;
    }

    public UUID getId() {
        return id;
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
        return new HashSet<>(reserved);
    }


    /**
     * Determines that no items can be placed in this shop
     *
     * @return true if this shop is full
     */
    public abstract boolean isFull();

    /**
     * Determines if this is an wontDeplete store. Unlike regular stores the items in wontDeplete stores
 won't deplete. The regular pick action will be cancelled for this stores inventory
     *
     * @return True if this store's items won't deplete
     */
    public boolean getWontDeplete() {
        return wontDeplete;
    }

    /**
     * Checks wheter this slot is a reserved slot
     *
     * @param slot The slot
     * @return True if this slot is marked as reserved
     */
    public boolean isReserved(int slot) {
        return reserved.contains(slot);
    }

    /**
     * The template inventory of this store
     *
     * @param player The player to set the inventory for
     */
    public abstract void setTemplateInventory(Player player);

    public abstract Inventory getTemplate();

    public abstract void onEnter(Player player);

    public abstract void onLeave(Player player);

}
