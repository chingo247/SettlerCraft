/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.plugin.shop;

import com.google.common.collect.Maps;
import static com.sc.plugin.shop.Menu.MENUSIZE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Chingo
 */
public class ItemShopCategoryMenu extends CategoryMenu {

    private final boolean endless;
    private final Map<String, ArrayList<MenuItemSlot>> items;
    private final Map<String, Session> visitors;

    /**
     * Default Constructor. Items will deplete and shop is not endless
     *
     * @param title The title of this shop
     */
    public ItemShopCategoryMenu(String title) {
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
    public ItemShopCategoryMenu(String title, boolean infinite, boolean endless) {
        this(UUID.randomUUID(), title, infinite, endless);
    }

    /**
     * Constructor
     *
     * @param id UUID must be provided
     * @param title The title of this shop
     * @param infinite If infinite items in this shop will/must never deplete Note: if infinite all
     * pick actions on this shop's inventory will be cancelled
     * @param endless wheter the shop should be endless(scrollable) or not
     *
     */
    public ItemShopCategoryMenu(UUID id, String title, boolean infinite, boolean endless) {
        this(id, title, new MenuCategorySlot(id, new ItemStack(Material.NETHER_STAR), title), infinite, endless);
    }

    public ItemShopCategoryMenu(UUID id, String title, MenuCategorySlot firstCategorySlot, boolean wontDeplete, boolean endless) {
        super(id, title, firstCategorySlot, 0, wontDeplete);
        this.items = Maps.newHashMap();
        this.visitors = Maps.newConcurrentMap();
        this.endless = endless;
        this.addCategory(firstCategorySlot);
    }

    public boolean addShopItem(ItemStack item, double price) {
        return addItem(item, price, getFIRST_CATEGORY().getName());
    }

    /**
     * Adds an item to the given category.
     *
     * @param item The item to add
     * @param price The price
     * @param category The category the item should be added to
     * @return true if category did exist and item succesfully added
     */
    public boolean addItem(ItemStack item, double price, String category) {
        MenuItemSlot ss = new MenuItemSlot(getId(), item, price);
        String c = category.toUpperCase();
        if (items.containsKey(c) && !isFull()) {
            if (getWontDeplete()) {
                item.setAmount(1);
            }
            return items.get(c).add(ss);
        } else {
            return false;
        }
    }

    /**
     * Sells the given item to the player Requires the player to have a bankaccount and to have
     * enough money to buy the item MenuSlotType
     *
     * @param player The player
     * @param item The item
     * @return true if item succesfully sold
     */
    public boolean sellItem(Player player, MenuItemSlot item) {
        if (!SCShopEconomy.getInstance().getEconomy().hasAccount(player.getName())) {
            onLeave(player);
            player.sendMessage(ChatColor.RED + "[" + getTitle() + "]: u dont have a bankaccount");
            return false;
        }
        if (SCShopEconomy.getInstance().getEconomy().has(player.getName(), item.getPrice() * item.getAmount())) {
            player.sendMessage(ChatColor.GOLD + "Bought " + item.getAmount() + " for " + item.getAmount() * item.getPrice());
            EconomyResponse ep = SCShopEconomy.getInstance().pay(player, item.getPrice() * item.getAmount());
            return true;
        } else {
            player.sendMessage("[" + getTitle() + "]: U can't afford that...");
            return false;
        }
    }

    private Inventory getItemTemplate() {
        Inventory in = Bukkit.createInventory(null, MENUSIZE, getTitle());
        for (int i = 0; i < MENUSIZE; i++) {
            if (isReserved(i) || isCategorySlot(i)) {
                in.setItem(i, getTemplate().getItem(i));
            }
        }
        return in;
    }

    @Override
    public void setTemplateInventory(Player player) {
        visitors.get(player.getName()).inventory.clear();
        Inventory inv = getItemTemplate();
        for (int i = 0; i < MENUSIZE; i++) {
            if (isReserved(i) || isCategorySlot(i)) {
                visitors.get(player.getName()).inventory.setItem(i, inv.getItem(i));
            }
        }
    }

    /**
     * TODO MULTILANGUAGE
     *
     * @return
     */
    public MenuButtonSlot nextButton() {
        return new MenuButtonSlot(getId(), new ItemStack(Material.BED_BLOCK), "Previous");
    }

    /**
     * TODO MULTILANGUAGE
     *
     * @return
     */
    public MenuButtonSlot prevButton() {
        return new MenuButtonSlot(getId(), new ItemStack(Material.BED_BLOCK), "Next");
    }

    private void fillInventory(Player player, List<MenuItemSlot> sss, boolean updateInventory) {
        Inventory inv = getItemTemplate();
        for (int i = getCategories().size(); i < MENUSIZE; i++) {
            if (visitors.get(player.getName()).lastIndex == sss.size()) {
                // NO MORE ITEMS
                break;
            } else if (visitors.get(player.getName()).lastIndex > 0 && i == getCategories().size()) {
                visitors.get(player.getName()).inventory.addItem(prevButton());
            } else if (i == MENUSIZE - 1) {
                visitors.get(player.getName()).inventory.addItem(nextButton());
            } else if (inv.getItem(i) == null || (inv.getItem(i) instanceof MenuSlot)) {
                MenuSlot ss = sss.get(visitors.get(player.getName()).lastIndex);
                ItemMeta meta = ss.getItemMeta();
//                List<String> lore
//                        = visitors.get(player.getName()).inventory.setItem(i,);
                visitors.get(player.getName()).lastIndex++;
            }
        }
        if (updateInventory) {
            player.updateInventory();
        }
    }

    /**
     * Change the category for this player, requires the player to be in the shop (onEnter() method
     * should have been called before this)
     *
     * @param player The player
     * @param category The new category
     */
    @Override
    public void changeCategory(Player player, String category) {
        if (!visitors.containsKey(player.getName())) {
            throw new IllegalStateException("Tried to change category without being in the shop");
        } else if (!category.equals(visitors.get(player.getName()).currentCategory)) { // otherwise, already at the right category, do nothing     
            System.out.println("Change category!");
            visitors.get(player.getName()).inventory.clear();
            visitors.get(player.getName()).currentPage = 0;
            visitors.get(player.getName()).currentCategory = category;
            setTitle(player);
            setTemplateInventory(player);
            List<MenuItemSlot> sss = getSlotsForCategory(category);
            fillInventory(player, sss, true);
        }
    }

    @Override
    public void nextPage(Player player) {
        if (!visitors.containsKey(player.getName())) {
            throw new IllegalStateException("Tried to change category without being in the shop");
        } else {
            List<MenuItemSlot> sss = getSlotsForCategory(visitors.get(player.getName()).currentCategory);
            if (visitors.get(player.getName()).lastIndex < sss.size()) {
                visitors.get(player.getName()).currentPage++;
                setTitle(player);
                setTemplateInventory(player);
                fillInventory(player, sss.subList(visitors.get(player.getName()).lastIndex, sss.size()), true);
            }
        }
    }

    @Override
    public void prevPage(Player player) {
        if (!visitors.containsKey(player.getName())) {
            throw new IllegalStateException("Tried to change category without being in the shop");
        } else {
            List<MenuItemSlot> sss = getSlotsForCategory(visitors.get(player.getName()).currentCategory);
            if (visitors.get(player.getName()).lastIndex > 0) {
                setTemplateInventory(player);
                sss = sss.subList(0, visitors.get(player.getName()).lastIndex);
                Collections.reverse(sss);
                fillInventory(player, sss, true);
            }
        }
    }

    public void setTitle(Player player) {
        Session session = visitors.get(player.getName());
        int pageNum = session.currentPage + 1; // page 0 => page 1
        String category = session.currentCategory;
        visitors.get(player.getName()).inventory = Bukkit.createInventory(null, MENUSIZE, getTitle() + " " + category + "[" + pageNum + "]");
    }

    /**
     * Adds a Category to this shop
     *
     * @param categorySlot The category slot
     * @return true if Category didn't exist in this shop
     */
    @Override
    public final boolean addCategory(MenuCategorySlot categorySlot) {
        String n = categorySlot.getName().toLowerCase();
        if (hasCategory(n)) {
            return false;
        } else {
            items.put(n, new ArrayList<MenuItemSlot>()); // Add a Set for this category
            addCategorySlot(categorySlot); // Add to templateInventory/view!
            return true;
        }
    }

        /**
     * Returns all the items that belong to the given category
     *
     * @param category The category
     * @return List of items or null if category didnt exist
     */
    @Override
    public final List<MenuItemSlot> getSlotsForCategory(String category) {
        if (category.equals(getFIRST_CATEGORY().getName())) {
            Set<MenuItemSlot> is = new TreeSet<>();
            for (ArrayList<MenuItemSlot> i : items.values()) {
                is.addAll(i);
            }
            return new ArrayList<>(is);
        } else {
            return new ArrayList<>(items.get(category));
        }

    }
    
    @Override
    public boolean isFull() {
        if (endless == true) {
            return false;
        } else {
            int emptyReservedSpaces = (getCategories().size() % getReserved().size());
            int amountOfItems = items.get(getFIRST_CATEGORY().getName()).size();
            return (amountOfItems + getCategories().size() + emptyReservedSpaces) == MENUSIZE;
        }
    }

    /**
     * Adds the player as customer for this shop, player will onLeave the shop when the player
     * closes the inventory Will throw IllegalStateException if player was already inside of the
     * shop. If The player doesnt have a bankaccount, the inventory won't be opened and the player
     * won't be added to the shop's customers the player will be send a response message.
     *
     * @param player The player
     */
    @Override
    public void onEnter(Player player) {
        if (!SCShopEconomy.getInstance().getEconomy().hasAccount(player.getName())) {
            player.sendMessage(ChatColor.RED + "[" + getTitle() + "]: U don't have a bankaccount!");
        } else if (!visitors.containsKey(player.getName())) {
            System.out.println("Open inventory!");
            Inventory inv = getTemplate();
            visitors.put(player.getName(), new Session(0, inv, getFIRST_CATEGORY().getName()));
            List<MenuItemSlot> sss = getSlotsForCategory(getFIRST_CATEGORY().getName());
            fillInventory(player, sss, false);
            player.openInventory(inv);
        } else {
            throw new IllegalStateException("Player already in shop");
        }
    }

    @Override
    public void onLeave(Player player) {
        if (visitors.containsKey(player.getName())) {
            visitors.remove(player.getName());
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
