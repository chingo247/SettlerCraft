/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structureapi.plan;

import java.io.File;

/**
 * Supports generating StructurePlans from schematics, advanced options incoming later
 * @author Chingo
 */
public abstract class StructurePlanGenerator {
    
    
    
    public void generate(File sourceFolder, File destinationFolder) {
        // Generate plans
    }
    
    public void generate(File sourceFolder, File destinationFolder, StructurePlan plan) {
        // Generate plans
    }
    
    public abstract StructurePlan load();
    
}
