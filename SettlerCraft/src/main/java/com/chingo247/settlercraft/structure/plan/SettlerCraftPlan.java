/*
 * Copyright (C) 2014 Chingo
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
package com.chingo247.settlercraft.structure.plan;

import com.chingo247.settlercraft.exception.StructureDataException;
import com.chingo247.settlercraft.plugin.SettlerCraft;
import com.chingo247.settlercraft.structure.plan.data.Nodes;
import com.chingo247.settlercraft.structure.plan.data.holograms.StructureHologram;
import com.chingo247.settlercraft.structure.plan.data.holograms.StructureHologramLoader;
import com.chingo247.settlercraft.structure.plan.data.overview.StructureOverview;
import com.chingo247.settlercraft.structure.plan.data.overview.StructureOverviewLoader;
import com.chingo247.settlercraft.structure.plan.data.worldguard.StructureRegionFlag;
import com.chingo247.settlercraft.structure.plan.data.worldguard.StructureRegionFlagLoader;
import com.chingo247.settlercraft.structure.plan.document.PlanDocument;
import com.chingo247.settlercraft.structure.plan.document.PluginElement;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 *
 * @author Chingo
 */
public final class SettlerCraftPlan {

    private File config;
    private File schematic;
    private long checksum;
    private String name;
    private String category = "Default";
    private String faction = "Default";
    private String description = "";
    private Double price = 0.0d;
    private List<StructureOverview> overviews = new ArrayList<>();
    private List<StructureHologram> holograms = new ArrayList<>();
    private List<StructureRegionFlag> flags = new ArrayList<>();
    private PluginElement pluginElement;
    
    

    public void load(PlanDocument planDocument) throws DocumentException, StructureDataException, IOException {
        config = planDocument.getDocumentFile();
        pluginElement = planDocument.getPluginElement(SettlerCraft.getInstance());
        Element scElement = pluginElement.getAsElement();
        
        Node schematicNode = scElement.selectSingleNode(Nodes.SCHEMATIC_NODE);
        if (schematicNode == null) {
            throw new StructureDataException("Missing  Structure Schematic in " + planDocument.getDocumentFile().getAbsolutePath());
        }

        File s = new File(planDocument.getDocumentFile().getParent(), schematicNode.getText());
        if (s.exists()) {
            schematic = s;
        } else {
            throw new FileNotFoundException("Couldn't resolve path for " + s.getAbsolutePath() + " for config:  " + planDocument.getDocumentFile().getAbsolutePath());
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
        faction = pluginElement.getStringValue(Nodes.FACTION_NODE);
        if(faction == null) {
            faction = "Default";
        }
        description = pluginElement.getStringValue(Nodes.DESCRIPTION_NODE);
        if(description == null) {
            description = "-";
        }
        
        try {
        price = pluginElement.getDoubleValue(Nodes.PRICE_NODE);
        } catch(NumberFormatException nfe) {
            throw new StructureDataException("Value of 'Price' must be a number, error generated in " + planDocument.getDocumentFile().getAbsolutePath());
        }
        if(price == null) {
            price = 0.0;
        }
        
        Node structureOverviewsNode = scElement.selectSingleNode(Nodes.STRUCTURE_OVERVIEWS_NODE);
        if(structureOverviewsNode != null) {
            overviews = new StructureOverviewLoader().load((Element)structureOverviewsNode);
        }
        
        Node structureHologramsNode = scElement.selectSingleNode(Nodes.HOLOGRAMS_NODE);
        if(structureOverviewsNode != null) {
            holograms = new StructureHologramLoader().load((Element)structureHologramsNode);
        }

        Node worldGuardFlagsNode = scElement.selectSingleNode(Nodes.WORLDGUARD_FLAGS_NODE);
        if(worldGuardFlagsNode != null) {
            flags = new StructureRegionFlagLoader().load((Element) worldGuardFlagsNode);
        }
    }
    
    public void save() {
        pluginElement.save();
    }

    public long getChecksum() {
        return checksum;
    }
    
    public File getConfig() {
        return config;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getFaction() {
        return faction;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public List<StructureOverview> getOverviews() {
        return overviews;
    }

    public List<StructureHologram> getHolograms() {
        return holograms;
    }

    public List<StructureRegionFlag> getFlags() {
        return flags;
    }

    public PluginElement getPluginElement() {
        return pluginElement;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setOverviews(List<StructureOverview> overviews) {
        this.overviews = overviews;
    }

    public void setHolograms(List<StructureHologram> holograms) {
        this.holograms = holograms;
    }

    public void setFlags(List<StructureRegionFlag> flags) {
        this.flags = flags;
    }

    public boolean addStructureOverview(StructureOverview overview) {
        return overviews.add(overview);
    }

    public boolean removeStructureOverview(StructureOverview overview) {
        return overviews.remove(overview);
    }
    
    public boolean addStructureHologram(StructureHologram hologram) {
        return holograms.add(hologram);
    }

    public boolean removeStructureHologram(StructureHologram hologram) {
        return holograms.remove(hologram);
    }
    
    public boolean addFlag(StructureRegionFlag regionFlag) {
        return flags.add(regionFlag);
    }
    
    public boolean removeFlag(StructureRegionFlag regionFlag) {
        return flags.remove(regionFlag);
    }
    
    
    
    public String getRelativePath() {
        String path = config.getAbsolutePath();
        String minus = "\\plugins\\SettlerCraft\\";
        path = path.substring(path.indexOf(minus) + minus.length());
        int length = path.length();
        path = path.substring(0, length - 4); // minus XML
        return path;
    }
    
    public File getSchematic() {
        return schematic;
    }
    

    

}
