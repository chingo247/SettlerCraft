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
     * @param start The startLocation
     * @param width The width of the foundation
     * @param length The length of the foundation
     * @param material The material which will be used to build the foundation
     */
    public static void createDefaultFoundation(Location start, int width, int length, Material material) {
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < length; z++) {
                start.clone().add(x, start.getBlockX(), z).getBlock().setType(material);
            }
        }
    }

    /**
     * Creates a foundation that covers an entire square
     * @param start The startLocation
     * @param obj The schematic object associated with this foundation
     * @param material The material which will be used to build the foundation
     */
    public static void createDefaultFoundation(Location start, SchematicObject obj, Material material) {
        createDefaultFoundation(start, obj.width, obj.length, material);
    }
    
     /**
     * Creates a foundation that covers an entire square
     * @param start The startLocation
     * @param schematic The schematic file associated with this foundation
     * @param material The material which will be used to build the foundation
     */
    public static void createDefaultFoundation(Location start, File schematic, Material material) {
        SchematicObject obj = SchematicUtil.readFile(schematic);
        createDefaultFoundation(start, obj.width, obj.length, material);
    }
    
    

}
