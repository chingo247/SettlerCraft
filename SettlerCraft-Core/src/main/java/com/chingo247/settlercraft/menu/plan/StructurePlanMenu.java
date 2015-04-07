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
package com.chingo247.settlercraft.menu.plan;

import com.chingo247.settlercraft.menu.CategoryMenu;
import com.chingo247.settlercraft.menu.DefaultCategoryMenu;
import com.chingo247.settlercraft.menu.MenuAPI;
import com.chingo247.settlercraft.structure.placement.Placement;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.chingo247.xcore.core.APlatform;
import com.google.common.base.Preconditions;

/**
 *
 * @author Chingo
 */
public class StructurePlanMenu {

    private final CategoryMenu menu;
    private final MenuAPI menuAPI;
    private final APlatform platform;
    

    public StructurePlanMenu(APlatform platform, MenuAPI menuAPI, CategoryMenu menu) {
        Preconditions.checkNotNull(platform);
        Preconditions.checkNotNull(menuAPI);
        Preconditions.checkNotNull(menu);
        this.menu = menu;
        this.platform = platform;
        this.menuAPI = menuAPI;
    }

    public void load(StructurePlan plan) {
        Placement placement = plan.getPlacement();
       
        int width = placement.getDimension().getMaxX();
        int height = placement.getDimension().getMaxY();
        int length = placement.getDimension().getMaxZ();
        
        String id = plan.getId();
        String name = plan.getName();
        String category = plan.getCategory();
        String description = plan.getDescription();
        
        double price = plan.getPrice();
        
        menu.addItem(new StructurePlanItem(platform, id, name, category, price, width, height, length, description));
    }

    public CategoryMenu createPlanMenu() {
        return new DefaultCategoryMenu(menuAPI, menu.getTitle(), menu.getView(), menu.getAllItems());
    }

}
