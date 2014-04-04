/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.util.location;

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
  
  /**
   * Translates a yaw to direction
   * @param yaw The yaw
   * @return The direction
   */
  public static DIRECTION getDirection(float yaw) { 
    if(yaw >= 45 && yaw < 135 || yaw >= -315 && yaw < -225) return DIRECTION.WEST;
    else if(yaw >= 135 && yaw < 225 || yaw >= -225 && yaw < -135) return DIRECTION.NORTH;
    else if(yaw >= 225 && yaw < 315 || yaw >= -135 && yaw< -45) return DIRECTION.EAST;
    else /*(yaw >= 315 && yaw < 360 || yaw >= 0 && < 45) */ return DIRECTION.SOUTH;
  }
  
    /**
     * Gets the yaw for given direction
     * @param direction The direction
     * @return float, yaw value
     */
      public static float getYaw(DIRECTION direction) {
        switch(direction) {
            case SOUTH : return 0f;
            case EAST: return -90f;
            case NORTH: return -180f;
            case WEST: return -270f;
            default: throw new AssertionError("Unreachable");
        }
    }

      /**
       * Returns an int[] with length 2, where the first element is the x modifier and the second the z modifier
       * @param direction The direction
       * @return int[2] where first element is x modifier and second the z modifier
       */
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
    
    public static DIRECTION getDirection(int xMod, int zMod) {
      if(xMod != 1 && xMod != -1) throw new IllegalArgumentException("x modifier: "+ xMod +" not allowed, must be 1 or -1");
      if(zMod != 1 && zMod != -1) throw new IllegalArgumentException("z modifier: "+ zMod +" not allowed, must be 1 or -1");
      
      if(xMod == 1 && zMod == -1) {
        return DIRECTION.NORTH;
      } else if(xMod == 1 && zMod == 1) {
        return DIRECTION.EAST;
      } else if (xMod == -1 && zMod == 1) {
        return DIRECTION.SOUTH;
      } else /** if(xMod == -1 && zMod == -1 **/ {
        return DIRECTION.WEST;
      }
    }
}
