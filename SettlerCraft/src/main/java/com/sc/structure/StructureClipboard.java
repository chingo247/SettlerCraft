/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.structure;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.BlockType;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 *
 * @author Chingo
 */
public class StructureClipboard extends CuboidClipboard {

    private static final int PRIORITY_FIRST = 3;
    private static final int PRIORITY_LIQUID = 2;
    private static final int PRIORITY_LATER = 1;
    private static final int PRIORITY_FINAL = 0;
    private boolean reversed = false;

    public StructureClipboard(CuboidClipboard parrent) {
        super(parrent.getSize());
        this.setOffset(parrent.getOffset());
        this.setOrigin(parrent.getOrigin());

        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                for (int z = 0; z < getLength(); z++) {
                    BlockVector pt = new BlockVector(x, y, z);
                    BaseBlock block = parrent.getBlock(pt);
                    this.setBlock(pt, block);
                }
            }
        }
    }

    /**
     * Place blocks that
     *
     * @param editSession
     * @param pos
     * @param noAir
     * @throws MaxChangedBlocksException
     */
    @Override
    public void place(EditSession editSession, Vector pos, boolean noAir) throws MaxChangedBlocksException {

        for (int y = 0; y < getHeight(); y++) {
            Queue<StructureBlock> blocks = new PriorityQueue<>();
            for (int x = 0; x < getWidth(); x++) {
                for (int z = 0; z < getLength(); z++) {
                    final BlockVector v = new BlockVector(x, y, z);
                    
                    BaseBlock b = getBlock(v);
                    if (b == null || (noAir && b.isAir())) {
                        continue;
                    }

                    if (isLava(b) || isWater(b) || BlockType.shouldPlaceLast(b.getId()) || BlockType.shouldPlaceFinal(b.getId())) {
                        blocks.add(new StructureBlock(v, b));
                    } else {
                        buildBlock(editSession, b, v, pos, noAir);
                    }
                }
            }
            while (blocks.peek() != null) {
                StructureBlock b = blocks.poll();
                buildBlock(editSession, b.b, b.p, pos, noAir);
            }

        }

    }

    private void buildBlock(EditSession session, BaseBlock b, Vector blockPos, Vector pos, boolean noAir) {
        BaseBlock worldBlock = session.getBlock(blockPos.add(pos));
//        if (worldBlock.getId() != BlockID.BEDROCK && (worldBlock.getId() != b.getId() && worldBlock.getData() != b.getData())) {
             session.rawSetBlock(blockPos.add(pos), b);
//        }
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

    private class StructureBlock implements Comparable<StructureBlock> {

        private final Vector p;
        private final BaseBlock b;

        StructureBlock(Vector p, BaseBlock b) {
            this.p = p;
            this.b = b;
        }

        @Override
        public int compareTo(StructureBlock o) {

            return this.getPriority().compareTo(o.getPriority());
        }

        private Integer getPriority() {
            if (isWater(b) || isLava(b)) {
                return PRIORITY_LIQUID; // no 0
            }

            if (BlockType.shouldPlaceLast(b.getId())) {
                return PRIORITY_LATER;
            }

            if (BlockType.shouldPlaceFinal(b.getId())) {
                return PRIORITY_FINAL;
            }

            return PRIORITY_FIRST;

        }

    }

}
