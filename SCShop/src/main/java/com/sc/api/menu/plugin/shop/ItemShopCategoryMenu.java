/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.menu.plugin.shop;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
public class ItemShopCategoryMenu extends CategoryMenu {

    private final boolean endless;
    private final Map<String, List<MenuItemSlot>> items;
    private final Map<String, Session> visitors;
    private boolean chooseDefaultCategory = false;
    private String defaultCategory;

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
     * @param wontDeplete If infinite items in this shop will/must never deplete Note: if infinite
     * all pick actions on this shop's inventory will be cancelled
     * @param endless wheter the shop should be endless(scrollable) or not
     *
     */
    public ItemShopCategoryMenu(UUID id, String title, boolean wontDeplete, boolean endless) {
        super(id, title, wontDeplete);
        this.endless = endless;
        this.items = Maps.newHashMap();
        this.visitors = Maps.newHashMap();
    }
    
    public boolean hasVisitor(Player player) {
        return visitors.containsKey(player.getName());
    }

    public void setChooseDefaultCategory(boolean chooseDefaultCategory) {
        this.chooseDefaultCategory = chooseDefaultCategory;
    }

    public boolean setDefaultCategory(String category) {
        if (hasCategory(category)) {
            this.defaultCategory = category;
            return true;
        }
        return false;
    }

    public String getDefaultCategory() {
        return defaultCategory;
    }

    public boolean hasDefaultCategory() {
        return defaultCategory != null;
    }

    /**
     * Adds an item to the given category.
     *
     * @param item The item to add
     * @param itemName
     * @param price The price
     * @param category The category the item should be added to
     * @return true if category did exist and item succesfully added
     */
    public boolean addItem(ItemStack item, String itemName, double price, String category) {
        if (!hasCategory(category) && hasDefaultCategory()) {
            category = defaultCategory;
        } else {

            category = getCategoryName(category); // Also checks for aliases
        }

        MenuItemSlot ss = new MenuItemSlot(id, item, price, itemName);
        if (wontDeplete) {
            item.setAmount(1);
        }
        if (items.get(category) == null) {
            items.put(category, new ArrayList<MenuItemSlot>());
        }
        items.get(category).add(ss);

        return false;
    }

    public Inventory buildTemplate(String invTitle) {
        Inventory inv = Bukkit.createInventory(null, MENUSIZE, invTitle);
        Map<Integer, MenuSlot> slots = getSlots();
        for (int i = 0; i < MENUSIZE; i++) {
            if (slots.get(i) != null) {
//                if(slots.get(i) instanceof MenuCategorySlot) {
                    inv.setItem(i, slots.get(i));
//                } else if(slots.get(i) instanceof MenuActionSlot) {
//                    inv.setItem(i, (MenuActionSlot) slots.get(i));
//                } else if(slots.get(i) instanceof MenuSkillSlot) {
//                    inv.setItem(i, (MenuSkillSlot) slots.get(i));
//                } else {
//                    System.out.println("LOL");
//                }
            }
        }
        return inv;
    }



    private void fillInventory(Player player, Inventory inv, List<MenuItemSlot> miss) {
        Map<Integer, MenuSlot> slots = getSlots();
        List<MenuItemSlot> ms = miss.subList(visitors.get(player.getName()).lastIndex, miss.size());
        Iterator<MenuItemSlot> it = ms.iterator();

        for (int i = 0; i < MENUSIZE; i++) {
//            System.out.println("M=" + i);
            if(i == 0 && visitors.get(player.getName()).lastIndex > 0) {
                inv.setItem(i, new MenuActionSlot(id, new ItemStack(Material.BED_BLOCK), "Previous"));
                break;
            }
            
            if (i == (MENUSIZE - 1)) {
                inv.setItem(i, new MenuActionSlot(id, new ItemStack(Material.BED_BLOCK), "Next"));
                break;
            }
            if (!isLocked(i) && it.hasNext() && slots.get(i) == null) {
                inv.setItem(i, it.next());
                visitors.get(player.getName()).lastIndex++;
            }
        }

    }

    /**
     * Returns all the items of this shop
     *
     * @return All the items of this shop
     */
    public List<MenuItemSlot> getItems() {
        List<MenuItemSlot> sss = new ArrayList<>();
        for (List<MenuItemSlot> is : items.values()) {
            sss.addAll(is);
        }
        return sss;
    }

    public List<MenuItemSlot> getItems(String category) {
        return items.get(category);
    }
    
    private void getNextItems(Player player) {
        visitors.get(player.getName()).currentPage++;
        visitors.get(player.getName()).inventory.clear();
        visitors.get(player.getName()).inventory = Bukkit.createInventory(
                null, 
                MENUSIZE, 
                title + ":" + defaultCategory + "[" + visitors.get(player.getName()).currentPage + "]"
        );
        List<MenuItemSlot> ims = getItems();
        if(visitors.get(player.getName()).currentCategory.equals(defaultCategory)) {
            fillInventory(player, visitors.get(player.getName()).inventory, ims
                    .subList(
                            visitors.get(player.getName()).lastIndex, 
                            Math.min(ims.size(), visitors.get(player.getName()).lastIndex + MENUSIZE)
                    )
            );
        }
    }
    
    private void getPreviousItems(Player player) {
        visitors.get(player.getName()).currentPage--;
        visitors.get(player.getName()).inventory.clear();
        visitors.get(player.getName()).inventory = Bukkit.createInventory(
                null, 
                MENUSIZE, 
                title + ":" + defaultCategory + "[" + visitors.get(player.getName()).currentPage + "]"
        );
        List<MenuItemSlot> ims = getItems();
        if(visitors.get(player.getName()).currentCategory.equals(defaultCategory)) {
            fillInventory(player, visitors.get(player.getName()).inventory, ims
                    .subList(
                            Math.max(0, visitors.get(player.getName()).lastIndex - MENUSIZE), 
                            Math.min(ims.size(), visitors.get(player.getName()).lastIndex + MENUSIZE)
                    )
            );
        }
    }

    @Override
    public void onEnter(Player player) {
        if (!visitors.containsKey(player.getName())) {
            System.out.println("Open inventory!");
            Inventory inv = buildTemplate(title + " : " + defaultCategory + "[" + 0 + "]");
            visitors.put(player.getName(), new Session(0, inv, null));
            if (chooseDefaultCategory) {
                fillInventory(player, inv, getItems());
                visitors.get(player.getName()).currentCategory = defaultCategory;
            }
            visitors.get(player.getName()).inventory = inv;
            player.openInventory(inv);
        } else {
            throw new IllegalStateException("Player already in shop");
        }
    }

    public void onLeave(Player player) {
        if (visitors.containsKey(player.getName())) {
            visitors.remove(player.getName());
        }
    }

    @Override
    public void onCategoryClicked(MenuCategorySlot slot, Player whoClicked) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onItemClicked(MenuItemSlot slot, Player whoClicked) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onActionClicked(MenuActionSlot slot, Player whoClicked) {
        System.out.println("Action!" + slot.getName());
        switch(slot.getName().toLowerCase()) {
            case "next" : getNextItems(whoClicked);
                break;
            case "previous": getPreviousItems(whoClicked);
                break;
            default:break;
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
