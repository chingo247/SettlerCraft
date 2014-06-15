/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.construction.structure;

import com.sc.construction.generator.Enclosure;
import com.sc.plugin.ConfigProvider;
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
public class StructureClipboard extends CuboidClipboard {

    private boolean isDemolishing = false;
    private final Enclosure enclosure;
    private final CuboidClipboard parent;
    private Comparator<StructureBlock> buildMode;
    private Comparator<StructureBlock> demolisionMode;

    public StructureClipboard(CuboidClipboard parent) {
        super(parent.getSize());
        this.setOffset(parent.getOffset());
        this.setOrigin(parent.getOrigin());
        this.parent = parent;
        this.enclosure = new Enclosure(parent, 1, BlockID.IRON_BARS);
    }

    public void setDemolishing(boolean demolishing) {
        this.isDemolishing = demolishing;
    }

    public void setDemolisionMode(int mode) {
        this.demolisionMode = getMode(mode);
    }

    public void setBuildMode(int mode) {
        this.buildMode = getMode(mode);
    }

    private Comparator<StructureBlock> getMode(int mode) {
        switch (mode) {
            case 0:
                return StructureCompare.PERFORMANCE;
            case 1:
                return StructureCompare.COMPROMIS;
            case 2:
                return StructureCompare.FANCY;
            default:
                throw new AssertionError("Unreachable");
        }
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
        long start = System.currentTimeMillis();
        if (isDemolishing) {
            if (ConfigProvider.getInstance().isOverridingModes() || demolisionMode == null) {
                this.demolisionMode = getMode(ConfigProvider.getInstance().getDemolisionMode());
            }
            
            placeDemolishing(editSession, pos, noAir);
        } else {
            if (ConfigProvider.getInstance().isOverridingModes() || buildMode == null) {
                this.buildMode = getMode(ConfigProvider.getInstance().getBuildMode());
            }
            placeBuilding(editSession, pos, noAir);
        }
        long end = System.currentTimeMillis();
        System.out.println("Operation done in: " + (end - start));
    }

    private void placeDemolishing(EditSession editSession, Vector pos, boolean noAir) {
        Queue<StructureBlock> structurequeu = new PriorityQueue<>(demolisionMode.reversed());
        Queue<StructureBlock> enclosureQueue = new PriorityQueue<>();
        for (int y = parent.getHeight() - 1; y >= 0; y--) {
            for (int x = 0; x < parent.getWidth(); x++) {
                for (int z = 0; z < parent.getLength(); z++) {
                    final BlockVector v = new BlockVector(x, y, z);

                    BaseBlock b = parent.getBlock(v);
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
        for (int y = 0; y < parent.getHeight(); y++) {
            for (int x = 0; x < parent.getWidth(); x++) {
                for (int z = 0; z < parent.getLength(); z++) {
                    final BlockVector v = new BlockVector(x, y, z);
                    BaseBlock b = parent.getBlock(v);
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
