/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.menu.plugin.shop;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public abstract class Menu {
    
    public static final int MENUSIZE = 54;
    protected Map<Integer,MenuSlot> slots;
    protected final String title;
    protected final boolean wontDeplete;
    protected final Set<Integer> locked;



    public Menu(String title, boolean wontDeplete) {
        Preconditions.checkArgument(!title.contains(":"));
        this.title = title;
        this.wontDeplete = wontDeplete;
        this.locked = new HashSet<>();
        this.slots = Maps.newHashMap();
    }
    
    public Map<Integer, MenuSlot> getSlots() {
        return new HashMap<>(slots);
    }

    public String getTitle() {
        return title;
    }
    
    protected void put(int slot, MenuSlot ms) {
        slots.put(slot, ms);
    } 
    
    public MenuSlot getSlot(int slot) {
        return slots.get(slot);
    }
    
//    /**
//     * Puts an item in this menu
//     * @param slot The item slot
//     * @param item
//     * @param itemName 
//     */
//    public void putItem(int slot, ItemStack item, String itemName) {
//        putItem(slot, item, itemName, 0.0d);
//    }
//    
//    public void putItem(int slot, ItemStack item, String itemName, double price) {
//        ItemMeta meta = item.getItemMeta();
//        meta.setDisplayName(itemName);
//        item.setItemMeta(meta);
//        slots.put(slot, new MenuItemSlot(id, item, slot));
//    }

    public boolean isLocked(int slot) {
        Preconditions.checkArgument(slot >= 0 && slot < 54);
        return locked.contains(slot);
    }
    
    public void setLocked(Integer... slots) {
        locked.addAll(Arrays.asList(slots));
    }
    
    public boolean setLocked(int slot) {
        return locked.add(slot);
    }
    
    public abstract void onEnter(Player player);
    
    

}

