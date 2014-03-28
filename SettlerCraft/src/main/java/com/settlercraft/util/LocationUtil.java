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
  
      public static float getYaw(DIRECTION direction) {
        switch(direction) {
            case SOUTH : return 0f;
            case EAST: return -90f;
            case NORTH: return -180f;
            case WEST: return -270f;
            default: throw new AssertionError("Unreachable");
        }
    }

    public static int[] getModifiers(DIRECTION direction) {
        switch (direction) {
            case NORTH:
                return new int[]{1, -1};
            case EAST:
                return new int[]{1, 1};
            case SOUTH:
                return new int[]{-1, 1};
            case WEST:
                return new int[]{-1, -1};
            default:
                throw new AssertionError("Unreachable");
        }
    }
}
