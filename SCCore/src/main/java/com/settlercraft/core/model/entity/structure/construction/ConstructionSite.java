/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.model.entity.structure.construction;

import com.settlercraft.core.model.entity.structure.Structure;
import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author Chingo
 *
 */
@Entity
public class ConstructionSite implements Serializable {

    @OneToOne(cascade = CascadeType.ALL)
    private final Structure structure;

//    @OneToOne(cascade = CascadeType.ALL)
//    private LayerRequirement layerRequirement;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "chest_id")
//    private StructureChest structureChest;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "sign_id")
//    private StructureProgressSign structureSign;
    

    @Id
    @GeneratedValue
    private Long id;

    /**
     * JPA Constructor
     */
    protected ConstructionSite() {
        this.structure = null;
    }

    public ConstructionSite(Structure structure) {
        this.structure = structure;
    }

    public Long getId() {
        return id;
    }

    public Structure getStructure() {
        return structure;
    }
    
    
    
    public void proceed() {
        switch(structure.getStatus()) {
            case BUILDING_IN_PROGRESS: return; // Nothing to do here
            case COMPLETE: return; // remove me // Structure Complete Event!
            case CLEARING_SITE: 
                Builder.clearSite(structure);
                Builder.clearSiteFromEntities(structure);
                structure.setStatus(Structure.STATE.PLACING_FOUNDATION);
            case PLACING_FOUNDATION: 
                Builder.placeDefaultFoundation(structure);
                structure.setStatus(Structure.STATE.PLACING_FRAME);
            case PLACING_FRAME: 
                Builder.placeFrame(structure);
                structure.setStatus(Structure.STATE.BUILDING_IN_PROGRESS);
                break;
            default: throw new AssertionError("Unreachable");
        }
    }
    
    

}
