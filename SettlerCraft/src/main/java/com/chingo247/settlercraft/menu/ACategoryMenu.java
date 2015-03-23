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
package com.chingo247.settlercraft.menu;

import com.chingo247.settlercraft.SettlerCraft;
import com.chingo247.settlercraft.menu.item.TradeItem;
import com.chingo247.settlercraft.menu.slots.ActionSlot;
import com.chingo247.settlercraft.menu.slots.CategorySlot;
import com.chingo247.settlercraft.menu.slots.Slot;
import com.chingo247.settlercraft.core.EconomyProvider;
import com.chingo247.settlercraft.menu.slots.ItemSlot;
import com.chingo247.settlercraft.menu.slots.SlotFactory;
import com.chingo247.settlercraft.menu.util.ShopUtil;
import com.chingo247.xcore.core.AInventory;
import com.chingo247.xcore.core.APlatform;
import com.chingo247.xcore.core.IColor;
import com.chingo247.xcore.core.IPlayer;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.util.com.google.common.collect.Lists;
import org.bukkit.ChatColor;

/**
 *
 * @author Chingo
 */
public abstract class ACategoryMenu {
    
    private static final int MENU_SIZE = 54;
    private static final String ACTION_PREVIOUS = "Previous", ACTION_NEXT = "Next";
    
    private final Map<Integer,Slot> slots;
    private final IPlayer player;
    private final AInventory inventory;
    private final EconomyProvider economyProvider;
    private final SlotFactory slotFactory;
    private final APlatform platform;
    
    private final String title, currentCategory;
    private final boolean noCosts;
    
    private boolean enabled;
    private int currentPage = 0;
    private List<TradeItem> cachedItems;
    private MenuAPI menuAPI;

    public ACategoryMenu(String title, IPlayer player, AInventory notThePlayersInventory, boolean noCosts, String defaultCategory, int defaultCategoryIcon) {
        Preconditions.checkNotNull(title);
        Preconditions.checkNotNull(player);
        Preconditions.checkNotNull(notThePlayersInventory);
        
        this.title = title;
        this.player = player;
        this.slots = Maps.newHashMap();
//        this.items = Maps.newHashMap();
        this.inventory = notThePlayersInventory;
        this.noCosts = noCosts;
        this.economyProvider = SettlerCraft.getInstance().getEconomyProvider();
        this.slotFactory = SlotFactory.getInstance();
        this.platform = SettlerCraft.getInstance().getPlatform();
        this.enabled = true;
        this.currentCategory = defaultCategory;
        this.cachedItems = Lists.newArrayList();
        this.slots.put(0, slotFactory.createCategorySlot(defaultCategory, defaultCategoryIcon));
        this.menuAPI = null;
    }
    
    
    
    private IColor getColor() {
        return platform.getChatColors();
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Slot getSlot(int slot) {
        return slots.get(slot);
    }

    public int getPage() {
        return currentPage;
    }
    
    void setSlot(int i, Slot slot) {
        if(i == 0) {
            throw new IllegalArgumentException("Can't set the default slot '0'");
        }
        this.slots.put(i, slot);
    }

    void register(MenuAPI menuAPI) {
        this.menuAPI = menuAPI;
    }
    
    


    /**
     * Adds a category to this menu. If category already exists, entry for this category will be
     * overwritten.
     *
     * @param slot The inventory slot
     * @param category The category
     * @param icon The material that will be used as icon for this category
     */
    public final void setCategorySlot(int slot, String category, int icon) {
        if (slot < 0 || slot > MENU_SIZE) {
            throw new IndexOutOfBoundsException("Slot is not in range of 0 - " + MENU_SIZE);
        }

        CategorySlot categorySlot = slotFactory.createCategorySlot(category, slot);
        setSlot(slot, categorySlot);
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
        Slot s = new Slot(true);
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
            player.sendMessage("You bought: " + getColor().getBlue() +item.getName());
            player.sendMessage("Your new balance is: " + getColor().getGold() + economyProvider.getBalance(player.getUniqueId()));
            return true;
        }
        // Not enough money
        return false;
    }

