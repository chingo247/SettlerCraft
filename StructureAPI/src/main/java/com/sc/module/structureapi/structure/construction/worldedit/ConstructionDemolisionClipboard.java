/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.module.structureapi.structure.construction.worldedit;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.BlockType;
import java.util.Comparator;

/**
 *
 * @author Chingo
 */
public class ConstructionDemolisionClipboard extends ConstructionClipboard {

    

    public ConstructionDemolisionClipboard(CuboidClipboard parent, Comparator<StructureBlock> comparator) {
        super(parent, comparator);
    }

    @Override
    public void doblock(EditSession session, BaseBlock b, Vector blockPos, Vector pos) {
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

        if ((worldBlock.getId() == b.getId() && !b.isAir())) {
            session.rawSetBlock((blockPos.add(pos)), new BaseBlock(0));
        }
    }

}
