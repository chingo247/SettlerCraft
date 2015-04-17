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
package com.chingo247.menuapi.menu;

import com.chingo247.settlercraft.core.services.IEconomyProvider;
import com.chingo247.menuapi.menu.item.CategoryTradeItem;
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
        this(MenuAPI.getInstance().getEconomyProvider(), title, menuView, new HashMap<String,List<CategoryTradeItem>>());
    }
    
    public DefaultCategoryMenu(IEconomyProvider economyProvider,String title, MenuView menuView, Map<String,List<CategoryTradeItem>> items) {
        super(MenuAPI.getInstance().getEconomyProvider(), 
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
