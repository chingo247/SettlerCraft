/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.entity.structure;

import com.settlercraft.model.plan.requirement.material.LayerRequirement;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
    
    @OneToOne(cascade = CascadeType.ALL)
    private LayerRequirement layerRequirement;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "structure")
    private Structure structure;

    private boolean inProgress;

    /**
     * JPA Constructor.
     */
    protected StructureProgress() {
    }

    public Long getId() {
        return id;
    }

    /**
     * Constructor.
     *
     * @param structure The structure
     */
    public StructureProgress(Structure structure) {
        this.layerRequirement = structure.getPlan().getRequirement().getMaterialRequirement().getLayer(0);
        this.structure = structure;
        this.inProgress = false;
    }

    public LayerRequirement getResources() {
        return this.layerRequirement;
    }

    public boolean setNext() {
        if (!layerRequirement.getBasicResources().isEmpty() || !layerRequirement.getSpecialResources().isEmpty()) {
            throw new AssertionError("Can't move to next stage when layer requirements have not been met");
        }

        if (!hasNext()) {
            return false;
        } else {
            this.layerRequirement = structure.getPlan().getRequirement().getMaterialRequirement().getLayer(layerRequirement.getLayer() + 1);
            return true;
        }
    }

    public boolean hasNext() {
        return layerRequirement.getLayer() < structure.getPlan().getSchematic().layers;
    }




    public Structure getStructure() {
        return structure;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public boolean isInProgress() {
        return inProgress;
    }

}
