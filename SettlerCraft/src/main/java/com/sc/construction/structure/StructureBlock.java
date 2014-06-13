/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.construction.structure;

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

        int v = this.getPriority().compareTo(o.getPriority());
        if(v == 0) {
            Integer yMe = getPosition().getBlockY();
            Integer yO = getPosition().getBlockY();
            if(yO.compareTo(yMe) == 0) {
                Integer xMe = getPosition().getBlockX();
                Integer xO = getPosition().getBlockX();
                return xMe.compareTo(xO);
            }
            return yO.compareTo(yMe);
        }
        
        return v;
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
