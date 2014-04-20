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
import com.settlercraft.core.util.WorldUtil;
import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author Chingo
 */
public class FrameBuilder {

    private final Structure structure;

    FrameBuilder(final Structure structure) {
        this.structure = structure;
    }

    /**
     * Build construct for target structure
     */
    public void construct() {
        construct(FrameStrategy.DEFAULT);
    }

    /**
     * Use an animated builder to construct this frame
     * @param delay The delay in ticks
     * @return AnimatedFrameBuilder for this structure
     */
    public AnimatedFrameBuilder anim(int delay) {
        return new AnimatedFrameBuilder(structure, delay);
    }

    /**
     * Contructs a construct for this structure.
     * @param strategy The strategy to place this frame
     */
    public final void construct(FrameStrategy strategy) {
        switch (strategy) {
            case DEFAULT:
                placeDefaultFrame();
            case FANCY:
                placeFancyFrame();
            default:
                throw new UnsupportedOperationException("no strategy implemented for " + strategy);
        }
    }

    private void placeDefaultFrame() {
        SchematicObject schematic = structure.getPlan().getStructureSchematic();
        Direction direction = structure.getDirection();
        Location target = structure.getLocation();
        int[] mods = WorldUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        for (int y = 1; y < schematic.layers; y++) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    if (y == schematic.layers - 1 || z == 0 || x == 0 || z == schematic.length - 1 || x == schematic.width - 1) {
                        Block b;
                        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
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

    private void placeFancyFrame() {
        SchematicObject schematic = structure.getPlan().getStructureSchematic();
        Direction direction = structure.getDirection();
        Location target = structure.getLocation();
        Iterator<SchematicBlockData> it = schematic.getBlocksSorted().iterator();
        int[] mods = WorldUtil.getModifiers(direction);
        int xMod = mods[0];
        int zMod = mods[1];

        for (int y = 1; y < schematic.layers; y++) {
            for (int z = schematic.length - 1; z >= 0; z--) {
                for (int x = 0; x < schematic.width; x++) {
                    SchematicBlockData d = it.next();
                    if(d.getMaterial().isBlock()) {
                        Block b;
                        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
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


}