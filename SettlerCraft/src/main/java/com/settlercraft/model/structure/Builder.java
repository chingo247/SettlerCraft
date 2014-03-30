/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.structure;

import com.settlercraft.main.StructurePlanRegister;
import com.settlercraft.util.LocationUtil;
import com.settlercraft.util.LocationUtil.DIRECTION;
import com.settlercraft.util.schematic.model.BlockData;
import com.settlercraft.util.schematic.model.SchematicObject;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
public class Builder {

    public static void createDefaultFoundation(Structure structure) {
        SchematicObject schematic = StructurePlanRegister.getPlan(structure.getPlan())
                .getSchematic();
        DIRECTION direction = structure.getDirection();
        Location target = structure.getLocation();

        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];
        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    target.clone().add(x * xMod, 0, z * zMod).getBlock().setType(Material.COBBLESTONE);
                }
            }
        } else {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    target.clone().add(z * zMod, 0, x * xMod).getBlock().setType(Material.COBBLESTONE);
                }
            }
        }
    }

    /**
     * Builds the corresponding layer of this structure, whether the precoditions are met or not
     *
     * @param structure The structure
     * @param layer The layer to build
     */
    public static void buildLayer(Structure structure, int layer) {
        StructurePlan sp = StructurePlanRegister.getPlan(structure.getPlan());
        if (layer > sp.getSchematic().height) {
            throw new IndexOutOfBoundsException("layer out of bounds");
        }

        Iterator<BlockData> it = sp.getSchematic().getBlocksFromLayer(layer).iterator();
        SchematicObject schematic = sp.getSchematic();
        DIRECTION direction = structure.getDirection();
        Location target = structure.getLocation();

        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    target.clone().add(x * xMod, 0, z * zMod).getBlock().setType(it.next().getMaterial());
                }
            }
        } else { // SWAP X AND Z
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    target.clone().add(z * zMod, 0, x * xMod).getBlock().setType(it.next().getMaterial());
                }
            }
        }
    }

    public static void clearBuildSite(Structure structure) {
        DIRECTION direction = structure.getDirection();
        Location target = structure.getLocation();
        StructurePlan sp = StructurePlanRegister.getPlan(structure.getPlan());
        SchematicObject schematic = sp.getSchematic();

        int[] mods = LocationUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        if (direction == LocationUtil.DIRECTION.NORTH || direction == LocationUtil.DIRECTION.SOUTH) {
            for (int y = 0; y < schematic.height; y++) {
                for (int z = schematic.length - 1; z >= 0; z--) {
                    for (int x = 0; x < schematic.width; x++) {
                        target.clone().add(x * xMod, y, z * zMod).getBlock().setType(Material.AIR);
                    }
                }
            }
        } else { // SWAP X AND Z
            for (int y = 0; y < schematic.height; y++) {
                for (int z = schematic.length - 1; z >= 0; z--) {
                    for (int x = 0; x < schematic.width; x++) {
                        target.clone().add(z * zMod, y, x * xMod).getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }

    public static void placeStructureChest(Structure structure) {
        Location chestLocation = placeProgressEntity(structure.getLocation(), structure.getDirection(), 1, Material.CHEST);
        StructureChest chest = new StructureChest(chestLocation, structure);
        structure.setStructureChest(chest);
    }

    public static void placeStructureSign(Structure structure) {
        Location signLocation = placeProgressEntity(structure.getLocation(), structure.getDirection(), 2, Material.SIGN_POST);
        StructureSign sign = new StructureSign(signLocation, structure);
        structure.setStructureSign(sign);
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
}
