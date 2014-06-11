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
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Chingo
 */
public class ShopCategoryMenu extends CategoryMenu {

    private final boolean endless;
    private final Map<String, List<MenuSlot>> items;
    private final Map<String, Session> visitors;
    private boolean chooseDefaultCategory = false;
    private String defaultCategory;
    private final HashSet<String> accepts = new HashSet<>();;

    /**
     * Constructor
     *
     * @param title The title of this shop
     * @param wontDeplete If infinite items in this shop will/must never deplete Note: if infinite
     * all pick actions on this shop's inventory will be cancelled
     * @param endless wheter the shop should be endless(scrollable) or not
     */
    public ShopCategoryMenu(String title, boolean wontDeplete, boolean endless) {
        super(title, wontDeplete);
        this.endless = endless;
        this.items = Maps.newHashMap();
        this.visitors = Collections.synchronizedMap(new HashMap<String, Session>());
    }

    public boolean accepts(String type) {
        return accepts.add(type.toLowerCase());
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

    public void addItem(MenuSlot slot, String category) {
        addItem(slot, category, 0f);
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
        MenuManager.getInstance().putVisitor(player);
//        System.out.println("REMOVE THE CREDIT BONUS!!!!!");
//        SCVaultEconomyUtil.getInstance().getEconomy().depositPlayer(player.getName(), 100000);
        player.sendMessage(ChatColor.YELLOW + "[" + title + "]: " + ChatColor.RESET + "Hello " + ChatColor.GREEN + player.getName() + ChatColor.RESET + "!");
        double balance = SCVaultEconomyUtil.getInstance().getEconomy().getBalance(player.getName());
        if (balance > 0) {
            player.sendMessage(ChatColor.YELLOW + "[" + title + "]: " + ChatColor.RESET + " Your balance is " + ChatColor.GOLD + balance);
        } else {
            player.sendMessage(ChatColor.YELLOW + "[" + title + "]: " + ChatColor.RESET + " Your balance is " + ChatColor.RED + balance);
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
            MenuManager.getInstance().removeVisitor(player);
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

        double price = slot.getPrice();

        if (price > 0) {
            EconomyResponse er = SCVaultEconomyUtil.getInstance().getEconomy().withdrawPlayer(whoClicked.getName(), price);
            if (er.transactionSuccess()) {
                whoClicked.sendMessage(ChatColor.YELLOW + "[" + getTitle() + "]: "
                        + ChatColor.BLUE + slot.getName()
                        + ChatColor.RESET + " has been added to your inventory");
                double balance = er.balance;
                if (balance > 0) {
                    whoClicked.sendMessage("Your new balance: " + ChatColor.GOLD + balance);
                } else {
                    //FIXME Is this even possible?
                    whoClicked.sendMessage("Your new balance: " + ChatColor.RED + balance);
                }

                ItemStack stack = slot.getItemStack().clone();
                priceToValue(stack, price);
                whoClicked.getInventory().addItem(stack);
            } else {
                whoClicked.sendMessage(ChatColor.YELLOW + "[" + title + "]: " + ChatColor.RED + " Transaction failed, " + er.errorMessage);
            }
        } else {
            ItemStack stack = slot.getItemStack().clone();
            priceToValue(stack, price);
            whoClicked.getInventory().addItem(stack);
            whoClicked.sendMessage(ChatColor.YELLOW + "[" + getTitle() + "]: "
                    + ChatColor.BLUE + slot.getName()
                    + ChatColor.RESET + " has been added to your inventory");
        }

    }
    
    
    private void priceToValue(final ItemStack stack, double value) {
        
        List<String> lore = stack.getItemMeta().getLore();
        if (lore != null) {
            for (int i = 0; i < lore.size(); i++) {
                if(lore.get(i).contains("Price")){
                    lore.set(i, "Value: " + ChatColor.GOLD + value);
                }
            }
        }
        ItemMeta meta = stack.getItemMeta();
        meta.setLore(lore);
        stack.setItemMeta(meta);

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

    public boolean sellItem(final ItemStack item, Player player) {
        
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            player.sendMessage(ChatColor.RED + "Vault is not enabled");
            return false;
        }

        if (SCVaultEconomyUtil.getInstance().getEconomy() == null) {
            player.sendMessage(ChatColor.RED + "Economy not found");
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        String type = null;
        Double price = null;
        for (String s : lore) {
            if (s.contains("Value")) {
                s = s.substring(s.indexOf(":") + 1);
                s = ChatColor.stripColor(s);
                price = Double.parseDouble(s.trim());
            }

            if (s.contains("Type")) {
                s = s.substring(s.indexOf(":") + 1);
                s = ChatColor.stripColor(s);
                type = s.trim().toLowerCase();
            }
        }
        if (type == null || !accepts.contains(type)) {
            player.sendMessage(ChatColor.RED + "[" + title + "]: Sorry, we don't accept your item");
            return false;
        }

        if (price == null) {
            player.sendMessage(ChatColor.YELLOW + "[" + title + "]: Your item doesn't have any value!");
            return false;
        }

        Economy economy = SCVaultEconomyUtil.getInstance().getEconomy();
        economy.depositPlayer(player.getName(), price * item.getAmount());
        player.sendMessage(new String[]{
            ChatColor.YELLOW + "[" + title + "]: " + ChatColor.RESET + "You have been refunded " + ChatColor.GOLD + (price * item.getAmount()),
            ChatColor.BLUE + item.getItemMeta().getDisplayName() + ChatColor.RESET + " has been removed from your inventory"
        });

        return true;

    }

    private class Session {

        private String currentCategory;
        private Inventory inventory;
        private int currentPage = 0;
        private Map<Integer, List<MenuSlot>> pages;

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

}
