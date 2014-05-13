/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction.builder;

import com.sc.api.structure.construction.builder.strategies.FrameStrategy;
import com.sc.api.structure.model.structure.Structure;
import com.sc.api.structure.util.CuboidUtil;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import org.bukkit.Material;

/**
 *
 * @author Chingo
 */
public class SCFrameBuilder {

    public static void placeFrame(EditSession session, Structure structure, FrameStrategy strategy, int hGap, int vGap, boolean autoflush) {
        CuboidClipboard cuboidClipboard = null;
        switch (strategy) {
            case SIMPLE:
                cuboidClipboard = generateSimpleFrame(structure, vGap, hGap);
                break;
            case ADVANCED:
                cuboidClipboard = generateFancyFrame(structure, vGap, hGap);
                break;
            default: throw new UnsupportedOperationException("Unkown action for: " + strategy);
        }
//        SCCuboidBuilder.placeLayered(cuboidClipboard, cuboidClipboard.getLength()*cuboidClipboard.getWidth(), structure.getLocation(),  structure.getDirection());
    }

    static CuboidClipboard generateSimpleFrame(Structure structure, int vGap, int hGap) {
        CuboidClipboard schematic = structure.getPlan().getSchematic();

        CuboidClipboard frame = new CuboidClipboard(new Vector(schematic.getWidth(), schematic.getHeight(), schematic.getLength()));
        int mod;
        for (int y = 1; y < schematic.getHeight(); y++) {
            if (y % (vGap + 1) == 0) {
                mod = 1;
            } else {
                mod = hGap + 1;
            }
            for (int z = schematic.getLength() - 1; z >= 0; z -= mod) {
                for (int x = 0; x < schematic.getWidth(); x += schematic.getWidth() - 1) {
                    if (y == schematic.getHeight() - 1 || z == 0 || x == 0 || z == schematic.getLength() - 1 || x == schematic.getWidth() - 1) {
                        frame.setBlock(new BlockVector(x, y, z), new BaseBlock(Material.WOOD.getId()));
                    }
                }
            }
        }
        return frame;
    }

    static CuboidClipboard generateFancyFrame(Structure structure, int vGap, int hGap) {
        CuboidClipboard schematic = structure.getPlan().getSchematic();
        CuboidClipboard frame = new CuboidClipboard(new Vector(schematic.getWidth(), schematic.getHeight(), schematic.getLength()));
        int mod;
        int[][] hMap = CuboidUtil.getHeightMap(schematic);
        for (int y = 1; y < schematic.getHeight(); y++) {
            if (y % (vGap + 1) == 0 || y == schematic.getHeight() - 1) {
                mod = 1;
            } else {
                mod = hGap + 1;
            }

            for (int z = 0; z < schematic.getLength(); z += mod) {
                for (int x = 0; x < schematic.getWidth(); x += mod) {
//                    if (z < schematic.getLength() && x < schematic.getWidth()) {
                        if (y < hMap[x][z]) {
                            frame.setBlock(new BlockVector(x, y, z), new BaseBlock(Material.WOOD.getId()));
                        }
//                    }
                }
            }
        }
        return frame;
    }
    
    public static void placeFrameAnimated(EditSession session, Structure structure, FrameStrategy strategy, int hGap, int vGap, int interval) {
        CuboidClipboard cuboidClipboard = null;
        switch (strategy) {
            case SIMPLE:
                cuboidClipboard = generateSimpleFrame(structure, vGap, hGap);
                break;
            case ADVANCED:
                cuboidClipboard = generateFancyFrame(structure, vGap, hGap);
                break;
            default: throw new UnsupportedOperationException("Unknown Action for: " + strategy);

        }
//        SCStructureBuilder.buildLayered(session, cuboidClipboard, structure.getLocation(), structure.getDirection(), BuildDirection.UP, interval);
    }

}
