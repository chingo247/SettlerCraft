/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.model.plan.schematic;

/**
 * SchematicBlockData consists all information about a block inside a schematic
 *
 * @author Chingo
 */
public class SchematicBlockData extends BlockMaterial implements Comparable<SchematicBlockData> {

    /**
     * The x position within the schematic
     */
    public final int x;
    /**
     * The z position within the schematic
     */
    public final int z;
    /**
     * The height or layer within a schematic
     */
    public final int layer;

    public SchematicBlockData(int x, int z, int layer, int material, byte data) {
        super(material, data);
        this.x = x;
        this.z = z;
        this.layer = layer;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SchematicBlockData)) {
            return false;
        }

        SchematicBlockData b = (SchematicBlockData) o;
        return this.x == b.x
                && this.z == b.z
                && this.layer == b.layer;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + this.x;
        hash = 59 * hash + this.z;
        hash = 59 * hash + this.layer;
        return hash;
    }

    @Override
    public int compareTo(SchematicBlockData o) {
        if (layer > o.layer) {
            return 1;
        } else if (layer == o.layer) {
            if (z > o.z) {
                return 1;
            } else if (z == o.z) {
                if (x > o.x) {
                    return 1;
                } else {
                    return -1;
                }

            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    /**
     * Gets the x value of this block within the schematic
     * @return The x value
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the z value of this block within the schematic
     * @return The z value
     */
    public int getZ() {
        return z;
    }

    /**
     * Gets the height/layer value of this block within the schematic
     * @return The layer value
     */
    public int getLayer() {
        return layer;
    }

}
