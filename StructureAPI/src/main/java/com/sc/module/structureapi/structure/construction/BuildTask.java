/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.structureapi.structure.construction;

import java.util.UUID;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;

/**
 *
 * @author Chingo
 */
public class BuildTask implements Runnable {
    
    private final ConstructionEntry entry;
    private final UUID issuer;

    public BuildTask(ConstructionEntry entry, UUID issuer) {
        if(entry == null) {
            throw new AssertionError("Null entry");
        }
        if(issuer == null) {
            throw new AssertionError("Null issuer");
        }
        
        this.entry = entry;
        this.issuer = issuer;
    }
    
    

    @Override
    public void run() {
        if(entry.getJobId() != -1) {
              AsyncWorldEditMain.getInstance().getBlockPlacer().cancelJob(entry.getPlayer(), entry.getJobId());
        }
        
        
        
    }
    
    
    
}
