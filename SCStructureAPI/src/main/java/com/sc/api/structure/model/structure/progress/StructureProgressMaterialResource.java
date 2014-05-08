/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.api.structure.model.structure.progress;

import com.sc.api.structure.model.structure.schematic.SchematicMaterialResourceId;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
@Entity
public class StructureProgressMaterialResource implements Serializable  {
    @ManyToOne(cascade = CascadeType.ALL)
    private StructureProgressLayer layerRequirement;

    @Embedded
    private SchematicMaterialResourceId materialResourceId;
    @Id
    private Long id;
    
    private int amount;
    
    /**
     * JPA Constructor.
     */
    protected StructureProgressMaterialResource() {}

    /**
     * Constructor.
     *
     * @param layerRequirement The parent layer requirement which holds this material resources as element of a collection
     * @param material The material
     * @param amount The amount
     */
    public StructureProgressMaterialResource(StructureProgressLayer layerRequirement, Material material, byte data, int amount) {
        this.materialResourceId = new SchematicMaterialResourceId(material, data);
        this.layerRequirement = layerRequirement;
        this.amount = amount;
    }
    
    public StructureProgressMaterialResource(StructureProgressLayer layerRequirement, Material material, Integer data, int amount) {
        this(layerRequirement, material, data.byteValue(), amount);
    }

    public StructureProgressLayer getLayerRequirement() {
        return layerRequirement;
    }

    public Long getId() {
        return id;
    }
    
    public Material getMaterial() {
        return materialResourceId.getMaterial();
    }

    public Integer getData() {
        return materialResourceId.getData();
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    
}
