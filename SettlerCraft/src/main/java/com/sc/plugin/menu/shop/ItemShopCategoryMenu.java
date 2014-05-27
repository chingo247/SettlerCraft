/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin.menu.shop;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.sc.plugin.menu.CategoryMenu;
import com.sc.plugin.menu.MenuSlot;
import com.sc.plugin.menu.MenuSlot.MenuSlotType;
import com.sc.plugin.menu.SCVaultEconomyUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class ItemShopCategoryMenu extends CategoryMenu {

    private final boolean endless;
    private final Map<String, List<MenuSlot>> items;
    private final Map<String, Session> visitors;
    private boolean chooseDefaultCategory = false;
    private String defaultCategory;
    private ShopCallback callback;

    /**
     * Constructor
     *
     * @param title The title of this shop
     * @param wontDeplete If infinite items in this shop will/must never deplete Note: if infinite
     * all pick actions on this shop's inventory will be cancelled
     * @param endless wheter the shop should be endless(scrollable) or not
     * @param callback
     *
     */
    public ItemShopCategoryMenu(String title, boolean wontDeplete, boolean endless, ShopCallback callback) {
        super(title, wontDeplete);
        this.endless = endless;
        this.items = Maps.newHashMap();
        this.visitors = Maps.newHashMap();
        this.callback = callback;
    }

    /**
     * Constructor
     *
     * @param title The title of this shop
     * @param wontDeplete If infinite items in this shop will/must never deplete Note: if infinite
     * all pick actions on this shop's inventory will be cancelled
     * @param endless wheter the shop should be endless(scrollable) or not
     *
     */
    public ItemShopCategoryMenu(String title, boolean wontDeplete, boolean endless) {
        this(title, wontDeplete, endless, null);
    }

    /**
     * Checks if this shop has the specified player as a visitor (player has the shops menu opened)
     *
     * @param player The player
     * @return true if the player is currently shopping in this menu
     */
    public boolean hasVisitor(Player player) {
        return visitors.containsKey(player.getName());
    }

    /**
     * Whether this shop has default category that should be opened/loaded when a player enters this
     * shop
     *
     * @param chooseDefaultCategory set True if this shop should open it
     */
    public void setChooseDefaultCategory(boolean chooseDefaultCategory) {
        this.chooseDefaultCategory = chooseDefaultCategory;
    }

    /**
     * The default category that should be picked when an item doesn't belong to any category
     *
     * @param category The name of the default category
     */
    public void setDefaultCategory(String category) {
        if (hasCategory(category)) {
            this.defaultCategory = category;
        } else {
            throw new IllegalArgumentException("Shop doesnt have a category called: " + category);
        }
    }

    /**
     * Gets the default category that should be opened/loaded when a player enters this shop
     *
     * @return The default category or null if this shop doesnt have one
     */
    public String getDefaultCategory() {
        return defaultCategory;
    }

    /**
     * Checks wheter this shop has a default category
     *
     * @return True if this shop has a default category, otherwise false
     */
    public boolean hasDefaultCategory() {
        return defaultCategory != null;
    }

    public void addItem(ItemStack item, String itemName, String category) {
        addItem(item, itemName, 0, category);
    }
    
    public void addItem(MenuSlot slot, String category, double price) {
        Preconditions.checkArgument(slot.getType() == MenuSlotType.ITEM);
        category = getCategoryName(category);
//        System.out.println("Category: " + category);

        if (category == null && hasDefaultCategory()) {
            category = defaultCategory;
        } else if (category == null) {
            throw new AssertionError("Unknown action for category: " + category);
        }

        if (wontDeplete) {
            slot.getItemStack().setAmount(1);
        }
        if (items.get(category) == null) {
            items.put(category, new ArrayList<MenuSlot>());
        }
        if (price > 0) {
            slot.setPrice(price);
        }
        items.get(category).add(slot);
    }

    /**
     * Adds an item to the given category.
     *
     * @param item The item to add
     * @param itemName
     * @param price The price
     * @param category The category the item should be added to
     */
    public void addItem(ItemStack item, String itemName, double price, String category) {
//        System.out.println("Has category: " + category + " " + hasCategory(category));

        MenuSlot ms = new MenuSlot(item, itemName, MenuSlotType.ITEM);
        ms.setPrice(price);
        addItem(ms, category, price);
    }

    /**
     * Clears/Removes all slots of type item
     */
    public void clearItemSlots() {
        MenuSlot[] mss = getMenuSlots();
        for (int i = 0; i < MENUSIZE; i++) {
            if (mss[i] != null && mss[i].getType() == MenuSlotType.ITEM) {
//                System.out.println("TYPE: " + mss[i].getType() + " : " + mss[i].getName());
                setSlot(i, null);
//                System.out.println("TYPE: " + getMenuSlots()[i]);
            }

        }
    }

    /**
     * Returns all the items of this shop
     *
     * @return All the items of this shop
     */
    public List<MenuSlot> getItems() {
        List<MenuSlot> sss = new ArrayList<>();
        for (List<MenuSlot> is : items.values()) {
            sss.addAll(is);
        }
//        System.out.println("TOTAL: " + sss.size());
        return sss;
    }

    /**
     * Gets the items from a specific category
     *
     * @param category The category
     * @return A list of items from a specific category
     */
    public List<MenuSlot> getItems(String category) {
        return items.get(category);
    }

    private void getNextItems(Player player) {
        if (visitors.get(player.getName()).hasNext()) {
            visitors.get(player.getName()).currentPage++;  // Called before build!
            clearAndBuildTemplate(player, visitors.get(player.getName()).inventory);

            List<MenuSlot> is = visitors.get(player.getName()).pages.get(visitors.get(player.getName()).currentPage);
            Collections.sort(is, ALPHABETICAL_ORDER);
            Iterator<MenuSlot> it = is.iterator();
            for (int i = 0; i < MENUSIZE; i++) {
                if (!it.hasNext()) {
                    break;
                }
                if (getMenuSlots()[i] == null && !isLocked(i)) {
                    MenuSlot ms = it.next();
                    setSlot(i, ms);
                    visitors.get(player.getName()).inventory.setItem(i, ms.getItemStack());
                }
            }
        }
    }

    private void getPreviousItems(Player player) {

        if (visitors.get(player.getName()).hasPrev()) {
            visitors.get(player.getName()).currentPage--; // Called before build!
            clearAndBuildTemplate(player, visitors.get(player.getName()).inventory);

            List<MenuSlot> is = visitors.get(player.getName()).pages.get(visitors.get(player.getName()).currentPage);
            Collections.sort(is, ALPHABETICAL_ORDER);
            Iterator<MenuSlot> it = is.iterator();
            for (int i = 0; i < MENUSIZE; i++) {
                if (!it.hasNext()) {
                    break;
                }
                if (getMenuSlots()[i] == null && !isLocked(i)) {
                    MenuSlot ms = it.next();
                    setSlot(i, ms);
                    visitors.get(player.getName()).inventory.setItem(i, ms.getItemStack());
                }
            }
        }
    }

    private void clearAndBuildTemplate(Player player, final Inventory inventory) {
        inventory.clear();
        clearItemSlots();
        int currentPage = visitors.get(player.getName()).currentPage;
        MenuSlot[] dummyInventorySlot = getMenuSlots();
        for (int i = 0; i < MENUSIZE; i++) {
            MenuSlot ms = dummyInventorySlot[i];
            if (ms != null) {

                if (ms.getType() == MenuSlotType.ACTION) {
                    if (ms.getName().equals("Previous")) {
                        if (visitors.get(player.getName()).hasPrev()) {
                            ms.setData("To Page", /*(*/ currentPage /*- 1) + 1*/, ChatColor.BLUE);      // USERS DON'T COUNT FROM 0 BUT 1
                            ms.setData("Current Page", currentPage + 1, ChatColor.BLUE);
                        } else {
                            ms.setData("To Page", " - ", ChatColor.BLUE);
                            ms.setData("Current Page", currentPage + 1, ChatColor.BLUE);
                        }
                    } else if (ms.getName().equals("Next")) {
                        if (visitors.get(player.getName()).hasNext()) {
                            ms.setData("To Page", (currentPage + 1) + 1, ChatColor.BLUE);
                            ms.setData("Current Page", currentPage + 1, ChatColor.BLUE);
                        } else {
                            ms.setData("To Page", " - ", ChatColor.BLUE);
                            ms.setData("Current Page", currentPage + 1, ChatColor.BLUE);
                        }
                    }
                }
                // Adds the category, button, anything thats not an item
                inventory.setItem(i, ms.getItemStack());
            }
        }
    }

    @Override
    public void onEnter(Player player) {
        onEnter(player, false);
    }

    public void onEnter(Player player, boolean getsForFree) {
        System.out.println("REMOVE THE CREDIT BONUS!!!!!");
        SCVaultEconomyUtil.getInstance().getEconomy().depositPlayer(player.getName(), 100000);
        player.sendMessage(ChatColor.YELLOW + "[" + title + "]: Hello " + player.getName() + "!");
        double balance = SCVaultEconomyUtil.getInstance().getEconomy().getBalance(player.getName());
        if (balance > 0) {
            player.sendMessage(ChatColor.YELLOW + "[" + title + "]: Your balance is " + ChatColor.GREEN + balance);
        } else {
            player.sendMessage(ChatColor.YELLOW + "[" + title + "]: Your balance is " + ChatColor.RED + balance);
        }

        if (visitors.containsKey(player.getName())) {
            throw new IllegalStateException("player already in shop");
        } else {
//            System.out.println("Open inventory!");

            // Create a session for this player
            Inventory inv = Bukkit.createInventory(null, MENUSIZE, title);
            visitors.put(player.getName(), new Session(0, inv, null));

            if (chooseDefaultCategory) {
                visitors.get(player.getName()).currentCategory = defaultCategory;
                visitors.get(player.getName()).setPages(player, defaultCategory);
                List<MenuSlot> msItems = visitors.get(player.getName()).pages.get(visitors.get(player.getName()).currentPage);
                Iterator<MenuSlot> it = msItems.iterator();

                // Build it's session inventory with the needed categories/actions
                clearAndBuildTemplate(player, inv);

                for (int i = 0; i < MENUSIZE; i++) {
                    if (!it.hasNext()) {
                        break;
                    }
                    if (getMenuSlots()[i] == null && !isLocked(i)) {
                        MenuSlot ms = it.next();
                        setSlot(i, ms);
                        visitors.get(player.getName()).inventory.setItem(i, ms.getItemStack());
                    }
                }

                visitors.get(player.getName()).inventory = inv;
                player.openInventory(inv);
            } else {
                // Build it's session inventory with the needed categories/actions
                clearAndBuildTemplate(player, inv);
                visitors.get(player.getName()).inventory = inv;
                player.openInventory(inv);
            }

        }
    }

    public void playerLeave(Player player) {
        if (visitors.containsKey(player.getName())) {
            visitors.remove(player.getName());
        }
    }

    public void onMenuSlotClicked(MenuSlot slot, Player whoClicked) {
        switch (slot.getType()) {
            case ACTION:
                onActionClicked(slot, whoClicked);
                break;
            case CATEGORY:
                onCategoryClicked(slot.getName(), whoClicked);
                break;
            case ITEM:
                onItemClicked(slot, whoClicked);
                break;
            default:
                // Not intrested...
                break;
        }
    }

    @Override
    public void onCategoryClicked(String categoryName, Player whoClicked) {
        if (!hasCategory(categoryName)) {
            throw new AssertionError("Category doesnt exist! This shouldn't happen!");
        }
//        System.out.println("Category Click!");
        if (!visitors.get(whoClicked.getName()).currentCategory.equals(categoryName)) {
            visitors.get(whoClicked.getName()).currentCategory = categoryName;
            visitors.get(whoClicked.getName()).currentPage = 0;
            visitors.get(whoClicked.getName()).setPages(whoClicked, categoryName);

            // Build it's session inventory with the needed categories/actions
            Inventory inv = visitors.get(whoClicked.getName()).inventory;
            List<MenuSlot> msItems = visitors.get(whoClicked.getName()).pages.get(visitors.get(whoClicked.getName()).currentPage);

            if (msItems != null) {
                Collections.sort(msItems, ALPHABETICAL_ORDER);
                Iterator<MenuSlot> it = msItems.iterator();

                clearAndBuildTemplate(whoClicked, inv);

                for (int i = 0; i < MENUSIZE; i++) {
                    if (!it.hasNext()) {
                        break;
                    }
                    if (getMenuSlots()[i] == null && !isLocked(i)) {
                        MenuSlot ms = it.next();
                        setSlot(i, ms);
                        inv.setItem(i, ms.getItemStack());
                    }
                }
            } else {
                clearAndBuildTemplate(whoClicked, inv);
            }
            visitors.get(whoClicked.getName()).inventory = inv;
            whoClicked.updateInventory();
        }
    }

    public void onItemClicked(MenuSlot slot, Player whoClicked) {

        if (!SCVaultEconomyUtil.getInstance().getEconomy().hasAccount(whoClicked.getName())) {
            whoClicked.closeInventory();
            playerLeave(whoClicked);
            whoClicked.sendMessage(ChatColor.RED + "You don't have a bankaccount");
            return;
        }

        double price = slot.getPrice();

        EconomyResponse er = SCVaultEconomyUtil.getInstance().getEconomy().withdrawPlayer(whoClicked.getName(), price);
        if (er.transactionSuccess()) {
            whoClicked.sendMessage(ChatColor.YELLOW + "[" + getTitle() + "]: "
                    + ChatColor.BLUE + slot.getName()
                    + ChatColor.YELLOW + " has been added to your inventory");
            double balance = er.balance;
            if (balance > 0) {
                whoClicked.sendMessage(ChatColor.YELLOW + "Your new balance: " + ChatColor.GREEN + balance);
            } else {
                whoClicked.sendMessage(ChatColor.YELLOW + "Your new balance: " + ChatColor.RED + balance);
            }

            ItemStack stack = slot.getItemStack().clone();
            if (callback != null) {
                callback.onItemSold(whoClicked, stack, price);
            }
            whoClicked.getInventory().addItem(stack);
        } else {
            whoClicked.sendMessage(ChatColor.RED + "[" + title + "]: Transaction failed, " + er.errorMessage);
        }

    }

    public void onActionClicked(MenuSlot slot, Player whoClicked) {
//        System.out.println("Action!" + slot.getName());
        switch (slot.getName().toLowerCase()) {
            case "next":
                getNextItems(whoClicked);
                break;
            case "previous":
                getPreviousItems(whoClicked);
                break;
            default:
                break;
        }
    }

    private final Comparator<MenuSlot> ALPHABETICAL_ORDER = new Comparator<MenuSlot>() {
        @Override
        public int compare(MenuSlot str1, MenuSlot str2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(str1.getName(), str2.getName());
            if (res == 0) {
                res = str1.getName().compareTo(str2.getName());
            }
            return res;
        }
    };

    private class Session {

        private String currentCategory;
        private Inventory inventory;
        private int currentPage = 0;
        private Map<Integer, List<MenuSlot>> pages;
        private MenuSlot playerInfo;

        public Session(int currentPage, Inventory inventory, String currentCategory) {
            this.inventory = inventory;
            this.currentCategory = currentCategory;
            this.pages = Maps.newHashMap();
        }

        public boolean hasNext() {
            return pages.get(currentPage + 1) != null && !pages.get(currentPage + 1).isEmpty();
        }

        public boolean hasPrev() {
            return currentPage > 0;
        }

        private int getFreeSlots() {
            int slots = 0;
            for (int i = 0; i < getMenuSlots().length; i++) {
                if (getMenuSlots()[i] == null) {
                    slots++;
                }
            }
            return slots;
        }

        public void setPages(Player player, String category) {
            this.currentPage = 0;
            this.pages.clear();
            if (category.equalsIgnoreCase(defaultCategory)) {
                clearItemSlots();
                List<MenuSlot> is = getItems();
                if (is == null) {
                    return;
                }
                Collections.sort(is, ALPHABETICAL_ORDER);
                final int max = getFreeSlots();
//                System.out.println("max = " + max);
                int current = 0;
                for (int i = 0; i < is.size(); i++) {
                    if (pages.get(current) == null) {
                        pages.put(current, new ArrayList<MenuSlot>(max));
                    }
                    if (pages.get(current).size() == max) {
                        current++;
                        pages.put(current, new ArrayList<MenuSlot>(max));
                    }
                    pages.get(current).add(is.get(i));
//                    System.out.println("item: " + i);
                }
            } else {
                clearItemSlots();
//                System.out.println("Category: " + getCategoryName(category));
                List<MenuSlot> is = getItems(getCategoryName(category));

                if (is == null) {
                    return;
                }
                Collections.sort(is, ALPHABETICAL_ORDER);
                final int max = getFreeSlots();
//                System.out.println("max = " + max);
                int current = 0;
                for (int i = 0; i < is.size(); i++) {
                    if (pages.get(current) == null) {
                        pages.put(current, new ArrayList<MenuSlot>(max));
                    }
                    if (pages.get(current).size() == max) {
                        current++;
                        pages.put(current, new ArrayList<MenuSlot>(max));
                    }
                    pages.get(current).add(is.get(i));
//                    System.out.println("item: " + i);
                }
            }
        }

    }

    public interface ShopCallback {

        void onItemSold(final Player buyer, final ItemStack stack, final double price);

    }
}
