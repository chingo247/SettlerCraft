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

import com.sc.api.structure.entity.Structure;
import com.sc.api.structure.entity.schematic.SchematicBlockReport;
import com.sc.api.structure.entity.schematic.SchematicMaterialLayer;
import com.sc.api.structure.entity.schematic.SchematicMaterialResource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 *
 * @author Chingo
 */
@Entity
public class StructureProgress implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    private int currentLayer = 0;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Structure structure;

    @OneToMany(cascade = CascadeType.ALL)
    private List<StructureProgressLayer> layerRequirements;

    /**
     * JPA Constructor.
     */
    protected StructureProgress() {
    }

    /**
     * Constructor.
     *
     * @param blockReport
     * @param structure The structure
     */
    public StructureProgress(Structure structure, SchematicBlockReport blockReport) {
        this.layerRequirements = new ArrayList<>(blockReport.getHeight());
        this.structure = structure;
        this.setProgressLayers(blockReport.getLayerRequirements());
    }

    private void setProgressLayers(List<SchematicMaterialLayer> layerRqs) {
        for (SchematicMaterialLayer lr : layerRqs) {
            StructureProgressLayer progressLayer = new StructureProgressLayer(this, lr.getLayer());
            for (SchematicMaterialResource materialResource : lr.getResources()) {
                progressLayer.addResource(new StructureProgressMaterialResource(
                        progressLayer,
                        materialResource.getMaterial(),
                        materialResource.getData(),
                        materialResource.getAmount()));
            }
            layerRequirements.add(progressLayer);
        }
    }

    public boolean setNext() {
        if (layerRequirements.get(currentLayer).getResources().isEmpty() && currentLayer < layerRequirements.size()) {
            currentLayer++;
            return true;
        }
        return false;
    }

    public StructureProgressLayer getCurrentLayerRequirement() {
        return layerRequirements.get(currentLayer);
    }

    public StructureProgressLayer getLayerRequirement(int index) {
        return layerRequirements.get(index);
    }

    public int size() {
        return layerRequirements.size();
    }

    public Structure getStructure() {
        return structure;
    }

    public Long getId() {
        return id;
    }

}
