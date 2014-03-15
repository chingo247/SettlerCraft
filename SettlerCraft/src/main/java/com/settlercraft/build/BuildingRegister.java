/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.build;

import com.settlercraft.model.structure.Structure;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Chingo
 */
public class BuildingRegister {
    public static final String BUILDING_FILE_NAME = "Building.schematic";
    public static final String BUILDING_FOUNDATION_FILE_NAME = "BuildingFoundation.schematic";
    public static final String BUILDING_FILE_INFO = "Building.yml";
    
    private Set<Structure> structures = new HashSet<>();

    public void registerBuildings(File buildingFolder) {
        for (File folder : buildingFolder.listFiles()) {
            if (folder.isDirectory()) {
                processBuilding(folder);
            }
        }
    }

    private void processBuilding(File folder) {
       
    }
    
    private boolean validFolder(File folder) {
        boolean hasBuildingFile = false;
        boolean hasBuildingInfoFile = false;
        for(File file : folder.listFiles()) {
            if(file.getName().equals(BUILDING_FILE_INFO)) hasBuildingInfoFile = true;
            if(file.getName().equals(BUILDING_FILE_NAME)) hasBuildingFile = true;
        }
        return hasBuildingFile && hasBuildingInfoFile;
    }
    
    private boolean hasFoundationFile(File folder) {
        boolean hasFoundationFile = false;
        for(File file : folder.listFiles()) {
            if(file.getName().equals(BUILDING_FOUNDATION_FILE_NAME)) hasFoundationFile = true;
        }
        return hasFoundationFile;
    }



 

}
