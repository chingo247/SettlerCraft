/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin.shop;

import com.google.common.base.Preconditions;
import static com.sc.plugin.shop.Menu.MENUSIZE;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author Chingo
 */
abstract class CategoryMenu extends Menu {

    private final List<MenuCategorySlot> categories;
    private final Set<Integer> categorySlots;
    private final Inventory template;
    private final MenuCategorySlot FIRST_CATEGORY;
    private final int FIRSTSLOT;

    public CategoryMenu(UUID id, String title, MenuCategorySlot categorySlot, int firstSlot, boolean wondDeplete) {
        super(title, wondDeplete);
        Preconditions.checkArgument(firstSlot >= 0 && firstSlot < MENUSIZE);
        this.FIRST_CATEGORY = categorySlot;
        this.categories = new ArrayList<>();
        this.template = Bukkit.createInventory(null, MENUSIZE, title);
        this.categorySlots = new HashSet<>();
        this.setCategorySlot(firstSlot);
        this.FIRSTSLOT = firstSlot;
    }

    public int getFIRSTSLOT() {
        return FIRSTSLOT;
    }

    public MenuCategorySlot getFIRST_CATEGORY() {
        return FIRST_CATEGORY;
    }
    
    

    public final void setCategorySlot(int slot) {
        categorySlots.add(slot);
        this.setSlot(slot, true);
    }

    public final void setCategoryRow(int row) {
        int rowSize = 9;
        for (int slot = row * rowSize; slot < row + rowSize; slot++) {
            setCategorySlot(slot);
        }
    }

    public final void setCategoryColumn(int column) {
        for (int i = column; i < MENUSIZE; i += 9) {
            setCategorySlot(i);
        }
    }

    @Override
    public final void setSlot(int slot, boolean reserve) {
        if (slot != 0) {
            if (reserve) {
                reserved.add(slot);
            } else {
                reserved.remove(slot);
            }
        }
    }

    @Override
    public final void setRow(int row, boolean reserved) {
        int rowSize = 9;
        for (int slot = row * rowSize; slot < row + rowSize; slot++) {
            setSlot(slot, reserved);
        }
    }

    @Override
    public final void setColumn(int column, boolean reserved) {
        for (int i = column; i < 54; i += 9) {
            setSlot(i, reserved);
        }
    }

    /**
     * Adds a Category to this shop
     *
     * @param name The name of the category, this will be set as display name of the given
     * itemstack.
     * @param icon The itemstack, will be abused as icon
     * @param lore The lore, can be used a description for the category
     * @return true if Category didn't exist in this shop
     */
    public abstract boolean addCategory(MenuCategorySlot categorySlot);

    /**
     * Puts the Category int the template inventory, making it visible to the player
     * @param category The menucategory
     * @return false if the category couldnt be added, because the menu doesnt supply enough categroy slots
     */
    boolean addCategorySlot(MenuCategorySlot category) {
        if (category.getItemMeta().getDisplayName().equals(FIRST_CATEGORY.getName())) {
            template.setItem(FIRSTSLOT, category);
            return true;
        } else {
            Iterator<Integer> it = categorySlots.iterator();
            while (it.hasNext()) {
                int i = it.next();
                if (template.getItem(i) == null) {
                    template.setItem(i, category);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Returns all the items that belong to the given category
     *
     * @param category The category
     * @return List of items or null if category didnt exist
     */
    public abstract List<? extends MenuSlot> getSlotsForCategory(String category);

    public int getAmountOfItems(String category) {
        return getSlotsForCategory(category).size();
    }

    /**
     * Returns a copy of the list of all categories
     *
     * @return The categories as list of itemstacks
     */
    public final List<MenuCategorySlot> getCategories() {
        return new ArrayList<>(categories);
    }

    public boolean hasCategory(String category) {
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i).getItemMeta().getDisplayName().equals(category)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCategorySlot(int slot) {
        return categorySlots.contains(slot);
    }

    @Override
    public Inventory getTemplate() {
        return this.template;
    }

    /**
     * Change the category for this player, requires the player to be in the shop (onEnter() method
     * should have been called before this)
     *
     * @param player The player
     * @param category The new category
     */
    public abstract void changeCategory(Player player, String category);

    public abstract void nextPage(Player player);

    public abstract void prevPage(Player player);

    @Override
    public abstract void onEnter(Player player);

    @Override
    public abstract void onLeave(Player player);

}
