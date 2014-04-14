/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.core.model.plan.schematic;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

/**
 * SchematicObject contains all the information that was read from a .schematic file
 * @author Chingo
 */
public class SchematicObject {

    public final int width;
    public final int layers;
    public final int length;

    private final List<SchematicBlockData> blocks;
    private final List entities;
    private final List tileEntities;

    public SchematicObject(int width, int height, int length, List<SchematicBlockData> blocks, List entities, List tileEntities) {
        this.width = width;
        this.layers = height;
        this.length = length;
        this.blocks = blocks;
        this.entities = entities;
        this.tileEntities = tileEntities;
    }

    /**
     * Gets all the schematic blocks whitin this schematic sorted in the way they will be build
     * @return all blocks of this schematic
     */
    public TreeSet<SchematicBlockData> getBlocksSorted() {
        return new TreeSet<>(this.blocks);
    }
    
    public SchematicBlockData[][][] getBlocksAsArray() {
        SchematicBlockData[][][] blks = new SchematicBlockData[layers][length][width];
        for (int layer = 0; layer < layer * length * width; layer += width * length) {
            for (int z = 0; z < length * width; z += width) {
                for (int x = 0; x < width; x++) {
                    blks[layers][length][width] = blocks.get(layer * length * width);
                }
            }
        }
        return blks;
    }


    /**
     * Gets all blocks of corresponding layer
     *
     * @param layer The layer between 0 and the layers of this building
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
 layers of the blocks. The lowest blocks are at layer 0 (WHICH HAS NOTHING TO DO WITH THE REAL
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
        return layers;
    }

    public int getLength() {
        return length;
    }

    public Collection<SchematicBlockData> getBlocks() {
        return blocks;
    }

    public Collection getEntities() {
        return entities;
    }

    public Collection getTileEntities() {
        return tileEntities;
    }

    @Override
    public String toString() {
        return "width: " + width + " length: " + length + " height: " + layers;
    }

}
