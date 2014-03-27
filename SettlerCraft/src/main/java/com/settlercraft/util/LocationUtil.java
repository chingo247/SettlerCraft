/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.util;

/**
 *
 * @author Chingo
 */
public class LocationUtil {
  
  public enum DIRECTION {
    NORTH,
    EAST,
    SOUTH,
    WEST
  }
  
  public static DIRECTION getDirection(float yaw) { 
    if(yaw >= 45 && yaw < 135 || yaw >= -315 && yaw < -225) return DIRECTION.WEST;
    else if(yaw >= 135 && yaw < 225 || yaw >= -225 && yaw < -135) return DIRECTION.NORTH;
    else if(yaw >= 225 && yaw < 315 || yaw >= -135 && yaw< -45) return DIRECTION.EAST;
    else /*(yaw >= 315 && yaw < 360 || yaw >= 0 && < 45) */ return DIRECTION.SOUTH;
  }
}
