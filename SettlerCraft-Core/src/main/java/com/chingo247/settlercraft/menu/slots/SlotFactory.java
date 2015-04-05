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
package com.chingo247.settlercraft.menu.slots;

import com.chingo247.settlercraft.SettlerCraft;
import com.chingo247.settlercraft.menu.item.TradeItem;
import com.chingo247.xcore.core.APlatform;

/**
 *
 * @author Chingo
 */
public class SlotFactory {
    
    private final APlatform platform;
    
    private static SlotFactory instance;
    
    public static SlotFactory getInstance() {
        if(instance == null) {
            instance = new SlotFactory();
        }
        return instance;
    }
    
    private SlotFactory() {
        this.platform = SettlerCraft.getInstance().getPlatform();
    }
    
    public ActionSlot createActionSlot(String action, int icon, String[] lore) {
        return new ActionSlot(platform, action, icon, lore);
    }
    
    public CategorySlot createCategorySlot(String category, int icon) {
        return new CategorySlot(platform, category, icon);
    }
    
    public ItemSlot createItemSlot(TradeItem item) {
        return new ItemSlot(item);
    }
    
    public MenuSlot createLockedSlot() {
        return new MenuSlot(true);
    }
    
}
