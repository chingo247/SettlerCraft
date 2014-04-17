package com.sc.api.structure.io;


import com.sc.api.structure.exception.InvalidStructurePlanException;
import com.sc.api.structure.exception.UnsupportedStructureException;
import com.settlercraft.core.model.plan.StructurePlan;
import com.settlercraft.core.model.plan.schematic.SchematicObject;
import com.settlercraft.core.model.plan.yaml.StructureConfig;
import com.settlercraft.core.model.plan.yaml.StructureConfigReader;
import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 
 * @author Chingo
 */
public class StructureReader {
    /**
     * Assembles a config file and its schematic into a structure plan.
     * @param schematic The schematic file
     * @param structureYAML The structure config file
     * @return Structure plan containing both the schematic and the config
     * @throws UnsupportedStructureException When the schematic contains an unsupported entity or material
     * @throws InvalidStructurePlanException When the config file has missing or invalid nodes.
     */
    public StructurePlan assemble(File schematic, File structureYAML) throws UnsupportedStructureException, InvalidStructurePlanException {
        SchematicReader sr = new SchematicReader();
        SchematicObject obj = sr.readFile(schematic);
        YamlConfiguration structureInfo = YamlConfiguration.loadConfiguration(structureYAML);
        if(!hasChestSpace(structureInfo)) {
            throw new InvalidStructurePlanException("[SettlerCraft]: no chest space defined in "+ structureYAML.getAbsolutePath() +", the sum of all reserved sides should be bigger than 0");
        } else if(!checKvalues(structureInfo)) {
            throw new InvalidStructurePlanException("[SettlerCraft]: failed to create structure plan for " + structureYAML.getAbsolutePath() + ", invalid values");
        }
        StructureConfigReader scr = new StructureConfigReader();
        StructureConfig config = scr.read(structureYAML);
        StructurePlan structure = new StructurePlan(obj,config);
        return structure;
    }
    
    private boolean checKvalues(YamlConfiguration yaml) {
        return yaml.getString("name") != null 
                && yaml.isInt("reserved.north")
                && yaml.isInt("reserved.east")
                && yaml.isInt("reserved.south")
                && yaml.isInt("reserved.west");
    }
    
    
    private boolean hasChestSpace(YamlConfiguration yaml) {
        if(yaml.getInt("reserved.south") + yaml.getInt("reserved.east")
                + yaml.getInt("reserved.north") + yaml.getInt("reserved.west") == 0) {
            return false;
        }
        return true;
    }


}
