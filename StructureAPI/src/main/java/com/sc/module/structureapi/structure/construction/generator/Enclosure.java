/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sc.module.structureapi.structure.construction.generator;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;

/**
 *
 * @author Chingo
 */
public class Enclosure extends CuboidClipboard {
    private static final int START_HEIGHT = 1; // 1 block above ground level
    private final int height;
    private final int material;

    public Enclosure(CuboidClipboard structure, int height, int material) {
        super(new BlockVector(structure.getWidth(), START_HEIGHT + height, structure.getLength()));
        Preconditions.checkArgument(height > 0);
        this.height = height;
        this.material = material;
        
        for (int z = 0; z < getLength(); z+= (getLength() - 1)) {
            for (int x = 0; x < getWidth(); x++) {
                for (int y = START_HEIGHT; y < getHeight(); y++) {
                    Vector v = new BlockVector(x, y, z);
                    setBlock(v, new BaseBlock(material));
                }
            }
        }
        
        for(int z = 1; z < getLength() - 1; z++) {
            for(int x = 0; x < getWidth(); x += (getWidth()-1)) {
               for (int y = START_HEIGHT; y < getHeight(); y++) {
                    Vector v = new BlockVector(x, y, z);
                    setBlock(v, new BaseBlock(material));
                } 
            }
        }
    }

    public int getEnclosureHeight() {
        return height;
    }

    public int getMaterial() {
        return material;
    }
    
}
