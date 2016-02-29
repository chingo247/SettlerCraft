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

import com.chingo247.xplatform.core.AItemStack;
import com.chingo247.xplatform.core.APlatform;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Chingo
 */
public class ActionSlot extends MenuSlot {
    
    private final String action;
    private final List<String> lore;
    private final int icon;
    private final APlatform platform;
    
    
    private ActionSlot(APlatform platform, String action, int icon, List<String> list) {
        super(false);
        this.platform = platform;
        this.action = action;
        this.icon = icon;
        this.lore = list;
    }
    
    ActionSlot(APlatform platform, String action, int icon, String... lore) {
        super(false);
        Preconditions.checkArgument(icon > 0);
        Preconditions.checkNotNull(action);
        this.action = action;
        this.lore = lore != null ? new ArrayList<>(Arrays.asList(lore)) : new ArrayList<String>();
        this.icon = icon;
        this.platform = platform;
    }

    public String getAction() {
        return action;
    }
    
    public void setLore(String... lore) {
        this.lore.clear();
        this.lore.addAll(Arrays.asList(lore));
    }
    
    public AItemStack getIcon() {
        AItemStack stack = platform.createItemStack(icon);
        stack.setName(action);
        stack.setLore(lore);
        return stack;
    }

    @Override
    public ActionSlot clone() {
        return new ActionSlot(platform, action, icon, new ArrayList<>(lore));
    }
    
    
    
}
