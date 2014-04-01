/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft;

import com.settlercraft.model.structure.StructurePlan;
import com.settlercraft.util.StructureReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Chingo
 */
public class StructurePlanRegister {

  private static final TreeMap<String, StructurePlan> structures = new TreeMap<>();

  public static void registerStructures(File buildingFolder) {

    String[] extensions = {"yml"};
    Iterator<File> it = FileUtils.iterateFiles(buildingFolder, extensions, true);
    int count = 0;
    while (it.hasNext()) {
      File yamlBuildingFile = it.next();

      YamlConfiguration yaml = YamlConfiguration.loadConfiguration(yamlBuildingFile);
      if (yaml.getString("schematic.building") == null) {
        System.out.println("[SettlerCraft]: " + yamlBuildingFile.getAbsolutePath() + " contains no schematic information, skipping...");
        continue;
      }
//            File f = new File(file.getParentFile().get + "\\"+ yaml.getString("schematic"));
      File schematicBuildingFile = FileUtils.getFile(yamlBuildingFile.getParent(), yaml.getString("schematic.building"));
      if (!schematicBuildingFile.exists()) {
        System.out.println("[SettlerCraft]: " + yamlBuildingFile.getParentFile().getAbsolutePath() + " contains no schematic, skipping...");
        continue;
      }

      StructurePlan structure = StructureReader.read(schematicBuildingFile, yamlBuildingFile);
      if (structure == null) {
        System.out.println("[SettlerCraft]: failed to create building for " + schematicBuildingFile.getAbsolutePath() + ", skipping...");
      } else {
        if (!structures.containsKey(structure.getConfig().getName())) {
          System.out.println("[SettlerCraft]: loaded " + structure.getConfig().getName());
          structures.put(structure.getConfig().getName(), structure);
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

  static void printStructures(File f) {
    try (PrintWriter pw = new PrintWriter(new FileOutputStream(f))) {
      int count = 0;
      for (StructurePlan plan : structures.values()) {
        pw.println("#" + count + ": " + plan.getConfig().getName() + " \t TYPE: " + plan.getConfig().getType());
      }

      pw.close();

    } catch (FileNotFoundException ex) {
      Logger.getLogger(StructurePlanRegister.class.getName()).log(Level.SEVERE, null, ex);

    }
  }

  public static StructurePlan getPlan(String key ) {
    return structures.get(key);
  }
  
}
