/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.util;

import com.settlercraft.model.structure.StructurePlan;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class Builder {
  
  private final Player player;
  
  private enum DIRECTION {
    NORTH,
    EAST,
    SOUTH,
    WEST
  }
  
  private final DIRECTION direction;
  
  Builder(Player player, StructurePlan plan, Location buildLocation) {
    this.player = player;
    this.direction = getDirection();
  }
  
  private DIRECTION getDirection() {
    return DIRECTION.NORTH;
  }
  
  private void placeFoundation() {
    
  }  
}
