/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.menuapi.menu;

import static com.chingo247.menuapi.menu.ACategoryMenu.MENU_SIZE;
import com.chingo247.menuapi.menu.slots.ItemSlot;
import com.chingo247.menuapi.menu.slots.MenuSlot;
import com.chingo247.menuapi.menu.slots.SlotFactory;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Chingo
 */
public class MenuView {
    
    private Map<Integer, MenuSlot> slots;
    private final SlotFactory slotFactory;
    
    private MenuView(Map<Integer,MenuSlot> slots, SlotFactory slotFactory) {
        this.slots = slots;
        this.slotFactory = slotFactory;
    }

    public MenuView() {
        this.slotFactory = SlotFactory.getInstance();
        this.slots = Maps.newHashMap();
    }

    public Map<Integer, MenuSlot> getSlots() {
        return slots;
    }
    
    public void setSlot(int slotIndex, MenuSlot slot) {
        this.slots.put(slotIndex, slot);
    }
    
    public MenuSlot getSlot(int slotIndex) {
        return slots.get(slotIndex);
    }
    
    public int getFreeSlots() {
        int count = 0;
        for(int i = 0; i < MENU_SIZE; i++) {
            if(slots.get(i) == null || (slots.get(i) instanceof ItemSlot)) count++;
        }
        return count;
    }
    
    public MenuView clone() {
        return new MenuView(new HashMap<>(slots), slotFactory);
    }
    
    
    
}
