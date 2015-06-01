/*
 * Copyright (C) 2015 Chingo
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
import com.chingo247.settlercraft.core.platforms.services.IEconomyProvider;
import com.chingo247.menuapi.menu.item.CategoryTradeItem;
import com.chingo247.settlercraft.core.SettlerCraft;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DefaultCategory menu offers an inventory view for ONE player. 
 * @author Chingo
 */
public class DefaultCategoryMenu extends CategoryMenu{
    
    private static final int NETHER_STAR_ID = 399;
    
    /**
     * Constructor
     * @param title The menu title
     * @param menuView The menuView, NOTE: the menuview will be cloned
     */
    public DefaultCategoryMenu(String title, MenuView menuView) {
        this(SettlerCraft.getInstance().getEconomyProvider(), title, menuView, new HashMap<String,List<CategoryTradeItem>>());
    }
    
    public DefaultCategoryMenu(IEconomyProvider economyProvider,String title, MenuView menuView, Map<String,List<CategoryTradeItem>> items) {
        super(SettlerCraft.getInstance().getEconomyProvider(), 
                title, 
                MenuAPI.getInstance().getPlatform().createInventory(title, MENU_SIZE), 
                "All", 
                NETHER_STAR_ID,
                menuView.clone(),
                items
        );
    }
    

    public DefaultCategoryMenu(String title) {
        this(title, new MenuView());
    }
    
}
