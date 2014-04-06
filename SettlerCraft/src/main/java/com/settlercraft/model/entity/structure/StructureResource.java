/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.entity.structure;

import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
public class StructureResource {
    private final byte data;
    private final boolean checkByteValue;
    private final Material material;
    private final int amount;
    
    public StructureResource(byte data, Material material, int amount, boolean checkByteValue) {
        this.data = data;
        this.material = material;
        this.checkByteValue = checkByteValue;
        this.amount = amount;
    }

    public byte getData() {
        return data;
    }

    public boolean isCheckByteValue() {
        return checkByteValue;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }



    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof StructureResource)) {
            return false;
        }
        StructureResource res = (StructureResource) obj;
        return res.getData() == this.getData() && res.getMaterial() == this.getMaterial();
    }
    
    
    
    
    
    
    
    
    
}
