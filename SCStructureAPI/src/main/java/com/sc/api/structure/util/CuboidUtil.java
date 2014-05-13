/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.util;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.BlockType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Chingo
 */
public class CuboidUtil {

    public static int getHighestYAt(CuboidClipboard clipboard, int x, int z) {
        int height = clipboard.getHeight();

        int y = height - 1;
        while (y > 0) {
            if (clipboard.getBlock(new Vector(x, y, z)).isAir()) {
                y--;
            } else {
                break;
            }
        }
        return y;
    }

    public static int[][] getHeightMap(CuboidClipboard clipboard) {
        int[][] hMap = new int[clipboard.getWidth()][clipboard.getLength()];
        System.out.println("length:" + clipboard.getLength());
        System.out.println("width: " + clipboard.getWidth());

        for (int x = 0; x < clipboard.getWidth(); x++) {
            for (int z = 0; z < clipboard.getLength(); z++) {
                hMap[x][z] = getHighestYAt(clipboard, x, z);
            }
        }
        return hMap;
    }

    public static List<CuboidClipboard> getLayers(CuboidClipboard clipboard) {
        Preconditions.checkArgument(clipboard.getHeight() > 1);
        List<CuboidClipboard> layers = new ArrayList<>(clipboard.getHeight());
        CuboidClipboard placeLater = new CuboidClipboard(clipboard.getSize());
        CuboidClipboard placeFinal = new CuboidClipboard(clipboard.getSize());
//        CuboidClipboard water = new CuboidClipboard(clipboard.getSize());
//        CuboidClipboard lava = new CuboidClipboard(clipboard.getSize());

        for (int layer = 0; layer < clipboard.getHeight(); layer++) {
            CuboidClipboard ccb = new CuboidClipboard(new BlockVector(clipboard.getSize()));
            for (int x = 0; x < clipboard.getWidth(); x++) {
                for (int z = 0; z < clipboard.getLength(); z++) {
                    
                    BaseBlock b = clipboard.getBlock(new BlockVector(x, layer, z));
                    BlockVector vector = new BlockVector(x, layer, z);
                    if(b.getId() == BlockID.AIR) {
                        continue;
                    }
//                    if (isWater(b)) {
//                        water.setBlock(vector, b);
//                    } else if (isLava(b)) {
//                        lava.setBlock(vector, b);
                    if (BlockType.shouldPlaceLast(b.getId())) {
                        if(layer > 0) {
                            placeLater.setBlock(vector.add(0, -1, 0), ccb.getBlock(new BlockVector(x, layer - 1, z)));
                        }
                        placeLater.setBlock(vector, b);
                    } else if (BlockType.shouldPlaceFinal(b.getId())) {
                        if(layer > 0) {
                            placeLater.setBlock(vector.add(0, -1, 0), b);
                        }
                        placeFinal.setBlock(vector, b);
                    } else {
                        ccb.setBlock(vector, b);
                    }
                }
            }
            layers.add(ccb);
        }
       
//        layers.add(water);
//        layers.add(lava);
        layers.add(placeLater);
        layers.add(placeFinal);
        System.out.println("layers: " + layers.size());
        return layers;
    }

    private static boolean isLava(BaseBlock b) {
        Integer bi = b.getType();
        if (bi == BlockID.LAVA || bi == BlockID.STATIONARY_LAVA) {
            return true;
        }
        return false;
    }

    private static boolean isWater(BaseBlock b) {
        Integer bi = b.getType();
        if (bi == BlockID.WATER || bi == BlockID.STATIONARY_WATER) {
            return true;
        }
        return false;
    }

    public static CuboidClipboard getLayer(CuboidClipboard whole, int layer) {
        CuboidClipboard layerClip = new CuboidClipboard(new BlockVector(whole.getWidth(), 1, whole.getLength()));
        for (int x = 0; x < whole.getWidth(); x++) {
            for (int z = 0; z < whole.getLength(); z++) {
                layerClip.setBlock(new BlockVector(x, 0, z), whole.getBlock(new BlockVector(x, layer, z)));
            }
        }
        return layerClip;
    }

}
