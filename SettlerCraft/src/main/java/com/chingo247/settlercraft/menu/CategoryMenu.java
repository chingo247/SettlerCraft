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

import commons.EconomyProvider;
import com.chingo247.settlercraft.menu.item.CategoryTradeItem;
import com.chingo247.xcore.core.AInventory;
import com.chingo247.xcore.core.IPlayer;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Chingo
 */
public class CategoryMenu extends ACategoryMenu {
    
    private Map<String, List<CategoryTradeItem>> items;
    
    
    public CategoryMenu(MenuAPI menuAPI, EconomyProvider economyProvider, String title, IPlayer player, AInventory notThePlayersInventory, String defaultCategory, int defaultCategoryIcon) {
        super(menuAPI, economyProvider, title, player, notThePlayersInventory, false, defaultCategory, defaultCategoryIcon);
    }
    
    public void addItem(CategoryTradeItem tradeItem) {
        String category = matchCategoryForName(tradeItem.getCategory());
        List<CategoryTradeItem> itemList = items.get(category);
        if(itemList == null) {
            itemList = Lists.newArrayList();
            items.put(category, itemList);
        }
        itemList.add(tradeItem);
    }
    
    public List<CategoryTradeItem> getItemsForCategoryName(String category) {
       String matchCategory = matchCategoryForName(category);
       return items.get(matchCategory);
    }

    @Override
    protected List<CategoryTradeItem> getItems(String category, int page, IPlayer player) {
        List<CategoryTradeItem> tradeItems = items.get(category);
        Preconditions.checkNotNull(tradeItems); // May NEVER be null at this point
        int slotsToFill = getFreeSlots();
        int from = Math.max(0 , (page + 1) * slotsToFill);
        int to = Math.min(from + slotsToFill, tradeItems.size());
        System.out.println("category '"+category+"' from " + from + " to " + to);
        return tradeItems.subList(from, to);
    }


   
    
}
