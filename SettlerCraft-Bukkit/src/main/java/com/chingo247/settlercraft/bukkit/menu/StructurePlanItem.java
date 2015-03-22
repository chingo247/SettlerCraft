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
package com.chingo247.settlercraft.bukkit.menu;

/**
 *
 * @author Chingo
 */
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


import com.chingo247.menu.item.CategoryTradeItem;
import com.chingo247.menu.item.TradeItem;
import com.chingo247.menu.util.ShopUtil;
import com.chingo247.settlercraft.structure.placement.Placement;
import com.chingo247.settlercraft.exception.SettlerCraftException;
import com.chingo247.settlercraft.model.persistence.entities.world.CuboidDimension;
import com.chingo247.settlercraft.structure.plan.StructurePlan;
import com.sk89q.worldedit.data.DataException;
import java.io.IOException;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.dom4j.DocumentException;

/**
 * Represents a StructurePlanItem
 *
 * @author Chingo
 */
public class StructurePlanItem implements CategoryTradeItem {

    public final String path;
    public final String name;
    public final String category;
    public final String description;
    public double price;
    public final int width;
    public final int height;
    public final int length;

    private StructurePlanItem(String path, String name, String category, double price, int width, int height, int length, String description) {
        this.path = path;
        this.name = name;
        this.category = category;
        this.price = price;
        this.width = width;
        this.height = height;
        this.length = length;
        this.description = description;
    }

    public static StructurePlanItem createItemFromPlan(StructurePlan plan) throws IOException, DataException, DocumentException, SettlerCraftException {
        
        Placement p = plan.getPlacement();
        
        CuboidDimension dimension = new CuboidDimension(p.getMinPosition(), p.getMaxPosition());
        
        int width = dimension.getMaxX();
        int height = dimension.getMaxY();
        int length = dimension.getMaxZ();
        String id = plan.getId();
        String name = plan.getName();
        String category = plan.getCategory();
        String description = plan.getDescription();
        double price = plan.getPrice();

        

        StructurePlanItem item = new StructurePlanItem(id, name, category, price, width, height, length, description);
        return item;
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
    public ItemStack getItemStack() {
        ItemStack stack = new ItemStack(Material.PAPER);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);

        meta.setLore(Arrays.asList(
                "Description: " + ((description == null || description.trim().isEmpty()) ? "-" : ChatColor.GOLD + description),
                "Price: " + (price == 0 ? ChatColor.GOLD + "FREE" : ChatColor.GOLD + ShopUtil.valueString(price)),
                "Width: " + ChatColor.GOLD + width,
                "Length: " + ChatColor.GOLD + length,
                "Height: " + ChatColor.GOLD + height,
                "Path: " + ChatColor.GOLD + path,
                "Type: " + ChatColor.GOLD + "StructurePlan"));

        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public TradeItem clone() {
        return new StructurePlanItem(path, name, category, price, width, height, length, description);
    }

}
