/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.actions.build;

import com.settlercraft.model.structure.Structure;
import com.settlercraft.util.Structures;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Chingo
 */
public class BuildingRegister {

    
    private Set<Structure> structures = new HashSet<>();
    

    public void registerCustomBuildings(File buildingFolder) {
        String[] extensions = {"yml"};
        Iterator<File> it = FileUtils.iterateFiles(buildingFolder, extensions, true);
        
        while(it.hasNext()) {
            File yamlBuildingFile = it.next();
            System.out.println(yamlBuildingFile.getName());
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(yamlBuildingFile);
            if(yaml.getString("schematic") == null) {
                System.out.println("[SettlerCraft]: " + yamlBuildingFile.getAbsolutePath() + " contains no schematic information, skipping...");
                continue;
            } 
//            File f = new File(file.getParentFile().get + "\\"+ yaml.getString("schematic"));
            File schematicBuildingFile = FileUtils.getFile(yamlBuildingFile.getParent(), yaml.getString("schematic"));
            if(!schematicBuildingFile.exists()) {
                System.out.println("[SettlerCraft]: " + yamlBuildingFile.getParentFile().getAbsolutePath() + " contains no schematic, skipping...");
                continue;
            }
            
            Structure structure = Structures.read(schematicBuildingFile, yamlBuildingFile);
            if(structure == null) {
               System.out.println("[SettlerCraft]: failed to create building for "  + schematicBuildingFile.getAbsolutePath() + ", skipping..."); 
            } else {
                structures.add(structure);
            }
        }
    }


}
