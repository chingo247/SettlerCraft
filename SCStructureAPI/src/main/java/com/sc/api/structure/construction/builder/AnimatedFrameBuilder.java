///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.sc.api.structure.construction.builder;
//
//import com.sc.api.structure.SCStructureAPI;
//import com.sc.api.structure.construction.strategies.FrameStrategy;
//import com.settlercraft.core.model.entity.structure.Structure;
//import com.settlercraft.core.model.entity.structure.StructureState;
//import com.settlercraft.core.model.plan.schematic.SchematicObject;
//import com.settlercraft.core.model.world.Direction;
//import com.settlercraft.core.persistence.StructureService;
//import com.settlercraft.core.util.WorldUtil;
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.block.Block;
//
///**
// *
// * @author Chingo
// */
//public class AnimatedFrameBuilder {
//
//
//        /**
//     * Recursively Calls this method every interval this frame is finished
//     * @param structure The Structure to construct the frame
//     * @param strategy The strategy to use to build to frame
//     * @param hGap The gap between blocks horizontally
//     * @param vGap The gap between blocks vertically
//     * @param material The material to use, IMPORTANT NOTE: NEVER EVER USE FENCES OR ANY BLOCK THAT KEEPS TRACK OF ITS NEIGHBOURS
//     * When using fences the performance will drop dramatically (0-1 fps). This is not because of the algorithm that is used.
//     * fences keep track of their neighbours, therefore they cost more ticks to build. The amount of ticks needed increases when the amount of neightbours grows
//     * @param interval The interval at which layers are constructed, u can multiply {@link Ticks.ONE_SECOND} to get the amount of ticks in seconds
//     */
//    public static void construct(Structure structure, FrameStrategy strategy, int hGap, int vGap, Material material, int interval) {
//        StructureService structureService =  new StructureService();
//        structureService.setStatus(structure, StructureState.PLACING_FRAME);
//        final int startLayer = 1;
//        switch (strategy) {
//            case SIMPLE:
//                placeDefaultAnimatedFrame(startLayer, structure, hGap, vGap, material, interval);
//                break;
//            case FANCY:
//                placeFancyAnimatedFrame(startLayer, structure, hGap, vGap, material, interval);
//                break;
//            default:
//                throw new UnsupportedOperationException("no strategy implemented for " + strategy);
//        }
//
//    }
//
//    /**
//     * Recursively Calls this method every interval this frame is finished
//     * @param layer The layer to construct the frame
//     * @param structure The Structure to construct the frame for
//     * @param hGap The gap between blocks horizontally
//     * @param vGap The gap between blocks vertically
//     * @param material The material to use, IMPORTANT NOTE: NEVER EVER USE FENCES OR ANY BLOCK THAT KEEPS TRACK OF ITS NEIGHBOURS
//     * When using fences the performance will drop dramatically (0-1 fps). This is not because of the algorithm that is used.
//     * fences keep track of their neighbours, therefore they cost more ticks to build. The amount of ticks needed increases when the amount of neightbours grows
//     */
//    private static void placeDefaultAnimatedFrame(int layer, final Structure structure, final int hGap, final int vGap, final Material material, final int delay) {
//        SchematicObject schematic = structure.getPlan().getStructureSchematic();
//        if (layer < schematic.layers) {
//            Direction direction = structure.getDirection();
//            Location target = structure.getLocation();
//            int[] mods = WorldUtil.getModifiers(direction);
//            int xMod = mods[0];
//            int zMod = mods[1];
//            int mod;
//            if (layer % (vGap + 1) == 0) {
//                mod = 1;
//            } else {
//                mod = hGap + 1;
//            }
//            for (int z = schematic.length - 1; z >= 0; z -= mod) {
//                for (int x = 0; x < schematic.width; x += mod) {
//                    if (layer == schematic.layers - 1 || z == 0 || x == 0 || z == schematic.length - 1 || x == schematic.width - 1) {
//
//                        Block b;
//                        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
//                            b = target.clone().add(x * xMod, layer, z * zMod).getBlock();
//                        } else {
//                            b = target.clone().add(z * zMod, layer, x * xMod).getBlock();
//                        }
//                        b.setType(material);
//                    }
//                }
//            }
//            final int next = layer + 1;
//            Bukkit.getScheduler().runTaskLaterAsynchronously(SCStructureAPI.getInstance(), new Runnable() {
//                @Override
//                public void run() {
//                    placeDefaultAnimatedFrame(next, structure, hGap, next, Material.AIR, delay);
//                }
//            }, delay);
//        } else {
//            StructureService structureService = new StructureService();
//            structureService.setStatus(structure, StructureState.READY_TO_BE_BUILD);
//        }
//
//    }
//
//        /**
//     * Recursively Calls this method every interval this frame is finished
//     * @param layer The layer to construct the frame
//     * @param structure The Structure to construct the frame for
//     * @param hGap The gap between blocks horizontally
//     * @param vGap The gap between blocks vertically
//     * @param material The material to use, IMPORTANT NOTE: NEVER EVER USE FENCES OR ANY BLOCK THAT KEEPS TRACK OF ITS NEIGHBOURS
//     * When using fences the performance will drop dramatically (0-1 fps). This is not because of the algorithm that is used.
//     * fences keep track of their neighbours, therefore they cost more ticks to build. The amount of ticks needed increases when the amount of neightbours grows
//     */
//    private static void placeFancyAnimatedFrame(int layer, final Structure structure, final int hGap, final int vGap, final Material material, final int delay) {
//        SchematicObject schematic = structure.getPlan().getStructureSchematic();
//
//        if (layer < schematic.layers) {
//            Direction direction = structure.getDirection();
//            Location target = structure.getLocation();
//            int xMod = structure.getxMod();
//            int zMod = structure.getzMod();
//
//            int mod;
//            if (layer % (vGap + 1) == 0) {
//                mod = 1; // DO A COMPLETE LAYER
//            } else {
//                mod = hGap + 1; // DO SOME IN THIS LAYER
//            }
//
//            for (int z = schematic.length - 1; z >= 0; z -= mod) {
//                for (int x = 0; x < schematic.width; x += mod) {
//                    if (layer <= schematic.getHighestAt(x, z)) {
//                        Block b;
//                        if (direction == Direction.NORTH || direction == Direction.SOUTH) {
//                            b = target.clone().add(x * xMod, layer, z * zMod).getBlock();
//                        } else {
//                            b = target.clone().add(z * zMod, layer, x * xMod).getBlock();
//                        }
//                        b.setType(material);
//                    }
//                }
//            }
//            final int next = layer + 1;
//            Bukkit.getScheduler().runTaskLaterAsynchronously(SCStructureAPI.getInstance(), new Runnable() {
//                @Override
//                public void run() {
//                    placeFancyAnimatedFrame(next, structure, hGap, vGap, material, delay);
//                }
//            }, delay);
//        } else {
//            StructureService structureService = new StructureService();
//            structureService.setStatus(structure, StructureState.READY_TO_BE_BUILD);
//        }
//    }
//
//}
