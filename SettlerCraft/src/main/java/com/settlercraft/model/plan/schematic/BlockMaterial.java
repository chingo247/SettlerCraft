/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.plan.schematic;

import org.bukkit.Material;

/**
 * BlockMaterial describes what a Block is made of
 * @author Chingo
 */
public class BlockMaterial {
    /**
     * The Material id
     */
    public final Material material;
    /**
     * The Byte value
     */
    public final Byte data;

    public BlockMaterial(int material, byte data) {
        this.material = Material.getMaterial(material);
        this.data = data;
    }
    
    public BlockMaterial(Material material, byte data) {
        this.material = material;
        this.data = data;
    }


    public byte getData() {
        return data;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof BlockMaterial)) {
            return false;
        }
        BlockMaterial res = (BlockMaterial) obj;
        return this.data == res.data && this.material == res.material;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + this.material.getId();
        hash = 73 * hash + this.data;
        return hash;
    }
    
}
