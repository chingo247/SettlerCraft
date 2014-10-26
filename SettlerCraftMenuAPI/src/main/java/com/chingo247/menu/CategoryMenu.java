/*
 * Copyright (C) 2014 Chingo247
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

package com.chingo247.menu;

import com.google.common.collect.Maps;
import com.chingo247.menu.item.CategoryTradeItem;
import com.chingo247.menu.item.TradeItem;
import com.chingo247.menu.slots.ActionSlot;
import com.chingo247.menu.slots.CategorySlot;
import com.chingo247.menu.slots.LockedSlot;
import com.chingo247.menu.slots.Slot;
import com.chingo247.menu.util.EconomyUtil;
import com.chingo247.menu.util.ShopUtil;
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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Chingo
 */
public class CategoryMenu implements Listener {

    private static final String ACTION_PREVIOUS = "Previous";
    private static final String ACTION_NEXT = "Next";
    private static final CategorySlot DEFAULT_CATEGORY = new CategorySlot("All", Material.NETHER_STAR);
    private static final Comparator<TradeItem> ALPHABETICAL = new Comparator<TradeItem>() {
        @Override
        public int compare(TradeItem o1, TradeItem o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    };
    

    private final Map<CategorySlot, List<TradeItem>> items;

    private final Map<Integer, Slot> slots;
    private final Map<UUID, Session> sessions;
    private final String title;
    private final UUID id;
    private final int MENU_SIZE;
    private final Plugin plugin;

    private boolean enabled = false;

    CategoryMenu(String title, int menusize, Plugin plugin) {
        if (menusize < 0) {
            throw new AssertionError("Size of menu must be greater than 0");
        }
        if (plugin == null) {
            throw new AssertionError("Null plugin");
        }
        this.plugin = plugin;
        this.items = Maps.newHashMap();
        this.slots = Maps.newHashMap();
        this.sessions = Maps.newHashMap();
        this.title = title;
        this.MENU_SIZE = menusize;
        this.id = UUID.randomUUID();
        putCategorySlot(0, DEFAULT_CATEGORY.getName(), DEFAULT_CATEGORY.getIcon().getType());
    }

    public UUID getId() {
        return id;
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
        CategorySlot s = matchCategory(item.getCategory());
        items.get(s).add(item);
    }

    public final void putCategorySlot(int slot, String category, Material icon) {
        this.putCategorySlot(slot, category, icon, new String[]{});
    }

    /**
     * Adds a category to this menu. If category already exists, entry for this category will be
     * overwritten. All items related to this Category will be removed. If the slot was already
     * inuse by another category. The slot will be overwritten and the old category will be removed
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

        CategorySlot c = new CategorySlot(category.trim(), icon);
        c.addAliases(aliases);
        

        // Check if slot was inuse by another Category
        Slot s = slots.get(slot);
        if (s instanceof CategorySlot) {
            CategorySlot cs = (CategorySlot) s;
            // Overwrite this one
            items.remove(cs);
        }
        
        // Add Category
        items.put(c, new ArrayList<TradeItem>());
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


    private void give(Player player, TradeItem item) {
        player.getInventory().addItem(item.getItemStack());
    }
    
    private boolean sell(Player player, TradeItem item) {
        Economy economy = EconomyUtil.getInstance().getEconomy();
        double price = item.getPrice();
        double balance = economy.getBalance(player.getName());
        
        // Has enough?
        if (price <= balance) {
            player.getInventory().addItem(item.getItemStack());
            economy.withdrawPlayer(player.getName(), price);
            player.sendMessage("You bought: " + ChatColor.BLUE +item.getName());
            player.sendMessage("Your new balance is: " + ChatColor.GOLD + economy.getBalance(player.getName()));
            return true;
        }
        // Not enough money
        return false;
    }

    private void setTemplateSlots(final Inventory inventory, final Player player) {

        final int page = sessions.get(player.getUniqueId()).page;

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
                                    ChatColor.BLUE + "Current Page " + ChatColor.GOLD + (page + 1)
                            );
                        } else {
                            actionSlot.setLore(
                                    ChatColor.BLUE + "To Page " + ChatColor.GOLD + " - ",
                                    ChatColor.BLUE + "Current Page " + ChatColor.GOLD + (page + 1)
                            );
                        }
                    } else if (actionSlot.getAction().equals(ACTION_NEXT)) {
                        
                        if (hasNext(player)) {
                            actionSlot.setLore(
                                    ChatColor.BLUE + "To Page " + ChatColor.GOLD + (page + 1 + 1),
                                    ChatColor.BLUE + "Current Page " + ChatColor.GOLD + (page + 1)
                            );
                        } else {
                            actionSlot.setLore(
                                    ChatColor.BLUE + "To Page " + ChatColor.GOLD + " - ",
                                    ChatColor.BLUE + "Current Page " + ChatColor.GOLD + (page + 1)
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

    private void updateMenu(Player player, CategorySlot category, int page) {
        if (!hasSession(player.getUniqueId())) {
            return;
        }
        

        // Get the session for this player
        Session session = sessions.get(player.getUniqueId());
        Inventory inv = session.inventory;
        
        
        // Get the new category
        if(category.getName().equals(session.currentCategory.getName()) && page == session.page)  {
            return; // Nothing changed
        }
        
        
        // Category has changed, reset page index
        if(!session.currentCategory.getName().equals(category.getName()) 
                && page == session.page) {
            sessions.put(player.getUniqueId(), new Session(0, inv, category, session.forFree));
        } else {
            sessions.put(player.getUniqueId(), new Session(page, inv, category, session.forFree));
        }

        // Clear previous session and create a new one
        inv.clear();
        
        
        Session newSession = sessions.get(player.getUniqueId());

        // Set ActionSlots & CategorySlots
        setTemplateSlots(inv, player);

        // Fill Inventory with items
        List<TradeItem> tradeItems = fetchItems(category, page);
        Iterator<TradeItem> it = tradeItems.iterator();

        
        for (int i = 0; i < MENU_SIZE && it.hasNext(); i++) {
            if (slots.get(i) == null) {
                TradeItem ti = it.next();
                if(session.forFree) {
                    TradeItem clone = ti.clone();
                    clone.setPrice(0);
                    newSession.sessionItems.put(i, clone);
                    inv.setItem(i, clone.getItemStack());
                } else {
                    newSession.sessionItems.put(i, ti);
                    inv.setItem(i, ti.getItemStack());
                }
            }
        }

        // Update Inventory
        sessions.put(player.getUniqueId(), newSession);
        sessions.get(player.getUniqueId()).inventory = inv;
        player.updateInventory();

    }
    
     public void openMenu(Player player, boolean forFree) {
         
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

        Session session = new Session(currentPage, inv, currentCategory, forFree);
        sessions.put(player.getUniqueId(), session);
        

        // Set ActionSlots & CategorySlots
        setTemplateSlots(inv, player);

        // Fill Inventory with items
        List<TradeItem> tradeItems = fetchItems(currentCategory, currentPage);
        Iterator<TradeItem> it = tradeItems.iterator();

        for (int i = 0; i < MENU_SIZE && it.hasNext(); i++) {
            if (slots.get(i) == null) {
                TradeItem ti = it.next();
                if(forFree) {
                    TradeItem clone = ti.clone();
                    clone.setPrice(0);
                    session.sessionItems.put(i, clone);
                    inv.setItem(i, clone.getItemStack());
                } else {
                    session.sessionItems.put(i, ti);
                    inv.setItem(i, ti.getItemStack());
                }
            }
        }

        // Open Inventory
        session.inventory = inv;
        sessions.put(player.getUniqueId(), session);

        player.openInventory(inv);
     }

    public void openMenu(Player player) {
        
        openMenu(player, false);
    }

    public void leave(Player player) {
        if (sessions.containsKey(player.getUniqueId())) {
            sessions.remove(player.getUniqueId());
            player.closeInventory();
        }
    }

    public void makeAllLeave() {
        for (UUID uuid : sessions.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                leave(player);
            }
        }
    }
    
    public void clearItems() {
        for(List<TradeItem> tis : items.values()) {
            tis.clear();
        }
    }

    public boolean hasSession(UUID session) {
        return sessions.get(session) != null;
    }

    public boolean hasSession(Player player) {
        return hasSession(player.getUniqueId());
    }

    private int getFreeSlots() {
        return MENU_SIZE - slots.size();
    }

    protected List<TradeItem> fetchItems(CategorySlot category, int page) {
        int maxItems = getFreeSlots();
        

        List<TradeItem> tradeItems;
        if (!category.equals(DEFAULT_CATEGORY)) {
            tradeItems = items.get(category);
        } else {
            tradeItems = new ArrayList<>();
            for (List<TradeItem> tis : items.values()) {
                tradeItems.addAll(tis);
            }
        }
        if(tradeItems == null) {
            tradeItems = new ArrayList<>(0);
        }
        
        
        if(!tradeItems.isEmpty()) {
            Collections.sort(tradeItems, ALPHABETICAL);
        }
        
        int min = Math.min(page * maxItems, tradeItems.size());
        int max = Math.min(min + maxItems, tradeItems.size());
        

        List<TradeItem> pageItems = tradeItems.subList(min, max);
        

        return pageItems;
    }

    private boolean hasNext(Player player) {
        Session session = sessions.get(player.getUniqueId());
        return !(fetchItems(session.currentCategory, session.page + 1).isEmpty());
    }

    private boolean hasPrev(Player player) {
        return sessions.get(player.getUniqueId()).page > 0;
    }

    private CategorySlot matchCategory(String category) {
        Iterator<CategorySlot> it = items.keySet().iterator();
        
        while (it.hasNext()) {
            CategorySlot cs = it.next();
            if (cs.getName().equalsIgnoreCase(category) || cs.hasAlias(category)) {
                return cs;
            }
        }
        return DEFAULT_CATEGORY;
    }

    private class Session {

        private CategorySlot currentCategory;
        private Inventory inventory;
        private int page = 0;
        private Map<Integer, TradeItem> sessionItems;
        private boolean forFree = false;

        public Session(int currentPage, Inventory inventory, CategorySlot currentCategory, boolean forFree) {
            this.inventory = inventory;
            this.currentCategory = currentCategory;
            this.sessionItems = Maps.newHashMap();
            this.page = currentPage;
            this.forFree = forFree;
        }

    }

    @EventHandler
    private void onPlayerLeavesShop(InventoryCloseEvent ice) {
        Player player = (Player) ice.getPlayer();

        // Check wheter this player is visiting a menu
        if (hasSession(player.getUniqueId())) {
            // remove player
            leave(player);
        }
    }
    
    @EventHandler
    private void onPlayerLogout(PlayerQuitEvent pqe) {
        Player player = (Player) pqe.getPlayer();

        // Check wheter this player is visiting a menu
        if (hasSession(player.getUniqueId())) {
            // remove player
            leave(player);
        }
    }

    @EventHandler
    private void onMenuSlotClicked(InventoryClickEvent ice) {
        if (!enabled) {
            return;
        }

        
        Player player = (Player) ice.getWhoClicked();
        if (!hasSession(player.getUniqueId())) {
            return; // Player is not using this menu
        }
        

        if (ice.getClick() == ClickType.RIGHT
                || ice.getClick() == ClickType.SHIFT_RIGHT
                || ice.getClick() == ClickType.SHIFT_LEFT
                || ice.getRawSlot() < MENU_SIZE && ice.getRawSlot() >= 0
                || ice.getClick() == ClickType.DOUBLE_CLICK) {
//          Cancel the action
            ice.setCancelled(true);
        }

        if (ice.getRawSlot() < MENU_SIZE && ice.getRawSlot() >= 0) {

            
            
            
            ItemStack stack = ice.getCurrentItem();

            if (stack != null
                    && stack.getType() != Material.AIR
                    && stack.getAmount() > 0) {

                Session session = sessions.get(ice.getWhoClicked().getUniqueId());

                Slot slot = slots.get(ice.getRawSlot());
                
                
                // Slot is item slot
                if (slot == null) {

                    TradeItem item = session.sessionItems.get(ice.getRawSlot());

                    if (item != null) {
                        if(Bukkit.getPluginManager().getPlugin("Vault") != null) {
                            sell(player, item);
                        } else {
                            give(player, item);
                        }
                    } 
                } else if (slot instanceof ActionSlot) {
                    ActionSlot as = (ActionSlot) slot;
                    switch (as.getAction().toLowerCase()) {
                        case "next":
                            if(hasNext(player)) {
                                updateMenu(player, session.currentCategory, session.page + 1);
                            }
                            break;
                        case "previous":
                            if (hasPrev(player)) {
                                updateMenu(player, session.currentCategory, session.page - 1);
                            }
                            break;
                        default:
                            break;
                    }
                } else if (slot instanceof CategorySlot) {
                    CategorySlot cs = (CategorySlot) slot;
                    updateMenu(player, cs, session.page);
                }
            }
        }

    }

    @EventHandler
    private void onReload(PluginDisableEvent disableEvent) {
        if (this.plugin.getName().equals(disableEvent.getPlugin().getName())) {
            this.setEnabled(false);
            this.makeAllLeave();
            this.clearItems();
        }
    }
    
}
