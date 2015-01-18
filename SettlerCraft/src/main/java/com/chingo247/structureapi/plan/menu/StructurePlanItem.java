package com.chingo247.structureapi.plan.menu;

//
///*
// * The MIT License
// *
// * Copyright 2015 Chingo.
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in
// * all copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// * THE SOFTWARE.
// */
//
//package com.chingo247.settlercraft.structureapi.structure.plan.menu;
//
//
//import com.chingo247.menu.item.CategoryTradeItem;
//import com.chingo247.menu.item.TradeItem;
//import com.chingo247.menu.util.ShopUtil;
//import com.chingo247.settlercraft.structureapi.exception.StructureDataException;
//import com.chingo247.settlercraft.structureapi.plan.StructurePlan;
//import com.chingo247.settlercraft.structureapi.structure.plan.processor.SchematicData;
//import com.sk89q.worldedit.data.DataException;
//import java.io.IOException;
//import java.util.Arrays;
//import org.bukkit.ChatColor;
//import org.bukkit.Material;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.ItemMeta;
//import org.dom4j.DocumentException;
//
///**
// * Represents a StructurePlanItem
// *
// * @author Chingo
// */
//public class StructurePlanItem implements CategoryTradeItem {
//
//    public final String path;
//    public final String name;
//    public final String category;
//    public final String description;
//    public double price;
//    public final int width;
//    public final int height;
//    public final int length;
//    public final int blocks;
//
//    private StructurePlanItem(String path, String name, String category, double price, int width, int height, int length, int blocks, String description) {
//        this.path = path;
//        this.name = name;
//        this.category = category;
//        this.price = price;
//        this.width = width;
//        this.height = height;
//        this.length = length;
//        this.blocks = blocks;
//        this.description = description;
//    }
//
//    public static StructurePlanItem load(SchematicData data, StructurePlan plan) throws IOException, DataException, DocumentException, StructureDataException {
//        
//        
//        
//        
//        if(data == null) {
//            throw new AssertionError("SchematicData was null");
//        }
//
//        int width = data.getWidth();
//        int height = data.getHeight();
//        int length = data.getLength();
//        int blocks = data.getBlocks();
//        String path = plan.getRelativePath();
//        String name = plan.getName();
//        String category = plan.getCategory();
//        String description = plan.getDescription();
//        double price = plan.getPrice();
//
//        
//
//        StructurePlanItem item = new StructurePlanItem(path, name, category, price, width, height, length, blocks, description);
//        return item;
//    }
//
//
//    @Override
//    public String getCategory() {
//        return category;
//    }
//
//    @Override
//    public double getPrice() {
//        return price;
//    }
//
//    @Override
//    public String getName() {
//        return name;
//    }
//
//    @Override
//    public void setPrice(double price) {
//        this.price = price;
//    }
//
//    @Override
//    public ItemStack getItemStack() {
//        ItemStack stack = new ItemStack(Material.PAPER);
//        ItemMeta meta = stack.getItemMeta();
//        meta.setDisplayName(name);
//
//        meta.setLore(Arrays.asList(
//                "Description: " + ((description == null || description.trim().isEmpty()) ? "-" : ChatColor.GOLD + description),
//                "Price: " + (price == 0 ? ChatColor.GOLD + "FREE" : ChatColor.GOLD + ShopUtil.valueString(price)),
//                "Width: " + ChatColor.GOLD + width,
//                "Length: " + ChatColor.GOLD + length,
//                "Height: " + ChatColor.GOLD + height,
//                "Blocks: " + ChatColor.GOLD + ShopUtil.valueString(blocks),
//                "Path: " + ChatColor.GOLD + path,
//                "Type: " + ChatColor.GOLD + "Plan"));
//
//        stack.setItemMeta(meta);
//        return stack;
//    }
//
//    @Override
//    public TradeItem clone() {
//        return new StructurePlanItem(path, name, category, price, width, height, length, blocks, description);
//    }
//
//}
