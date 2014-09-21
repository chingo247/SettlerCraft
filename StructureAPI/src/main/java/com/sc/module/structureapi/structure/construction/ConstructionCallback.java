/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.structureapi.structure.construction;

import com.sc.module.structureapi.persistence.ConstructionSiteService;
import com.sc.module.structureapi.structure.Structure;
import com.sc.module.structureapi.structure.Structure.State;
import com.sc.module.structureapi.structure.StructureHologramManager;
import com.sc.module.structureapi.structure.construction.asyncworldedit.SCJobEntry;
import java.util.UUID;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;


/**
 *
 * @author Chingo
 */
public abstract class ConstructionCallback {
    
    protected final UUID issuer;
    protected final Structure structure;
    
    protected ConstructionCallback(UUID issuer, final Structure structure) {
        this.issuer = issuer;
        this.structure = structure;
    }
    
    public abstract void onJobAdded(SCJobEntry entry);
    

    public void onJobCanceled(JobEntry entry) {
        ConstructionSiteService siteService = new ConstructionSiteService();
        siteService.setState(structure, State.STOPPED);
        // Update Hologram
        StructureHologramManager.getInstance().updateHolo(structure);
        
        ConstructionManager.getInstance().getEntry(structure.getId()).setJobId(-1);
    }
    
    
}
