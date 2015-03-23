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

package com.chingo247.settlercraft.menu;

import com.chingo247.settlercraft.menu.item.TradeItem;
import com.chingo247.settlercraft.menu.slots.ActionSlot;
import com.chingo247.settlercraft.menu.slots.CategorySlot;
import com.chingo247.settlercraft.menu.slots.Slot;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
public class CategoryMenu extends ACategoryMenu {

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
                            if (hasPrev()) {
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
