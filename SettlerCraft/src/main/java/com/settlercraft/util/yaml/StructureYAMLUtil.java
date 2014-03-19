/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.util.yaml;

import java.io.File;
import java.util.EnumMap;
import org.bukkit.configuration.file.YamlConfiguration;


/**
 *
 * @author Christian
 */
public class StructureYAMLUtil {
  
  public static YAMLStructure read(File yaml) {
    YamlConfiguration config = YamlConfiguration.loadConfiguration(yaml);
    
    EnumMap<YAMLStructure.RESERVED_SIDE, Boolean> reserved = new EnumMap<>(YAMLStructure.RESERVED_SIDE.class);
    reserved.put(YAMLStructure.RESERVED_SIDE.EAST, config.getBoolean("reserved.east"));
    
    YAMLStructure ys = new YAMLStructure(
            config.getString("name"),
            config.getString("displayName"),
            config.getString("description"),
            reserved
    );
    
    
    
    return ys;
  }
}
