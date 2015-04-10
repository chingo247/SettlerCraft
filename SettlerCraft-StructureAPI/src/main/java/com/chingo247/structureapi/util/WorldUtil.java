
/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.structureapi.util;


import com.chingo247.structureapi.world.Direction;
import static com.chingo247.structureapi.world.Direction.EAST;
import static com.chingo247.structureapi.world.Direction.NORTH;
import static com.chingo247.structureapi.world.Direction.SOUTH;
import static com.chingo247.structureapi.world.Direction.WEST;
import com.sk89q.worldedit.Vector;




/**
 *
 * @author Chingo
 */
public class WorldUtil {

    private WorldUtil(){}
    
    /**
     * Translates a yaw to direction
     *
     * @param yaw The yaw
     * @return The direction
     */
    public static Direction getDirection(int yaw) {
        return getDirection((float)yaw);
    }
    
    private static float normalizeYaw(float yaw) {
        float ya = yaw;
        if(yaw > 360) {
            int times = (int)((ya - (ya % 360)) / 360);
            int normalizer = times * 360;
            
            ya -= normalizer;
        } else if (yaw < -360) {
            ya = Math.abs(ya);
            int times = (int)((ya - (ya % 360)) / 360);
            int normalizer = times * 360;
            ya = yaw + normalizer;
        }
        return ya;
    } 
    
    public static Direction getDirection(float yaw) {
        
        yaw = normalizeYaw(yaw);
        
        System.out.println("YAW: " + yaw);
        
        if (yaw >= 45f && yaw < 135f || yaw >= -315f && yaw < -225f) {
            return Direction.WEST;
        } else if (yaw >= 135f && yaw < 225f || yaw >= -225f && yaw < -135f) {
            return Direction.NORTH;
        } else if (yaw >= 225f && yaw < 315f || yaw >= -135f && yaw < -45f) {
            return Direction.EAST;
        } else /*(yaw >= 315 && yaw < 360 || yaw >= 0 && < 45) */ {
            return Direction.SOUTH;
        }
    }

    public static Vector translateLocation(Vector location, Direction direction, double xOffset, double yOffset, double zOffset) {
        switch (direction) {
            case EAST:
                return location.add(zOffset, yOffset, xOffset);
            case SOUTH:
                return location.add(-xOffset, yOffset, zOffset);
            case WEST:
                return location.add(-zOffset, yOffset, -xOffset);
            case NORTH:
                
                return location.add(xOffset, yOffset, -zOffset);
            default:
                throw new AssertionError("unreachable");
        }
    }

    /**
     * Gets the yaw for given direction
     *
     * @param direction The direction
     * @return float, yaw value
     */
    public static int getYaw(Direction direction) {
        switch (direction) {
            case SOUTH:
                return 0;
            case EAST:
                return -90;
            case NORTH:
                return -180;
            case WEST:
                return -270;
            default:
                throw new AssertionError("Unreachable");
        }
    }

}
