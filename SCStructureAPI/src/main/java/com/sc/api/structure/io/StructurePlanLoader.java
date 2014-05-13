package com.sc.api.structure.io;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.sc.api.structure.model.structure.plan.StructurePlan;
import com.sc.api.structure.persistence.StructurePlanService;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Chingo
 */
public class StructurePlanLoader {

    public List<StructurePlan> loadStructures(File buildingFolder) throws FileNotFoundException {
        String[] extensions = {"yml"};
        

        Iterator<File> it = FileUtils.iterateFiles(buildingFolder, extensions, true);
        StructurePlanService sps = new StructurePlanService();

        List<StructurePlan> structurePlans = new ArrayList<>();

        while (it.hasNext()) {
            File yamlStructureFile = it.next();
            StructurePlan plan = load(yamlStructureFile);
            structurePlans.add(plan);
        }
        sps.save(structurePlans);

        return structurePlans;
    }

    public StructurePlan load(File structureYAML) throws FileNotFoundException {
        StructurePlan spv = null;
        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(structureYAML);

            File schematicStructureFile = FileUtils.getFile(structureYAML.getParent(), config.getString("schematic"));
            if (!schematicStructureFile.exists()) {
                throw new FileNotFoundException("No such file: " + structureYAML.getParent() + "\"" + config.getString("schematic"));
            }

            SchematicFormat format = SchematicFormat.getFormat(schematicStructureFile);
            if (!format.isOfFormat(schematicStructureFile)) {
                System.err.print("[SCStructureAPI]: Unsupported format for " + format.getName() + " in: " + schematicStructureFile.getName());
                return null;
            }

            String id;
            if (config.contains("id")) {
                id = String.valueOf(config.get("id"));
            } else {

                id = schematicStructureFile.getName();
                config.addDefault("id", id);
            }

            spv = new StructurePlan(id, schematicStructureFile);

            if (config.contains("displayname")) {
                spv.setDisplayName(String.valueOf(config.get("displayname")));
            }

            if (config.contains("price")) {
                spv.setPrice(config.getDouble("price"));
            }

            if (config.contains("reserved.north")) {
                spv.setReservedNorth(config.getInt("reserved.north"));
            }

            if (config.contains("reserved.east")) {
                spv.setReservedEast(config.getInt("reserved.east"));
            }

            if (config.contains("reserved.south")) {
                spv.setReservedSouth(config.getInt("reserved.south"));
            }

            if (config.contains("reserved.west")) {
                spv.setReservedWest(config.getInt("reserved.west"));
            }

            if (config.contains("reserved.up")) {
                spv.setReservedUp(config.getInt("reserved.up"));
            }

            if (config.contains("reserved.down")) {
                spv.setReservedDown(config.getInt("reserved.down"));
            }

            if (config.contains("description")) {
                spv.setDescription(config.getString("description"));
            }

            if (config.contains("faction")) {
                spv.setFaction(config.getString("faction"));
            }

            if (config.contains("category")) {
                spv.setCategory(config.getString("category"));
            }

            if (config.contains("start-y")) {
                spv.setStartY(config.getInt("start-y"));
            }

            return spv;
        }
        catch (IOException | DataException ex) {
            Logger.getLogger(StructurePlanLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return spv;
    }

}
