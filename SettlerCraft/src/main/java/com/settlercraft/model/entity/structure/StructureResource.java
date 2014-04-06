/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.entity.structure;

import java.io.Serializable;
import java.util.Objects;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */

public class StructureResource implements Serializable{

    private byte data;
    private boolean checkByteValue;
    private Material material;
    private int amount;

    protected StructureResource() {
    }
    
    public StructureResource(byte data, Material material, int amount) {
        this.data = data;
        this.material = material;
        this.checkByteValue = false;
        this.amount = amount;
    }
    
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

    void setAmount(int amount) {
        this.amount = amount;
    }


    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof StructureResource)) {
            return false;
        }
        StructureResource res = (StructureResource) obj;
        if(checkByteValue == false && res.checkByteValue == false) {
            return this.material == res.material;
        } else {
            return this.material == res.material && this.data == res.data;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.material);
        return hash;
    }


    @Override
    public String toString() {
        return "material: " + material + " data: " + data + " amount: " + amount; 
    }
    
    
    
    
    
    
    
    
    
    
}
