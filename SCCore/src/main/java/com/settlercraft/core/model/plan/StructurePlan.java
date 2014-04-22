/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.model.plan;

import com.settlercraft.core.model.plan.requirement.StructureRequirement;
import com.settlercraft.core.model.plan.schematic.SchematicObject;
import com.settlercraft.core.model.plan.yaml.StructureConfig;

/**
 * @author Chingo
 */
public class StructurePlan {

  private Long id;

  private final SchematicObject schematic;
  
  private final SchematicObject foundation;

  private final StructureConfig config;

  private final StructureRequirement requirement;
  
  

  public StructurePlan(SchematicObject structure, StructureConfig structureConfig) {
    this.schematic = structure;
    this.config = structureConfig;
    this.requirement = new StructureRequirement(this);
    this.foundation = null;
  }

  public StructurePlan(SchematicObject structure, SchematicObject foundation, StructureConfig structureConfig) {
    this.schematic = structure;
    this.config = structureConfig;
    this.requirement = new StructureRequirement(this);
    this.foundation = foundation;
  }

  public StructureRequirement getRequirement() {
    return requirement;
  }

  public SchematicObject getFoundationSchematic() {
    return foundation;
  }
  
  public SchematicObject getStructureSchematic() {
    return schematic;
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
