/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.api.structure.construction.builder;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Blocks that need extra treatment (to be placed later) attachables and such
 * need a block to get attached to, if the attachable is placed before the
 * block. The attachable will break.
 *
 * @author Chingo
 */
class SpecialBlock {

    Material material;
    Byte data;
    Block block;

    public SpecialBlock(Material material, Byte data, Block block) {
        this.material = material;
        this.data = data;
        this.block = block;
    }

    public void place() {
        block.setData(data);
        block.setType(material);
    }

    public Block getBlock() {
        return block;
    }

    public Byte getData() {
        return data;
    }

    public Material getMaterial() {
        return material;
    }

}
