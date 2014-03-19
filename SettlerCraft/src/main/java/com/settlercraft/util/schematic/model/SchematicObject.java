/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.util.schematic.model;

import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

/**
 *
 * @author Chingo
 */
public class SchematicObject {

    public final int width;
    public final int height;
    public final int length;

    private final List<BlockData> blocks;
    private final List entities;
    private final List tileEntities;
    

    public SchematicObject(int width, int height, int length, List<BlockData> blocks, List entities, List tileEntities) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.blocks = blocks;
        this.entities = entities;
        this.tileEntities = tileEntities;
    }

    /**
     * Returns the blocks in the same order they came also the correct order to write to a file
     * @return all blocks of this schematic
     */
    public TreeSet<BlockData> getBlocksSorted() {
        return new TreeSet<>(this.blocks);
    }

    /**
     * Returns a HashMap of blocks with the layer as key, a schematic doesnt know anything about the
     * height of the blocks. The lowest blocks are at layer 0 (WHICH HAS NOTHING TO DO WITH THE REAL
     * HEIGHT)
     *
     * @return
     */
    public HashMap<Integer, TreeSet<BlockData>> getBlocksLayered() {
        HashMap<Integer, TreeSet<BlockData>> blks = new HashMap<>();

        for (BlockData b : blocks) {
            if (blks.get(b.layer) == null) {
                blks.put(b.layer, new TreeSet<BlockData>());
            }
            blks.get(b.layer).add(b);

        }
        return blks;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getLength() {
        return length;
    }

    public List<BlockData> getBlocks() {
        return blocks;
    }

    public List getEntities() {
        return entities;
    }

    public List getTileEntities() {
        return tileEntities;
    }
    
    
    
    

}
