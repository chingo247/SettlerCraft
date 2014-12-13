/*
 * Copyright (C) 2014 Chingo247
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

package com.chingo247.settlercraft.structure.construction.worldedit;

import com.chingo247.settlercraft.structure.construction.DemolitionOptions;
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
