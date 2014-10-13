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

package com.chingo247.settlercraft.structure.worldedit;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 *
 * @author Chingo
 */
public abstract class StructureClipboard extends SmartClipboard {

    private final Comparator<StructureBlock> CONSTRUCTION_ORDER;
    
    public StructureClipboard(CuboidClipboard parent, Comparator<StructureBlock> constructionOrder) {
        super(parent);
        this.CONSTRUCTION_ORDER = constructionOrder;
    }
    
    @Override
    public void place(EditSession editSession, Vector pos, boolean noAir) throws MaxChangedBlocksException {
        Queue<StructureBlock> structurequeu = new PriorityQueue<>(CONSTRUCTION_ORDER);
        for (int y = 0; y < getHeight(); y++) {
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
            doblock(editSession, b.getBlock(), b.getPosition(), pos);
        }
    }

    public abstract void doblock(EditSession session, BaseBlock b, Vector blockPos, Vector pos);

}
