/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin.shop;

import com.google.common.collect.Maps;
import static com.sc.plugin.shop.Shop.SHOPSIZE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
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

    private final List<ShopSlot> categories;
    private final Map<String, ArrayList<ShopSlot>> items;
    private final Map<String, Session> customers;
    private final Set<Integer> categorySlots;
    private final Inventory templateInventory;
    private final boolean endless;

    public final String FIRST_CATEGORY;

    /**
     * Default Constructor. Items will deplete and shop is not endless
     *
     * @param title The title of this shop
     */
    public CategoryShop(String title) {
        this(title, false, false);
    }

    /**
     * Constructor
     *
     * @param title The title of this shop
     * @param infinite If infinite items in this shop will/must never deplete Note: if infinite all
     * pick actions on this shop's inventory will be cancelled
     * @param endless wheter the shop should be endless(scrollable) or not
     */
    public CategoryShop(String title, boolean infinite, boolean endless) {
        this(UUID.randomUUID(), title, infinite, endless);
    }

    public CategoryShop(UUID id, String title, boolean infinite, boolean endless) {
        this(id, title, new ItemStack(Material.NETHER_STAR), "All", infinite, endless);
    }

    public CategoryShop(UUID id, String title, ItemStack firstCategory, String firstCategoryName, boolean infinite, boolean endless) {
        super(title, infinite);
        this.FIRST_CATEGORY = firstCategoryName;
        this.categories = new ArrayList<>();
        this.customers = Maps.newConcurrentMap();
        this.items = Maps.newHashMap();
        this.templateInventory = Bukkit.createInventory(null, SHOPSIZE, title);
        this.categorySlots = new HashSet<>();
        this.setCategorySlot(0);
        this.addCategory(FIRST_CATEGORY, firstCategory);
        this.endless = endless;
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
        for (int i = column; i < SHOPSIZE; i += 9) {
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

    @Override
    public boolean addItem(ItemStack item, double price) {
        return addItem(item, price, FIRST_CATEGORY);
    }

    /**
     * Adds an item to the given category
     *
     * @param item The item to add
     * @param price The price
     * @param category The category the item should be added to
     * @return true if category did exist and item succesfully added
     */
    public boolean addItem(ItemStack item, double price, String category) {
        ShopSlot ss = new ShopSlot(getId(), item, price);
        String c = category.toUpperCase();
        if (items.containsKey(c) && !isFull()) {
            if (isInfinite()) {
                item.setAmount(1);
            }
            return items.get(c).add(ss);
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
        String n = name.toLowerCase();
        if (hasCategory(n)) {
            return false;
        } else {
            ItemMeta meta = icon.getItemMeta();
            if (lore != null) {
                meta.setLore(lore);
            } else {
                meta.setLore(new ArrayList<String>());
            }
            meta.setDisplayName(n);
            icon.setItemMeta(meta);
            items.put(n, new ArrayList<ShopSlot>()); // Add a Set for this category
            addCategory(new ShopSlot(getId(), icon, ShopSlot.ShopSlotType.CATEGORY)); // Add to templateInventory/view!
            return true;
        }
    }

    private boolean addCategory(ShopSlot category) {
        if (category.getItemMeta().getDisplayName().equals(FIRST_CATEGORY)) {
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
     * Returns all the items that belong to the given category
     *
     * @param category The category
     * @return List of items or null if category didnt exist
     */
    public final List<ShopSlot> getItemsForCategory(String category) {
        if (category.equals(FIRST_CATEGORY)) {
            Set<ShopSlot> is = new TreeSet<>();
            for (ArrayList<ShopSlot> i : items.values()) {
                is.addAll(i);
            }
            return new ArrayList<>(is);
        } else {
            return new ArrayList<>(items.get(category));
        }

    }

    public int getAmountOfItems(String category) {
        return getItemsForCategory(category).size();
    }

    /**
     * Returns a copy of the list of all categories
     *
     * @return The categories as list of itemstacks
     */
    public final List<ShopSlot> getCategories() {
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
        if (endless == true) {
            return false;
        } else {
            int emptyReservedSpaces = (categories.size() % getReserved().size());
            int amountOfItems = items.get(FIRST_CATEGORY).size();
            return (amountOfItems + categories.size() + emptyReservedSpaces) == SHOPSIZE;
        }
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
    public void setTemplateInventory(Player player) {
        customers.get(player.getName()).inventory.clear();
        for (int i = 0; i < SHOPSIZE; i++) {
            if (isReserved(i) || isCategorySlot(i)) {
                customers.get(player.getName()).inventory.setItem(i, templateInventory.getItem(i));
            }
        }
    }

    private ShopSlot nextButton() {
        return arrowButton(new ShopSlot(getId(), new ItemStack(Material.BED_BLOCK), ShopSlot.ShopSlotType.ITEM));
    }

    private ShopSlot prevButton() {
        return arrowButton(new ShopSlot(getId(), new ItemStack(Material.BED_BLOCK), ShopSlot.ShopSlotType.ITEM));
    }

    private ShopSlot arrowButton(ShopSlot slot) {
        ItemMeta meta = slot.getItemMeta();
        if (slot.getSlotType() == ShopSlot.ShopSlotType.PREVIOUS) {
            meta.setDisplayName("Previous");
        } else {
            meta.setDisplayName("Next");
        }
        slot.setItemMeta(meta);
        return slot;
    }

    private void setTitle(Player player) {
        Session session = customers.get(player.getName());
        int pageNum = session.currentPage + 1; // page 0 => page 1
        String category = session.currentCategory;
        customers.get(player.getName()).inventory = Bukkit.createInventory(null, SHOPSIZE, getTitle() + " " + category + "[" + pageNum + "]");
    }

    private void fillInventory(Player player, List<ShopSlot> items, boolean updateInventory) {
        for (int i = categories.size(); i < SHOPSIZE; i++) {
            if (customers.get(player.getName()).lastIndex == items.size()) {
                // NO MORE ITEMS
                break;
            } else if (customers.get(player.getName()).lastIndex > 0 && i == categories.size()) {
                customers.get(player.getName()).inventory.addItem(prevButton());
            } else if (i == SHOPSIZE - 1) {
                customers.get(player.getName()).inventory.addItem(nextButton());
            } else if (templateInventory.getItem(i) == null || (templateInventory.getItem(i) instanceof ShopSlot)) {
                customers.get(player.getName()).inventory.setItem(i, items.get(customers.get(player.getName()).lastIndex));
                customers.get(player.getName()).lastIndex++;
            }
        }
        if (updateInventory) {
            player.updateInventory();
        }
    }

    /**
     * Change the category for this player, requires the player to be in the shop (visit() method
     * should have been called before this)
     *
     * @param player The player
     * @param category The new category
     */
    public void changeCategory(Player player, String category) {
        if (!customers.containsKey(player.getName())) {
            throw new IllegalStateException("Tried to change category without being in the shop");
        } else if (!category.equals(customers.get(player.getName()).currentCategory)) { // otherwise, already at the right category, do nothing     
            System.out.println("Change category!");
            customers.get(player.getName()).inventory.clear();
            customers.get(player.getName()).currentPage = 0;
            customers.get(player.getName()).currentCategory = category;
            setTitle(player);
            setTemplateInventory(player);
            List<ShopSlot> sss = getItemsForCategory(category);
            fillInventory(player, sss, true);
        }
    }

    public void nextPage(Player player) {
        if (!customers.containsKey(player.getName())) {
            throw new IllegalStateException("Tried to change category without being in the shop");
        } else {
            List<ShopSlot> sss = getItemsForCategory(customers.get(player.getName()).currentCategory);
            if (customers.get(player.getName()).lastIndex < sss.size()) {
                customers.get(player.getName()).currentPage++;
                setTitle(player);
                setTemplateInventory(player);
                fillInventory(player, sss.subList(customers.get(player.getName()).lastIndex, sss.size()), true);
            }
        }
    }

    public void prevPage(Player player) {
        if (!customers.containsKey(player.getName())) {
            throw new IllegalStateException("Tried to change category without being in the shop");
        } else {
            List<ShopSlot> sss = getItemsForCategory(customers.get(player.getName()).currentCategory);
            if (customers.get(player.getName()).lastIndex > 0) {
                setTemplateInventory(player);
                sss = sss.subList(0, customers.get(player.getName()).lastIndex);
                Collections.reverse(sss);
                fillInventory(player, sss, true);
            }
        }
    }

    /**
     * Adds the player as customer for this shop, player will leave the shop when the player closes the inventory
     * Will throw IllegalStateException if player was already inside of the shop
     * @param player The player
     */
    @Override
    public void visit(Player player) {
        if (!customers.containsKey(player.getName())) {
            System.out.println("Open inventory!");
            Inventory inv = getTemplateInventory();
            customers.put(player.getName(), new Session(0, inv, FIRST_CATEGORY));
            List<ShopSlot> sss = getItemsForCategory(FIRST_CATEGORY);
            fillInventory(player, sss, false);
            player.openInventory(inv);
        } else {
            throw new IllegalStateException("Player already in shop");
        }

    }

    /**
     * The player will pay for the selected item
     * @param player 
     * @param item
     * @return 
     */
    @Override
    public boolean pay(Player player, ShopSlot item) {
        return super.sellItem(player, item);
    }

    @Override
    public void leave(Player player) {
        if (customers.containsKey(player.getName())) {
            customers.remove(player.getName());
        }
    }

    private class Session {

        private String currentCategory;
        private Inventory inventory;
        private int currentPage = 0;
        private int lastIndex = 0;

        public Session(int currentPage, Inventory inventory, String currentCategory) {
            this.currentPage = currentPage;
            this.inventory = inventory;
            this.currentCategory = currentCategory;
        }

    }

}
