/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction;

import com.settlercraft.core.model.entity.structure.Structure;
import com.settlercraft.core.model.plan.schematic.SchematicObject;
import com.settlercraft.core.model.world.Direction;
import com.settlercraft.core.util.Ticks;
import com.settlercraft.core.util.WorldUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author Chingo
 */
public class AnimatedFrameBuilder {

    private final Structure structure;
    private final int delay;
    private static final int defaultDelay = 2 * Ticks.ONE_SECOND;
    private final int DEFAULT_WALL_HEIGHT = 2;
    private final int hGap = 8;
    private final int vGap = 1;

    AnimatedFrameBuilder(Structure structure, int delay) {
        this.structure = structure;
        this.delay = delay;
    }

    AnimatedFrameBuilder(Structure structure) {
        this.structure = structure;
        this.delay = defaultDelay;
    }

    /**
     * Build frame for target structure
     */
    public void construct() {
        construct(FrameStrategy.DEFAULT);
    }

    /**
     * Contructs a frame for this structure
     *
     * @param strategy The strategy to place this frame
     */
    public void construct(FrameStrategy strategy) {
        switch (strategy) {
            case DEFAULT:
                placeDefaultAnimatedFrame(1);
                break;
            case FANCY:
                placeFancyAnimatedFrame(1);
                break;
            default:
                throw new UnsupportedOperationException("no strategy implemented for " + strategy);
        }
    }

    private void placeDefaultAnimatedFrame(int start) {
        SchematicObject schematic = structure.getPlan().getStructureSchematic();
        if (start < schematic.layers) {
            Direction direction = structure.getDirection();
            Location target = structure.getLocation();
            int[] mods = WorldUtil.getModifiers(direction);
            int xMod = mods[0];
            int zMod = mods[1];
            int mod;
            if (start % (vGap + 1) == 0 || start < DEFAULT_WALL_HEIGHT) {
                mod = 1;
            } else {
                mod = hGap + 1;
            }
            for (int z = schematic.length - 1; z >= 0; z -= mod) {
                for (int x = 0; x < schematic.width; x += mod) {
                    if (start == schematic.layers - 1 || z == 0 || x == 0 || z == schematic.length - 1 || x == schematic.width - 1) {

                        Block b;
                        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                            b = target.clone().add(x * xMod, start, z * zMod).getBlock();
                        } else {
                            b = target.clone().add(z * zMod, start, x * xMod).getBlock();
                        }
                        b.setType(Material.WOOD);
                    }
                }
            }
            final int next = start + 1;
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin(SCStructureAPI.MAIN_PLUGIN_NAME), new Runnable() {
                @Override
                public void run() {
                    placeDefaultAnimatedFrame(next);
                }
            }, delay);
        }

    }

    private void placeFancyAnimatedFrame(int start) {
        SchematicObject schematic = structure.getPlan().getStructureSchematic();

        if (start < schematic.layers) {
            Direction direction = structure.getDirection();
            Location target = structure.getLocation();
            int xMod = structure.getxMod();
            int zMod = structure.getzMod();

            int mod;
            if (start % (vGap + 1) == 0 || start < DEFAULT_WALL_HEIGHT) {
                mod = 1; // DO A COMPLETE LAYER
            } else {
                mod = hGap + 1; // DO SOME IN THIS LAYER
            }

            for (int z = schematic.length - 1; z >= 0; z -= mod) {
                for (int x = 0; x < schematic.width; x += mod) {
                    if (start <= schematic.getHighestAt(x, z)) {
                        Block b;
                        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                            b = target.clone().add(x * xMod, start, z * zMod).getBlock();
                        } else {
                            b = target.clone().add(z * zMod, start, x * xMod).getBlock();
                        }
                        b.setType(Material.WOOD);
                    }
                }
            }
            final int next = start + 1;
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin(SCStructureAPI.MAIN_PLUGIN_NAME), new Runnable() {
                @Override
                public void run() {
                    placeFancyAnimatedFrame(next);
                }
            }, delay);
        }
    }

}
