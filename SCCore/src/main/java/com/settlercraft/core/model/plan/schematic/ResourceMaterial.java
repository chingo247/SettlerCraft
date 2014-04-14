/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.core.model.plan.schematic;

import org.bukkit.Material;

/**
 * ResourceMaterial describes what a Block is made of
 * @author Chingo
 */
public class ResourceMaterial {
    /**
     * The Material 
     */
    public Material material;
    /**
     * The Byte value
     */
    public Byte data;
    
    protected ResourceMaterial(){}

    public ResourceMaterial(int material, byte data) {
        this.material = Material.getMaterial(material);
        this.data = data;
    }
    
    public ResourceMaterial(Material material, byte data) {
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
        if(! (obj instanceof ResourceMaterial)) {
            return false;
        }
        ResourceMaterial res = (ResourceMaterial) obj;
        return this.data.equals(res.data) && this.material == res.material;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + this.material.getId();
        hash = 73 * hash + this.data;
        return hash;
    }
    
}
