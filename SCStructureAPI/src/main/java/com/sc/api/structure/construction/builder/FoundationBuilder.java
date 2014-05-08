///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.sc.api.structure.construction.builder;
//
//import com.sc.api.structure.construction.strategies.FoundationStrategy;
//import com.settlercraft.core.model.entity.structure.Structure;
//import com.settlercraft.core.model.entity.structure.StructureState;
//import com.settlercraft.core.model.plan.schematic.SchematicBlockData;
//import com.settlercraft.core.model.plan.schematic.SchematicObject;
//import com.settlercraft.core.model.world.Direction;
//import com.settlercraft.core.persistence.StructureService;
//import com.settlercraft.core.util.WorldUtil;
//import java.util.Iterator;
//import org.bukkit.Location;
//import org.bukkit.Material;
//
///**
// *
// * @author Chingo
// */
//class FoundationBuilder {
//
//
//
//    /**
//     * A foundation will be created beneath the structure. A foundation is
//     * doesnt have any functionality its just there to give the player some
//     * feedback. And also clears the construction site from any blocks
//     * @param structure The structure
//     * @param strategy The strategy
//     */
//    public static void construct(Structure structure, FoundationStrategy strategy) {
//        StructureService structureService = new StructureService();
//        structureService.setStatus(structure, StructureState.PLACING_FOUNDATION);
//        switch (strategy) {
//            case DEFAULT:
//                if (structure.getPlan().getFoundationSchematic() != null) {
//                    placeProvidedFoundation(structure);
//                } else {
//                    placeDefaultFoundation(structure);
//                }
//                break;
//            case FANCY:
//                placeFancyFoundation(structure);
//
//        }
//
//        structureService.setStatus(structure, StructureState.PLACING_FRAME);
//    }
//
//    private static void placeDefaultFoundation(Structure structure) {
//        SchematicObject schematic = structure.getPlan().getStructureSchematic();
//        Direction direction = structure.getDirection();
//        Location target = structure.getLocation();
//
//        int[] mods = WorldUtil.getModifiers(direction);
//        int xMod = mods[0];
//        int zMod = mods[1];
//        for (int z = schematic.length - 1; z >= 0; z--) {
//            for (int x = 0; x < schematic.width; x++) {
//                Location l;
//                if (direction == Direction.NORTH || direction == Direction.SOUTH) {
//                    l = target.clone().add(x * xMod, 0, z * zMod);
//                } else {
//                    l = target.clone().add(z * zMod, 0, x * xMod);
//                }
//                l.getBlock().setType(Material.COBBLESTONE);
//            }
//        }
//    }
//
//    private static void placeProvidedFoundation(Structure structure) {
//        SchematicObject schematic = structure.getPlan().getFoundationSchematic();
//        Direction direction = structure.getDirection();
//        Location target = structure.getLocation();
//        Iterator<SchematicBlockData> it = structure.getPlan().getFoundationSchematic().getBlocksSorted().iterator();
//
//        int[] mods = WorldUtil.getModifiers(direction);
//        int xMod = mods[0];
//        int zMod = mods[1];
//        for (int z = schematic.length - 1; z >= 0; z--) {
//            for (int x = 0; x < schematic.width; x++) {
//                SchematicBlockData sbd = it.next();
//                Location l;
//                if (direction == Direction.NORTH || direction == Direction.SOUTH) {
//                    l = target.clone().add(x * xMod, 0, z * zMod);
//                } else {
//                    l = target.clone().add(z * zMod, 0, x * xMod);
//                }
//                l.getBlock().setType(sbd.getMaterial());
//                l.getBlock().setData(sbd.getData());
//            }
//        }
//    }
//
//    private static void placeFancyFoundation(Structure structure) {
//        throw new UnsupportedOperationException("This feature is not supported yet");
//    }
//
//}
