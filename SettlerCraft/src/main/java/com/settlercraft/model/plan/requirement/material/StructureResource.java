/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.model.plan.requirement.material;

import java.io.Serializable;
import javax.persistence.Embeddable;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
@Embeddable
public class StructureResource implements Serializable{
    private Byte data;
    private Material material;
    private int amount;

    /**
     * JPA Constructor.
     */
    protected StructureResource() {}
    
    public StructureResource(Material material, int amount) {
        this.data = null;
        this.material = material;
        this.amount = amount;
    }
    
    public StructureResource(Material material, int amount, byte data) {
        this.data = data;
        this.material = material;
        this.amount = amount;
    }

    public byte getData() {
        return data;
    }

    public boolean isSpecial() {
        return data != null;
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
    public String toString() {
        return "material: " + material + " data: " + data + " amount: " + amount; 
    }
    
    
    
    
    
    
    
    
    
    
}
