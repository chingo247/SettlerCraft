package com.settlercraft.main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import com.settlercraft.exception.InvalidStructurePlanException;
import com.settlercraft.model.entity.structure.StructurePlan;
import com.settlercraft.model.plan.StructureReader;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Chingo
 */
public class StructurePlanRegister {

  private final TreeMap<String, StructurePlan> structures = new TreeMap<>();

  public StructurePlanRegister(File buildingFolder) throws InvalidStructurePlanException {
      registerStructures(buildingFolder);
  }

  private void registerStructures(File buildingFolder) throws InvalidStructurePlanException {
    String[] extensions = {"yml"};
    Iterator<File> it = FileUtils.iterateFiles(buildingFolder, extensions, true);
    while (it.hasNext()) {
      File yamlBuildingFile = it.next();
      YamlConfiguration yaml = YamlConfiguration.loadConfiguration(yamlBuildingFile);
      File schematicBuildingFile = FileUtils.getFile(yamlBuildingFile.getParent(), yaml.getString("schematic.building"));
      
      if (yaml.getString("schematic.building") == null) {
        throw new InvalidStructurePlanException("[SC~BuildApi]: " + yamlBuildingFile.getAbsolutePath() + " contains no schematic node");
      } else if (!schematicBuildingFile.exists()) {
        throw new InvalidStructurePlanException("[SC~BuildApi]: " + yamlBuildingFile.getParentFile().getAbsolutePath() + ", schematic file doesnt exist");
      }
      
      StructureReader sr = new StructureReader();
      StructurePlan structure = sr.read(schematicBuildingFile, yamlBuildingFile);
      
      if (structure == null) {
         throw new InvalidStructurePlanException("[SC~BuildApi]: failed to create structure plan for " + schematicBuildingFile.getAbsolutePath());
      } else {
        if (!structures.containsKey(structure.getConfig().getName())) {
          System.out.println("[SC~BuildApi]: loaded " + structure.getConfig().getName());
          structures.put(structure.getConfig().getName(), structure);
        } else {
          throw new InvalidStructurePlanException("[SC~BuildApi]: unable to load building for "
                  + yamlBuildingFile.getAbsolutePath() + ", name: "
                  + structure.getConfig().getName() + " already in use...");  
        }
      }
    }
  }

  public final StructurePlan getPlan(String key ) {
    return structures.get(key);
  }
  
  public final Map<String,StructurePlan> getPlans() {
      return new HashMap<>(structures);
  }
  
}
