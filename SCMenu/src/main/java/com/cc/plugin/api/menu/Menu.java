/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cc.plugin.api.menu;

import com.google.common.base.Preconditions;
import com.cc.plugin.api.menu.MenuSlot.MenuSlotType;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public abstract class Menu {
    
    public static final int MENUSIZE = 54;
    private final MenuSlot[] menuSlots;
//    protected Map<Integer,MenuSlot> slots;
    protected final String title;
    protected final boolean wontDeplete;
//    protected final Set<Integer> locked;

    /**
     * Constructor.
     * @param title The title of this menu
     * @param wontDeplete Wheter or not the items should deplete int this menu
     */
    public Menu(String title, boolean wontDeplete) {
        this.title = title;
        this.wontDeplete = wontDeplete;
        this.menuSlots = new MenuSlot[MENUSIZE];
    }
    
    public String getTitle() {
        return title;
    }
    
    protected void setSlot(int slot, MenuSlot ms) {
        menuSlots[slot] = ms;
    } 
    
    public MenuSlot getSlot(int slot) {
        
        return menuSlots[slot];
    }

    public MenuSlot[] getMenuSlots() {
        return menuSlots;
    }
    
    public boolean isLocked(int slot) {
        Preconditions.checkArgument(slot >= 0 && slot < menuSlots.length);
        if(menuSlots[slot] == null) {
            return false;
        }
        return menuSlots[slot].getType() == MenuSlot.MenuSlotType.LOCKED;
    }
   
    public void setLocked(Integer... slots) {
        for(int i : slots) {
            menuSlots[i] = new MenuSlot(null, null, MenuSlotType.LOCKED);
        }
        
    }

    public abstract void onEnter(Player player);
    
    

}

