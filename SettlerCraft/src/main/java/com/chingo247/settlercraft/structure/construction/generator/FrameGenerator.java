package com.chingo247.settlercraft.structure.construction.generator;

/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
