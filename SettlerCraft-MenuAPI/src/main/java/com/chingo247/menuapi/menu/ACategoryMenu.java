/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.menuapi.menu;

import com.chingo247.xplatform.core.AInventory;
import com.chingo247.xplatform.core.AItemStack;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IColors;
import com.chingo247.xplatform.core.IPlayer;
import com.chingo247.settlercraft.core.platforms.services.IEconomyProvider;
import com.chingo247.menuapi.menu.item.TradeItem;
import com.chingo247.menuapi.menu.slots.ActionSlot;
import com.chingo247.menuapi.menu.slots.CategorySlot;
import com.chingo247.menuapi.menu.slots.MenuSlot;
import com.chingo247.menuapi.menu.slots.ItemSlot;
import com.chingo247.menuapi.menu.slots.SlotFactory;
import com.chingo247.menuapi.menu.util.ShopUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author Chingo
 */
public abstract class ACategoryMenu {

    public static final int LEFT_CLICK = 0, RIGHT_CLICK = 1, SHIFT_LEFT = 2, SHIFT_RIGHT = 3, DOUBLE_CLICK = 4;
    public static final int MENU_SIZE = 54;
    private static final String ACTION_PREVIOUS = "Previous", ACTION_NEXT = "Next";

//    private final Map<Integer,MenuSlot> slots;
    private final MenuView view;
    private final Map<String, Category> categories;

    private IPlayer player;
    private final AInventory inventory;
    private final IEconomyProvider economyProvider;
    private final SlotFactory slotFactory;
    private final APlatform platform;
    private final MenuAPI menuAPI;
    private final IColors color;

    protected final String title, defaultCategory;
    private boolean noCosts;

    private boolean enabled;
    private int currentPage = 0;

    private String currentCategory;

    public ACategoryMenu(IEconomyProvider economyProvider, String title, AInventory notThePlayersInventory, String defaultCategory, int defaultCategoryIcon, MenuView view) {
        Preconditions.checkNotNull(title);
        Preconditions.checkNotNull(notThePlayersInventory);
        Preconditions.checkNotNull(view);
        this.menuAPI = MenuAPI.getInstance();
        this.view = view;
        this.title = title;
        this.inventory = notThePlayersInventory;
        this.noCosts = false;
        this.economyProvider = economyProvider;
        this.slotFactory = SlotFactory.getInstance();
        this.platform = MenuAPI.getInstance().getPlatform();
        this.enabled = true;
        this.currentCategory = defaultCategory;
        this.view.setSlot(0, slotFactory.createCategorySlot(defaultCategory, defaultCategoryIcon));
        this.categories = Maps.newHashMap();
        this.defaultCategory = defaultCategory;
        this.color = platform.getChatColors();
    }

    public void setNoCosts(boolean noCosts) {
        this.noCosts = noCosts;
    }

    public MenuView getView() {
        return view.clone();
    }

