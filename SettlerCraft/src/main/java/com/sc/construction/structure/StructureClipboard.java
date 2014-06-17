/*
 * Copyright (C) 2014 Chingo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 *
 * @author Chingo
 */
public class StructureClipboard extends SmartClipboard {

    private boolean isDemolishing = false;
    private final Enclosure enclosure;
    private Comparator<StructureBlock> buildMode;
    private Comparator<StructureBlock> demolisionMode;

    public StructureClipboard(CuboidClipboard parent, Comparator<StructureBlock> buildMode, Comparator<StructureBlock> demolisionMode) {
        super(parent, buildMode);
        this.enclosure = new Enclosure(parent, 1, BlockID.IRON_BARS); // Default
        this.buildMode = buildMode;
        this.demolisionMode = demolisionMode.reversed();
    }

    public void setDemolishing(boolean demolishing) {
        this.isDemolishing = demolishing;
    }

    public Enclosure getEnclosure() {
        return enclosure;
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
        if (isDemolishing) {
            placeDemolishing(editSession, pos, noAir);
        } else {
            placeBuilding(editSession, pos, noAir);
        }
    }

    private void placeDemolishing(EditSession editSession, Vector pos, boolean noAir) {
        Queue<StructureBlock> structurequeu = new PriorityQueue<>(demolisionMode.reversed());
        Queue<StructureBlock> enclosureQueue = new PriorityQueue<>();
        for (int y = getParent().getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < getWidth(); x++) {
                for (int z = 0; z < getLength(); z++) {
                    final BlockVector v = new BlockVector(x, y, z);

                    BaseBlock b = getBlock(v);
                    if (b == null || (noAir && b.isAir())) {
                        continue;
                    }
                    structurequeu.add(new StructureBlock(v, b));
                }
            }

        }
        while (structurequeu.peek() != null) {
            StructureBlock b = structurequeu.poll();
            demolishBlock(editSession, b.getBlock(), b.getPosition(), pos);
        }

        while (enclosureQueue.peek() != null) {
            StructureBlock b = enclosureQueue.poll();
            demolishBlock(editSession, b.getBlock(), b.getPosition(), pos);
        }
    }

    private void placeBuilding(EditSession editSession, Vector pos, boolean noAir) {
        Queue<StructureBlock> enclosureQueue = new PriorityQueue<>();
        Queue<StructureBlock> structurequeu = new PriorityQueue<>(buildMode);
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                for (int z = 0; z < getLength(); z++) {
                    final BlockVector v = new BlockVector(x, y, z);
                    BaseBlock b = getBlock(v);
                    if (b == null || (noAir && b.isAir())) {
                        continue;
                    }

                    if ((b.isAir() || BlockType.canPassThrough(b.getId()))
                            && y <= Enclosure.START_HEIGHT + enclosure.getHeight()
                            && y >= Enclosure.START_HEIGHT
                            && (x == 0 || x == getWidth() - 1
                            || z == 0 || z == getLength() - 1)) {
                        enclosureQueue.add(new StructureBlock(v, b));
                        continue;
                    }

                    structurequeu.add(new StructureBlock(v, b));
                }
            }
        }

        while (structurequeu.peek() != null) {
            StructureBlock b = structurequeu.poll();
            buildBlock(editSession, b.getBlock(), b.getPosition(), pos);
        }

        while (enclosureQueue.peek() != null) {
            StructureBlock b = enclosureQueue.poll();
            buildBlock(editSession, b.getBlock(), b.getPosition(), pos);
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

        // If we are on the first floor
        if (blockPos.getBlockY() == 0 && !worldBlock.isAir()) {
            Vector wUnderPos = blockPos.add(pos).subtract(0, 1, 0);
            BaseBlock wUnder = session.getBlock(wUnderPos);
            // replace the block with the block underneath you if it is a natural block
            if (BlockType.isNaturalTerrainBlock(wUnder)) {
                session.rawSetBlock((blockPos.add(pos)), wUnder);
                return;
            }
        }

        if ((worldBlock.getId() == b.getId() && !b.isAir()) || worldBlock.getId() == enclosure.getMaterial()) {
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
