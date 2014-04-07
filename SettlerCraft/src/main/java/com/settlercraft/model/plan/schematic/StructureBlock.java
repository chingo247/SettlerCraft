/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.plan.schematic;

import org.bukkit.Material;

/**
 * Structure Block describes what a Block is made of
 * @author Chingo
 */
public class StructureBlock {
    /**
     * The Material id
     */
    public final int material;
    /**
     * The Byte value
     */
    public final byte data;

    public StructureBlock(int material, byte data) {
        this.material = material;
        this.data = data;
    }


    public byte getData() {
        return data;
    }
    
    public Material getMaterial() {
        return Material.getMaterial(material);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof StructureBlock)) {
            return false;
        }
        StructureBlock res = (StructureBlock) obj;
        return this.data == res.data && this.material == res.material;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + this.material;
        hash = 73 * hash + this.data;
        return hash;
    }
    
}
