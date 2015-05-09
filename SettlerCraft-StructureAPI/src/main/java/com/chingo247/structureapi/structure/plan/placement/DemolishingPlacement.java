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
package com.chingo247.structureapi.structure.plan.placement;

import com.chingo247.structureapi.structure.plan.placement.options.DemolishingOptions;
import com.chingo247.settlercraft.core.util.TopDownCubicIterator;
import com.chingo247.structureapi.structure.plan.placement.AbstractCuboidPlacement;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.BlockType;

/**
 *
 * @author Chingo
 */
public class DemolishingPlacement extends AbstractCuboidPlacement<DemolishingOptions> {

    public DemolishingPlacement(Vector size) {
        super(size.getBlockX(), size.getBlockY(), size.getBlockZ());
    }

    @Override
    public void place(EditSession session, Vector pos, DemolishingOptions option) {
        TopDownCubicIterator topDownCubicIterator = new TopDownCubicIterator(new BlockVector(width, height, length), option.getxAxisCube(), option.getyAxisCube(), option.getzAxisCube());
       
        while(topDownCubicIterator.hasNext()) {
            Vector relativePosition = topDownCubicIterator.next();
            Vector worldPosition = relativePosition.add(pos);
            
            BaseBlock currentBlock = session.getWorld().getBlock(worldPosition);
            
            if (currentBlock.isAir()  || currentBlock.getId() == BlockID.BEDROCK) {
                continue;
            }
            
            if (relativePosition.getBlockY() == 0 && !currentBlock.isAir()) {
                Vector wUnderPos = worldPosition.subtract(0, 1, 0);
                BaseBlock worldBlockUnder = session.getWorld().getBlock(wUnderPos);
            // replace the block with the block underneath you if it is a natural block
                if (BlockType.isNaturalTerrainBlock(worldBlockUnder)) {
                    session.rawSetBlock(worldPosition, worldBlockUnder);
                }
            } else {
                session.rawSetBlock(worldPosition, new BaseBlock(BlockID.AIR));
            }
            
            
                   
        }
        
    }
    
    private boolean shouldRemove(BaseBlock b) {
        return !BlockType.isNaturalTerrainBlock(b);
    }

    
    /**
     * Not allowed, doesn't have a type
     * @throws UnsupportedOperationException when called...
     */
    @Override
    public String getTypeName() {
        throw new UnsupportedOperationException("Not allowed...");
    }

    
}
