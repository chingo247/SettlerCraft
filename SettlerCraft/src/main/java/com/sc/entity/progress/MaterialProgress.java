package com.sc.entity.progress;

///*
// * Copyright (C) 2014 Chingo
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package com.sc.api.structure.entity.progress;
//
//import com.sc.api.structure.entity.Structure;
//import com.sc.api.structure.entity.schematic.SchematicBlockReport;
//import com.sc.api.structure.entity.schematic.SchematicMaterialLayer;
//import com.sc.api.structure.entity.schematic.SchematicMaterialResource;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
//import javax.persistence.OneToMany;
//import javax.persistence.OneToOne;
//import org.hibernate.annotations.Cascade;
//
///**
// *
// * @author Chingo
// */
//@Entity
//public class MaterialProgress implements Serializable {
//
//    @Id
//    @GeneratedValue
//    private Long id;
//
//    private int currentLayer = 0;
//
//    @OneToOne(fetch = FetchType.EAGER)
//    @Cascade(org.hibernate.annotations.CascadeType.ALL)
//    private Structure structure;
//
//    @OneToMany
//    @Cascade(org.hibernate.annotations.CascadeType.ALL)
//    private List<MaterialLayerProgress> layerRequirements;
//
//    /**
//     * JPA Constructor.
//     */
//    protected MaterialProgress() {
//    }
//
//    /**
//     * Constructor.
//     *
//     * @param blockReport
//     * @param structure The structure
//     */
//    public MaterialProgress(Structure structure, SchematicBlockReport blockReport) {
//        this.layerRequirements = new ArrayList<>(blockReport.getHeight());
//        this.structure = structure;
//        this.setProgressLayers(blockReport.getLayerRequirements());
//    }
//
//    private void setProgressLayers(List<SchematicMaterialLayer> layerRqs) {
//        for (SchematicMaterialLayer lr : layerRqs) {
//            MaterialLayerProgress progressLayer = new MaterialLayerProgress(this, lr.getLayer());
//            for (SchematicMaterialResource materialResource : lr.getResources()) {
//                progressLayer.addResource(new MaterialResourceProgress(
//                        progressLayer,
//                        materialResource.getMaterial(),
//                        materialResource.getData(),
//                        materialResource.getAmount()));
//            }
//            layerRequirements.add(progressLayer);
//        }
//    }
//
//    public boolean setNext() {
//        if (layerRequirements.get(currentLayer).getResources().isEmpty() && currentLayer < layerRequirements.size()) {
//            currentLayer++;
//            return true;
//        }
//        return false;
//    }
//
//    public MaterialLayerProgress getCurrentLayerRequirement() {
//        return layerRequirements.get(currentLayer);
//    }
//
//    public MaterialLayerProgress getLayerRequirement(int index) {
//        return layerRequirements.get(index);
//    }
//
//    public int size() {
//        return layerRequirements.size();
//    }
//
//    public Structure getStructure() {
//        return structure;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//}
