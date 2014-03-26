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
    float yaw = playerLocation.getYaw();
      System.out.println(yaw);
      
      
      
//        for (int x = 0; x < width; x++) {
//            for (int z = 0; z < length; z++) {
//                Location loc = start.clone().add(x*xMod, 0, z*zMod);
//                System.out.println("X: " + loc.getBlockX() + " Y:"+ loc.getBlockY() +  " Z:" + loc.getBlockZ());
//                loc.getBlock().setType(material);
//            }
//        }
    }
    
    public static void createDefaultFoundation(Location playerLocation, Location start, SchematicObject schematic, Material material) {
      createDefaultFoundation(playerLocation, start, schematic.width, schematic.length, material);
    }


    
    

}
