package com.settlercraft.core.model.plan.requirement.material;

import com.settlercraft.core.model.entity.SettlerCraftEntity;
import com.settlercraft.core.model.entity.structure.StructureProgress;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import org.bukkit.Material;

/**
 * Structure resource contains a material and a value to describe the amount that is needed of this
 * material.
 *
 * @author Chingo
 */
@Entity
public class MaterialResource extends SettlerCraftEntity implements Serializable {

    /**
     * The material.
     */
    @NotNull
    protected final Material material;

    @Id
    @GeneratedValue
    private Long id;
    
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @NotNull
    private StructureProgress progress;
    
    /**
     * The value of this resource
     */
    protected int value;
    

    /**
     * JPA Constructor.
     */
    protected MaterialResource() {
        this.material = null;
        this.progress = null;
    }

    public int getValue() {
        return value;
    }
    
    /**
     * Constructor.
     * @param material The material of this StructureResource
     * @param value The amount that is needed of this resource
     */
    public MaterialResource(Material material, int value) {
        this.material = material;
        this.value = value;
    }


/**
     * Constructor.
     * @param progress The parent entity
     * @param material The material of this StructureResource
     * @param value The amount that is needed of this resource
     */
    public MaterialResource(StructureProgress progress, Material material, int value) {
        this.material = material;
        this.value = value;
        this.progress = progress;
    }

    /**
     * Gets the value
     *
     * @return
     */
    public int getAmount() {
        return value;
    }

    /**
     * Sets the value of this resource
     *
     * @param amount The value
     */
    public void setAmount(int amount) {
        this.value = amount;
    }

    public void setProgress(StructureProgress progress) {
        this.progress = progress;
    }

    public StructureProgress getProgress() {
        return progress;
    }
    
    

    /**
     * Removes an amount of this resource, if the amount is higher than the value of this resource
     * the resource will be set to 0
     *
     * @param amount The amount to substract from this resource
     */
    public void removeAmount(int amount) {
        this.value = value - Math.min(amount, value);
    }

    /**
     * Gets the material
     *
     * @return The material
     */
    public Material getMaterial() {
        return material;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MaterialResource other = (MaterialResource) obj;
        if (this.material != other.material) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.material);
        return hash;
    }

    @Override
    public String toString() {
        return "[Material: " + material + "] : " + value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    

}
