package com.settlercraft.util;

import com.settlercraft.model.entity.structure.StructurePlan;
import com.settlercraft.util.schematic.model.SchematicObject;
import com.settlercraft.util.schematic.util.SchematicUtil;
import com.settlercraft.util.yaml.StructureYAMLUtil;
import com.settlercraft.util.yaml.StructureConfig;
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
    
    
    public static StructurePlan read(File schematic, File structureYAML) {
        SchematicObject obj = SchematicUtil.readFile(schematic);
        YamlConfiguration structureInfo = YamlConfiguration.loadConfiguration(structureYAML);
        if(!validate(structureInfo)) {
            return null;
        }
        StructureConfig yaml = StructureYAMLUtil.read(structureYAML);
        StructurePlan structure = new StructurePlan(obj,yaml);
        return structure;
    }
    
    private static boolean validate(YamlConfiguration yaml) {
        return yaml.getString("name") != null 
                && yaml.isBoolean("reserved.north")
                && yaml.isBoolean("reserved.east")
                && yaml.isBoolean("reserved.south")
                && yaml.isBoolean("reserved.west");
    }


}
