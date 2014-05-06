package com.sc.api.structure.io;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.sc.api.structure.exception.InvalidStructurePlanException;
import com.sc.api.structure.exception.NoStructureSchematicNodeException;
import com.sc.api.structure.exception.SchematicFileNotFoundException;
import com.sc.api.structure.exception.UnsupportedStructureException;
import com.settlercraft.core.exception.DuplicateStructurePlanException;
import com.settlercraft.core.manager.StructurePlanManager;
import com.settlercraft.core.model.plan.ReservedSide;
import com.settlercraft.core.model.plan.StructurePlan;
import com.settlercraft.core.model.plan.schematic.SchematicObject;
import java.io.File;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Chingo
 */
public class StructurePlanLoader {

    public void load(File buildingFolder) throws InvalidStructurePlanException, SchematicFileNotFoundException, NoStructureSchematicNodeException {
        StructurePlanManager.getInstance().clear();
        String[] extensions = {"yml"};
        String structureSchematicNode = "schematic.structure";
        String foundationSchematicNode = "schematic.foundation";
        Iterator<File> it = FileUtils.iterateFiles(buildingFolder, extensions, true);

        while (it.hasNext()) {
            
            File yamlStructureFile = it.next();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(yamlStructureFile);
            if (yaml.getString(structureSchematicNode) == null) {
                throw new NoStructureSchematicNodeException(yamlStructureFile);
            }
            File schematicStructureFile = FileUtils.getFile(yamlStructureFile.getParent(), yaml.getString(structureSchematicNode));
            if (!schematicStructureFile.exists()) {
                throw new SchematicFileNotFoundException(schematicStructureFile);
            }
            File schematicFoundationFile = null;
            if(yaml.getString(foundationSchematicNode) != null) {
                schematicFoundationFile = FileUtils.getFile(yamlStructureFile.getParent(), yaml.getString(foundationSchematicNode));
                if(!schematicFoundationFile.exists()) {
                    throw new SchematicFileNotFoundException(schematicFoundationFile);
                }
            }
            
            
            StructurePlan plan;
            try {
                plan = assemble(schematicStructureFile, schematicFoundationFile, yamlStructureFile);
                StructurePlanManager.getInstance().addPlan(plan);
                System.out.println("[SCStructureAPI]: loaded " + plan.getName());
            }
            catch (UnsupportedStructureException | DuplicateStructurePlanException ex) {
                Logger.getLogger(StructurePlanLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /**
     * Assembles a config file and its schematic into a structure plan.
     * @param structureSchematic
     * @param structureYAML The structure config file
     * @return Structure plan containing both the schematic and the config
     * @throws UnsupportedStructureException When the schematic contains an
     * unsupported entity or material
     * @throws InvalidStructurePlanException When the config file has missing or
     * invalid nodes.
     */
    public StructurePlan assemble(File structureSchematic, File structureYAML) throws UnsupportedStructureException, InvalidStructurePlanException {
        return assemble(structureSchematic, null, structureYAML);
    }

    public StructurePlan assemble(File stSchematicFile, File fdSchematicFile, File structureYAML) throws InvalidStructurePlanException, UnsupportedStructureException {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(structureYAML);
        if (!hasChestSpace(config)) {
            throw new InvalidStructurePlanException("[SettlerCraft]: structure doesnt have any reserved sides " + structureYAML.getAbsolutePath() + ", the sum of all reserved sides should be bigger than 0");
        } else if (!valid(config)) {
            throw new InvalidStructurePlanException("[SettlerCraft]: failed to create structure plan for " + structureYAML.getAbsolutePath() + ", invalid values");
        }
        
        if(config.get("cost") == null) {
            throw new InvalidStructurePlanException("Missing node: \"cost\" in " + structureYAML.getAbsolutePath());
        } 
                
        EnumMap<ReservedSide, Integer> reserved = new EnumMap<>(ReservedSide.class);
        if (config.get("reserved.north") != null) {
            reserved.put(ReservedSide.NORTH, config.getInt("reserved.north"));
        }
        if (config.get("reserved.east") != null) {
            reserved.put(ReservedSide.EAST, config.getInt("reserved.east"));
        }
        if (config.get("reserved.south") != null) {
            reserved.put(ReservedSide.SOUTH, config.getInt("reserved.south"));
        }
        if (config.get("reserved.west") != null) {
            reserved.put(ReservedSide.WEST, config.getInt("reserved.west"));
        }
        if (config.get("reserved.up") != null) {
            reserved.put(ReservedSide.UP, config.getInt("reserved.up"));
        }
        if (config.get("reserved.north") != null) {
            reserved.put(ReservedSide.DOWN, config.getInt("reserved.down"));
        }
        
        for(Map.Entry<ReservedSide, Integer> e : reserved.entrySet()) {
            if(e.getValue() == null) reserved.put(e.getKey(), 0);
        }
        
        
        double cost = config.getDouble("cost");
        String name = config.getString("name");
        String description = config.getString("description");
        String culture = config.getString("culture");
        String category = config.getString("category");
        int layers = config.getInt("layers-start");
        
        SchematicReader sr = new SchematicReader();
        SchematicObject structure = sr.readFile(stSchematicFile);
        if (fdSchematicFile != null) {
            SchematicObject foundation = sr.readFile(fdSchematicFile);
            return new StructurePlan(structure, foundation, name, culture, category, cost, reserved, description);
        } else {
            return new StructurePlan(structure, name, culture, category, cost, reserved, description);
        }
    }

    private boolean valid(YamlConfiguration yaml) {
        return yaml.getString("name") != null
                && yaml.isInt("reserved.north")
                && yaml.isInt("reserved.east")
                && yaml.isInt("reserved.south")
                && yaml.isInt("reserved.west");
    }

    private boolean hasChestSpace(YamlConfiguration yaml) {
        if (yaml.getInt("reserved.south") + yaml.getInt("reserved.east")
                + yaml.getInt("reserved.north") + yaml.getInt("reserved.west") == 0) {
            return false;
        }
        return true;
    }
   

}
