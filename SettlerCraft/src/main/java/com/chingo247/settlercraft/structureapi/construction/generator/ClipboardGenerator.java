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

package com.chingo247.settlercraft.structureapi.construction.generator;

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
