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
package com.chingo247.settlercraft.structureapi.construction.worldedit;

import com.chingo247.settlercraft.util.functions.CubicIterator;
import com.chingo247.settlercraft.structureapi.construction.options.ConstructionOptions;
import com.chingo247.settlercraft.structureapi.construction.worldedit.mask.ReplaceBlockClipboardMask;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.BlockType;
import java.util.PriorityQueue;

/**
 *
 * @author Chingo
 */
public abstract class StructureAsyncClipboard extends SmartClipboard {

    private static final int PRIORITY_FIRST = 4;
    private static final int PRIORITY_LIQUID = 3;
    private static final int PRIORITY_LATER = 2;
    private static final int PRIORITY_FINAL = 1;
    private final ConstructionOptions options;
    private static final int BLOCK_BETWEEN = 100;
    private static final int MAX_PLACE_LATER_TO_PLACE = 10;

    public StructureAsyncClipboard(CuboidClipboard parent, ConstructionOptions options) {
        super(parent);
        this.options = options;
        
        
    }

    @Override
    public void place(EditSession editSession, Vector pos, boolean noAir) throws MaxChangedBlocksException {
        int x = options.getXCube()<= 0 ? getSize().getBlockX(): options.getXCube();
        int y = options.getYCube()<= 0 ? getSize().getBlockY(): options.getYCube();
        int z = options.getZCube()<= 0 ? getSize().getBlockZ(): options.getZCube();
        
        ReplaceBlockClipboardMask mask = new ReplaceBlockClipboardMask(this, 35, 14, 35, 11);
        
        CubicIterator traversal = new CubicIterator(getSize(), x, y, z);
        PriorityQueue<StructureBlock> placeLater = new PriorityQueue<>();

        int placeLaterPlaced = 0;
        int placeLaterPause = 0;
        
        

        // Cube traverse this clipboard
        while (traversal.hasNext()) {
            Vector v = traversal.next();
            
            if(mask.test(v)) {
                setBlock(v, new BaseBlock(mask.getMaterial(), mask.getData()));
            }
            
            BaseBlock b = getBlock(v);
            if(b == null) continue;
            
            int priority = getPriority(b);

            if (priority == PRIORITY_FIRST) {
                doblock(editSession, b, v, pos);
            } else {
                placeLater.add(new StructureBlock(v, b));
            }

            // For every 10 place intensive blocks, place 100 normal blocks
            if (placeLaterPause > 0) {
                placeLaterPause--;
            } else {
                
                // only place after a greater ZCube-value, this way torches and other attachables will not be placed against air and break
                while (placeLater.peek() != null && (getCube(placeLater.peek().getPosition().getBlockZ(), options.getZCube()) < (getCube(v.getBlockZ(), options.getZCube())))) {
                    StructureBlock plb = placeLater.poll();
                    doblock(editSession, plb.getBlock(), plb.getPosition(), pos);
                    placeLaterPlaced++;
                    if (placeLaterPlaced >= MAX_PLACE_LATER_TO_PLACE) {
                        placeLaterPause = BLOCK_BETWEEN;
                        placeLaterPlaced = 0;
                    }
                }
            }
            

        }
        // Empty the queue
        while (placeLater.peek() != null) {
            StructureBlock plb = placeLater.poll();
            doblock(editSession, plb.getBlock(), plb.getPosition(), pos);
        }
    }

    public abstract void doblock(EditSession session, BaseBlock b, Vector blockPos, Vector pos);

    private int getCube(int index, int cube) {
        if (index % cube == 0) {
            return index / cube;
        } else {
            index -= (index % cube);
            return index / cube;
        }
    }

    private int getPriority(BaseBlock block) {
        if (isWater(block) || isLava(block)) {
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
        int bi = b.getType();
        if (bi == BlockID.LAVA || bi == BlockID.STATIONARY_LAVA) {
            return true;
        }
        return false;
    }

    private boolean isWater(BaseBlock b) {
        int bi = b.getType();
        if (bi == BlockID.WATER || bi == BlockID.STATIONARY_WATER) {
            return true;
        }
        return false;
    }

}
