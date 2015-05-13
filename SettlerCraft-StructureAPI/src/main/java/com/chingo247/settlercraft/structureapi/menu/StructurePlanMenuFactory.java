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
package com.chingo247.settlercraft.structureapi.menu;

import com.chingo247.menuapi.menu.CategoryMenu;
import com.chingo247.menuapi.menu.DefaultCategoryMenu;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structureapi.structure.plan.StructurePlan;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.regions.CuboidRegion;

/**
 *
 * @author Chingo
 */
public class StructurePlanMenuFactory {

    private final CategoryMenu menu;
    private final APlatform platform;
    

    public StructurePlanMenuFactory(APlatform platform, CategoryMenu menu) {
        Preconditions.checkNotNull(platform);
        Preconditions.checkNotNull(menu);
        this.menu = menu;
        this.platform = platform;
    }

    public void load(StructurePlan plan) {
        Placement placement = plan.getPlacement();
       
        CuboidRegion region = placement.getCuboidRegion();
        
        int width = region.getMaximumPoint().getBlockX();
        int height = region.getMaximumPoint().getBlockY();
        int length = region.getMaximumPoint().getBlockZ();
        
        String id = plan.getId();
        String name = plan.getName();
        String category = plan.getCategory();
        String description = plan.getDescription();
        
        double price = plan.getPrice();
        
        
        menu.addItem(new StructurePlanItem(platform, id, name, category, price, width, height, length, description));
    }

    public CategoryMenu createPlanMenu() {
        return new DefaultCategoryMenu(SettlerCraft.getInstance().getEconomyProvider(),menu.getTitle(), menu.getView(), menu.getAllItems());
    }
    
    

}
