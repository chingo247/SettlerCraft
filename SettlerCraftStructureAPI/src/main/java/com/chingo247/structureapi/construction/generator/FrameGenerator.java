package com.chingo247.structureapi.construction.generator;

///*
// * Copyright (C) 2014 Chingo247
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package com.sc.api.structure.construction.generator;
//
//import com.sc.api.structure.construction.builder.strategies.FrameStrategy;
//import com.sc.api.structure.model.Structure;
//import com.sc.api.structure.model.structure.Structure;
//import com.sc.api.structure.util.CuboidUtil;
//import com.sk89q.worldedit.BlockVector;
//import com.sk89q.worldedit.CuboidClipboard;
//import com.sk89q.worldedit.EditSession;
//import com.sk89q.worldedit.Vector;
//import com.sk89q.worldedit.blocks.BaseBlock;
//import org.bukkit.Material;
//import static org.hsqldb.Tokens.SIMPLE;
//
///**
// *
// * @author Chingo
// * @deprecated Uses to many system resources
// */
//public class FrameGenerator {
//
//    public static void placeFrame(EditSession session, Structure structure, FrameStrategy strategy, int hGap, int vGap, boolean autoflush) {
//        CuboidClipboard cuboidClipboard = null;
//        switch (strategy) {
//            case SIMPLE:
//                cuboidClipboard = generateSimpleFrame(structure, vGap, hGap);
//                break;
//            case ADVANCED:
//                cuboidClipboard = generateFancyFrame(structure, vGap, hGap);
//                break;
//            default: throw new UnsupportedOperationException("Unkown action for: " + strategy);
//        }
////        SCCuboidBuilder.placeLayered(cuboidClipboard, cuboidClipboard.getLength()*cuboidClipboard.getWidth(), structure.getLocation(),  structure.getDirection());
//    }
//
//    /**
//     * generate a simple frame wich covers the entire tar
//     * @param structure
//     * @param vGap
//     * @param hGap
//     * @return 
//     */
//    public static CuboidClipboard generateSimpleFrame(Structure structure, int vGap, int hGap) {
//        CuboidClipboard schematic = structure.getPlan().getSchematic();
//
//        CuboidClipboard frame = new CuboidClipboard(new Vector(schematic.getWidth(), schematic.getHeight(), schematic.getLength()));
//        int mod;
//        for (int y = 1; y < schematic.getHeight(); y++) {
//            if (y % (vGap + 1) == 0) {
//                mod = 1;
//            } else {
//                mod = hGap + 1;
//            }
//            for (int z = schematic.getLength() - 1; z >= 0; z -= mod) {
//                for (int x = 0; x < schematic.getWidth(); x += schematic.getWidth() - 1) {
//                    if (y == schematic.getHeight() - 1 || z == 0 || x == 0 || z == schematic.getLength() - 1 || x == schematic.getWidth() - 1) {
//                        frame.setBlock(new BlockVector(x, y, z), new BaseBlock(Material.WOOD.getId()));
//                    }
//                }
//            }
//        }
//        return frame;
//    }
//
//    static CuboidClipboard generateFancyFrame(Structure structure, int vGap, int hGap) {
//        CuboidClipboard schematic = structure.getPlan().getSchematic();
//        CuboidClipboard frame = new CuboidClipboard(new Vector(schematic.getWidth(), schematic.getHeight(), schematic.getLength()));
//        int mod;
//        int[][] hMap = CuboidUtil.getHeightMap(schematic);
//        for (int y = 1; y < schematic.getHeight(); y++) {
//            if (y % (vGap + 1) == 0 || y == schematic.getHeight() - 1) {
//                mod = 1;
//            } else {
//                mod = hGap + 1;
//            }
//
//            for (int z = 0; z < schematic.getLength(); z += mod) {
//                for (int x = 0; x < schematic.getWidth(); x += mod) {
////                    if (z < schematic.getLength() && x < schematic.getWidth()) {
//                        if (y < hMap[x][z]) {
//                            frame.setBlock(new BlockVector(x, y, z), new BaseBlock(Material.WOOD.getId()));
//                        }
////                    }
//                }
//            }
//        }
//        return frame;
//    }
//    
//    public static void placeFrameAnimated(EditSession session, Structure structure, FrameStrategy strategy, int hGap, int vGap, int interval) {
//        CuboidClipboard cuboidClipboard = null;
//        switch (strategy) {
//            case SIMPLE:
//                cuboidClipboard = generateSimpleFrame(structure, vGap, hGap);
//                break;
//            case ADVANCED:
//                cuboidClipboard = generateFancyFrame(structure, vGap, hGap);
//                break;
//            default: throw new UnsupportedOperationException("Unknown Action for: " + strategy);
//
//        }
////        SCStructureBuilder.buildLayered(session, cuboidClipboard, structure.getLocation(), structure.getDirection(), BuildDirection.UP, interval);
//    }
//
//}
