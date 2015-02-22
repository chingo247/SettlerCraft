
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
package com.chingo247.settlercraft.util;


import com.chingo247.settlercraft.world.Direction;
import static com.chingo247.settlercraft.world.Direction.EAST;
import static com.chingo247.settlercraft.world.Direction.NORTH;
import static com.chingo247.settlercraft.world.Direction.SOUTH;
import static com.chingo247.settlercraft.world.Direction.WEST;
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
        if (yaw >= 45 && yaw < 135 || yaw >= -315 && yaw < -225) {
            return Direction.WEST;
        } else if (yaw >= 135 && yaw < 225 || yaw >= -225 && yaw < -135) {
            return Direction.NORTH;
        } else if (yaw >= 225 && yaw < 315 || yaw >= -135 && yaw < -45) {
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
    

    

//    public static CuboidDimension getWorldDimension(World world, Vector location, Direction direction, CuboidClipboard clipboard) {
//        Vector pos2 = translateLocation(location, direction, clipboard.getWidth() - 1, clipboard.getHeight() - 1, clipboard.getLength() - 1);
//        return new CuboidDimension(location, pos2);
//    }

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


//    /**
//     * Moves the given entity from the given target structure, the entity will be moved beyond the
//     * closest border. If the entity isnt within the structure, no actions will be taken. If the new
//     * location of the entity is a location on another structure, then this method will call itself
//     * recursively To prevent a stackoverflow the distance value will be doubled each recursive call
//     *
//     * @param entity
//     * @param distance
//     * @param targetStructure
//     */
//    public static void moveEntityFromLot(LivingEntity entity, int distance, Structure targetStructure) {
//        Preconditions.checkArgument(distance > 0);
//        if (targetStructure.isOnLot(entity.getLocation())) {
//            return;
//        }
//
//        WorldDimension dimension = targetStructure.getDimension();
//        Location start = dimension.getMin();
//        Location end = dimension.getMax();
//        if (entity.getLocation().distance(start) < entity.getLocation().distance(end)) {
//            Location xMinus = new Location(start.getWorld(),
//                    start.getBlockX() - distance, // X
//                    start.getWorld().getHighestBlockYAt(start.getBlockX() - distance, entity.getLocation().getBlockZ()), // Y
//                    entity.getLocation().getBlockZ() // Z
//            );
//            Location zMinus = new Location(start.getWorld(),
//                    entity.getLocation().getBlockX(),
//                    start.getWorld().getHighestBlockYAt(entity.getLocation().getBlockX() - distance, start.getBlockZ() - distance),
//                    start.getBlockZ() - distance
//            );
//            if (entity.getLocation().distance(xMinus) < entity.getLocation().distance(zMinus)) {
//                moveEntity(entity, distance, xMinus);
//            } else {
//                moveEntity(entity, distance, zMinus);
//            }
//        } else {
//            Location xPlus = new Location(end.getWorld(),
//                    end.getBlockX() + distance, // X
//                    end.getWorld().getHighestBlockYAt(end.getBlockX() + distance, entity.getLocation().getBlockZ()), // Y
//                    entity.getLocation().getBlockZ()
//            );                                                                      // Z
//
//            Location zPlus = new Location(end.getWorld(),
//                    entity.getLocation().getBlockX(),
//                    end.getWorld().getHighestBlockYAt(entity.getLocation().getBlockX() + distance, end.getBlockZ() + distance),
//                    end.getBlockZ() + distance
//            );
//            if (entity.getLocation().distance(xPlus) < entity.getLocation().distance(zPlus)) {
//                moveEntity(entity, distance, xPlus);
//            } else {
//                moveEntity(entity, distance, zPlus);
//            }
//        }
//    }
//
//    private static void moveEntity(LivingEntity entity, int distance, Location target) {
//        StructureService structureService = new StructureService();
//        if (target.getBlock().getType() == Material.LAVA) {
//            // Alternative?
//            // TODO use alternative
//
//        } else if (structureService.isOnStructure(target)) {
//            moveEntityFromLot(entity, distance * 2, structureService.getStructure(target));
//        }
//    }
}
