/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.model.plan.yaml;

import java.io.File;
import java.util.EnumMap;
import java.util.Map.Entry;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Christian
 */
public class StructureConfigReader {

    public StructureConfigReader() {
    }

    public StructureConfig read(File yaml) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(yaml);

        EnumMap<StructureConfig.RESERVED_SIDE, Integer> reserved = new EnumMap<>(StructureConfig.RESERVED_SIDE.class);
        if (config.get("reserved.north") != null) {
            reserved.put(StructureConfig.RESERVED_SIDE.NORTH, config.getInt("reserved.north"));
        }
        if (config.get("reserved.east") != null) {
            reserved.put(StructureConfig.RESERVED_SIDE.EAST, config.getInt("reserved.east"));
        }
        if (config.get("reserved.south") != null) {
            reserved.put(StructureConfig.RESERVED_SIDE.SOUTH, config.getInt("reserved.south"));
        }
        if (config.get("reserved.west") != null) {
            reserved.put(StructureConfig.RESERVED_SIDE.WEST, config.getInt("reserved.west"));
        }
        if (config.get("reserved.up") != null) {
            reserved.put(StructureConfig.RESERVED_SIDE.UP, config.getInt("reserved.up"));
        }
        if (config.get("reserved.north") != null) {
            reserved.put(StructureConfig.RESERVED_SIDE.DOWN, config.getInt("reserved.down"));
        }
        
        for(Entry<StructureConfig.RESERVED_SIDE, Integer> e : reserved.entrySet()) {
            if(e.getValue() == null) reserved.put(e.getKey(), 0);
        }

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
