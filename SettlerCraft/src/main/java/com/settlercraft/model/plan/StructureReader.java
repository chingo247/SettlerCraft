package com.settlercraft.model.plan;


import com.settlercraft.model.entity.structure.StructurePlan;
import com.settlercraft.model.plan.schematic.SchematicObject;
import com.settlercraft.model.plan.schematic.SchematicReader;
import com.settlercraft.model.plan.yaml.StructureConfig;
import com.settlercraft.model.plan.yaml.StructureConfigReader;
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
    
    
    public StructurePlan read(File schematic, File structureYAML) {
        SchematicReader sr = new SchematicReader();
        SchematicObject obj = sr.readFile(schematic);
        YamlConfiguration structureInfo = YamlConfiguration.loadConfiguration(structureYAML);
        if(!validate(structureInfo)) {
            return null;
        }
        StructureConfigReader scr = new StructureConfigReader();
        StructureConfig config = scr.read(structureYAML);
        StructurePlan structure = new StructurePlan(obj,config);
        return structure;
    }
    
    private boolean validate(YamlConfiguration yaml) {
        return yaml.getString("name") != null 
                && yaml.isInt("reserved.north")
                && yaml.isInt("reserved.east")
                && yaml.isInt("reserved.south")
                && yaml.isInt("reserved.west")
                && yaml.getInt("reserved.south") >= 1 // Min room for structureChest
                && yaml.getInt("reserved.east")  >= 0
                && yaml.getInt("reserved.north") >= 0
                && yaml.getInt("reserved.west")  >= 0;
                    
    }


}
