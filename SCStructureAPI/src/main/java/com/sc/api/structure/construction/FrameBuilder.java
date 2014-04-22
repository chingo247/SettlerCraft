/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction;

import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.plan.schematic.SchematicBlockData;
import com.settlercraft.core.model.plan.schematic.SchematicObject;
import com.settlercraft.core.model.world.Direction;
import com.settlercraft.core.persistence.StructureService;
import com.settlercraft.core.util.WorldUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author Chingo
 */
public class FrameBuilder {
    private final StructureService structureService;
    private final Structure structure;
    private final int DEFAULT_WALL_HEIGHT = 2;
    private final int hGap = 2;
    private final int vGap = 1;

    FrameBuilder(final Structure structure) {
        this.structure = structure;
        this.structureService = new StructureService();
    }

    /**
     * Build construct for target structure
     */
    public void construct() {
        structureService.setStatus(structure, Structure.StructureState.PLACING_FRAME);
        construct(FrameStrategy.DEFAULT);
        structureService.setStatus(structure, Structure.StructureState.READY_TO_BE_BUILD);
    }

    /**
     * Use an animated builder to construct this frame
     *
     * @param delay The delay in ticks
     * @return AnimatedFrameBuilder for this structure
     */
    public AnimatedFrameBuilder anim(int delay) {
        return new AnimatedFrameBuilder(structure, delay);
    }

    /**
     * Use an animated builder to construct this frame
     *
     * @return AnimatedFrameBuilder for this structure
     */
    public AnimatedFrameBuilder anim() {
        return new AnimatedFrameBuilder(structure);
    }

    /**
     * Contructs a construct for this structure.
     *
     * @param strategy The strategy to place this frame
     */
    public final void construct(FrameStrategy strategy) {
        structureService.setStatus(structure, Structure.StructureState.PLACING_FRAME);
        switch (strategy) {
            case DEFAULT:
                placeDefaultFrame();
                break;
            case FANCY:
                placeFancyFrame();
                break;
            default:
                throw new UnsupportedOperationException("no strategy implemented for " + strategy);
        }
        structureService.setStatus(structure, Structure.StructureState.READY_TO_BE_BUILD);
    }

    private void placeDefaultFrame() {
        SchematicObject schematic = structure.getPlan().getStructureSchematic();
        Direction direction = structure.getDirection();
        Location target = structure.getLocation();
        int[] mods = WorldUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        int mod;
        for (int y = 1; y < schematic.layers; y++) {
            if (y % (vGap + 1) == 0 || y < DEFAULT_WALL_HEIGHT ) {
                mod = 1;
            } else {
                mod = hGap + 1;
            }
            for (int z = schematic.length - 1; z >= 0; z-= mod) {
                for (int x = 0; x < schematic.width; x+= mod) {
                    if (y == schematic.layers - 1 || z == 0 || x == 0 || z == schematic.length - 1 || x == schematic.width - 1) {

                        Block b;
                        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                            b = target.clone().add(x * xMod, y, z * zMod).getBlock();
                        } else {
                            b = target.clone().add(z * zMod, y, x * xMod).getBlock();
                        }
                        b.setType(Material.WOOD);
                    }
                }
            }
        }
    }

    private void placeFancyFrame() {
        SchematicObject schematic = structure.getPlan().getStructureSchematic();
        Direction direction = structure.getDirection();
        Location target = structure.getLocation();
        SchematicBlockData[][][] arr = schematic.getBlocksAsArray();
        int xMod = structure.getxMod();
        int zMod = structure.getzMod();
        int mod;
        for (int y = 1; y < schematic.layers; y++) {
            if (y % (vGap + 1) == 0 || y < DEFAULT_WALL_HEIGHT) { // To avoid small entities from entering this structure as good as possible therefore 3
                mod = 1;
            } else {
                mod = hGap + 1;
            }

            for (int z = schematic.length - 1; z >= 0; z -= mod) {
                for (int x = 0; x < schematic.width; x += mod) {
                    if (y <= schematic.getHighestAt(x, z)) {
                        Block b;
                        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                            b = target.clone().add(x * xMod, y, z * zMod).getBlock();
                        } else {
                            b = target.clone().add(z * zMod, y, x * xMod).getBlock();
                        }
                        b.setType(Material.WOOD);
                    }
                }
            }
        }
    }
}
