/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.model.entity.structure.construction;

import com.google.common.base.Preconditions;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.plan.StructurePlan;
import com.settlercraft.core.model.plan.schematic.SchematicBlockData;
import com.settlercraft.core.model.plan.schematic.SchematicObject;
import com.settlercraft.core.persistence.StructureService;
import com.settlercraft.core.util.LocationUtil;
import com.settlercraft.core.util.LocationUtil.DIRECTION;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 *
 * @author Chingo
 */
public class Builder {

    /**
     * Removes all blocks (replace with air) that stand in the way of this building
     *
     * @param structure
     */
    public static void clearSite(Structure structure) {
        DIRECTION direction = structure.getDirection();
        Location target = structure.getDimensionStartLocation();
        StructurePlan sp = structure.getPlan();
        SchematicObject schematic = sp.getSchematic();

        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        for (int y = 0; y < schematic.layers; y++) {
            for (int z = schematic.length - 1; z > 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    Block b;
                    if (direction == DIRECTION.NORTH || direction == DIRECTION.SOUTH) {
                        b = target.clone().add(x * xMod, y, z * zMod).getBlock();
                    } else {
                        b = target.clone().add(z * zMod, y, x * xMod).getBlock();
                    }
                    b.setType(Material.AIR);
                }
            }
        }
        
    }

    /**
     * Instantly builds a structure
     *
     * @param structure The structure
     */
    public static void instantBuildStructure(Structure structure) {
        SchematicObject schematic = structure.getPlan().getSchematic();
        Preconditions.checkArgument(schematic.layers >= 2);
        Iterator<SchematicBlockData> it = schematic.getBlocksSorted().iterator();
        DIRECTION direction = structure.getDirection();
        Location target = structure.getStructureStartLocation();
        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        for (int y = 0; y < schematic.layers; y++) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    Block b;
                    if (direction == DIRECTION.NORTH || direction == DIRECTION.SOUTH) {
                        b = target.clone().add(x * xMod, y, z * zMod).getBlock();
                    } else {
                        b = target.clone().add(z * zMod, y, x * xMod).getBlock();
                    }
                    SchematicBlockData d = it.next();
                    b.setType(d.getMaterial());
                    b.setData(d.getData());
                }
            }
        }
    }

    /**
     * Creates a default foundation for given structure, this foundation will be immediately
     * generated at the buildings target location
     *
     * @param structure The structure
     */
    public static void placeDefaultFoundation(Structure structure) {
        SchematicObject schematic = structure.getPlan().getSchematic();
        DIRECTION direction = structure.getDirection();
        Location target = structure.getStructureStartLocation();

        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        for (int z = schematic.length - 1; z >= 0; z--) {
            for (int x = 0; x < schematic.width; x++) {
                Location l;
                if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
                    l = target.clone().add(x * xMod, 0, z * zMod);
                } else {
                    l = target.clone().add(z * zMod, 0, x * xMod);
                }
                l.getBlock().setType(Material.COBBLESTONE);
            }
        }
    }

    /**
     * Clears the lot from entities
     *
     * @param structure
     */
    public static void clearSiteFromEntities(Structure structure) {
        Set<Entity> entities = LocationUtil.getEntitiesWithin(structure.getStructureStartLocation(), structure.getStructureEndLocation());
        System.out.println(entities.size());
        for (Entity e : entities) {
//            if (structure.onLot(e.getLocation())) { // already selected within
                System.out.println("on lot!");
                if(e instanceof LivingEntity) {
                    System.out.println("moving: " + e);
                    moveEntityFromLot(structure, (LivingEntity) e);
                }
//            }
        }
    }

    private static void moveEntityFromLot(Structure structure, LivingEntity entity) {
        
        DIRECTION direction = structure.getDirection();
        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        int threshold = 5;

        Location[] locations = new Location[4];
        locations[0] = structure.getStructureStartLocation().clone().add(-threshold * xMod, 0, -threshold * zMod);
        locations[1] = structure.getStructureStartLocation().clone().add(threshold * xMod, 0, threshold * zMod);
        locations[2] = structure.getStructureStartLocation().clone().add(-threshold * xMod, 0, threshold * zMod);
        locations[3] = structure.getStructureStartLocation().clone().add(threshold * xMod, 0, -threshold * zMod);
        
        Location l = LocationUtil.getClosest(entity.getLocation(), locations);
        StructureService ss = new StructureService();
        Structure s = ss.getStructure(l);
        if(s != null && !s.getId().equals(structure.getId())) {
            moveEntityFromLot(s, entity);
        } else {
            Block target = l.getWorld().getHighestBlockAt(l.getBlockX(), l.getBlockY());
            if(target.getType() == Material.LAVA) {
                //TODO moveToAlternative location
            } else {
                entity.teleport(target.getLocation().add(0,1,0));
            }
        }
    }
    
  
    
    

    public static void placeFrame(Structure structure) {
        SchematicObject schematic = structure.getPlan().getSchematic();
        Preconditions.checkArgument(schematic.layers >= 2);
        DIRECTION direction = structure.getDirection();
        Location target = structure.getStructureStartLocation();
        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        for (int y = 0; y < schematic.layers; y++) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    if (y != 0 && (y == schematic.layers - 1 || z == 0 || x == 0 || z == schematic.length - 1 || x == schematic.width - 1)) {
                        Block b;
                        if (direction == DIRECTION.NORTH || direction == DIRECTION.SOUTH) {
                            b = target.clone().add(x * xMod, y, z * zMod).getBlock();
                        } else {
                            b = target.clone().add(z * zMod, y, x * xMod).getBlock();
                        }
                        b.setType(Material.FENCE);
                    }
                }
            }
        }
    }

    /**
     * Places an unfinished building at target location. The orientation must be given
     *
     * @param structure
     * @return true if structure was succesfully placed
     */
    public static boolean placeStructure(Structure structure) {
        StructureService ss = new StructureService();
        if (ss.overlaps(structure)) {
            Player player = Bukkit.getServer().getPlayer(structure.getOwner());
            if (player != null && player.isOnline()) {
                player.sendMessage(ChatColor.RED + "[SC]: Structure overlaps"); // TODO BETTER FEEDBACK
            }
            return false;
        }
        ss.save(structure); // CLAIMS Ground (Dimension)!
        structure.getConstructionSite().proceed();
//        Builder.clearSite(structure);
//        Builder.placeDefaultFoundation(structure);
//        Builder.placeFrame(structure);
        return true;
    }

}
