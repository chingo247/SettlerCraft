/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.model.structure.schematic;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import org.bukkit.Material;

/**
 * The material and the Integer data value will serve as unique idenitifier for {@link  SchematicMaterialResource}
 * @author Chingo
 */
@Embeddable
public class SchematicMaterialResourceId implements Serializable {
    @NotNull
    private Material material;
    @NotNull
    private Integer data;

    /**
     * JPA Constructor
     */
    protected SchematicMaterialResourceId() {
    }

    /**
     * Constructor.
     * @param material The material
     * @param data The data or Byte value
     */
    public SchematicMaterialResourceId(Material material, Byte data) {
        this.material = material;
        this.data = data.intValue();
    }

    /**
     * Get the data
     * @return The data value 
     */
    public Integer getData() {
        return data;
    }

    /**
     * Get the material
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
        final SchematicMaterialResource other = (SchematicMaterialResource) obj;
        if (this.material != other.getMaterial()) {
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
    
    
    
}
