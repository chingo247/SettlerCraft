/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.menu.plugin.shop;

import com.google.common.collect.Maps;
import com.sc.api.menu.plugin.shop.MenuSlot.MenuSlotType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
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
    private final Map<String, List<MenuSlot>> items;
    private final Map<String, Session> visitors;
    private boolean chooseDefaultCategory = false;
    private String defaultCategory;

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
        super(title, wontDeplete);
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
     */
    public void addItem(ItemStack item, String itemName, double price, String category) {
        if (!hasCategory(category) && hasDefaultCategory()) {
            category = defaultCategory;
        } else {

            category = getCategoryName(category); // Also checks for aliases
        }

        MenuSlot ms = new MenuSlot(item, itemName, MenuSlotType.ITEM);
        if (wontDeplete) {
            item.setAmount(1);
        }
        if (items.get(category) == null) {
            items.put(category, new ArrayList<MenuSlot>());
        }
        if (price > 0) {
            ms.setData("Price", price);
        }
        items.get(category).add(ms);
    }
    


    private void clearItemSlots() {
        for (int i = 0; i < MENUSIZE; i++) {
            if (slots.get(i) != null && slots.get(i).getType() == MenuSlotType.ITEM) {
                slots.remove(i);
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
        System.out.println("TOTAL: " + sss.size());
        return sss;
    }

    public List<MenuSlot> getItems(String category) {
        return items.get(category);
    }

    private void getNextItems(Player player) {
        System.out.println("CURPAGE: " + visitors.get(player.getName()).currentPage);
        if (visitors.get(player.getName()).hasNext()) {
            clearAndBuildTemplate(player, title + " : "
                    + visitors.get(player.getName()).currentCategory
                    + "[" + visitors.get(player.getName()).currentPage
                    + "]", visitors.get(player.getName()).inventory);
            System.out.println("HAS NEXT");
            visitors.get(player.getName()).currentPage++;
            System.out.println("NEXT PAGE: " + visitors.get(player.getName()).currentPage);
            List<MenuSlot> is = visitors.get(player.getName()).pages.get(visitors.get(player.getName()).currentPage);
            System.out.println("AMOUNT: " + is.size());
            Iterator<MenuSlot> it = is.iterator();
            for (int i = 0; i < MENUSIZE; i++) {
                if(!it.hasNext()) break;
                if (slots.get(i) == null && !isLocked(i)) {
                    MenuSlot ms = it.next();
                    put(i, ms);
                    visitors.get(player.getName()).inventory.setItem(i, ms.getItemStack());
                }
            }
        }
    }

    private void getPreviousItems(Player player) {
        System.out.println("CURPAGE: " + visitors.get(player.getName()).currentPage);
        if (visitors.get(player.getName()).hasPrev()) {
            clearAndBuildTemplate(player, title + " : "
                    + visitors.get(player.getName()).currentCategory
                    + "[" + visitors.get(player.getName()).currentPage
                    + "]", visitors.get(player.getName()).inventory);
            visitors.get(player.getName()).currentPage--;
            List<MenuSlot> is = visitors.get(player.getName()).pages.get(visitors.get(player.getName()).currentPage);
            Iterator<MenuSlot> it = is.iterator();
            for (int i = 0; i < MENUSIZE; i++) {
                if(!it.hasNext()) break;
                if (slots.get(i) == null && !isLocked(i)) {
                    MenuSlot ms = it.next();
                    put(i, ms);
                    visitors.get(player.getName()).inventory.setItem(i, ms.getItemStack());
                }
            }
        }
    }
    
    public Inventory clearAndBuildTemplate(Player player, String invTitle, final Inventory inventory) {
        inventory.clear();
        clearItemSlots();
        int currentPage = visitors.get(player.getName()).currentPage;
        Map<Integer, MenuSlot> ss = getSlots();
        for (int i = 0; i < MENUSIZE; i++) {
            MenuSlot ms = ss.get(i);
            if (ms != null) {
                if(ms.getType() == MenuSlotType.ACTION) {
                    
                    if(ms.getName().equals("Previous")) {
                        ItemMeta meta = ms.getItemStack().getItemMeta();
                        List<String> lore = new ArrayList<>();
                        if(currentPage > 0) {
                            lore.add(String.valueOf(currentPage - 1));
                        } else {
                            lore.add("-");
                        }
                        meta.setLore(lore);
                        ms.getItemStack().setItemMeta(meta);
                    } else if(ms.getName().equals("Next")){
                        ItemMeta meta = ms.getItemStack().getItemMeta();
                        List<String> lore = new ArrayList<>();
                        lore.add(String.valueOf(currentPage + 1));
                        meta.setLore(lore);
                        ms.getItemStack().setItemMeta(meta);
                    }
                }
                inventory.setItem(i, ms.getItemStack());
            }
        }
        return inventory;
    }

    @Override
    public void onEnter(Player player) {
        if (!visitors.containsKey(player.getName())) {
            System.out.println("Open inventory!");
            Inventory inv = Bukkit.createInventory(null, MENUSIZE, title);
            visitors.put(player.getName(), new Session(0, inv, null));
            visitors.get(player.getName()).setPages(player, defaultCategory);
            clearAndBuildTemplate(player, title + " : " + defaultCategory + "[" + 0 + "]", inv);
            if (chooseDefaultCategory) {
                List<MenuSlot> is = visitors.get(player.getName()).pages.get(visitors.get(player.getName()).currentPage);
                System.out.println("CURRENT SIZE: " + is.size());
                Iterator<MenuSlot> it = is.iterator();
                for (int i = 0; i < MENUSIZE; i++) {
                    if(!it.hasNext()) break;
                    if (slots.get(i) == null && !isLocked(i)) {
                        MenuSlot ms = it.next();
                        put(i, ms);
                        visitors.get(player.getName()).inventory.setItem(i, ms.getItemStack());
                    }
                }
                visitors.get(player.getName()).currentCategory = defaultCategory;
            }
            visitors.get(player.getName()).inventory = inv;
            player.openInventory(inv);
        } else {
            throw new IllegalStateException("Player already in shop");
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
                break;
        }
    }

    @Override
    public void onCategoryClicked(String categoryName, Player whoClicked) {
        System.out.println("Category:" + categoryName);
    }

    public void onItemClicked(MenuSlot slot, Player whoClicked) {
        System.out.println("Item:" + slot.getName());
    }

    public void onActionClicked(MenuSlot slot, Player whoClicked) {
        System.out.println("Action!" + slot.getName());
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

        public void setPages(Player player, String category) {
            this.currentPage = 0;
            if (category.equalsIgnoreCase(defaultCategory)) {
                List<MenuSlot> is = getItems();
                final int max = MENUSIZE - (slots.size() + locked.size());
                System.out.println("max = " + max);
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
