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
package com.sc.plugin.scmenu;

import com.sc.plugin.scmenu.MenuSlot.MenuSlotType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Chingo
 */
public abstract class CategoryMenu extends Menu {

    /**
     * Constructor
     *
     * @param title The title
     * @param wontDeplete Wheter or not the items in this menu should deplete
     */
    public CategoryMenu(String title, boolean wontDeplete) {
        super(title, wontDeplete);
    }

    /**
     * Adds a category to this menu at the specified slot
     *
     * @param slot The slot
     * @param icon The itemStack that will be used to represent the Category
     * @param categoryName The category name, will be used on the given itemstack to change the
     * display name
     * @param aliases The alliases for this category
     * @return true if succesfully added, false if category already exists or slot was locked
     */
    public boolean addCategory(int slot, ItemStack icon, String categoryName, String... aliases) {
        if (!isLocked(slot) && !hasCategory(categoryName)) {
            ItemMeta meta = icon.getItemMeta();
            meta.setDisplayName(categoryName);
            icon.setItemMeta(meta);
            setSlot(slot, new MenuSlot(icon, categoryName, MenuSlotType.CATEGORY, aliases));
            return true;
        }
        return false;
    }

    /**
     * Adds an action to this menu at the specified slot
     *
     * @param slot The slot
     * @param icon The itemstack that will be used to represent this action
     * @param actionName The action name, will be used to change the display name of the given
     * itemstack
     * @param aliases The alliases for this action
     * @return true if item succesfully added, false if slot was locked
     */
    public boolean addActionSlot(int slot, ItemStack icon, String actionName, String... aliases) {
        if (!isLocked(slot)) {
            ItemMeta meta = icon.getItemMeta();
            meta.setDisplayName(actionName);
            icon.setItemMeta(meta);
            setSlot(slot, new MenuSlot(icon, actionName, MenuSlotType.ACTION, aliases));
            return true;
        }
        return false;
    }

    /**
     * Checks wheter the given category or alias already exists
     *
     * @param category The category
     * @return true if category already exists or has a child that's called the same way as the
     * given parameter
     */
    public boolean hasCategory(String category) {
        for (MenuSlot ms : getMenuSlots()) {
            if (ms != null && ms.getType() == MenuSlotType.CATEGORY) {
                if (ms.getName().trim().equalsIgnoreCase(category.trim()) || ms.hasAlias(category)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Every Category owns a list of alliases, the parent name will be the displayname given to the
     * category
     *
     * @param alias The allias
     * @return The parent's name for given allias
     */
    public String getCategoryName(String alias) {
        for (MenuSlot ms : getMenuSlots()) {
            if (ms != null && ms.getType() == MenuSlotType.CATEGORY
                    && ((ms.getName().trim().equalsIgnoreCase(
                            alias.trim()) || 
                            ms.hasAlias(alias.trim())))) {
                return ms.getName();
            }
        }
        return null;
    }

    /**
     * Code that should be executed when a player clicks on a category slot
     *
     * @param categoryName The category name
     * @param whoClicked The player
     */
    public abstract void onCategoryClicked(String categoryName, Player whoClicked);

}
