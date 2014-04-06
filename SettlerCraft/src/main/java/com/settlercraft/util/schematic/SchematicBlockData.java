/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.settlercraft.util.schematic;





/**
 * Schematic blockdata knows the place and the exact material the block is made of using the byte 
 * @author Chingo
 */
public class SchematicBlockData extends StructureBlock implements Comparable<SchematicBlockData> {
    public final int x;
    public final int z;
    public final int layer;

    public SchematicBlockData(int x, int z, int layer, int material, byte data) {
        super(material, data);
        this.x = x;
        this.z = z;
        this.layer = layer;
    }

  @Override
  public boolean equals(Object o) {
    if(!(o instanceof SchematicBlockData)) {
      return false;
    } 
    
    SchematicBlockData b = (SchematicBlockData) o;
    return this.x == b.x 
            && this.z == b.z 
            && this.layer == b.layer;
  }
    
    

    @Override
    public int compareTo(SchematicBlockData o) {
        if (layer > o.layer) {
            return 1;
        } else if (layer == o.layer) {
            if (z > o.z) {
                return 1;
            } else if (z == o.z) {
                    if(x > o.x) {
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

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public int getLayer() {
        return layer;
    }


    
    
    
   
}
