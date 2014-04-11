/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.entity.structure.construction;

import com.google.common.base.Preconditions;
import com.settlercraft.model.entity.structure.Structure;
import com.settlercraft.model.plan.StructurePlan;
import com.settlercraft.model.plan.schematic.SchematicBlockData;
import com.settlercraft.model.plan.schematic.SchematicObject;
import com.settlercraft.util.location.LocationUtil;
import com.settlercraft.util.location.LocationUtil.DIRECTION;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

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
    public static void clearBuildSite(Structure structure) {
        DIRECTION direction = structure.getDirection();
        Location target = structure.getStartLocation();
        StructurePlan sp = structure.getPlan();
        SchematicObject schematic = sp.getSchematic();

        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
            for (int y = 0; y < schematic.layers; y++) {
                for (int z = schematic.length - 1; z > 0; z--) {
                    for (int x = 0; x < schematic.width; x++) {
                        target.clone().add(x * xMod, y, z * zMod).getBlock().setType(Material.AIR);
                    }
                }
            }
        } else { // SWAP X AND Z
            for (int y = 0; y < schematic.layers; y++) {
                for (int z = schematic.length - 1; z > 0; z--) {
                    for (int x = 0; x < schematic.width; x++) {
                        target.clone().add(z * zMod, y, x * xMod).getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }

    public static void instantBuildStructure(Structure structure) {
        SchematicObject schematic = structure.getPlan().getSchematic();
        Preconditions.checkArgument(schematic.layers >= 2);
        Iterator<SchematicBlockData> it = schematic.getBlocksSorted().iterator();
        DIRECTION direction = structure.getDirection();
        Location target = structure.getStructureLocation();
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
        SchematicObject schematic = structure.getPlan()
                .getSchematic();
        System.out.println(schematic);
        DIRECTION direction = structure.getDirection();
        Location target = structure.getStructureLocation();

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

    public static void placeFrame(Structure structure) {
        SchematicObject schematic = structure.getPlan().getSchematic();
        Preconditions.checkArgument(schematic.layers >= 2);
        DIRECTION direction = structure.getDirection();
        Iterator<SchematicBlockData> it = schematic.getBlocksSorted().iterator();
        Location target = structure.getStructureLocation();
        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        for (int y = 0; y < schematic.layers; y++) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    if (z != 0 || x != 0 || z != schematic.length - 1 || x != schematic.width - 1) {
                        it.next();
                        continue;
                    }
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
 * Places an unfinished building at target location. The orientation must be given
 *
 * @param structure
 * @return true if structure was succesfully placed
 */
public static boolean placeStructure(Structure structure) {
//        StructureService ss = new StructureService();
//        if (ss.overlaps(structure)) {
//            Player player = Bukkit.getServer().getPlayer(structure.getOwner());
//            if (player != null && player.isOnline()) {
//                player.sendMessage(ChatColor.RED + "[SC]: Structure overlaps"); // TODO BETTER FEEDBACK
//            }
//            return false;
//        }
//        ss.save(structure); // CLAIMS Ground (Dimension)!
        Builder.clearBuildSite(structure);
        Builder.placeDefaultFoundation(structure);
        Builder.instantBuildStructure(structure);
        return true;
    }

}
