/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.entity.structure;

import com.settlercraft.util.schematic.model.SchematicObject;
import com.settlercraft.util.yaml.StructureConfig;

/**
 * @author Chingo
 */
public class StructurePlan {

    private Long id;

    private final SchematicObject structure;

    private final StructureConfig config;

    public StructurePlan(SchematicObject structure, StructureConfig structureConfig) {
        this.structure = structure;
        this.config = structureConfig;
    }

    public SchematicObject getSchematic() {
        return structure;
    }

    public StructureConfig getConfig() {
        return config;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StructurePlan)) {
            return false;
        }
        StructurePlan sp = (StructurePlan) o;
        return sp.getConfig().getName().equals(this.getConfig().getName());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
