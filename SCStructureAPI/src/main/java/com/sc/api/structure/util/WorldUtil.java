/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.util;

import com.google.common.base.Preconditions;
import com.sc.api.structure.model.structure.Structure;
import com.sc.api.structure.model.structure.world.Direction;
import com.sc.api.structure.model.structure.world.WorldDimension;
import com.sc.api.structure.persistence.StructureService;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

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
    public static Direction getDirection(float yaw) {
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

    /**
     * Gets the yaw for given direction
     *
     * @param direction The direction
     * @return float, yaw value
     */
    public static float getYaw(Direction direction) {
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
    public static int[] getModifiers(Direction direction) {
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

    public static Direction getDirection(int xMod, int zMod) {
        if (xMod != 1 && xMod != -1) {
            throw new IllegalArgumentException("x modifier: " + xMod + " not allowed, must be 1 or -1");
        }
        if (zMod != 1 && zMod != -1) {
            throw new IllegalArgumentException("z modifier: " + zMod + " not allowed, must be 1 or -1");
        }

        if (xMod == 1 && zMod == -1) {
            return Direction.NORTH;
        } else if (xMod == 1 && zMod == 1) {
            return Direction.EAST;
        } else if (xMod == -1 && zMod == 1) {
            return Direction.SOUTH;
        } else /**
         * if(xMod == -1 && zMod == -1 *
         */
        {
            return Direction.WEST;
        }
    }

    public static Direction getDirection(Entity entity) {
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
     *
     * @param start The start location
     * @param end The end location
     * @return All entities within this box
     */
    public static Set<Entity> getEntitiesWithin(Location start, Location end) {
        System.out.println(start.getX() + " : " + start.getY() + " : " + start.getZ());
        System.out.println(end.getX() + " : " + end.getY() + " : " + end.getZ());
        Set<Entity> entities = new HashSet<>();
        Location s = start.getChunk().getBlock(0, 0, 0).getLocation();
        int xMod = (start.getBlockX() < end.getBlockX()) ? 1 : -1;
        int zMod = (start.getBlockZ() < end.getBlockZ()) ? 1 : -1;
        int deltaX = (int) Math.ceil(start.distance(new Location(start.getWorld(), end.getBlockX(), start.getBlockY(), start.getBlockZ()))); // Horizontal distance
        int deltaZ = (int) Math.ceil(start.distance(new Location(start.getWorld(), start.getBlockX(), start.getBlockY(), end.getBlockZ()))); // Vertical distance

        for (int x = 0; x <= Math.max(deltaX, 16); x += 16) {
            for (int z = 0; z <= Math.max(deltaZ, 16); z += 16) {
                Location l = s.clone().add(x * xMod, 0, z * zMod);
                for (Entity e : l.getChunk().getEntities()) {
                    if (e.getLocation().getBlockY() >= Math.min(start.getBlockY(), end.getBlockY())
                            && e.getLocation().getBlockY() <= Math.max(start.getBlockY(), end.getBlockY())
                            && e.getLocation().getBlockX() >= Math.min(start.getBlockX(), end.getBlockX())
                            && e.getLocation().getBlockX() <= Math.max(start.getBlockX(), end.getBlockX())
                            && e.getLocation().getBlockZ() >= Math.min(start.getBlockZ(), end.getBlockZ())
                            && e.getLocation().getBlockZ() <= Math.max(start.getBlockZ(), end.getBlockZ())) {
                        entities.add(e);
                    }
                }
            }
        }
        return entities;
    }

    /**
     * Gets all entities between bounding square of the two given locations, where start is the
     * origin
     *
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
                    if (e.getLocation().getBlockX() >= Math.min(start.getBlockX(), end.getBlockX())
                            && e.getLocation().getBlockX() <= Math.max(start.getBlockX(), end.getBlockX())
                            && e.getLocation().getBlockZ() >= Math.min(start.getBlockZ(), end.getBlockZ())
                            && e.getLocation().getBlockZ() <= Math.max(start.getBlockZ(), end.getBlockZ())) {
                        entities.add(e);
                    }
                }
            }
        }
        return entities;
    }

    /**
     * Moves all entities from structure within the structure to the borders of this structure if
     * the new location is on another structure, the entity will be moved again
     *
     * @param structure The structure
     */
    public static void evacuate(Structure structure) {
        Set<Entity> entities = WorldUtil.getEntitiesWithin(structure.getDimension().getStart(), structure.getDimension().getEnd());
        for (Entity e : entities) {
            if (e instanceof LivingEntity) {
                moveEntityFromLot((LivingEntity) e, 5, structure);
            }
        }
    }

    /**
     * Moves the given entity from the given target structure, the entity will be moved beyond the
     * closest border. If the entity isnt within the structure, no actions will be taken. If the new
     * location of the entity is a location on another structure, then this method will call itself
     * recursively To prevent a stackoverflow the distance value will be doubled each recursive call
     *
     * @param entity
     * @param distance
     * @param targetStructure
     */
    public static void moveEntityFromLot(LivingEntity entity, int distance, Structure targetStructure) {
        Preconditions.checkArgument(distance > 0);
        if (targetStructure.isOnLot(entity.getLocation())) {
            return;
        }

        WorldDimension dimension = targetStructure.getDimension();
        Location start = dimension.getStart();
        Location end = dimension.getEnd();
        if (entity.getLocation().distance(start) < entity.getLocation().distance(end)) {
            Location xMinus = new Location(start.getWorld(),
                    start.getBlockX() - distance, // X
                    start.getWorld().getHighestBlockYAt(start.getBlockX() - distance, entity.getLocation().getBlockZ()), // Y
                    entity.getLocation().getBlockZ() // Z
            );
            Location zMinus = new Location(start.getWorld(),
                    entity.getLocation().getBlockX(),
                    start.getWorld().getHighestBlockYAt(entity.getLocation().getBlockX() - distance, start.getBlockZ() - distance),
                    start.getBlockZ() - distance
            );
            if (entity.getLocation().distance(xMinus) < entity.getLocation().distance(zMinus)) {
                moveEntity(entity, distance, xMinus);
            } else {
                moveEntity(entity, distance, zMinus);
            }
        } else {
            Location xPlus = new Location(end.getWorld(),
                    end.getBlockX() + distance, // X
                    end.getWorld().getHighestBlockYAt(end.getBlockX() + distance, entity.getLocation().getBlockZ()), // Y
                    entity.getLocation().getBlockZ()
            );                                                                      // Z

            Location zPlus = new Location(end.getWorld(),
                    entity.getLocation().getBlockX(),
                    end.getWorld().getHighestBlockYAt(entity.getLocation().getBlockX() + distance, end.getBlockZ() + distance),
                    end.getBlockZ() + distance
            );
            if (entity.getLocation().distance(xPlus) < entity.getLocation().distance(zPlus)) {
                moveEntity(entity, distance, xPlus);
            } else {
                moveEntity(entity, distance, zPlus);
            }
        }
    }

    private static void moveEntity(LivingEntity entity, int distance, Location target) {
        StructureService structureService = new StructureService();
        if (target.getBlock().getType() == Material.LAVA) {
            // Alternative?
            // TODO use alternative

        } else if (structureService.isOnStructure(target)) {
            moveEntityFromLot(entity, distance * 2, structureService.getStructure(target));
        }
    }

}
