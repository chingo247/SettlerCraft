/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.entity.structure;

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
     * Builds the corresponding layer of this structure, whether the precoditions are met or not
     *
     * @param structure The structure
     * @param layer The layer to build
     */
    public static void buildLayer(Structure structure, int layer) {
        StructurePlan sp = structure.getPlan();
        if (layer > sp.getSchematic().height) {
            throw new IndexOutOfBoundsException("layer out of bounds");
        }

        Iterator<SchematicBlockData> it = sp.getSchematic().getBlocksFromLayer(layer).iterator();
        SchematicObject schematic = sp.getSchematic();
        DIRECTION direction = structure.getDirection();
        Location target = structure.getStartLocation();

        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    Block b = target.clone().add(x * xMod, layer, z * zMod).getBlock();
                    SchematicBlockData d = it.next();
                    b.setType(d.getMaterial());
                    b.setData(d.getData());
                }
            }
        } else { // SWAP X AND Z
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    Block b = target.clone().add(z * zMod, layer, x * xMod).getBlock();
                    SchematicBlockData d = it.next();
                    b.setType(d.getMaterial());
                    b.setData(d.getData());
                }
            }
        }
    }

    public static void clearBuildSite(Structure structure) {
        DIRECTION direction = structure.getDirection();
        Location target = structure.getStartLocation();
        StructurePlan sp = structure.getPlan();
        SchematicObject schematic = sp.getSchematic();

        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
            for (int y = 0; y < schematic.height; y++) {
                for (int z = schematic.length - 1; z > 0; z--) {
                    for (int x = 0; x < schematic.width; x++) {
                        target.clone().add(x * xMod, y, z * zMod).getBlock().breakNaturally();
                    }
                }
            }
        } else { // SWAP X AND Z
            for (int y = 0; y < schematic.height; y++) {
                for (int z = schematic.length - 1; z > 0; z--) {
                    for (int x = 0; x < schematic.width; x++) {
                        target.clone().add(z * zMod, y, x * xMod).getBlock().breakNaturally();
                    }
                }
            }
        }
    }

    public static StructureChest placeStructureChest(Structure structure) {
        Location chestLocation = placeProgressEntity(structure.getStartLocation(), structure.getDirection(), 1, Material.CHEST);
        StructureChest chest = new StructureChest(chestLocation, structure);
        structure.setStructureChest(chest);
        return chest;
    }

    public static StructureSign placeStructureSign(Structure structure) {
        Location signLocation = placeProgressEntity(structure.getStartLocation(), structure.getDirection(), 2, Material.SIGN_POST);
        StructureSign sign = new StructureSign(signLocation, structure);
        structure.setStructureSign(sign);
        return sign;
    }

    static Location placeProgressEntity(Location target, LocationUtil.DIRECTION direction, int yOffset, Material m) {
        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        Location loc;
        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
            loc = target.clone().add(0 * xMod, yOffset, -1 * zMod);
        } else {
            loc = target.clone().add(-1 * zMod, yOffset, 0 * xMod);
        }
        loc.getBlock().setType(m);
        return loc;
    }

    public static void instantBuildStructure(Location playerLocation, Location target, SchematicObject schematic) {
        DIRECTION direction = LocationUtil.getDirection(playerLocation.getYaw());
        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        Iterator<SchematicBlockData> it = schematic.getBlocksSorted().iterator();
        if (direction == DIRECTION.NORTH || direction == DIRECTION.SOUTH) {
            for (int y = 0; y < schematic.height; y++) {
                for (int z = schematic.length - 1; z >= 0; z--) {
                    for (int x = 0; x < schematic.width; x++) {
                        Block b = target.clone().add(x * xMod, y, z * zMod).getBlock();
                        SchematicBlockData d = it.next();
                        b.setType(d.getMaterial());
                        b.setData(d.getData());
                    }
                }
            }
        } else {
            for (int y = 0; y < schematic.height; y++) {
                for (int z = schematic.length - 1; z >= 0; z--) {
                    for (int x = 0; x < schematic.width; x++) {
                        Block b = target.clone().add(z * zMod, y, x * xMod).getBlock();
                        SchematicBlockData d = it.next();
                        b.setType(d.getMaterial());
                        b.setData(d.getData());
                    }
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
    public static void createDefaultFoundation(Structure structure) {
        SchematicObject schematic = structure.getPlan()
                .getSchematic();
        System.out.println(schematic);
        DIRECTION direction = structure.getDirection();
        Location target = structure.getStructureLocation();

        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    Location l = target.clone().add(x * xMod, 0, z * zMod);
                    l.getBlock().setType(Material.COBBLESTONE);
                }
            }
        } else {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    Location l = target.clone().add(z * zMod, 0, x * xMod);
                    l.getBlock().setType(Material.COBBLESTONE);
                }
            }
        }
    }
}
