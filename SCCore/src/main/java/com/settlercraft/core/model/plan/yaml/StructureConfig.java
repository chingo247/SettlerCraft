/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.model.plan.yaml;

import java.util.EnumMap;

/**
 * Defines all extra information of a structure, create YAML structure with the
 * {@link StructureYAMLUtil.class}
 *
 * @author Chingo
 */
public class StructureConfig {

    private final String name;
    private final String description;
    private final String culture;
    private final String type;

    private final EnumMap<RESERVED_SIDE, Integer> reserved;

    public enum RESERVED_SIDE {

        NORTH,
        EAST,
        SOUTH,
        WEST
    }

    StructureConfig(String name,
            String description,
            EnumMap<RESERVED_SIDE, Integer> reserved,
            int layersBeneathGround,
            String culture,
            String type
    ) {

        this.name = name;
        this.description = description;
        this.reserved = reserved;
        this.type = type;
        this.culture = culture;
    }

    public EnumMap<RESERVED_SIDE, Integer> getReserved() {
        return new EnumMap<>(reserved);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCulture() {
        return culture;
    }

    public String getType() {
        return type;
    }

}
