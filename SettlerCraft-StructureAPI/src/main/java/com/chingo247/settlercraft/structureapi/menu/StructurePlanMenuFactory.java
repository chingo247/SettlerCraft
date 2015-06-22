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


package com.chingo247.settlercraft.structureapi.menu;

import com.chingo247.menuapi.menu.CategoryMenu;
import com.chingo247.menuapi.menu.DefaultCategoryMenu;
import com.chingo247.settlercraft.core.SettlerCraft;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.Placement;
import com.chingo247.settlercraft.structureapi.structure.plan.IStructurePlan;
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
    
    public void clearAll() {
        menu.clearAll();
    }

    public void load(IStructurePlan plan) {
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
