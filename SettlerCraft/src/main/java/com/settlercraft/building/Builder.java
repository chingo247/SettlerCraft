/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.building;

import com.settlercraft.model.structure.StructurePlan;
import com.settlercraft.util.Foundations;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class Builder {
  
  private Builder(){}
  
  
  public static void buildStructure(Player builder, StructurePlan plan, Location location) {
    // Place foundation
    Foundations.createDefaultFoundation(builder.getLocation(), location, plan.getSchematic(), Material.COBBLESTONE);
    // Place Chest
    
  }
}
