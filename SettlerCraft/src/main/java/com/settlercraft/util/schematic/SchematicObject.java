/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.util.schematic;

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

    private final List<SchematicBlockData> blocks;
    private final List entities;
    private final List tileEntities;

    public SchematicObject(int width, int height, int length, List<SchematicBlockData> blocks, List entities, List tileEntities) {
        this.width = width;
        this.height = height;
        this.length = length;
        this.blocks = blocks;
        this.entities = entities;
        this.tileEntities = tileEntities;
    }

    /**
     * Returns the blocks in the same order they came also the correct order to write to a file
     *
     * @return all blocks of this schematic
     */
    public TreeSet<SchematicBlockData> getBlocksSorted() {
        return new TreeSet<>(this.blocks);
    }

    /**
     * Gets all materials of this building
     *
     * @return Map where the key is a blockdata and the value the frequency of the blockdata
     * FIXME DOESNT WORK
     */
    public HashMap<BlockData, Integer> getBlockData() {
        HashMap<BlockData, Integer> m = new HashMap<>();
        for (BlockData b : blocks) {
          BlockData data = b;
            if (m.get(data) == null) {
                m.put(data, 1);
            } else {
                m.put(data, m.get(data) + 1);
            }
        }
        return m;
    }

    /**
     * Gets all blocks of corresponding layer
     *
     * @param layer The layer between 0 and the height of this building
     * @return TreeSet of blockdata ordered by it place priority
     */
    public TreeSet<SchematicBlockData> getBlocksFromLayer(int layer) {
        TreeSet<SchematicBlockData> data = new TreeSet<>();
        for (SchematicBlockData b : blocks) {
            if (b.layer == layer) {
                data.add(b);
            }
        }
        return data;
    }

    /**
     * Returns a HashMap of blocks with the layer as key, a schematic doesnt know anything about the
     * height of the blocks. The lowest blocks are at layer 0 (WHICH HAS NOTHING TO DO WITH THE REAL
     * HEIGHT)
     *
     * @return
     */
    public HashMap<Integer, TreeSet<SchematicBlockData>> getBlocksLayered() {
        HashMap<Integer, TreeSet<SchematicBlockData>> blks = new HashMap<>();

        for (SchematicBlockData b : getBlocksSorted()) {
            if (blks.get(b.layer) == null) {
                blks.put(b.layer, new TreeSet<SchematicBlockData>());
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

    public List<SchematicBlockData> getBlocks() {
        return blocks;
    }

    public List getEntities() {
        return entities;
    }

    public List getTileEntities() {
        return tileEntities;
    }

    @Override
    public String toString() {
        return "width: " + width + " length: " + length + " height: " + height;
    }

}
