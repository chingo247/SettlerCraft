/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.structure;

import com.settlercraft.util.yaml.YAMLStructure;
import com.settlercraft.util.schematic.model.SchematicObject;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Chingo
 */
public class Structure {
  
  private final SchematicObject structure;
//  private final Map<Integer, StructureLayer> layers; // Layer Height , Layer



  public Structure(SchematicObject structure, YAMLStructure buildingConfig) {
    this.structure = structure;
    
//    this.layers = new HashMap<>(structure.getHeight());
  }



  public SchematicObject getSchematic() {
    return structure;
  }



}
