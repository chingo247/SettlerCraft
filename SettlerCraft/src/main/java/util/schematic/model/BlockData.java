/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.schematic.model;

import org.bukkit.Material;





/**
 *
 * @author Chingo
 */
public class BlockData implements Comparable<BlockData> {
    public final int data;
    public final int x;
    public final int z;
    public final int layer;
    public final int material;

    public BlockData(int x, int z, int layer, int material, int data) {
        this.x = x;
        this.z = z;
        this.layer = layer;
        this.material = material;
        this.data = data;
    }

    @Override
    public int compareTo(BlockData o) {
        if (layer > o.layer) {
            return 1;
        } else if (layer == o.layer) {
            if (z > o.z) {
                return 1;
            } else if (z == o.z) {
                    if(x > o.x) {
                        return 1;
                    } else {
                        return -1;
                    }
                        
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }
    
    /**
     * Attempts to get the material of this block otherwise returns null
     * @return 
     */
    public Material getMaterial() {
        return Material.getMaterial(material);
    }
    
    /**
     * Returns wheter the material is woodplanks or woodplanks slab or double slab
     * @param material
     * @return 
     */
    public boolean isWoodPlanks(int material) {
        return material == 5 || material == 125;
    }
    
    public enum SAPPLING {
        OAK(0),
        SPRUCE(1),
        BIRCH(2),
        JUNGLE(3),
        ACACIA(4),
        DARK_OAK(5);
        
        private int value;
        
        SAPPLING(int value) {
            this.value = value;
        }
        
        public SAPPLING getSappling(int value) {
            for(SAPPLING s : SAPPLING.values()) {
                if(s.value == value) return s;
            }
            return null;
        }
    }
    
    
    
    public boolean isSappling(int material) {
        return material == 6;
    }
    
    public boolean isLOG(int material) {
        return material == 17;
    }
    
    public boolean isLeaves(int material) {
        return material == 18;
    }
    
}
