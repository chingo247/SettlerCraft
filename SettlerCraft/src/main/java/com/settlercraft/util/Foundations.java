/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.util;

import com.settlercraft.util.schematic.model.SchematicObject;
import com.settlercraft.util.schematic.util.SchematicUtil;
import java.io.File;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
public class Foundations {
  
    

    private Foundations(){}
    
    /**
     * Creates a foundation that covers an entire square
     * @param playerLocation The location of the player
     * @param start The startLocation
     * @param width The width of the foundation
     * @param length The length of the foundation
     * @param material The material which will be used to build the foundation
     */
    public static void createDefaultFoundation(Location playerLocation, Location start, int width, int length, Material material) {
      Location direction = playerLocation.subtract(start);
      int zMod;
      if(direction.getZ() < 0) { 
        zMod = -1;
      } else {
        zMod = 1;
      }
      
      int xMod;
      if(direction.getZ() < 0) { 
        xMod = -1;
      } else {
        xMod = 1;
      }
        for (int x = 0; x < width; x+=xMod) {
            for (int z = 0; z < length; z+= zMod) {
                start.clone().add(x, start.getBlockX(), z).getBlock().setType(material);
            }
        }
    }
    
    public static void createDefaultFoundation(Location playerLocation, Location start, SchematicObject schematic, Material material) {
      createDefaultFoundation(playerLocation, start, schematic.width, schematic.length, material);
    }


    
    

}
