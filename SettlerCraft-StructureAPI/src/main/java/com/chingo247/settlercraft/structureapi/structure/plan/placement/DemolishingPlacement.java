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
package com.chingo247.settlercraft.structureapi.structure.plan.placement;

import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.DemolishingOptions;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.AbstractPlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.PlacementTypes;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.iterator.TopDownCuboidIterator;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.BlockType;
import java.util.Iterator;

/**
 *
 * @author Chingo
 */
public class DemolishingPlacement extends AbstractPlacement<DemolishingOptions> {
    

    public DemolishingPlacement(Vector size) {
        super(size.getBlockX(), size.getBlockY(), size.getBlockZ());
    }

    @Override
    public void place(EditSession session, Vector pos, DemolishingOptions option) {
        TopDownCuboidIterator cit = new TopDownCuboidIterator(
                option.getCubeX() < 0 ? getSize().getBlockX() : option.getCubeX(),
                option.getCubeY() < 0 ? getSize().getBlockY() : option.getCubeY(), 
                option.getCubeZ() < 0 ? getSize().getBlockZ() : option.getCubeZ()
        );
        Iterator<Vector> traversal = cit.iterate(getSize());
       
        while(traversal.hasNext()) {
            Vector relativePosition = traversal.next();
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
    
    @Override
    public String getTypeName() {
        return PlacementTypes.DEMOLISHING;
    }

    
}
