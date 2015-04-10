
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
package com.chingo247.structureapi.structure.construction.worldedit;

import com.chingo247.structureapi.structure.construction.options.ConstructionOptions;
import com.chingo247.structureapi.structure.construction.worldedit.mask.ReplaceBlockClipboardMask;
import com.chingo247.settlercraft.core.util.CubicIterator;
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
