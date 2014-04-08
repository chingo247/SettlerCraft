/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.entity.structure;

import com.settlercraft.model.plan.requirement.material.LayerRequirement;
import com.settlercraft.model.plan.requirement.material.MaterialRequirement;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Chingo
 */
@Entity
@Table(name = "Structure_Progress")
public class StructureProgress implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    
    @Embedded
    private LayerRequirement resources;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "structure")
    private Structure structure;

//    @Column(name = "layer")
//    private int currentLayer;

    public StructureProgress() {
    }

    public StructureProgress(Structure structure) {
//        this.currentLayer = 0;
        this.resources = structure.getPlan().getRequirement().getMaterialRQ().getLayerRequirement(0);
        this.structure = structure;
    }




//    /**
//     * Gets the currentLayer this structure is building
//     *
//     * @return the currentLayer
//     */
//    public int getCurrentLayer() {
//        return currentLayer;
//    }
//
//    /**
//     * Sets the currentLayer this structure is building
//     *
//     * @param currentLayer The currentLayer
//     */
//    public void setCurrentLayer(int currentLayer) {
//        this.currentLayer = currentLayer;
//    }

    public Long getId() {
        return id;
    }

}
