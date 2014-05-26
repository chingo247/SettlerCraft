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
package com.sc.api.structure.util;

import com.google.common.base.Preconditions;
import com.sc.api.structure.entity.world.SimpleCardinal;
import com.sc.api.structure.entity.world.WorldDimension;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.Vector;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;

/**
 *
 * @author Chingo
 */
public class WorldUtil {

    /**
     * Translates a yaw to direction
     *
     * @param yaw The yaw
     * @return The direction
     */
    public static SimpleCardinal getCardinal(int yaw) {
        if (yaw >= 45 && yaw < 135 || yaw >= -315 && yaw < -225) {
            return SimpleCardinal.WEST;
        } else if (yaw >= 135 && yaw < 225 || yaw >= -225 && yaw < -135) {
            return SimpleCardinal.NORTH;
        } else if (yaw >= 225 && yaw < 315 || yaw >= -135 && yaw < -45) {
            return SimpleCardinal.EAST;
        } else /*(yaw >= 315 && yaw < 360 || yaw >= 0 && < 45) */ {
            return SimpleCardinal.SOUTH;
        }
    }

    private static com.sk89q.worldedit.Location calculatePoint2(com.sk89q.worldedit.Location point1, SimpleCardinal direction, CuboidClipboard clipboard) {
        switch (direction) {
            case EAST:
                return point1.add(clipboard.getSize().subtract(1, 1, 1));
            case SOUTH:
                clipboard.rotate2D(90);
                return point1.add(-(clipboard.getWidth() - 1), clipboard.getHeight() - 1, (clipboard.getLength() - 1));
            case WEST:
                clipboard.rotate2D(180);
                return point1.add(-(clipboard.getWidth() - 1), clipboard.getHeight(), -(clipboard.getLength() - 1));
            case NORTH:
                clipboard.rotate2D(270);
                return point1.add((clipboard.getWidth() - 1), clipboard.getHeight() - 1, -(clipboard.getLength() - 1));
            default:
                throw new AssertionError("unreachable");
        }
    }

    public static WorldDimension getWorldDimension(com.sk89q.worldedit.Location location, SimpleCardinal direction, CuboidClipboard clipboard) {
        return new WorldDimension(location, calculatePoint2(location, direction, clipboard));
    }

    /**
     * Gets the yaw for given direction
     *
     * @param direction The direction
     * @return float, yaw value
     */
    public static int getYaw(SimpleCardinal direction) {
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

    public static SimpleCardinal getCardinal(Entity entity) {
        return getCardinal((int) entity.getLocation().getYaw());
    }

    /**
     * Gets the location that is furthest to target location from given array.
     *
     * @param target The target location
     * @param locations The array of locations
     * @return The furthest location
     */
    public static Location getFurthest(Location target, Location[] locations) {
        Preconditions.checkArgument(locations.length > 0);
        Location l = null;
        for (int i = 0; i < locations.length; i++) {
            if (l == null) {
                l = locations[i];
            } else if (locations[i].getPosition().distance(target.getPosition()) > l.getPosition().distance(target.getPosition())) {
                l = locations[i];
            }
        }
        return l;
    }

    /**
     * Gets the location that is closest to target location from given array.
     *
     * @param target The target location
     * @param locations The array of locations
     * @return The closest location
     */
    public static Location getClosest(Location target, Location[] locations) {
        Preconditions.checkArgument(locations.length > 0);
        Location l = null;
        for (int i = 0; i < locations.length; i++) {
            if (l == null) {
                l = locations[i];
            } else if (locations[i].getPosition().distance(target.getPosition()) < l.getPosition().distance(target.getPosition())) {
                l = locations[i];
            }
        }
        return l;
    }

    public static Location calculateEndLocation(Location point1, SimpleCardinal direction, CuboidClipboard clipboard) {
        switch (direction) {
            case EAST:
                return point1.add(clipboard.getSize().subtract(1, 1, 1));
            case SOUTH:
                clipboard.rotate2D(90);
                return point1.add(-(clipboard.getWidth() - 1), clipboard.getHeight() - 1, (clipboard.getLength() - 1));
            case WEST:
                clipboard.rotate2D(180);
                return point1.add(-(clipboard.getWidth() - 1), clipboard.getHeight(), -(clipboard.getLength() - 1));
            case NORTH:
                clipboard.rotate2D(270);
                return point1.add((clipboard.getWidth() - 1), clipboard.getHeight() - 1, -(clipboard.getLength() - 1));
            default:
                throw new AssertionError("unreachable");
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
//        Location start = dimension.getStart();
//        Location end = dimension.getEnd();
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
    /**
     * Creates a sign on top of target location
     *
     * @param location The location
     * @param cardinal The cardinal
     * @return The created sign
     * @deprecated Is currently used as workaround
     */
    public static Sign createSign(Location location, SimpleCardinal cardinal) {

        byte data;

        switch (cardinal) {
            case EAST:
                data = 4;
                break;
            case NORTH:
                data = 0;
                break;
            case WEST:
                data = 12;
                break;
            case SOUTH:
                data = 8;
                break;
            default:
                throw new AssertionError("Unreachable");
        }

        Vector pos = location.getPosition().add(new BlockVector(0, 1, 0));
        Block b = Bukkit.getWorld(location.getWorld().getName()).getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        b.setType(Material.SIGN_POST);
        Sign sign = (Sign) b.getState();
        sign.getData().setData(data);
        sign.update();
        return sign;
    }

    public static BlockState getBlockState(Location location) {
        Vector pos = location.getPosition();
        Block b = Bukkit.getWorld(location.getWorld().getName()).getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
        return b.getState();
    }

    /**
     * Gets the sign at target location
     *
     * @param location The location that is a sign
     * @return The sign
     * @deprecated Currently used for a workaround
     */
    public static Sign getSign(Location location) {
        BlockState b = getBlockState(location);
        if (b instanceof Sign) {
            return (Sign) b;
        } else {
            return null;
        }
    }
}
