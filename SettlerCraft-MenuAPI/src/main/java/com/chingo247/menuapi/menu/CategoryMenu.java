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

package com.chingo247.menuapi.menu;

import com.chingo247.xplatform.core.AInventory;
import com.chingo247.xplatform.core.IPlayer;
import com.chingo247.settlercraft.core.platforms.services.IEconomyProvider;
import com.chingo247.menuapi.menu.item.CategoryTradeItem;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Chingo
 */
public class CategoryMenu extends ACategoryMenu {
    
    private static Comparator<CategoryTradeItem> ALPHABETICAL = new Comparator<CategoryTradeItem>() {

        @Override
        public int compare(CategoryTradeItem t, CategoryTradeItem t1) {
            return t.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
        }
    };
    private Map<String, List<CategoryTradeItem>> items;
    
    
    public CategoryMenu(IEconomyProvider economyProvider, String title, AInventory notThePlayersInventory, String defaultCategory, int defaultCategoryIcon, MenuView view, Map<String, List<CategoryTradeItem>> items) {
        super(economyProvider, title, notThePlayersInventory, defaultCategory, defaultCategoryIcon, view);
        this.items = items;
    }
    
    public CategoryMenu(IEconomyProvider economyProvider, String title, AInventory notThePlayersInventory, String defaultCategory, int defaultCategoryIcon, MenuView view) {
        this(economyProvider, title, notThePlayersInventory, defaultCategory, defaultCategoryIcon, view, new HashMap<String, List<CategoryTradeItem>>());
    }
    
    public Map<String, List<CategoryTradeItem>> getAllItems() {
        return Maps.newHashMap(items);
    }
    
    
    public void addItem(CategoryTradeItem tradeItem) {
        String category = matchCategoryForName(tradeItem.getCategory());
        List<CategoryTradeItem> itemList = items.get(category);
        if(itemList == null) {
            itemList = Lists.newArrayList();
            items.put(category, itemList);
        }
        items.get(category).add(tradeItem);
        
        
    }
    
    public List<CategoryTradeItem> getItemsForCategoryName(String category) {
       String matchCategory = matchCategoryForName(category);
       return items.get(matchCategory);
    }
    
    public int getTotalInventorySize() {
        int count = 0;
        for(List<CategoryTradeItem> itemsList : items.values()) {
            count += itemsList.size();
        }
        return count;
    }

    @Override
    protected List<CategoryTradeItem> getItems(String category, int page, IPlayer player) {
        List<CategoryTradeItem> tradeItems = Lists.newArrayList();
        if(category.equals(defaultCategory)) {
            for(List<CategoryTradeItem> itemsList : items.values()) {
                tradeItems.addAll(itemsList);
            }
        } else if(items.get(category) != null) {
            tradeItems = items.get(category);
        }
        
        Collections.sort(tradeItems, ALPHABETICAL);
        
        
        Preconditions.checkNotNull(tradeItems); // May NEVER be null at this point
        int slotsToFill = getFreeSlots();
        int min = Math.min(page * slotsToFill, tradeItems.size());
        int max = Math.min(min + slotsToFill, tradeItems.size());
        
        return tradeItems.subList(min, max);
    }


   
    
}
