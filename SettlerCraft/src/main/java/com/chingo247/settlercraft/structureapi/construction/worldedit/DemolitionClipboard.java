/*
 * The MIT License
 *
 * Copyright 2015 Chingo.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.chingo247.settlercraft.structureapi.construction.worldedit;

import com.chingo247.settlercraft.structureapi.construction.options.DemolitionOptions;
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
public class DemolitionClipboard extends StructureAsyncClipboard {

    

    public DemolitionClipboard(CuboidClipboard parent, DemolitionOptions options) {
        super(parent, options);
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

        if ((worldBlock.getId() == b.getId() && !b.isAir()) || worldBlock.getId() == BlockID.IRON_BARS) {
            session.rawSetBlock((blockPos.add(pos)), new BaseBlock(0));
        }
    }

}
