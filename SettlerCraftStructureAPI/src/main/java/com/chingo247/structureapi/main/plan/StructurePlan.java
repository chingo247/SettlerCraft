/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.main.plan;

import com.chingo247.structureapi.main.exception.StructureDataException;
import com.chingo247.structureapi.main.plan.document.AbstractDocument;
import com.chingo247.structureapi.main.plan.document.DocumentPluginElement;
import com.chingo247.structureapi.bukkit.holograms.holograms.StructureHologram;
import com.chingo247.structureapi.bukkit.holograms.holograms.StructureHologramLoader;
import com.chingo247.structureapi.bukkit.holograms.overview.StructureOverview;
import com.chingo247.structureapi.bukkit.holograms.overview.StructureOverviewLoader;
import com.chingo247.structureapi.main.plan.worldguard.StructureRegionFlag;
import com.chingo247.structureapi.main.plan.worldguard.StructureRegionFlagLoader;
import com.chingo247.structureapi.main.util.Elements;
import com.chingo247.structureapi.main.util.Nodes;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author Chingo
 */
public class StructurePlan  {
    
    private File xmlFile;
    private File schematic;
    private long checksum;
    private String name;
    private String category = "Default";
    private String description = "";
    private Double price = 0.0d;
    private DocumentPluginElement pluginElement;
    private List<StructureRegionFlag> regionFlags;
    private List<StructureOverview> overviews = new ArrayList<>();
    private List<StructureHologram> holograms = new ArrayList<>();
    
    public void save() {
        pluginElement.setValue(Elements.NAME, name);
        pluginElement.setValue(Elements.DESCRIPTION, description);
        pluginElement.setValue(Elements.CATEGORY, category);
        pluginElement.setValue(Elements.PRICE, price);
        pluginElement.save();
    }

    public void load(AbstractDocument document) throws StructureDataException, IOException {
        xmlFile = document.getDocumentFile();
        pluginElement =  document.getPluginElement(Bukkit.getPluginManager().getPlugin("SettlerCraft"));
        
        Element scElement = pluginElement.getAsElement();
        
        Node schematicNode = scElement.selectSingleNode(Nodes.SCHEMATIC_NODE);
        if (schematicNode == null) {
            throw new StructureDataException("Missing  Structure Schematic in " + document.getDocumentFile().getAbsolutePath());
        }

        File s = new File(document.getDocumentFile().getParent(), schematicNode.getText());
        if (s.exists()) {
            schematic = s;
        } else {
            throw new FileNotFoundException("Couldn't resolve path for " + s.getAbsolutePath() + " for config:  " + document.getDocumentFile().getAbsolutePath());
        }
        
        checksum = FileUtils.checksumCRC32(s);

        name = pluginElement.getStringValue(Nodes.NAME_NODE);
        if(name == null) {
            name = FilenameUtils.getBaseName(schematic.getName());
        }
        category = pluginElement.getStringValue(Nodes.CATEGORY_NODE);
        if(category == null) {
            category = "Default";
        }
        
        description = pluginElement.getStringValue(Nodes.DESCRIPTION_NODE);
        if(description == null) {
            description = "-";
        }
        
        try {
        price = pluginElement.getDoubleValue(Nodes.PRICE_NODE);
        } catch(NumberFormatException nfe) {
            throw new StructureDataException("Value of 'Price' must be a number, error generated in " + document.getDocumentFile().getAbsolutePath());
        }
        if(price == null) {
            price = 0.0;
        }
        
        Node worldGuardFlagsNode = scElement.selectSingleNode(Nodes.WORLDGUARD_FLAGS_NODE);
        if(worldGuardFlagsNode != null) {
            regionFlags = new StructureRegionFlagLoader().load((Element) worldGuardFlagsNode);
        } else {
            regionFlags = new LinkedList<>();
        }
        
        
        Node structureOverviewsNode = scElement.selectSingleNode(Nodes.STRUCTURE_OVERVIEWS_NODE);
        if(structureOverviewsNode != null) {
            overviews = new StructureOverviewLoader().load((Element)structureOverviewsNode);
        } else {
            overviews = new LinkedList<>();
        }
        
        Node structureHologramsNode = scElement.selectSingleNode(Nodes.HOLOGRAMS_NODE);
        if(structureOverviewsNode != null) {
            holograms = new StructureHologramLoader().load((Element)structureHologramsNode);
        } else {
            holograms = new LinkedList<>();
        }
    }

    public File getConfig() {
        return xmlFile;
    }

    public File getSchematic() {
        return schematic;
    }

    public List<StructureRegionFlag> getRegionFlags() {
        return regionFlags;
    }

    public List<StructureHologram> getHolograms() {
        return holograms;
    }

    public List<StructureOverview> getOverviews() {
        return overviews;
    }
    
    public long getChecksum() {
        return checksum;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public DocumentPluginElement getPluginElement() {
        return pluginElement;
    }
    
    public String getRelativePath() {
        return pluginElement.getRoot().getRelativePath();
    }
    
    public static boolean isStructurePlan(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        if (itemStack.getType() != Material.PAPER) {
            return false;
        }

        List<String> lore = itemStack.getItemMeta().getLore();
        if (lore.isEmpty()) {
            return false;
        } else {
            for (String s : lore) {
                if (s.contains("Type") && s.contains("Plan")) {
                    return true;
                }
            }
            return false;
        }
    }

    public static String getPlanID(ItemStack itemStack) {
        if (isStructurePlan(itemStack)) {
            List<String> lore = itemStack.getItemMeta().getLore();
            for (String s : lore) {
                if (s.contains("Path")) {
                    s = s.substring(s.indexOf(":") + 1);
                    s = ChatColor.stripColor(s);
                    return s.trim();
                }
            }
        }
        return null;
    }
    
    public static double getValue(ItemStack itemStack) {
        double price = 0;
        if (isStructurePlan(itemStack)) {
           
            List<String> lore = itemStack.getItemMeta().getLore();
            for (String s : lore) {
                if (s.contains("Price")) {
                    s = s.substring(s.indexOf(":") + 1);
                    s = ChatColor.stripColor(s);
                    if(s.contains("FREE")) {
                        return 0;
                    }
                    
                    try {
                        price = Double.parseDouble(s.trim());
                    } catch (NumberFormatException nfe) {
                        return 0;
                    }
                    return price;
                }
            }
        }
        return price;
    }

    
}
