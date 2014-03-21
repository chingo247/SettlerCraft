/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.structure;

import com.settlercraft.util.schematic.model.SchematicObject;
import com.settlercraft.util.yaml.YAMLStructure;

/**
 * @author Chingo
 */
public class StructurePlan {
    private final SchematicObject structure;
    private final YAMLStructure config;

    public StructurePlan(SchematicObject structure, YAMLStructure buildingConfig) {
        this.structure = structure;
        this.config = buildingConfig;
    }

    public SchematicObject getSchematic() {
        return structure;
    }

    public YAMLStructure getConfig() {
        return config;
    }
}
