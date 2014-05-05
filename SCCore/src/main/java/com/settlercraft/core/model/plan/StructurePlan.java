/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.model.plan;

import com.settlercraft.core.model.plan.requirement.StructureRequirement;
import com.settlercraft.core.model.plan.schematic.SchematicObject;
import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumMap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Chingo
 */
public class StructurePlan implements Serializable {

  private String name;
  private String description;
  private String culture;
  private String category;
  private int cost;
  
  private final SchematicObject schematic;
  private final SchematicObject foundation;
  private final StructureRequirement requirement;
  
  private final EnumMap<ReservedSide, Integer> reserved;

  public StructurePlan(SchematicObject structure, String name, String culture, String type, int cost, EnumMap<ReservedSide, Integer> reserved, String description) {
    this.schematic = structure;
    this.requirement = new StructureRequirement(this);
    this.foundation = null;
    this.cost = cost;
    this.category = type;
    this.culture = culture;
    this.name = name;
    this.reserved = reserved;
  }

  public StructurePlan(SchematicObject structure, SchematicObject foundation, String name, String culture, String type, int cost, EnumMap<ReservedSide, Integer> reserved, String description) {
    this.schematic = structure;
    this.requirement = new StructureRequirement(this);
    this.foundation = null;
    this.cost = cost;
    this.category = type;
    this.culture = culture;
    this.name = name;
    this.description = description;
    this.reserved = new EnumMap<>(ReservedSide.class);
  }
  
  
  
  public ItemStack toItemStack(int amount) {
      ItemStack stack = new ItemStack(Material.PAPER, amount);
      ItemMeta meta = stack.getItemMeta();
      meta.setDisplayName(name);
      meta.setLore(Arrays.asList(
              "Culture: " + culture,
              "Category: " + category,
              "Cost: " + cost)
      );
      stack.setItemMeta(meta);
      return stack;
  }

  public StructureRequirement getRequirement() {
    return requirement;
  }

  public SchematicObject getFoundationSchematic() {
    return foundation;
  }
  
  public SchematicObject getStructureSchematic() {
    return schematic;
  }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCulture(String culture) {
        this.culture = culture;
    }
  
  

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getCulture() {
        return culture;
    }

    public String getCategory() {
        return category;
    }

    public SchematicObject getSchematic() {
        return schematic;
    }

    public SchematicObject getFoundation() {
        return foundation;
    }

    public EnumMap<ReservedSide, Integer> getReserved() {
        return reserved;
    }
  
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof StructurePlan)) {
      return false;
    }
    StructurePlan sp = (StructurePlan) o;
    return this.name.equals(sp.name);
  }

}
