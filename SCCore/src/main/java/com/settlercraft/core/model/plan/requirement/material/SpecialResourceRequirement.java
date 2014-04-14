/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.model.plan.requirement.material;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
@Entity
public class SpecialResourceRequirement implements Serializable  {

    @Id
    @GeneratedValue
    protected Long id;

    @NotNull
    protected final Material material;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    protected LayerRequirement layerRequirement;

    protected int amount;

    protected final Byte byteValue;

    protected SpecialResourceRequirement() {
        this.material = null;
        this.byteValue = null;
    }

    public SpecialResourceRequirement(LayerRequirement layerRequirement, Material material, Byte byteValue, int amount) {
        this.material = material;
        this.layerRequirement = layerRequirement;
        this.amount = amount;
        this.byteValue = byteValue;
    }

    public Long getId() {
        return id;
    }

    public LayerRequirement getLayerRequirement() {
        return layerRequirement;
    }

    public void setLayerRequirement(LayerRequirement layerRequirement) {
        this.layerRequirement = layerRequirement;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


    public Byte getData() {
        return byteValue;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.material);
        hash = 67 * hash + Objects.hashCode(this.byteValue);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SpecialResourceRequirement other = (SpecialResourceRequirement) obj;
        if (this.material != other.material) {
            return false;
        }
        return Objects.equals(this.byteValue, other.byteValue);
    }


    
    

}
