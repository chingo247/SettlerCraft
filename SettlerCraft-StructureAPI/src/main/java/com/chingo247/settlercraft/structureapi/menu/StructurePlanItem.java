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

/**
 *
 * @author Chingo
 */

import com.chingo247.menuapi.menu.item.CategoryTradeItem;
import com.chingo247.menuapi.menu.item.TradeItem;
import com.chingo247.menuapi.menu.util.ShopUtil;
import com.chingo247.xplatform.core.AItemStack;
import com.chingo247.xplatform.core.APlatform;
import com.chingo247.xplatform.core.IColors;
import com.sk89q.worldedit.blocks.ItemID;
import java.util.Arrays;

/**
 * Represents a StructurePlanItem
 *
 * @author Chingo
 */
public class StructurePlanItem implements CategoryTradeItem {

    private final String id;
    private final String name;
    private final String category;
    private final String description;
    private double price;
    private final int width;
    private final int height;
    private final int length;
    private final APlatform platform;

    StructurePlanItem(APlatform platform, String id, String name, String category, double price, int width, int height, int length, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.width = width;
        this.height = height;
        this.length = length;
        this.description = description;
        this.platform = platform;
    }




    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public AItemStack getItemStack() {
        AItemStack item = platform.createItemStack(ItemID.PAPER);
        item.setName(name);
        
        IColors color = platform.getChatColors();
        
        item.setLore(Arrays.asList(
                "Description: " + ((description == null || description.trim().isEmpty()) ? "-" : color.gold() + description),
                "Price: " + (price == 0 ? color.gold() + "FREE" : color.gold() + ShopUtil.valueString(price)),
                "Width: " + color.gold() + width,
                "Length: " + color.gold() + length,
                "Height: " + color.gold() + height,
                "Blocks: " + color.gold() + ShopUtil.valueString(width*height*length),
                "UniqueId: " + color.gold() + id,
                "Type: " + color.gold() + "StructurePlan"));

        
        return item;
    }

    @Override
    public TradeItem clone() {
        return new StructurePlanItem(platform,id, name, category, price, width, height, length, description);
    }

}
