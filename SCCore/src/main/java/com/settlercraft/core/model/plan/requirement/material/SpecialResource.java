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
public class SpecialResource implements Serializable {

    @Id
    @GeneratedValue
    protected Long id;

    @NotNull
    protected final Material material;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    protected StructureLayer layerRequirement;

    protected int value;

    protected final Byte byteValue;

    protected SpecialResource() {
        this.material = null;
        this.byteValue = null;
    }

    public SpecialResource(StructureLayer layerRequirement, Material material, Byte byteValue, int amount) {
        this.material = material;
        this.layerRequirement = layerRequirement;
        this.value = amount;
        this.byteValue = byteValue;
    }

    public Long getId() {
        return id;
    }

    public StructureLayer getLayerRequirement() {
        return layerRequirement;
    }

    public void setLayerRequirement(StructureLayer layerRequirement) {
        this.layerRequirement = layerRequirement;
    }

    public int getValue() {
        return value;
    }

    public void setAmount(int amount) {
        this.value = amount;
    }

    /**
     * Removes an value of this resource, if the value is higher than the value of this resource
 the resource will be set to 0
     *
     * @param amount The value to substract from this resource
     */
    public void removeAmount(int amount) {
        this.value = value - Math.min(amount, value);
    }

    public Byte getData() {
        return byteValue;
    }

    public Material getMaterial() {
        return material;
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
        final SpecialResource other = (SpecialResource) obj;
        if (this.material != other.material) {
            return false;
        }
        return Objects.equals(this.byteValue, other.byteValue);
    }

}
