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
package com.sc.api.structure.entity.plan;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Embeddable;
import javax.persistence.Lob;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Chingo
 */
@Embeddable
public class StructurePlan implements Serializable {

    private final String id;
    private final String displayName;
    @Lob
    private final File structureSchematic;
    private String category = "default";
    private String faction = "default";
    private String description;
    private int startHeight = 0;
    private double price = 0.0;
    
    

    /**
     * JPA Constructor
     */
    protected StructurePlan() {
        this.structureSchematic = null;
        this.id = null;
        this.displayName = null;
    }

    public StructurePlan(String id, String displayName, File schematic) throws IOException, DataException {
        this.id = id;
        this.displayName = displayName;
        this.structureSchematic = schematic;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartY(int startY) {
        this.startHeight = startY;
    }

    public CuboidClipboard getSchematic() {
        try {
            CuboidClipboard structure = SchematicFormat.getFormat(structureSchematic).load(structureSchematic);
            return structure;
        } catch (IOException | DataException ex) {
            Logger.getLogger(StructurePlan.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double cost) {
        this.price = cost;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getFaction() {
        return faction;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getHeight() {
        return startHeight;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StructurePlan other = (StructurePlan) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
        return hash;
    }

    public static boolean isStructurePlan(ItemStack itemStack) {
        List<String> lore = itemStack.getItemMeta().getLore();
        if(lore.isEmpty()) return false;
        else {
            for(String s : lore) {
                if(s.contains("[Type]") && s.contains("Plan")){
                    return true;
                }
            }
            return false;
        }
    }
    
    public static String getPlanID(ItemStack itemStack) {
        if(isStructurePlan(itemStack)) {
            List<String> lore = itemStack.getItemMeta().getLore();
            for(String s : lore) {
                if(s.contains("Id")){
                    s = s.substring(s.indexOf(":") + 1);
                    s = ChatColor.stripColor(s);
                    return s.trim();
                }
            }
        } 
        return null;
    }
    
}
