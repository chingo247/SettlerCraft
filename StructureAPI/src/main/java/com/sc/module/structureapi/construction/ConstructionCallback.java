/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.structureapi.construction;

import com.sc.module.structureapi.construction.asyncworldedit.SCJobEntry;
import com.sc.module.structureapi.persistence.ConstructionSiteService;
import com.sc.module.structureapi.structure.ConstructionSite;
import com.sc.module.structureapi.structure.StructureHologramManager;
import java.util.UUID;
import org.bukkit.plugin.Plugin;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;


/**
 *
 * @author Chingo
 */
public abstract class ConstructionCallback {
    
    protected final UUID issuer;
    protected final ConstructionSite constructionSite;
    protected final Plugin plugin;
    
    ConstructionCallback(Plugin plugin, UUID issuer, final ConstructionSite cosntructionSite) {
        this.issuer = issuer;
        this.constructionSite = cosntructionSite;
        this.plugin = plugin;
    }
    
    public abstract void onJobAdded(SCJobEntry entry);
    

    public void onJobCanceled(JobEntry entry) {
        ConstructionSiteService siteService = new ConstructionSiteService();
        siteService.setState(constructionSite, ConstructionSite.State.STOPPED);
        // Update Hologram
        StructureHologramManager.getInstance().updateHolo(plugin, constructionSite.getStructure());
        
        ConstructionManager.getInstance().getEntry(constructionSite.getId()).setJobId(-1);
    }
    
    
}
