/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.core.model.plan.yaml;

import java.io.File;
import java.util.EnumMap;
import org.bukkit.configuration.file.YamlConfiguration;


/**
 *
 * @author Christian
 */
public class StructureConfigReader {
  
  public StructureConfigReader(){}
  
  public StructureConfig read(File yaml) {
    YamlConfiguration config = YamlConfiguration.loadConfiguration(yaml);
    
    EnumMap<StructureConfig.RESERVED_SIDE, Integer> reserved = new EnumMap<>(StructureConfig.RESERVED_SIDE.class);
    reserved.put(StructureConfig.RESERVED_SIDE.NORTH, config.getInt("reserved.north"));
    reserved.put(StructureConfig.RESERVED_SIDE.EAST, config.getInt("reserved.east"));
    reserved.put(StructureConfig.RESERVED_SIDE.SOUTH, config.getInt("reserved.south"));
    reserved.put(StructureConfig.RESERVED_SIDE.WEST, config.getInt("reserved.west"));
    
    
    StructureConfig ys = new StructureConfig(
            config.getString("name"),
            config.getString("description"),
            reserved,
            config.getInt("layers-start"),
            config.getString("culture"),
            config.getString("type")
    );
    
    return ys;
  }
}
