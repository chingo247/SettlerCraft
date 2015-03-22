package com.chingo247.settlercraft.world;

import java.io.File;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Chingo
 */
public class WorldConfig {
    
    private File file;
    private boolean allowsStructures;
    private boolean zonesOnly;
    
    protected WorldConfig(WorldConfig config) {
        this.file = config.file;
        this.allowsStructures = config.allowsStructures;
        this.zonesOnly = config.zonesOnly;
    }
    
    private WorldConfig() {
    }

    public boolean isZonesOnly() {
        return zonesOnly;
    }

    public void setAllowsStructures(boolean allowsStructures) {
        this.allowsStructures = allowsStructures;
    }

    public boolean allowsStructures() {
        return allowsStructures;
    }

    public void setZonesOnly(boolean zonesOnly) {
        this.zonesOnly = zonesOnly;
    }
    
//    public static WorldConfig load(File file) {
//        throw new UnsupportedOperationException();
//    }
//    
//    public static WorldConfig createDefault(File file) {
//        throw new UnsupportedOperationException();
//    }
    
    
}
