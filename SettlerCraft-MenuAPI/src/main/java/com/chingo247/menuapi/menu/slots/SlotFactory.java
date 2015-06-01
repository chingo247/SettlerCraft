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
package com.chingo247.menuapi.menu.slots;

import com.chingo247.xplatform.core.APlatform;
import com.chingo247.menuapi.menu.MenuAPI;
import com.chingo247.menuapi.menu.item.TradeItem;

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
        this.platform = MenuAPI.getInstance().getPlatform();
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