    private IColors getColor() {
        return platform.getChatColors();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getPage() {
        return currentPage;
    }

    protected MenuSlot getSlot(int i) {
        return view.getSlot(i);
    }

    protected void setSlot(int i, MenuSlot slot) {
        if (i == 0) {
            throw new IllegalArgumentException("Can't set the default slot '0'");
        }
        this.view.setSlot(i, slot);
    }

    public void setCategorySlot(int slot, CategorySlot categorySlot) {
        if (slot < 0 || slot > MENU_SIZE) {
            throw new IndexOutOfBoundsException("Slot is not in range of 0 - " + MENU_SIZE);
        }
        if (categories.get(categorySlot.getName()) != null) {
            throw new RuntimeException("Category already exists!");
        }
        synchronized (categories) {
            Category cat = new Category(title);
            cat.addAliasses(categorySlot.getAliases());
            categories.put(title, cat);
            setSlot(slot, categorySlot);
        }
    }

    /**
     * Adds a category to this menu. If category already exists, entry for this
     * category will be overwritten.
     *
     * @param slot The inventory slot
     * @param category The category
     * @param icon The material that will be used as icon for this category
     * @param aliases The aliases that can be used for this category
     */
    public final void setCategorySlot(int slot, String category, int icon, String... aliases) {
        CategorySlot categorySlot = slotFactory.createCategorySlot(category, icon);
        categorySlot.addAliases(aliases);
        setCategorySlot(slot, categorySlot);
    }

    protected String matchCategoryForName(String name) {

        synchronized (categories) {
            if (name == null) {
                return defaultCategory;
            }

            for (Entry<String, Category> e : categories.entrySet()) {
                if (e.getValue().matches(name)) {
                    return e.getKey();
                }
            }

        }
        return defaultCategory;
    }

    public void setActionSlot(int slot, String text, int icon) {
        if (slot < 0 || slot > MENU_SIZE) {
            throw new IndexOutOfBoundsException("Slot is not in range of 0 - " + MENU_SIZE);
        }
        setActionSlot(slot, text, icon, null);
    }

    public void setActionSlot(int slot, String text, int icon, String[] lore) {
        if (slot < 0 || slot > MENU_SIZE) {
            throw new IndexOutOfBoundsException("Slot is not in range of 0 - " + MENU_SIZE);
        }

        ActionSlot actionSlot = slotFactory.createActionSlot(text, icon, lore);
        setSlot(slot, actionSlot);
    }

    public final void setLocked(int slot) {
        if (slot < 0 || slot > MENU_SIZE) {
            throw new IndexOutOfBoundsException("Slot is not in range of 0 - " + MENU_SIZE);
        }

        // Check if slot was inuse by another Category
        MenuSlot s = new MenuSlot(true);
        setSlot(slot, s);
    }

    public final void setLocked(int... slots) {
        for (int i : slots) {
            setLocked(i);
        }
    }

    protected void give(TradeItem item) {
        player.getInventory().addItem(item.getItemStack());
    }

    protected boolean sell(TradeItem item) {
        double price = item.getPrice();
        double balance = economyProvider.getBalance(player.getUniqueId());

        // Has enough?
        if (price <= balance) {
            player.getInventory().addItem(item.getItemStack());
            economyProvider.withdraw(player.getUniqueId(), price);
            if (price > 0) {
                player.sendMessage("You bought: " + getColor().blue() + item.getName());
                player.sendMessage("Your new balance is: " + getColor().gold() + economyProvider.getBalance(player.getUniqueId()));
            } else {
                player.sendMessage("You got: " + getColor().blue() + item.getName());
            }
            return true;
        } else {
            player.sendMessage(color.red() + "You don't have enough money...");
        }
        // Not enough money
        return false;
    }

    public String getTitle() {
        return title;
    }

    public int getFreeSlots() {
        return view.getFreeSlots();
    }

    private void resetInventory() {
        inventory.clear();
        IColors colors = getColor();
        for (int i = 0; i < MENU_SIZE; i++) {
            MenuSlot slot = view.getSlot(i);
            if (slot != null && !slot.isNoneSlot()) {

                if (slot instanceof ActionSlot) {
                    ActionSlot actionSlot = (ActionSlot) slot;
                    if (actionSlot.getAction().equals(ACTION_PREVIOUS)) {
                        if (hasPrev()) {
                            // Only computer scientists count from 0...
                            actionSlot.setLore(
                                    colors.blue() + "To Page " + colors.gold() + currentPage,
                                    colors.blue() + "Current Page " + colors.gold() + (currentPage + 1)
                            );
                        } else {
                            actionSlot.setLore(
                                    colors.blue() + "To Page " + colors.gold() + " - ",
                                    colors.blue() + "Current Page " + colors.gold() + (currentPage + 1)
                            );
                        }
                    } else if (actionSlot.getAction().equals(ACTION_NEXT)) {

                        if (hasNext()) {
                            actionSlot.setLore(
                                    colors.blue() + "To Page " + colors.gold() + (currentPage + 1 + 1),
                                    colors.blue() + "Current Page " + colors.gold() + (currentPage + 1)
                            );
                        } else {
                            actionSlot.setLore(
                                    colors.blue() + "To Page " + colors.gold() + " - ",
                                    colors.blue() + "Current Page " + colors.gold() + (currentPage + 1)
                            );
                        }
                    }
                    inventory.setItem(i, actionSlot.getIcon());
                } else if (slot instanceof CategorySlot) {
                    CategorySlot cs = (CategorySlot) slot;
                    inventory.setItem(i, cs.getIcon());
                } else if (slot instanceof ItemSlot) {
                    view.setSlot(i, null);
                }
            }
        }
    }

    private synchronized void updateMenu(String newCategory, int newPage, boolean checkNoUpdate) {

//        // Get the new category
//        if (checkNoUpdate && newCategory.equals(currentCategory) && newPage == currentPage) {
//            return; // Nothing changed
//        }
        if (!newCategory.equals(currentCategory)) {
            currentPage = 0;
            currentCategory = newCategory;
        } else {
            currentPage = newPage;
        }

        resetInventory();

        // Fill Inventory with items
        List<TradeItem> tradeItems = getItems(currentCategory, currentPage, player);
        Iterator<TradeItem> it = tradeItems.iterator();

        for (int i = 0; i < MENU_SIZE && it.hasNext(); i++) {
            if (view.getSlot(i) == null) {
                TradeItem ti = it.next();
                TradeItem clone = ti.clone();

                if (noCosts) {
                    clone.setPrice(0);
                }

                ItemSlot slot = slotFactory.createItemSlot(ti);
                setSlot(i, slot);
                inventory.setItem(i, clone.getItemStack());
            }
        }
        // Update Inventory
        player.updateInventory();
    }

    public void openMenu(IPlayer player) {
        if (player != null) {
            openMenuWithoutCheck(player);
        } else {
            throw new AssertionError("Only one player can open a menu at a time!");
        }
    }

    protected void openMenuWithoutCheck(IPlayer player) {
        this.player = player;
        menuAPI.registerMenu(this);
        // Notify player's balance
        player.sendMessage(color.yellow() + "[" + title + "]: " + color.reset() + "Hello " + color.green() + player.getName() + color.reset() + "!");
        if (economyProvider != null) {
            double balance = economyProvider.getBalance(player.getUniqueId());
            if (balance > 0) {
                player.sendMessage(color.yellow() + "[" + title + "]: " + color.reset() + "Your balance is " + color.gold() + ShopUtil.valueString(balance));
            } else {
                player.sendMessage(color.yellow() + "[" + title + "]: " + color.reset() + "Your balance is " + color.red() + balance);
            }

        }
        updateMenu(currentCategory, currentPage, false);
        player.openInventory(inventory);
    }

    protected boolean hasNext() {
        return !getItems(title, currentPage, player).isEmpty();
    }

    protected boolean hasPrev() {
        return currentPage > 0;
    }

    protected abstract <T extends TradeItem> List<T> getItems(String category, int page, IPlayer player);

    protected IPlayer getPlayer() {
        return player;
    }

    public void close(String reason) {
        player.closeInventory();
        if (reason != null && !reason.isEmpty()) {
            player.sendMessage(reason);
        }
    }

    /**
     *
     * @param slot
     * @param clickType
     */
    boolean onMenuSlotClicked(int slot, int clickType, AItemStack stack, AItemStack cursor) {
        boolean disable = false;
        if (clickType == RIGHT_CLICK
                || clickType == SHIFT_RIGHT
                || clickType == SHIFT_LEFT
                || clickType < MENU_SIZE && slot >= 0
                || clickType == DOUBLE_CLICK) {
//          Cancel the action
            disable = true;
        }

        if (slot < MENU_SIZE && slot >= 0) {
            
            if (stack != null
                    && stack.getMaterial() != 0
                    && stack.getAmount() > 0) {

                MenuSlot s = view.getSlot(slot);

                // Slot is item slot
                if (s instanceof ItemSlot) {

                    ItemSlot itemSlot = (ItemSlot) s;
                    TradeItem item = itemSlot.getItem();

                    if (item != null) {
                        if (economyProvider != null) {
                            sell(item);
                        } else {
                            give(item);
                        }
                    }
                } else if (s instanceof ActionSlot) {
                    ActionSlot actionSlot = (ActionSlot) s;
                    switch (actionSlot.getAction().toLowerCase()) {
                        case "next":
                            if (hasNext()) {
                                updateMenu(currentCategory, currentPage + 1, true);
                            }
                            break;
                        case "previous":
                            if (hasPrev()) {
                                updateMenu(currentCategory, currentPage - 1, true);
                            }
                            break;
                        default:
                            break;
                    }
                } else if (s instanceof CategorySlot) {
                    CategorySlot categorySlot = (CategorySlot) s;
                    updateMenu(categorySlot.getName(), currentPage, true);
                }
            }
        }
        return disable;
    }

    private class Category {

        private String categoryName;
        private Set<String> aliases;

        public Category(String categoryName) {
            this.categoryName = categoryName;
            this.aliases = new HashSet<>();
        }

        public void addAliasses(String... aliases) {
            List<String> lowerCase = new ArrayList<>();
            for (String s : aliases) {
                lowerCase.add(s.toLowerCase());
            }
            this.aliases.addAll(lowerCase);
        }

        public void addAliasses(Collection<String> aliases) {
            List<String> lowerCase = new ArrayList<>();
            for (String s : aliases) {
                lowerCase.add(s.toLowerCase());
            }
            this.aliases.addAll(lowerCase);
        }

        public boolean hasAlias(String alias) {
            return aliases.contains(alias);
        }

        public boolean matches(String categoryName) {
            if (categoryName.equals(categoryName.toLowerCase())) {
                return true;
            } else {
                return aliases.contains(categoryName.toLowerCase());
            }
        }

    }
    
    

}
