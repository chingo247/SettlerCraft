/*
 * Copyright (C) 2014 Chingo247
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

package com.chingo247.menu.slots;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Chingo
 */
public class ActionSlot extends Slot {
    
    private final String action;
    private final List<String> lore;
    private final Material icon;
    
    public ActionSlot(String action, Material icon) {
        this(action, icon, new String[]{});
    }

    public ActionSlot(String action, Material icon, String... lore) {
        this.action = action;
        this.lore = new ArrayList<>(Arrays.asList(lore));
        this.icon = icon;
    }

    public String getAction() {
        return action;
    }
    
    public void setLore(String... lore) {
        this.lore.clear();
        this.lore.addAll(Arrays.asList(lore));
    }
    
    public ItemStack getIcon() {
        ItemStack stack = new ItemStack(icon);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(action);
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }
    
    
}
