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
package com.chingo247.settlercraft.menu.slots;

import com.chingo247.xcore.core.AItemStack;
import com.chingo247.xcore.core.APlatform;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Chingo
 */
public class CategorySlot extends MenuSlot {

    private final String name;
    private final Set<String> aliases = new HashSet<>();
    private final int icon;
    private final APlatform platform;

    CategorySlot(APlatform platform, String name, int icon) {
        super(false);
        Preconditions.checkArgument(icon >= 0);
        this.icon = icon;
        this.name = name;
        this.platform = platform;
    }

    public String getName() {
        return name;
    }

    public Set<String> getAliases() {
        return aliases;
    }
    
    public void addAlias(String alias) {
        aliases.add(alias);
    }

    public void addAliases(String... alias) {
        aliases.addAll(Arrays.asList(alias));
    }
    
    public void addAliases(Set<String> aliases) {
        this.aliases.addAll(aliases);
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

    public AItemStack getIcon() {
        AItemStack stack = platform.createItemStack(icon);
        stack.setMaterial(icon);
        stack.setName(name);
        stack.setLore(null);
        return stack;
    }

    @Override
    public CategorySlot clone() {
        CategorySlot slot = new CategorySlot(platform, name, icon);
        slot.addAliases(aliases);
        return slot;
    }

    
}
