/*
 * Copyright (C) 2014 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cc.plugin.api.menu;

import com.cc.plugin.api.menu.MenuSlot.MenuSlotType;
import com.google.common.base.Preconditions;
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