    public String getTitle() {
        return title;
    }
    
   
    
    private void resetInventory() {
        inventory.clear();
        IColor colors = getColor();
        for (int i = 0; i < MENU_SIZE; i++) {
            Slot slot = slots.get(i);
            if (slot != null && !slot.isNoneSlot()) {

                if (slot instanceof ActionSlot) {
                    ActionSlot actionSlot = (ActionSlot) slot;
                    if (actionSlot.getAction().equals(ACTION_PREVIOUS)) {
                        if (hasPrev()) {
                            // Only computer scientists count from 0...
                            actionSlot.setLore(
                                    colors.getBlue() + "To Page " + colors.getGold() + currentPage,
                                    colors.getBlue()  + "Current Page " + colors.getGold() + (currentPage + 1)
                            );
                        } else {
                            actionSlot.setLore(
                                    colors.getBlue()  + "To Page " + colors.getGold() + " - ",
                                    colors.getBlue()  + "Current Page " + colors.getGold() + (currentPage + 1)
                            );
                        }
                    } else if (actionSlot.getAction().equals(ACTION_NEXT)) {
                        
                        if (hasNext()) {
                            actionSlot.setLore(
                                    colors.getBlue() + "To Page " + colors.getGold() + (currentPage + 1 + 1),
                                    colors.getBlue() + "Current Page " + colors.getGold() + (currentPage + 1)
                            );
                        } else {
                            actionSlot.setLore(
                                    colors.getBlue()+ "To Page " + colors.getGold() + " - ",
                                    colors.getBlue() + "Current Page " + colors.getGold() + (currentPage + 1)
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
    
    private void updateMenu(String newCategory, int newPage) {
        
        // Get the new category
        if(newCategory.equals(currentCategory) && newPage == currentPage)  {
            return; // Nothing changed
        } 

        resetInventory();

        // Fill Inventory with items
        List<TradeItem> tradeItems = getItems(newCategory, newPage, player);
        Iterator<TradeItem> it = tradeItems.iterator();

        
        for (int i = 0; i < MENU_SIZE && it.hasNext(); i++) {
            if (slots.get(i) == null) {
                TradeItem ti = it.next();
                TradeItem clone = ti.clone();
                
                if(noCosts) {
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
    
    public void openMenu() {
        if(menuAPI == null) throw new IllegalStateException("Menu should be registered at the MenuAPI to work correctly!");
        
        // Notify player's balance
        player.sendMessage(ChatColor.YELLOW + "[" + title + "]: " + ChatColor.RESET + "Hello " + ChatColor.GREEN + player.getName() + ChatColor.RESET + "!");
        double balance = economyProvider.getBalance(player.getUniqueId());
        if (balance > 0) {
            player.sendMessage(ChatColor.YELLOW + "[" + title + "]: " + ChatColor.RESET + " Your balance is " + ChatColor.GOLD + ShopUtil.valueString(balance));
        } else {
            player.sendMessage(ChatColor.YELLOW + "[" + title + "]: " + ChatColor.RESET + " Your balance is " + ChatColor.RED + balance);
        }
        
        updateMenu(currentCategory, currentPage);
        player.openInventory(inventory);
     }
    
    protected boolean hasNext() {
        cachedItems = getItems(title, currentPage, player);
        return !cachedItems.isEmpty();
    }

    protected boolean hasPrev() {
        return currentPage > 0;
    }
    
    protected abstract List<TradeItem> getItems(String category, int page, IPlayer player);
    
    protected IPlayer getPlayer() {
        return player;
    }
    
    public void close(String reason) {
        player.closeInventory();
        if(reason != null && !reason.isEmpty()) {
            player.sendMessage(reason);
        } 
    }
    
}
