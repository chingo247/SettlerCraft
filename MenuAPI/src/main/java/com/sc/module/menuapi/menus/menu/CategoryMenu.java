/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.menu;

import com.google.common.collect.Maps;
import com.sc.module.menuapi.menus.menu.util.EconomyUtil;
import com.sc.module.menuapi.menus.menu.util.ShopUtil;
import com.sc.module.structureapi.menu.item.CategoryTradeItem;
import com.sc.module.structureapi.menu.item.TradeItem;
import com.sc.module.structureapi.menu.slots.ActionSlot;
import com.sc.module.structureapi.menu.slots.CategorySlot;
import com.sc.module.structureapi.menu.slots.LockedSlot;
import com.sc.module.structureapi.menu.slots.Slot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author Chingo
 */
public class CategoryMenu {

    private static final String ACTION_PREVIOUS = "Previous";
    private static final String ACTION_NEXT = "Next";
    private static final CategorySlot DEFAULT_CATEGORY = new CategorySlot("All", Material.NETHER_STAR);
    private static final Comparator<TradeItem> TRADE_ITEM_NAME_COMPARATOR = new Comparator<TradeItem>() {
        @Override
        public int compare(TradeItem o1, TradeItem o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    private final Map<CategorySlot, List<TradeItem>> items;

    private final Map<Integer, Slot> slots;
    private final Map<UUID, Session> sessions;
    private final String title;
    private final int MENU_SIZE;
    
    private boolean enabled = false;

    public CategoryMenu(String title, int menusize) {
        if (menusize < 0) {
            throw new AssertionError("Size of menu must be greater than 0");
        }
        this.items = Maps.newHashMap();
        this.slots = Maps.newHashMap();
        this.sessions = Maps.newHashMap();
        this.title = title;
        this.MENU_SIZE = menusize;
        putCategorySlot(0, DEFAULT_CATEGORY.getName(), DEFAULT_CATEGORY.getIcon().getType());
    }

    public String getTitle() {
        return title;
    }
    
    

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
    
    public void addItem(CategoryTradeItem item) {
        System.out.println("adding: " + item.getName());
        CategorySlot newItemCategory = new CategorySlot(item.getCategory(), Material.AIR);
        Iterator<CategorySlot> it = items.keySet().iterator();

        while (it.hasNext()) {
            CategorySlot cs = it.next();
            if (cs.getName().equals(newItemCategory.getName()) || cs.hasAlias(newItemCategory.getName())) {
                items.get(cs).add(item);
                System.out.println("Added " + item.getName() + " to " + cs.getName());
                return; //  Found'm!
            }
        }
        // Category wasn't found
        // Add to default Category
        System.out.println("Added " + item.getName() + " to " + DEFAULT_CATEGORY.getName());
        items.get(DEFAULT_CATEGORY).add(item);
    }
    
    public final void putCategorySlot(int slot, String category, Material icon) {
        this.putCategorySlot(slot, category, icon, new String[]{});
    }

    /**
     * Adds a category to this menu. If category already exists, entry for this category will be
     * overwritten. All items related to this Category will be removed. If the slot was already
     * inuse by another category. The slot will be overwrittenand the old category will be removed
     * as it's no longer available in the menu
     *
     * @param slot The inventory slot
     * @param category The category
     * @param icon The material that will be used as icon for this category
     * @param aliases The aliases for this category
     */
    public final void putCategorySlot(int slot, String category, Material icon, String... aliases) {
        if (slot < 0 || slot > MENU_SIZE) {
            throw new IndexOutOfBoundsException("Slot is not in range of 0 - " + MENU_SIZE);
        }
        
        CategorySlot c = new CategorySlot(category, icon);
        c.addAliases(aliases);
        // Add Category
        items.put(c, new ArrayList<TradeItem>());

        // Check if slot was inuse by another Category
        Slot s = slots.get(slot);
        if (s instanceof CategorySlot) {
            CategorySlot cs = (CategorySlot) s;
            // Remove this category
            items.remove(cs);
        }

        slots.put(slot, c);
    }
    
    public void putActionSlot(int slot, String text, Material material) {
        if (slot < 0 || slot > MENU_SIZE) {
            throw new IndexOutOfBoundsException("Slot is not in range of 0 - " + MENU_SIZE);
        }
        
        // Check if slot was inuse by a Category
        Slot s = slots.get(slot);
        if (s instanceof CategorySlot) {
            CategorySlot cs = (CategorySlot) s;
            // Remove this category
            items.remove(cs);
        }
        
        slots.put(slot, new ActionSlot(text, material));
        
    }


    public void putLocked(int slot) {
        if (slot < 0 || slot > MENU_SIZE) {
            throw new IndexOutOfBoundsException("Slot is not in range of 0 - " + MENU_SIZE);
        }

        // Check if slot was inuse by another Category
        Slot s = slots.get(slot);
        if (s instanceof CategorySlot) {
            CategorySlot cs = (CategorySlot) s;
            // Remove this category
            items.remove(cs);
        }
        slots.put(slot, new LockedSlot());
    }

    public void putLocked(int... slots) {
        for (int i : slots) {
            putLocked(i);
        }
    }

    public boolean isLocked(int slot) {
        return slots.get(slot) instanceof LockedSlot;
    }

    public void sellItem(Player player, int slot) {
        Session session = sessions.get(player.getUniqueId());
        session.items.get(slot);
    }

    public void onAction(int slot, Player player) {
        Session session = sessions.get(player.getUniqueId());
        if (session == null) {
            return;
        }

        Slot s = slots.get(slot);
        if (s instanceof ActionSlot) {
            ActionSlot as = (ActionSlot) s;
            switch (as.getAction().toLowerCase()) {
                case "next":
                    updateMenu(player, session.currentCategory.getName(), session.page + 1);
                    break;
                case "previous":
                    updateMenu(player, session.currentCategory.getName(), session.page - 1);
                    break;
                default:
                    break;
            }
        }
    }

    protected boolean sell(Player player, TradeItem item) {
        Economy economy = EconomyUtil.getInstance().getEconomy();
        double price = item.getPrice();
        double balance = economy.getBalance(player.getName());
        // Has enough?
        if (price <= balance) {
            player.getInventory().addItem(item.getItemStack());
            economy.withdrawPlayer(player.getName(), price);
            return true;
        }
        // Not enough money
        return false;
    }

    private void setTemplateSlots(final Inventory inventory, final Player player) {
        inventory.clear();
        final int page = sessions.get(player.getUniqueId()).page;
        final CategorySlot category = sessions.get(player.getUniqueId()).currentCategory;

        for (int i = 0; i < MENU_SIZE; i++) {
            Slot slot = slots.get(i);
            if (slot != null) {
                if (slot instanceof LockedSlot) {
                    continue;
                }

                if (slot instanceof ActionSlot) {
                    ActionSlot actionSlot = (ActionSlot) slot;
                    if (actionSlot.getAction().equals(ACTION_PREVIOUS)) {
                        if (hasPrev(player)) {
                            // Only computer scientists count from 0...
                            actionSlot.setLore(
                                    ChatColor.BLUE + "To Page " + ChatColor.GOLD + page,
                                    ChatColor.BLUE + "Current Page " + ChatColor.GOLD + page + 1
                            );
                        } else {
                            actionSlot.setLore(
                                    ChatColor.BLUE + "To Page " + ChatColor.GOLD + " - ",
                                    ChatColor.BLUE + "Current Page" + ChatColor.GOLD + page + 1
                            );
                        }
                    } else if (actionSlot.getAction().equals(ACTION_NEXT)) {
                        if (hasNext(player, category, page)) {
                            actionSlot.setLore(
                                    ChatColor.BLUE + "To Page " + ChatColor.GOLD + page + 1 + 1,
                                    ChatColor.BLUE + "Current Page " + ChatColor.GOLD + page + 1
                            );
                        } else {
                            actionSlot.setLore(
                                    ChatColor.BLUE + "To Page " + ChatColor.GOLD + " - ",
                                    ChatColor.BLUE + "Current Page" + String.valueOf(page + 1)
                            );
                        }
                    }
                    inventory.setItem(i, actionSlot.getIcon());
                } else if (slot instanceof CategorySlot) {
                    CategorySlot cs = (CategorySlot) slot;
                    inventory.setItem(i, cs.getIcon());
                }
            }
        }
    }

    public void updateMenu(Player player, String category, int page) {
        if (!hasSession(player.getUniqueId())) {
            return;
        }

        // Get the session for this player
        Inventory inv = sessions.get(player.getUniqueId()).inventory;
        final int currentPage = sessions.get(player.getUniqueId()).page;
        final CategorySlot currentCategory = matchCategory(category);

        sessions.put(player.getUniqueId(), new Session(currentPage, inv, currentCategory));

        // Set ActionSlots & CategorySlots
        setTemplateSlots(inv, player);

        // Fill Inventory with items
        List<TradeItem> tradeItems = fetchItems(player, currentCategory, currentPage);
        Iterator<TradeItem> it = tradeItems.iterator();
        Session session = sessions.get(player.getUniqueId());
        for (int i = 0; i < MENU_SIZE && it.hasNext(); i++) {
            if (slots.get(i) == null) {
                TradeItem ti = it.next();
                session.items.put(i, ti);
                inv.setItem(i, ti.getItemStack());
            }
        }

        // Open Inventory
        sessions.put(player.getUniqueId(), session);
        sessions.get(player.getUniqueId()).inventory = inv;
        player.updateInventory();

    }

    public void openMenu(Player player) {
        // Guarantee new Session from here
        if (hasSession(player.getUniqueId())) {
            sessions.remove(player.getUniqueId());
        }

        // Notify player's balance
        player.sendMessage(ChatColor.YELLOW + "[" + title + "]: " + ChatColor.RESET + "Hello " + ChatColor.GREEN + player.getName() + ChatColor.RESET + "!");
        double balance = EconomyUtil.getInstance().getEconomy().getBalance(player.getName());
        if (balance > 0) {
            player.sendMessage(ChatColor.YELLOW + "[" + title + "]: " + ChatColor.RESET + " Your balance is " + ChatColor.GOLD + ShopUtil.valueString(balance));
        } else {
            player.sendMessage(ChatColor.YELLOW + "[" + title + "]: " + ChatColor.RESET + " Your balance is " + ChatColor.RED + balance);
        }

        // Create a session for this player
        Inventory inv = Bukkit.createInventory(null, MENU_SIZE, title);
        final int currentPage = 0;
        final CategorySlot currentCategory = DEFAULT_CATEGORY;

        Session session = new Session(currentPage, inv, currentCategory);
        sessions.put(player.getUniqueId(), session);
        
        // Set ActionSlots & CategorySlots
        setTemplateSlots(inv, player);

        // Fill Inventory with items
        List<TradeItem> tradeItems = fetchItems(player, currentCategory, currentPage);
        System.out.println("Items in menu: " + tradeItems);
        Iterator<TradeItem> it = tradeItems.iterator();

        for (int i = 0; i < MENU_SIZE && it.hasNext(); i++) {
            if (slots.get(i) == null) {
                TradeItem ti = it.next();
                session.items.put(i, ti);
                inv.setItem(i, ti.getItemStack());
            }
        }

        // Open Inventory
        session.inventory = inv;
        sessions.put(player.getUniqueId(), session);
       
        player.openInventory(inv);
    }

    public void closeMenu(Player player) {
        if (sessions.containsKey(player.getUniqueId())) {
            sessions.remove(player.getUniqueId());
            player.closeInventory();
        }
    }
    
    public void makeAllLeave() {
        for(UUID uuid : sessions.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null) {
                closeMenu(player);
            }
        }
    }

    public boolean hasSession(UUID session) {
        return sessions.containsKey(session);
    }

    private int getFreeSlots() {
        return MENU_SIZE - slots.size();
    }

    private List<TradeItem> fetchItems(Player player, CategorySlot category, int page) {
        int maxItems = getFreeSlots();

        System.out.println("page: " + page);
        System.out.println("category: " + category.getName());
        
        
        List<TradeItem> tradeItems;
        if(!category.equals(DEFAULT_CATEGORY)) {
            tradeItems = items.get(category);
        } else {
            System.out.println("DEFAULT CATEGORY");
            tradeItems = new ArrayList<>();
            for(List<TradeItem> tis : items.values()) {
                tradeItems.addAll(tis);
            }
            System.out.println("Items: " + tradeItems.size());
        }
        
        
        int min = page * maxItems;
        int max = Math.min(min + maxItems, tradeItems.size());
        
        System.out.println("Min: " + min + " Max: " + max );

        List<TradeItem> pageItems = tradeItems.subList(min, max);
        Collections.sort(pageItems, TRADE_ITEM_NAME_COMPARATOR);

        return pageItems;
    }

    private boolean hasNext(Player player, CategorySlot category, int page) {
        return !fetchItems(player, category, page).isEmpty();
    }

    private boolean hasPrev(Player player) {
        return sessions.get(player.getUniqueId()).page > 0;
    }

    private CategorySlot matchCategory(String category) {
        Iterator<CategorySlot> it = items.keySet().iterator();
        while (it.hasNext()) {
            CategorySlot cs = it.next();
            if (cs.getName().equals(category) || cs.hasAlias(category)) {
                return cs;
            }
        }
        return DEFAULT_CATEGORY;
    }

    
    private class Session {

        private CategorySlot currentCategory;
        private Inventory inventory;
        private int page = 0;
        private Map<Integer, TradeItem> items;

        public Session(int currentPage, Inventory inventory, CategorySlot currentCategory) {
            this.inventory = inventory;
            this.currentCategory = currentCategory;
            this.items = Maps.newHashMap();
        }

    }

}
