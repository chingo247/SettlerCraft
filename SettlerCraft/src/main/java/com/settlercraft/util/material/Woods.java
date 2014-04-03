/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.util.material;

import com.google.common.collect.Maps;
import java.util.HashMap;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
public class Woods {
    
    private final HashMap<Material,Float> WOOD = Maps.newHashMap();

    Woods() {
        WOOD.put(Material.LOG, 4.0f);
        WOOD.put(Material.WOOD_DOUBLE_STEP, 1.0f);
        WOOD.put(Material.WORKBENCH, 4.0f);
        WOOD.put(Material.WOODEN_DOOR, 6.0f);
        WOOD.put(Material.STICK, 0.5f);
        WOOD.put(Material.FENCE, 3.0f);
        WOOD.put(Material.LADDER, 3.0f);
        WOOD.put(Material.SIGN, 2.5f);
        WOOD.put(Material.SIGN_POST, 2.5f);
        WOOD.put(Material.WOOD_STAIRS, 1.5f);
        WOOD.put(Material.BIRCH_WOOD_STAIRS, 1.5f);
        WOOD.put(Material.SPRUCE_WOOD_STAIRS, 1.5f);
        WOOD.put(Material.WOOD_STEP, 0.5f);
    }
    
    public boolean isWood(Material material) {
        return WOOD.containsKey(material);
    }
    
    /**
     * Returns the wood value for given material
     * @param material The material
     * @return The wood value of the material, throws AssertionError if material wasnt of type wood
     */
    public float getWoodValue(Material material) {
        if(isWood(material)) return WOOD.get(material);
        throw new AssertionError("Expected material of type wood");
    }
    
    
    
}
