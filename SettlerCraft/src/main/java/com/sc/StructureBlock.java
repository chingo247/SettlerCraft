/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.BlockType;

/**
 *
 * @author Chingo
 */
public class StructureBlock implements Comparable<StructureBlock> {

    private static final int PRIORITY_FIRST = 4;
    private static final int PRIORITY_LIQUID = 3;
    private static final int PRIORITY_LATER = 2;
    private static final int PRIORITY_FINAL = 1;

    private final Vector position;
    private final BaseBlock block;

    public StructureBlock(Vector p, BaseBlock b) {
        this.position = p;
        this.block = b;
    }

    public BaseBlock getBlock() {
        return block;
    }

    public Vector getPosition() {
        return position;
    }

    @Override
    public int compareTo(StructureBlock o) {

        return this.getPriority().compareTo(o.getPriority());
    }

    private Integer getPriority() {
        if (isWater(block) || isLava(block))  {
            return PRIORITY_LIQUID;
        }

        if (BlockType.shouldPlaceLast(block.getId())) {
            return PRIORITY_LATER;
        }

        if (BlockType.shouldPlaceFinal(block.getId())) {
            return PRIORITY_FINAL;
        }

        return PRIORITY_FIRST;

    }

    private boolean isLava(BaseBlock b) {
        Integer bi = b.getType();
        if (bi == BlockID.LAVA || bi == BlockID.STATIONARY_LAVA) {
            return true;
        }
        return false;
    }

    private boolean isWater(BaseBlock b) {
        Integer bi = b.getType();
        if (bi == BlockID.WATER || bi == BlockID.STATIONARY_WATER) {
            return true;
        }
        return false;
    }

}
