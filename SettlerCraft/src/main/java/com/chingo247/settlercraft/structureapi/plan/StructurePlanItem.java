
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

package com.chingo247.settlercraft.structureapi.plan;


import com.chingo247.menu.item.CategoryTradeItem;
import com.chingo247.menu.item.TradeItem;
import com.chingo247.menu.util.ShopUtil;
import com.chingo247.settlercraft.structureapi.exception.StructureDataException;
import com.chingo247.settlercraft.structureapi.plan.schematic.SchematicData;
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
    public final int blocks;

    private StructurePlanItem(String path, String name, String category, double price, int width, int height, int length, int blocks, String description) {
        this.path = path;
        this.name = name;
        this.category = category;
        this.price = price;
        this.width = width;
        this.height = height;
        this.length = length;
        this.blocks = blocks;
        this.description = description;
    }

    public static StructurePlanItem load(SchematicData data, StructurePlan plan) throws IOException, DataException, DocumentException, StructureDataException {
        
        
        
        
        if(data == null) {
            throw new AssertionError("SchematicData was null");
        }

        int width = data.getWidth();
        int height = data.getHeight();
        int length = data.getLength();
        int blocks = data.getBlocks();
        String path = plan.getRelativePath();
        String name = plan.getName();
        String category = plan.getCategory();
        String description = plan.getDescription();
        double price = plan.getPrice();

        

        StructurePlanItem item = new StructurePlanItem(path, name, category, price, width, height, length, blocks, description);
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
                "Blocks: " + ChatColor.GOLD + ShopUtil.valueString(blocks),
                "Path: " + ChatColor.GOLD + path,
                "Type: " + ChatColor.GOLD + "Plan"));

        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public TradeItem clone() {
        return new StructurePlanItem(path, name, category, price, width, height, length, blocks, description);
    }

}
