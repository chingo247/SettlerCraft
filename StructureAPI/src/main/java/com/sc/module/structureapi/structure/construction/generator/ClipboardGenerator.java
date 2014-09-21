/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.structure.construction.generator;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;

/**
 *
 * @author Chingo
 */
public class ClipboardGenerator {

    public static CuboidClipboard createEnclosure(CuboidClipboard structure, int material) {
        final int height = 1;
        CuboidClipboard enclosure = new CuboidClipboard(new Vector(structure.getWidth(), height, structure.getLength()));

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
