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
  
  public static StructureConfig read(File yaml) {
    YamlConfiguration config = YamlConfiguration.loadConfiguration(yaml);
    
    EnumMap<StructureConfig.RESERVED_SIDE, Boolean> reserved = new EnumMap<>(StructureConfig.RESERVED_SIDE.class);
    reserved.put(StructureConfig.RESERVED_SIDE.NORTH, config.getBoolean("reserved.north"));
    reserved.put(StructureConfig.RESERVED_SIDE.EAST, config.getBoolean("reserved.east"));
    reserved.put(StructureConfig.RESERVED_SIDE.SOUTH, config.getBoolean("reserved.south"));
    reserved.put(StructureConfig.RESERVED_SIDE.WEST, config.getBoolean("reserved.west"));
    
    StructureConfig ys = new StructureConfig(
            config.getString("name"),
            config.getString("description"),
            reserved,
            config.getInt("layers-beneath-ground"),
            config.getString("culture"),
            config.getString("type")
    );
    
    return ys;
  }
}
