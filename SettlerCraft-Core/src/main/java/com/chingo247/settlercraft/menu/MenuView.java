/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft.menu;

import static com.chingo247.settlercraft.menu.ACategoryMenu.MENU_SIZE;
import com.chingo247.settlercraft.menu.slots.MenuSlot;
import com.chingo247.settlercraft.menu.slots.SlotFactory;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.com.google.common.collect.Maps;

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
            if(slots.get(i) == null) count++;
        }
        return count;
    }
    
    public MenuView clone() {
        return new MenuView(new HashMap<>(slots), slotFactory);
    }
    
    
    
}
