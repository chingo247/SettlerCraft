/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.plan;

import com.sc.module.structureapi.menu.item.CategoryTradeItem;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldedit.util.Countable;
import construction.exception.StructurePlanException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import javax.persistence.Id;
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
public class StructurePlanItem  implements CategoryTradeItem {

    @Id
    public final String id;
    public final String name;
    public final String category;
    public final String faction;
    public final double price;
    public final int width;
    public final int height;
    public final int length;
    public final int blocks;

    private StructurePlanItem(String id, String name, String category, String faction, double price, int width, int height, int length, int blocks) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.faction = faction;
        this.price = price;
        this.width = width;
        this.height = height;
        this.length = length;
        this.blocks = blocks;
    }

    public static StructurePlanItem load(StructurePlan plan) throws IOException, DataException, DocumentException, StructurePlanException {
        File cfg = plan.getConfig();
        File sch = plan.getSchematic();

        if (!cfg.exists()) {
            throw new FileNotFoundException("File not found for: " + cfg.getAbsolutePath());
        } else if (!sch.exists()) {
            throw new FileNotFoundException("File not found for: " + sch.getAbsolutePath());
        }

        int width;
        int height;
        int length;
        int blocks;

        CuboidClipboard clip = SchematicFormat.MCEDIT.load(sch);
        width = clip.getWidth();
        length = clip.getLength();
        height = clip.getHeight();
        blocks = getBlocks(clip);

        SAXReader reader = new SAXReader();
        Document config = reader.read(cfg);

        String id;
        String name;
        String category;
        String faction;
        double price;
        
        Node idNode = config.selectSingleNode("StructurePlan/Id");
        Node nameNode = config.selectSingleNode("StructurePlan/Name");
        Node categoryNode = config.selectSingleNode("StructurePlan/Category");
        Node factionNode = config.selectSingleNode("StructurePlan/Faction");
        Node priceNode = config.selectSingleNode("StructurePlan/Price");
        
        if(idNode == null) throw new StructurePlanException("missing id node for: " + cfg.getAbsolutePath());
        id = idNode.getText();
        
        if(nameNode == null) throw new StructurePlanException("Missing name node for: " + cfg.getAbsolutePath());
        name = nameNode.getText();
        
        category = categoryNode != null ? categoryNode.getText() : "All";
        faction = factionNode != null ? factionNode.getText() : "Default";
        
        try {
            price = priceNode != null ? Double.parseDouble(priceNode.getText()) : 0d;
        } catch (NumberFormatException nfe) {
            throw new StructurePlanException("Invalid price value for: " + cfg.getAbsolutePath());
        }
        
        StructurePlanItem item = new StructurePlanItem(id, name, category, faction, price, width, height, length, blocks);
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
    public ItemStack getItemStack() {
        ItemStack stack = new ItemStack(Material.PAPER);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(
                "Price: " + ChatColor.GOLD + price,
                "Width: " + ChatColor.GOLD + width, 
                "Length: " + ChatColor.GOLD + length,
                "Height: " + ChatColor.GOLD + height,
                "Blocks: " + ChatColor.GOLD + blocks,
                "Id: " + ChatColor.GOLD + id
        ));
        stack.setItemMeta(meta);
        return stack;
    }

}
