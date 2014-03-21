package com.settlercraft.util;

import com.settlercraft.model.structure.Structure;
import com.settlercraft.model.structure.StructurePlan;
import com.settlercraft.util.schematic.model.SchematicObject;
import com.settlercraft.util.schematic.util.SchematicUtil;
import com.settlercraft.util.yaml.StructureYAMLUtil;
import com.settlercraft.util.yaml.YAMLStructure;
import java.io.File;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Chingo
 */
public class Structures {
    
    
    public static StructurePlan read(File schematic, File structureYAML) {
        SchematicObject obj = SchematicUtil.readFile(schematic);
        YamlConfiguration structureInfo = YamlConfiguration.loadConfiguration(structureYAML);
        if(!validate(structureInfo)) {
            return null;
        }
        YAMLStructure yaml = StructureYAMLUtil.read(structureYAML);
        StructurePlan structure = new StructurePlan(obj,yaml);
        return structure;
    }
    
    public static void build(Player owner, StructurePlan plan, Location location) {
        Structure structure = new Structure(owner, location, plan);
    }
    
    private static boolean validate(YamlConfiguration yaml) {
        return yaml.getString("name") != null 
                && yaml.isBoolean("reserved.north")
                && yaml.isBoolean("reserved.east")
                && yaml.isBoolean("reserved.south")
                && yaml.isBoolean("reserved.west");
    }


}
