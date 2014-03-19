/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.util.yaml;

import java.util.EnumMap;

/**
 *
 * @author Christian
 */
public class YAMLStructure {

  private final String name;
  private final String displayName;
  private final String description;
  private final EnumMap<RESERVED_SIDE, Boolean> reserved;

  public enum RESERVED_SIDE {
    NORTH,
    EAST,
    SOUTH,
    WEST
  }

  YAMLStructure(String name, String displayName, String description, EnumMap<RESERVED_SIDE, Boolean> reserved) {
    this.name = name;
    this.displayName = displayName;
    this.description = description;
    this.reserved = reserved;
  }

  public EnumMap<RESERVED_SIDE, Boolean> getReserved() {
    return reserved;
  }

}
