/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.core.model.entity.structure;

import com.avaje.ebean.validation.NotNull;
import com.settlercraft.core.model.world.WorldLocation;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

/**
 * A structure entity is a small object that could be placed around the building in a reserved area
 * Structure entity have a strong functional purpose for the building. It could be a chest where resources are gathered for the building to be constructed
 * or a mailbox for the building to receive materials that are related to the buildings its purpose
 * @author Chingo
 */
@MappedSuperclass
public abstract class StructureEntity {

    
    @NotNull
    @Embedded
    protected WorldLocation wlocation;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    protected Structure structure;

    /**
     * JPA Constructor
     */
    protected StructureEntity() {}

    public StructureEntity(WorldLocation wlocation, Structure structure) {
        this.wlocation = wlocation;
        this.structure = structure;
    }

    public WorldLocation getWlocation() {
        return wlocation;
    }

    public Structure getStructure() {
        return structure;
    }

}
