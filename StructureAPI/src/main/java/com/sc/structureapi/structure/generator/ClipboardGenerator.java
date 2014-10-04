/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.structureapi.structure.generator;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;

/**
 *
 * @author Chingo
 */
public class ClipboardGenerator {

    /**
     * Generate a cuboid clipboard with filled edges
     *
     * @param cuboidClipboard The base
     * @param material The material to use
     * @param height The height of the fence
     * @return The generated CuboidClipboard
     */
    public static CuboidClipboard createFence(CuboidClipboard cuboidClipboard, int material, int height) {
        return generate(cuboidClipboard.getSize(), material, height);
    }
    /**
     * Generate a cuboid clipboard with filled edges
     *
     * @param cuboidClipboard The size
     * @param material The material to use
     * @param height The height of the fence
     * @return The generated CuboidClipboard
     */
    public static CuboidClipboard generate(Vector cuboidClipboard, int material, int height) {
        CuboidClipboard enclosure = new CuboidClipboard(new Vector(cuboidClipboard.getBlockX(), height, cuboidClipboard.getBlockZ()));

        // Outer ring
        for (int z = 0; z < enclosure.getLength(); z += (enclosure.getLength() - 1)) {
            for (int x = 0; x < enclosure.getWidth(); x++) {
                for (int y = 0; y < height; y++) {
                    Vector v = new BlockVector(x, y, z);
                    enclosure.setBlock(v, new BaseBlock(material));
                }
            }
        }

        for (int z = 1; z < enclosure.getLength() - 1; z++) {
            for (int x = 0; x < enclosure.getWidth(); x += (enclosure.getWidth() - 1)) {
                for (int y = 0; y < enclosure.getHeight(); y++) {
                    Vector v = new BlockVector(x, y, z);
                    enclosure.setBlock(v, new BaseBlock(material));
                }
            }
        }

        return enclosure;
    }
}
