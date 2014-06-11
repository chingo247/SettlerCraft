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
package com.sc.structure;

import com.sc.structure.construction.ConstructionStrategyType;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.BlockType;
import java.util.List;

/**
 * The SmartClipboard was originally meant to place blocks in a provided order.
 * In the current state it's also able to skip blocks if the target block
 * already is of the same type as the cuboid wants to place. e.g. When a
 * structure was placed and construction was halted (e.g. on server shutdown)
 * next time it will try to rebuild the structure at the same position but will
 * skip all blocks that are already out there, therefore the blockplace will
 * continue to place blocks he left.
 *
 * @author Chingo
 */
public class SmartClipBoard extends CuboidClipboard {

    private final List<Vector> vertices;
    private final CuboidClipboard parent;
    private boolean reversed;


    public SmartClipBoard(CuboidClipboard clipboard, List<Vector> vertices) {
        super(clipboard.getSize());
        this.vertices = vertices;
        this.parent = clipboard;
    }
    
    public void setReverse(boolean reversed) {
        this.reversed = reversed;
    }

    public boolean isReversed() {
        return reversed;
    }
    
    
    
    /**
     * Constructor.
     *
     * @param clipboard The CuboidClipboard
     * @param strategy The strategy
     */
    public SmartClipBoard(CuboidClipboard clipboard, ConstructionStrategyType strategy) {
        this(clipboard, strategy, false);
    }

    /**
     * Constructor.
     *
     * @param clipboard The CuboidClipboard
     * @param strategy The strategy
     * @param noAir Wheter or not air(-blocks) should be placed
     */
    public SmartClipBoard(CuboidClipboard clipboard, ConstructionStrategyType strategy, boolean noAir) {
        super(clipboard.getSize());
        this.parent = clipboard;
        this.vertices = strategy.getList(clipboard, noAir);
    }

    /**
     * Place blocks that
     *
     * @param editSession
     * @param pos
     * @param noAir
     * @throws MaxChangedBlocksException
     */
    @Override
    public void place(EditSession editSession, Vector pos, boolean noAir) throws MaxChangedBlocksException {
        
        for (Vector v : vertices) {
            BaseBlock b = parent.getBlock(v);
            if(b == null || (noAir && b.isAir())) {
                continue;
            }

            BaseBlock worldBlock = editSession.getBlock(v.add(pos));
            
            
            
            if(!isReversed()) {
                if (worldBlock.getId() == BlockID.BEDROCK || (worldBlock.getId() == b.getId() && worldBlock.getData() == b.getData())) {
                    continue;
                }
                editSession.rawSetBlock((v.add(pos)), b);
            } else {
                if(b.isAir() && worldBlock.isAir() || worldBlock.getId() == BlockID.BEDROCK) {
                continue;
                }
                
                if(worldBlock.getId() == b.getId() && !b.isAir() 
                        || (!BlockType.isNaturalTerrainBlock(worldBlock.getId(), worldBlock.getId()) && !worldBlock.isAir())
                        
                        ){
                    editSession.rawSetBlock((v.add(pos)), new BaseBlock(0));
                }
            }
            
        }

    }

}
