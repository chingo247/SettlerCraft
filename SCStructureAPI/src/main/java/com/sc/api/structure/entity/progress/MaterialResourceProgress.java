/*
 * Copyright (C) 2014 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sc.api.structure.entity.progress;

import com.sc.api.structure.entity.schematic.SchematicMaterialResourceId;
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
public class MaterialResourceProgress implements Serializable {

    @ManyToOne(cascade = CascadeType.ALL)
    private MaterialLayerProgress layerRequirement;

    @Embedded
    private SchematicMaterialResourceId materialResourceId;
    @Id
    private Long id;

    private int amount;

    /**
     * JPA Constructor.
     */
    protected MaterialResourceProgress() {
    }

    /**
     * Constructor.
     *
     * @param materialProgressLayer The parent layer requirement which holds this material resources as
     * element of a collection
     * @param material The material
     * @param amount The amount
     */
    public MaterialResourceProgress(MaterialLayerProgress materialProgressLayer, Material material, byte data, int amount) {
        this.materialResourceId = new SchematicMaterialResourceId(material, data);
        this.layerRequirement = materialProgressLayer;
        this.amount = amount;
    }

    public MaterialResourceProgress(MaterialLayerProgress layerRequirement, Material material, Integer data, int amount) {
        this(layerRequirement, material, data.byteValue(), amount);
    }

    public MaterialLayerProgress getMaterialLayerProgress() {
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
