/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.main;

import com.settlercraft.model.structure.StructurePlan;
import com.settlercraft.util.Structures;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Chingo
 */
public class StructurePlanRegister {

    
    private Set<StructurePlan> structures = new HashSet<>();
   

    public void registerBuildings(File buildingFolder) {
        
        String[] extensions = {"yml"};
        Iterator<File> it = FileUtils.iterateFiles(buildingFolder, extensions, true);
        int count = 0;
        while(it.hasNext()) {
            File yamlBuildingFile = it.next();
            
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
            
            StructurePlan structure = Structures.read(schematicBuildingFile, yamlBuildingFile);
            if(structure == null) {
               System.out.println("[SettlerCraft]: failed to create building for "  + schematicBuildingFile.getAbsolutePath() + ", skipping..."); 
            } else {
                if(structures.add(structure)) {
                    System.out.println("[SettlerCraft]: loaded " + structure.getConfig().getName());
                    count++;
                } else {
                    System.out.println("[SettlerCraft]: unable to load building for " 
                            + yamlBuildingFile.getAbsolutePath() + ", name: "
                            + structure.getConfig().getName() + " already in use...");
                }
                
            }
        }
        
        System.out.println("[SettlerCraft]: " + count + " structures were loaded ");
    }


}
