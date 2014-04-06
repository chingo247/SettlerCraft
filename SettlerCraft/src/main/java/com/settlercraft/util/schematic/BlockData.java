/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.settlercraft.util.schematic;

import org.bukkit.Material;

/**
 * Blockdata contains the material and also the byte to recognize the true identity of a block
 * @author Chingo
 */
public class BlockData {
    public final int material;
    public final byte data;

    public BlockData(int material, byte data) {
        this.material = material;
        this.data = data;
    }


    public byte getData() {
        return data;
    }
    
    public Material getMaterial() {
        return Material.getMaterial(material);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof BlockData)) {
            return false;
        }
        BlockData res = (BlockData) obj;
        return res.getData() == this.getData() && res.getMaterial() == this.getMaterial();
    }
    
}
