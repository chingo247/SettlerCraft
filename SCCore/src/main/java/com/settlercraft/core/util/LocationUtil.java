/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.util;

import com.google.common.base.Preconditions;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

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
     *
     * @param yaw The yaw
     * @return The direction
     */
    public static DIRECTION getDirection(float yaw) {
        if (yaw >= 45 && yaw < 135 || yaw >= -315 && yaw < -225) {
            return DIRECTION.WEST;
        } else if (yaw >= 135 && yaw < 225 || yaw >= -225 && yaw < -135) {
            return DIRECTION.NORTH;
        } else if (yaw >= 225 && yaw < 315 || yaw >= -135 && yaw < -45) {
            return DIRECTION.EAST;
        } else /*(yaw >= 315 && yaw < 360 || yaw >= 0 && < 45) */ {
            return DIRECTION.SOUTH;
        }
    }

    /**
     * Gets the yaw for given direction
     *
     * @param direction The direction
     * @return float, yaw value
     */
    public static float getYaw(DIRECTION direction) {
        switch (direction) {
            case SOUTH:
                return 0f;
            case EAST:
                return -90f;
            case NORTH:
                return -180f;
            case WEST:
                return -270f;
            default:
                throw new AssertionError("Unreachable");
        }
    }

    /**
     * Returns an int[] with length 2, where the first element is the x modifier and the second the
     * z modifier
     *
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
        if (xMod != 1 && xMod != -1) {
            throw new IllegalArgumentException("x modifier: " + xMod + " not allowed, must be 1 or -1");
        }
        if (zMod != 1 && zMod != -1) {
            throw new IllegalArgumentException("z modifier: " + zMod + " not allowed, must be 1 or -1");
        }

        if (xMod == 1 && zMod == -1) {
            return DIRECTION.NORTH;
        } else if (xMod == 1 && zMod == 1) {
            return DIRECTION.EAST;
        } else if (xMod == -1 && zMod == 1) {
            return DIRECTION.SOUTH;
        } else /**
         * if(xMod == -1 && zMod == -1 *
         */
        {
            return DIRECTION.WEST;
        }
    }

    public static DIRECTION getDirection(Entity entity) {
        return getDirection(entity.getLocation().getYaw());
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
            } else if (locations[i].distance(target) > l.distance(target)) {
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
            } else if (locations[i].distance(target) < l.distance(target)) {
                l = locations[i];
            }
        }
        return l;
    }

    /**
     * Gets all entities within bounding box of the two given locations, where start is the origin
     * @param start The start location
     * @param end The end location
     * @return All entities within this box
     */
    public static Set<Entity> getEntitiesWithin(Location start, Location end) {
        Set<Entity> entities = new HashSet<>();
        Location s = start.getChunk().getBlock(0, 0, 0).getLocation();
        int xMod = (start.getBlockX() < end.getBlockX()) ? 1 : -1;
        int zMod = (start.getBlockZ() < end.getBlockZ()) ? 1 : -1;
        int deltaX = (int) Math.ceil(start.distance(new Location(start.getWorld(), end.getBlockX(), start.getBlockY(), start.getBlockZ()))); // Horizontal distance
        int deltaZ = (int) Math.ceil(start.distance(new Location(start.getWorld(), start.getBlockX(), start.getBlockY(), end.getBlockZ()))); // Vertical distance

        for (int x = 0; x <= deltaX; x += 16) {
            for (int z = 0; z <= deltaZ; z += 16) {
                Location l = s.clone().add(x * xMod, 0, z * zMod);
                    for (Entity e : l.getChunk().getEntities()) {
                        if (       e.getLocation().getBlockY() >= Math.min(start.getBlockY(), end.getBlockY()) 
                                && e.getLocation().getBlockY() <= Math.max(start.getBlockY(), end.getBlockY())              
                                && e.getLocation().getBlockX() >= Math.min(start.getBlockX(), end.getBlockX()) 
                                && e.getLocation().getBlockX() <= Math.max(start.getBlockX(), end.getBlockX())
                                && e.getLocation().getBlockZ() >= Math.min(start.getBlockZ(), end.getBlockZ()) 
                                && e.getLocation().getBlockZ() <= Math.max(start.getBlockZ(), end.getBlockZ()) 
                                ) {
                            entities.add(e);
                        }
                    }
            }
        }
        return entities;
    }
    
        /**
     * Gets all entities between bounding square of the two given locations, where start is the origin
     * @param start The start location
     * @param end The end location
     * @return All entities within this box
     */
    public static Set<Entity> getEntitiesBetween(Location start, Location end) {
        Set<Entity> entities = new HashSet<>();
        Location s = start.getChunk().getBlock(0, 0, 0).getLocation();
        int xMod = (start.getBlockX() < end.getBlockX()) ? 1 : -1;
        int zMod = (start.getBlockZ() < end.getBlockZ()) ? 1 : -1;
        int deltaX = (int) Math.ceil(start.distance(new Location(start.getWorld(), end.getBlockX(), start.getBlockY(), start.getBlockZ()))); // Horizontal distance
        int deltaZ = (int) Math.ceil(start.distance(new Location(start.getWorld(), start.getBlockX(), start.getBlockY(), end.getBlockZ()))); // Vertical distance

        for (int x = 0; x <= deltaX; x += 16) {
            for (int z = 0; z <= deltaZ; z += 16) {
                Location l = s.clone().add(x * xMod, 0, z * zMod);
                    for (Entity e : l.getChunk().getEntities()) {
                        if (       e.getLocation().getBlockX() >= Math.min(start.getBlockX(), end.getBlockX()) 
                                && e.getLocation().getBlockX() <= Math.max(start.getBlockX(), end.getBlockX())
                                && e.getLocation().getBlockZ() >= Math.min(start.getBlockZ(), end.getBlockZ()) 
                                && e.getLocation().getBlockZ() <= Math.max(start.getBlockZ(), end.getBlockZ()) 
                                ) {
                            entities.add(e);
                        }
                    }
            }
        }
        return entities;
    }
    
}
