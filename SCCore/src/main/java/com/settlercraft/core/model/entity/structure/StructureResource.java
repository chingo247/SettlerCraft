package com.settlercraft.core.model.entity.structure;

import com.settlercraft.core.manager.StructurePlanManager;
import com.settlercraft.core.model.plan.requirement.material.StructureLayer;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author Chingo
 */
@Entity
public class StructureResource implements Serializable {
    
    @Id
    @GeneratedValue
    private Long id;
    
    private int totalLayers;
    
    private String plan;
    
    @OneToOne(cascade = CascadeType.ALL)
    private Structure structure;
    
    
    
    @OneToOne(cascade = CascadeType.ALL)
    private StructureLayer requirement;

    /**
     * JPA Constructor.
     */
    protected StructureResource() {
    }

    /**
     * Constructor.
     * @param structure The structure
     */
    public StructureResource(Structure structure) {
        this.structure = structure;
        this.plan = structure.getPlan().getConfig().getName();
        this.requirement = structure.getPlan().getRequirement().getMaterialRequirement().getLayer(0);
        this.totalLayers = structure.getPlan().getSchematic().layers;
    }
    
    /**
     * Sets the currentRequirement to the nextLayer
     */
    public void nextLayer() {
        if(hasNextLayer()) {
            this.requirement = StructurePlanManager.getInstance().getPlan(plan).getRequirement()
                    .getMaterialRequirement().getLayer(requirement.getLayer() + 1);
        }
    }
    
    public void previousLayer() {
        if(hasPreviousLayer()) {
            this.requirement = StructurePlanManager.getInstance().getPlan(plan).getRequirement()
                    .getMaterialRequirement().getLayer(requirement.getLayer() - 1);
        }
    } 
    
    public boolean hasNextLayer() {
        return requirement.getLayer() != StructurePlanManager.getInstance()
                .getPlan(plan).getSchematic().layers;
    }
    
    public boolean hasPreviousLayer() {
        return requirement.getLayer() > 0;
    }

    public StructureLayer getRequirement() {
        return requirement;
    }
    
    
    
    
    
}
