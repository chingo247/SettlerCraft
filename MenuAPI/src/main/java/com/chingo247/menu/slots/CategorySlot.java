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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Chingo
 */
public class CategorySlot extends Slot {

    private final String name;
    private final Set<String> aliases = new HashSet<>();
    private final Material icon;

    public CategorySlot(String name, Material icon) {
       this.name = name;
       this.icon = icon;
    }
    
    

    public String getName() {
        return name;
    }

    public void addAlias(String alias) {
        aliases.add(alias);
    }

    public void addAliases(String... alias) {
        aliases.addAll(Arrays.asList(alias));
    }

    public boolean hasAlias(String alias) {
        return aliases.contains(alias);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CategorySlot other = (CategorySlot) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    public ItemStack getIcon() {
        ItemStack is = new ItemStack(icon);
        
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(null);
        is.setItemMeta(meta);
        return is;
    }

}
