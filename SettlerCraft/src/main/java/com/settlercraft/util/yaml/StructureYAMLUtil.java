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
  
  private StructureYAMLUtil(){}
  
  public static YAMLStructure read(File yaml) {
    YamlConfiguration config = YamlConfiguration.loadConfiguration(yaml);
    
    EnumMap<YAMLStructure.RESERVED_SIDE, Boolean> reserved = new EnumMap<>(YAMLStructure.RESERVED_SIDE.class);
    reserved.put(YAMLStructure.RESERVED_SIDE.NORTH, config.getBoolean("reserved.north"));
    reserved.put(YAMLStructure.RESERVED_SIDE.EAST, config.getBoolean("reserved.east"));
    reserved.put(YAMLStructure.RESERVED_SIDE.SOUTH, config.getBoolean("reserved.south"));
    reserved.put(YAMLStructure.RESERVED_SIDE.WEST, config.getBoolean("reserved.west"));
    
    YAMLStructure ys = new YAMLStructure(
            config.getString("name"),
            config.getString("displayName"),
            config.getString("description"),
            reserved,
            config.getInt("layers-beneath-ground"),
            config.getString("culture"),
            config.getString("type")
    );
    
    return ys;
  }
}
