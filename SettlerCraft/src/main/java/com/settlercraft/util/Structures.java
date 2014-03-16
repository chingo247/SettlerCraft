package com.settlercraft.util;

import com.settlercraft.model.structure.Structure;
import com.settlercraft.util.schematic.model.SchematicObject;
import com.settlercraft.util.schematic.util.SchematicUtil;
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
public class Structures {
    
    
    public static Structure read(File schematic, File structureYAML) {
        SchematicObject obj = SchematicUtil.readFile(schematic);
        YamlConfiguration structureInfo = YamlConfiguration.loadConfiguration(structureYAML);
        
        if(!validate(structureInfo)) {
            return null;
        }
        
        Structure structure = new Structure(structureInfo.getString("name"), obj, structureInfo.getInt("layers-beneath-ground"));
        structure.setReserved(Structure.RESERVED_SIDES.NORTH, structureInfo.getBoolean("reserved.north"));
        structure.setReserved(Structure.RESERVED_SIDES.EAST, structureInfo.getBoolean("reserved.east"));
        structure.setReserved(Structure.RESERVED_SIDES.SOUTH, structureInfo.getBoolean("reserved.south"));
        structure.setReserved(Structure.RESERVED_SIDES.WEST, structureInfo.getBoolean("reserved.west"));
        
        
        
        return structure;
        
    }
    
    private static boolean validate(YamlConfiguration yaml) {
        return yaml.getString("name") != null 
                && yaml.isBoolean("reserved.north")
                && yaml.isBoolean("reserved.east")
                && yaml.isBoolean("reserved.south")
                && yaml.isBoolean("reserved.west");
                
    }

    public static Structure read(File buildingFile, File buildingInfoFile, File foundationFile) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
