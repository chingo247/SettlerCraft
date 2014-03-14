/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.schematic.model;

import com.blockhaus2000.materialidlib.MaterialIdLib;
import org.bukkit.Material;





/**
 *
 * @author Chingo
 */
public class BlockData implements Comparable<BlockData> {
    public final byte data;
    public final int x;
    public final int z;
    public final int layer;
    public final int material;

    public BlockData(int x, int z, int layer, int material, byte data) {
        this.x = x;
        this.z = z;
        this.layer = layer;
        this.material = material;
        this.data = data;
    }

    @Override
    public int compareTo(BlockData o) {
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
    

    
    public Material getMaterial() {
        return MaterialIdLib.getMaterialById(material);
    }

    public byte getData() {
        return data;
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
