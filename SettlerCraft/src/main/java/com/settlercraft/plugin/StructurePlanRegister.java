package com.settlercraft.plugin;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.settlercraft.exception.InvalidStructurePlanException;
import com.settlercraft.exception.UnsupportedStructureException;
import com.settlercraft.model.plan.StructurePlan;
import com.settlercraft.model.plan.StructureReader;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
                throw new InvalidStructurePlanException("[SettlerCraft]: " + yamlBuildingFile.getAbsolutePath() + " contains no schematic node");
            } else if (!schematicBuildingFile.exists()) {
                throw new InvalidStructurePlanException("[SettlerCraft]: " + yamlBuildingFile.getParentFile().getAbsolutePath() + ", schematic file doesnt exist");
            }

            StructureReader sr = new StructureReader();
            StructurePlan structure = null;
            try {
                structure = sr.read(schematicBuildingFile, yamlBuildingFile);
            } catch (UnsupportedStructureException ex) {
                Logger.getLogger(StructurePlanRegister.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (structure == null) {
                
            } else {
                if (!structures.containsKey(structure.getConfig().getName())) {
                    System.out.println("[SettlerCraft]: loaded " + structure.getConfig().getName());
                    structures.put(structure.getConfig().getName(), structure);
                } else {
                    throw new InvalidStructurePlanException("[SettlerCraft]: unable to load building for "
                            + yamlBuildingFile.getAbsolutePath() + ", name: "
                            + structure.getConfig().getName() + " already in use...");
                }
            }
        }
    }

    public final StructurePlan getPlan(String key) {
        return structures.get(key);
    }

    public final Map<String, StructurePlan> getPlans() {
        return new HashMap<>(structures);
    }

}
