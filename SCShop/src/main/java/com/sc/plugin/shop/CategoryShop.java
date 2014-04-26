/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin.shop;

import com.google.common.collect.Maps;
import com.sc.plugin.AlphabeticalItemStackComperator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Chingo
 */
public class CategoryShop extends Shop {

    private final List<ItemStack> categories;
    private final Map<String, TreeSet<ItemStack>> items;
    private final Map<String, Inventory> visitors;
    private final Set<Integer> categorySlots;
    private final Inventory templateInventory;

    public static final String ALL_CATEGORY = "All";
    public static final String CATEGORY_SLOT = "Category_Slot";

    /**
     * Constructor.
     *
     * @param title The title of this shop
     */
    public CategoryShop(String title) {
        this(title, false);
    }

    /**
     * Constructor
     *
     * @param title The title of this shop
     * @param infinite If infinite items in this shop will/must never deplete Note: if infinite all
     * pick actions on this shop's inventory will be cancelled
     */
    public CategoryShop(String title, boolean infinite) {
        super(title, infinite);
        this.categories = new ArrayList<>();
        this.visitors = Maps.newConcurrentMap();
        this.items = Maps.newHashMap();
        this.templateInventory = Bukkit.createInventory(null, SHOPSIZE, title);
        this.categorySlots = new HashSet<>();
        this.setCategorySlot(0);
        this.addCategory(ALL_CATEGORY, new ItemStack(Material.NETHER_STAR));
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
        for (int i = column; i < 54; i += 9) {
            setCategorySlot(i);
        }
    }

    @Override
    public final void setSlot(int slot, boolean reserve) {
        if (slot != 0) {
            if (reserve) {
                reserved.put(slot, CATEGORY_SLOT);
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

    @Override
    public boolean addItem(ItemStack item) {
        return addItem(item, ALL_CATEGORY);
    }

    /**
     * Adds an item to the given category
     *
     * @param item The item to add
     * @param category The category the item should be added to
     * @return true if category did exist and item succesfully added
     */
    public boolean addItem(ItemStack item, String category) {
        if (items.containsKey(category) && !isFull()) {
            if (isInfinite()) {
                item.setAmount(1);
            }
            return items.get(category).add(item);
        } else {
            return false;
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
    public final boolean addCategory(String name, ItemStack icon, List<String> lore) {
        if (hasCategory(name)) {
            return false;
        } else {
            ItemMeta meta = icon.getItemMeta();
            if (lore != null) {
                meta.setLore(lore);
            } else {
                meta.setLore(new ArrayList<String>());
            }
            meta.setDisplayName(name);
            icon.setItemMeta(meta);
            items.put(name, new TreeSet<>(new AlphabeticalItemStackComperator())); // Add a Set for this category
            addCategory(icon); // Add to templateInventory/view!
            return true;
        }
    }

    private boolean addCategory(ItemStack category) {
        if (category.getItemMeta().getDisplayName().equals(ALL_CATEGORY)) {
            templateInventory.setItem(0, category);
            return true;
        } else {
            Iterator<Integer> it = categorySlots.iterator();
            while (it.hasNext()) {
                int i = it.next();
                if (templateInventory.getItem(i) == null) {
                    templateInventory.setItem(i, category);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Adds a Category to this shop
     *
     * @param name The name of the category, this will be set as display name of the given itemstack
     * @param icon The itemstack, will be abused as icon
     * @return true if Category didn't exist in this shop
     */
    public final boolean addCategory(String name, ItemStack icon) {
        return addCategory(name, icon, null);
    }

    /**
     * Removes a Category from this shop, all items in this category will be move to the all
     * category.
     *
     * @param name The name
     * @return If the name is equal to "All" or Category doesnt exist, this method returns false
     */
    public final boolean removeCategory(String name) {
        if (name.equals(ALL_CATEGORY) || !hasCategory(name)) {
            return false;
        } else {
            Iterator<ItemStack> it = categories.iterator();
            while (it.hasNext()) {
                ItemStack cat = it.next();
                if (cat.getItemMeta().getDisplayName().equals(name)) {
                    items.get(ALL_CATEGORY).addAll(getCategory(name));
                    it.remove();
                    return true;
                }
            }
            throw new AssertionError("Unreachable");
        }
    }

    /**
     * Returns all the items that belong to the given category
     *
     * @param category The category
     * @return List of items or null of category didnt exist
     */
    public final List<ItemStack> getCategory(String category) {
        if (category.equals(ALL_CATEGORY)) {
            TreeSet<ItemStack> is = new TreeSet<>(new AlphabeticalItemStackComperator());
            for (TreeSet<ItemStack> i : items.values()) {
                is.addAll(i);
            }
            return new ArrayList(is);
        } else {
            return new ArrayList<>(items.get(category));
        }

    }

    /**
     * Returns a copy of the list of all categories
     *
     * @return The categories as list of itemstacks
     */
    public final List<ItemStack> getCategories() {
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
    public boolean isFull() {
        int emptyReservedSpaces = (categories.size() % getReserved().size());
        int amountOfItems = items.get(ALL_CATEGORY).size();
        return (amountOfItems + categories.size() + emptyReservedSpaces) == SHOPSIZE;
    }

    @Override
    public Inventory getTemplateInventory() {
        Inventory in = Bukkit.createInventory(null, SHOPSIZE, getTitle());
        for (int i = 0; i < SHOPSIZE; i++) {
            if (isReserved(i) || isCategorySlot(i)) {
                in.setItem(i, templateInventory.getItem(i));
            }
        }
        return in;
    }
    
    @Override
    public void setTemplateInventory(final Inventory inventory) {
        inventory.clear();
        for (int i = 0; i < SHOPSIZE; i++) {
            if (isReserved(i) || isCategorySlot(i)) {
                inventory.setItem(i, templateInventory.getItem(i));
            }
        }
    }

    @Override
    public void visit(Player player) {
        visit(player, ALL_CATEGORY);
    }

    public void visit(Player player, String category) {
        if (!visitors.containsKey(player.getName())) {
            System.out.println("Open inventory!");
            Inventory inv = getTemplateInventory();
            visitors.put(player.getName(), inv);
            for (ItemStack i : getCategory(category)) {
                inv.addItem(i);
            }
            player.openInventory(inv);
        } else {
            System.out.println("Update inventory!");
            visitors.get(player.getName()).clear();
            setTemplateInventory(visitors.get(player.getName()));
            for (ItemStack i : getCategory(category)) {
                visitors.get(player.getName()).addItem(i);
            }
            player.updateInventory();
        }

    }

    @Override
    public void leave(Player player) {
        if (visitors.containsKey(player.getName())) {
            visitors.remove(player.getName());
            player.sendMessage("[" + getTitle() + "]: Have a nice day!");
        }
    }




}
