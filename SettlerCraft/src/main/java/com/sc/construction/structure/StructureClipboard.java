/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.construction.structure;

import com.sc.construction.generator.Enclosure;
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

    private boolean isDemolishing = false;
    private Enclosure enclosure;
    private final CuboidClipboard parent;

    public StructureClipboard(CuboidClipboard parent) {
        super(parent.getSize());
        this.setOffset(parent.getOffset());
        this.setOrigin(parent.getOrigin());
        this.parent = parent;
    }

    public void setDemolishing(boolean demolishing) {
        this.isDemolishing = demolishing;
    }

    public void setEnclosure(Enclosure enclosure) {
        this.enclosure = enclosure;
    }

    /**
     * Place blocks from the minimum corner using an alternative algorithm that skips blocks that
     * are already there.
     *
     * @param editSession
     * @param pos
     * @param noAir
     * @throws MaxChangedBlocksException
     */
    @Override
    public void place(EditSession editSession, Vector pos, boolean noAir) throws MaxChangedBlocksException {
        Queue<StructureBlock> enclosureQueue = new PriorityQueue<>();
        int start = 0;
        int mod = 1;
        if (isDemolishing) {
            mod = -1;
            start = parent.getHeight() - 1;
        }

        for (int y = start; (!isDemolishing && y < parent.getHeight()) || (isDemolishing && y >= 0); y += mod) {
            Queue<StructureBlock> blocks = new PriorityQueue<>();
            for (int x = 0; x < parent.getWidth(); x++) {
                for (int z = 0; z < parent.getLength(); z++) {
                    final BlockVector v = new BlockVector(x, y, z);

                    BaseBlock b = parent.getBlock(v);
                    if (b == null || (noAir && b.isAir())) {
                        continue;
                    }

                    if (!isDemolishing && enclosure != null) {
                        if (b.isAir()
                                && y <= Enclosure.START_HEIGHT + enclosure.getHeight()
                                && y >= Enclosure.START_HEIGHT
                                && (x == 0 || x == getWidth() - 1
                                || z == 0 || z == getLength() - 1)) {
                            enclosureQueue.add(new StructureBlock(v, b));
                            continue;
                        }
                    }

                    // If we are building place the breakable (torches, etc) structure afther every layer
                    if (needsAttention(b) && !isDemolishing) {
                        blocks.add(new StructureBlock(v, b));
                        continue;
                    // If we are demolishing first remove the breakables and then remove the rest
                    } else if (!needsAttention(b) && isDemolishing) {
                        blocks.add(new StructureBlock(v, b));
                        continue;
                    }

                    if (!isDemolishing) {
                        buildBlock(editSession, b, v, pos);
                    } else {
                        demolishBlock(editSession, b, pos, pos);
                    }
                }
            }
            while (blocks.peek() != null) {
                StructureBlock b = blocks.poll();
                if (!isDemolishing) {
                    buildBlock(editSession, b.getBlock(), b.getPosition(), pos);
                } else {
                    demolishBlock(editSession, b.getBlock(), pos, pos);
                }
            }

        }
        if(!isDemolishing) {
            while (enclosureQueue.peek() != null) {
                StructureBlock b = enclosureQueue.poll();
                buildBlock(editSession, b.getBlock(), b.getPosition(), pos);
            }
        }

    }

    private void buildBlock(EditSession session, BaseBlock b, Vector blockPos, Vector pos) {
        BaseBlock worldBlock = session.getBlock(blockPos.add(pos));
        if (worldBlock.getId() == b.getId() && worldBlock.getData() == b.getData()) {
            return;
        }
        session.rawSetBlock(blockPos.add(pos), b);
    }

    private void demolishBlock(EditSession session, BaseBlock b, Vector blockPos, Vector pos) {
        BaseBlock worldBlock = session.getBlock(blockPos.add(pos));
        if (b.isAir() && worldBlock.isAir() || worldBlock.getId() == BlockID.BEDROCK) {
            return;
        }

        if (worldBlock.getId() == b.getId() && !b.isAir()
                || (!BlockType.isNaturalTerrainBlock(worldBlock.getId(), worldBlock.getId()) && !worldBlock.isAir())) {
            session.rawSetBlock((blockPos.add(pos)), new BaseBlock(0));
        }
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
    
    private boolean needsAttention(BaseBlock b) {
        return (isLava(b) || isWater(b) || BlockType.shouldPlaceLast(b.getId()) || BlockType.shouldPlaceFinal(b.getId()));
    }

}
