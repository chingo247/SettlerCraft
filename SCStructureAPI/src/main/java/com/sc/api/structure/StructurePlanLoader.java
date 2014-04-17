package com.sc.api.structure;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.sc.api.structure.exception.InvalidStructurePlanException;
import com.sc.api.structure.exception.UnsupportedStructureException;
import com.sc.api.structure.io.StructureReader;
import com.settlercraft.core.exception.DuplicateStructurePlanException;
import com.settlercraft.core.manager.StructurePlanManager;
import com.settlercraft.core.model.plan.StructurePlan;
import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Chingo
 */
public class StructurePlanLoader {

    public void load(File buildingFolder) throws InvalidStructurePlanException {
        String[] extensions = {"yml"};
        Iterator<File> it = FileUtils.iterateFiles(buildingFolder, extensions, true);
        
        while (it.hasNext()) {
            File yamlBuildingFile = it.next();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(yamlBuildingFile);
            File schematicBuildingFile = FileUtils.getFile(yamlBuildingFile.getParent(), yaml.getString("schematic.building"));
            
//            System.out.println("Schematic: " + schematicBuildingFile.getAbsolutePath());
//            System.out.println("yaml: " + yamlBuildingFile);

            if (yaml.getString("schematic.building") == null) {
                throw new InvalidStructurePlanException("[SettlerCraft]: " + yamlBuildingFile.getAbsolutePath() + " contains no schematic node");
            } else if (!schematicBuildingFile.exists()) {
                throw new InvalidStructurePlanException("[SettlerCraft]: " + yamlBuildingFile.getParentFile().getAbsolutePath() + ", schematic file doesnt exist");
            }

            StructureReader sr = new StructureReader();
            StructurePlan plan;
            try {
                plan = sr.assemble(schematicBuildingFile, yamlBuildingFile);
                StructurePlanManager.getInstance().addPlan(plan);
                System.out.println("[SettlerCraft]: loaded " + plan.getConfig().getName());
            } catch (UnsupportedStructureException | DuplicateStructurePlanException ex) {
                Logger.getLogger(StructurePlanLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
