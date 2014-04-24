/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction.builders;

import com.sc.api.structure.construction.strategies.FrameStrategy;
import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.entity.structure.StructureState;
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
    private final int DEFAULT_WALL_HEIGHT = 1;
    private final int hGap = 2;
    private final int vGap = 1;
    private final FrameStrategy strategy;
    
    FrameBuilder(final Structure structure) {
        this.structure = structure;
        this.structureService = new StructureService();
        this.strategy = FrameStrategy.SIMPLE;
    }

    FrameBuilder(final Structure structure, FrameStrategy strategy) {
        this.structure = structure;
        this.structureService = new StructureService();
        this.strategy = strategy;
    }

    /**
     * Contructs a frame for this structure instantly
     */
    public final void construct() {
        structureService.setStatus(structure, StructureState.PLACING_FRAME);
        switch (strategy) {
            case SIMPLE:
                placeDefaultFrame();
                break;
            case FANCY:
                placeFancyFrame();
                break;
            default:
                throw new UnsupportedOperationException("no strategy implemented for " + strategy);
        }
        structureService.setStatus(structure, StructureState.READY_TO_BE_BUILD);
    }

    /**
     * Use an animated builder that will construct the frame layer by layer.
     * This builder will consume less ticks than the regular builder.
     * @param delay Delay between layers
     * @param strategy The strategy to be used
     * @return The animated builder
     */
    public AnimatedFrameBuilder anim(int delay, FrameStrategy strategy) {
        return new AnimatedFrameBuilder(structure, delay, strategy);
    }
    
    /**
     * Use an animated builder to construct this frame
     * @param delay The delay in ticks
     * @return AnimatedFrameBuilder for this structure
     */
    public AnimatedFrameBuilder anim(int delay) {
        return new AnimatedFrameBuilder(structure, delay, strategy);
    }

    /**
     * Use an animated builder that will construct a frame layer every 40 ticks (2 seconds).
     * This builder will consume less ticks than the regular builder.
     * @return AnimatedFrameBuilder for this structure
     */
    public AnimatedFrameBuilder anim() {
        return new AnimatedFrameBuilder(structure);
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
                for (int x = 0; x < schematic.width; x+= schematic.width - 1) {
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
