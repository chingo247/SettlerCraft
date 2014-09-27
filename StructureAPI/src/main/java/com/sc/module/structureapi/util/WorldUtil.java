/*
 * Copyright (C) 2014 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.sc.module.structureapi.util;

import com.sc.module.structureapi.world.Cardinal;
import com.sc.module.structureapi.world.Dimension;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import org.bukkit.World;
import org.bukkit.entity.Entity;

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
    public static Cardinal getCardinal(int yaw) {
        if (yaw >= 45 && yaw < 135 || yaw >= -315 && yaw < -225) {
            return Cardinal.WEST;
        } else if (yaw >= 135 && yaw < 225 || yaw >= -225 && yaw < -135) {
            return Cardinal.NORTH;
        } else if (yaw >= 225 && yaw < 315 || yaw >= -135 && yaw < -45) {
            return Cardinal.EAST;
        } else /*(yaw >= 315 && yaw < 360 || yaw >= 0 && < 45) */ {
            return Cardinal.SOUTH;
        }
    }

    public static Vector addOffset(Vector location, Cardinal direction, double xOffset, double yOffset, double zOffset) {
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
    

    

    public static Dimension getWorldDimension(World world, Vector location, Cardinal direction, CuboidClipboard clipboard) {
        Vector pos2 = addOffset(location, direction, clipboard.getWidth() - 1, clipboard.getHeight() - 1, clipboard.getLength() - 1);
        return new Dimension(location, pos2);
    }

    /**
     * Gets the yaw for given direction
     *
     * @param direction The direction
     * @return float, yaw value
     */
    public static int getYaw(Cardinal direction) {
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

    public static Cardinal getCardinal(Entity entity) {
        return getCardinal((int) entity.getLocation().getYaw());
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
