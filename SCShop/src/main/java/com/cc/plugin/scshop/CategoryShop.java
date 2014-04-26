/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cc.plugin.scshop;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
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

    private final ConcurrentHashMap<String, Visitor> visitors;
    private final List<ItemStack> categories;
    private final Map<String, TreeSet<ItemStack>> items;
    private final String ALL_CATEGORY = "All";
    private final Set<Integer> categorySlots = Sets.newTreeSet();

    public CategoryShop(String title) {
        this(title, false);
    }

    public CategoryShop(String title, boolean infinite) {
        super(title, infinite);
        this.categories = new ArrayList<>();
        this.visitors = new ConcurrentHashMap<>();
        this.items = Maps.newHashMap();
        this.setCategorySlot(0, true);
        addCategory(ALL_CATEGORY, new ItemStack(Material.NETHER_STAR));
    }

    /**
     * Adds an item to this shop, item will be added to a default category, which is "All".
     *
     * @param item The item to add
     * @return returns true if succesfully added
     */
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
            if (infinite) {
                item.setAmount(1);
            }
            super.addItem(item);
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
            addCategory(icon); // Add to inventory/view!
            return true;
        }
    }

    private boolean addCategory(ItemStack category) {
        if (category.getItemMeta().getDisplayName().equals(ALL_CATEGORY)) {
            inventory.setItem(0, category);
            return true;
        } else {
            Iterator<Integer> it = categorySlots.iterator();
            while (it.hasNext()) {
                int i = it.next();
                if (inventory.getItem(i) == null) {
                    inventory.setItem(i, category);
                    return true;
                }
            }
            return false;
        }
    }

    public final void setCategorySlot(int slot, boolean reserved) {
        if (slot != 0) {
            if (reserved) {
                categorySlots.add(slot);
            } else {
                categorySlots.remove(slot);
            }
            setSlot(slot, reserved);
        }
    }

    public final void setCategoryRow(int row, boolean reserved) {
        int rowSize = 9;
        for (int slot = row * rowSize; slot < row + rowSize; slot++) {
            setCategorySlot(slot, reserved);
        }
        invalidate();
    }

    public final void setCategoryColumn(int column, boolean reserved) {
        for (int i = column; i < 54; i += 9) {
            setCategorySlot(i, reserved);
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
     * category
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

    private Inventory template() {
        Inventory in = Bukkit.createInventory(null, SHOPSIZE, title);
        for (int i = 0; i < SHOPSIZE; i++) {
            if (isCategorySlot(i) || isReserved(i) || i == 0) {
                in.setItem(i, inventory.getItem(i));
            }
        }
        return in;
    }

    public void visit(Player player, String category) {
        if (!visitors.contains(player.getName())) {
            System.out.println(Arrays.toString(visitors.keySet().toArray()));
            System.out.println("Open new inventory!");
            visitors.put(player.getName(), new Visitor(player, title));
            visitors.get(player.getName()).inventory = template();
            for (ItemStack i : getCategory(category)) {
                visitors.get(player.getName()).inventory.addItem(i);
            }
            player.openInventory(visitors.get(player.getName()).inventory);
        } else {
            System.out.println("Update inventory!");
            visitors.put(player.getName(), new Visitor(player, title));
            visitors.get(player.getName()).inventory = template();
            for (ItemStack i : getCategory(category)) {
                visitors.get(player.getName()).inventory.addItem(i);
            }
            player.updateInventory();
        }
    }
    
    public void leave(Player player) {
        if(visitors.contains(player.getName())) {
            visitors.remove(player.getName());
            player.sendMessage("[" + title + "]: Have a nice day!");
        }
    }

    @Override
    public void visit(Player player) {
        visit(player, ALL_CATEGORY);
    }

    private void invalidate() {
        if (categorySlots.size() < categories.size()) {
            for (ItemStack i : categories.subList(categorySlots.size(), categories.size())) {
                removeCategory(i.getItemMeta().getDisplayName());
            }
        }
    }

    private class Visitor {

        private Inventory inventory;
        private final String player;
        private String currentCategory;

        public Visitor(Player player, String shop) {
            this.player = player.getName();
            this.inventory = Bukkit.createInventory(null, SHOPSIZE, shop);
        }

        public Inventory getInventory() {
            return inventory;
        }

    }

}
