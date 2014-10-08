/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.chingo247.settlercraft.structure.construction;

import com.chingo247.settlercraft.structure.asyncworldedit.SCJobEntry;
import org.primesoft.asyncworldedit.blockPlacer.entries.JobEntry;


/**
 *
 * @author Chingo
 */
public interface ConstructionCallback {
    
    public void onJobAdded(SCJobEntry entry);
    
    public void onJobCanceled(JobEntry entry);
    
}
