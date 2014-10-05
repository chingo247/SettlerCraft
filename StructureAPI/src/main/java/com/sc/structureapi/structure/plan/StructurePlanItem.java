/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.structureapi.structure.plan;

import com.sc.module.menuapi.menus.menu.item.CategoryTradeItem;
import com.sc.module.menuapi.menus.menu.item.TradeItem;
import com.sc.module.menuapi.menus.menu.util.ShopUtil;
import com.sc.structureapi.exception.StructureDataException;
import com.sc.structureapi.structure.plan.data.Nodes;
import com.sc.structureapi.structure.schematic.Schematic;
import com.sc.structureapi.structure.schematic.SchematicManager;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.util.Countable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

/**
 * Represents a StructurePlanItem
 *
 * @author Chingo
 */
public class StructurePlanItem implements CategoryTradeItem {

    public final String path;
    public final String name;
    public final String category;
    public final String faction;
    public final String description;
    public double price;
    public final int width;
    public final int height;
    public final int length;
    public final int blocks;

    private StructurePlanItem(String path, String name, String category, String faction, double price, int width, int height, int length, int blocks, String description) {
        this.path = path;
        this.name = name;
        this.category = category;
        this.faction = faction;
        this.price = price;
        this.width = width;
        this.height = height;
        this.length = length;
        this.blocks = blocks;
        this.description = description;
    }

    public static StructurePlanItem load(StructurePlan plan) throws IOException, DataException, DocumentException, StructureDataException {
        File cfg = plan.getConfigXML();
        File sch = plan.getSchematic();

        int width;
        int height;
        int length;
        int blocks;

        Schematic schematic = SchematicManager.getInstance().getSchematic(sch);

//        CuboidClipboard clip = SchematicFormat.MCEDIT.load(sch);
        width = schematic.getWidth();
        length = schematic.getLength();
        height = schematic.getHeight();
        blocks = schematic.getBlocks();
        
        SAXReader reader = new SAXReader();
        Document config = reader.read(cfg);

        String path = StructurePlanManager.getInstance().getRelativePath(cfg);
        String name;
        String category;
        String faction;
        String description;
        double price;

        Node nameNode = config.selectSingleNode(Nodes.NAME_NODE);
        Node categoryNode = config.selectSingleNode(Nodes.CATEGORY_NODE);
        Node factionNode = config.selectSingleNode(Nodes.FACTION_NODE);
        Node priceNode = config.selectSingleNode(Nodes.PRICE_NODE);
        Node descriptionNode = config.selectSingleNode(Nodes.DESCRIPTION_NODE);

        if (nameNode == null) {
            throw new StructureDataException("Missing name node for: " + cfg.getAbsolutePath());
        }
        name = nameNode.getText();

        category = categoryNode != null ? categoryNode.getText() : "All";
        faction = factionNode != null ? factionNode.getText() : "Default";

        try {
            price = priceNode != null ? Double.parseDouble(priceNode.getText()) : 0d;
        } catch (NumberFormatException nfe) {
            throw new StructureDataException("Invalid price value for: " + cfg.getAbsolutePath());
        }

        if (descriptionNode != null) {
            description = descriptionNode.getText();
        } else {
            description = null;
        }

        StructurePlanItem item = new StructurePlanItem(path, name, category, faction, price, width, height, length, blocks, description);
        return item;
    }

    private static int getBlocks(CuboidClipboard clip) {
        int blocks = 0;

        for (Countable<Integer> b : clip.getBlockDistribution()) {
            if (b.getID() != BlockID.AIR) {
                blocks += b.getAmount();
            }
        }
        return blocks;
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
        StructurePlanItem item = new StructurePlanItem(path, name, category, faction, price, width, height, length, blocks, description);
        return item;
    }

}
