/*
 * The MIT License
 *
 * Copyright 2013 SBPrime.
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
package com.sc.api.structure.construction.builder;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;
import com.sk89q.worldedit.blocks.BlockType;

/**
 * Features Vertically pasting / placing a ClipBoard
 * @author Chingo
 */
public class SCLayeredCuboidClipBoard extends CuboidClipboard {

    public SCLayeredCuboidClipBoard(CuboidClipboard clipboard) {
        super(clipboard.getSize());
        for (int y = 0; y < getHeight(); ++y) {
            for (int x = 0; x < getWidth(); ++x) {
                for (int z = 0; z < getLength(); ++z) {
                    final BaseBlock block = clipboard.getBlock(new BlockVector(x, y, z));
                    if (block == null) {
                        continue;
                    }
                    this.setBlock(getOrigin().add(new BlockVector(x, y, z)), block);
                }
            }
        }
    }

    @Override
    public void paste(EditSession editSession, Vector newOrigin, boolean noAir) throws MaxChangedBlocksException {
        this.paste(editSession, newOrigin, noAir, false);
    }
    
    

    @Override
    public void paste(EditSession editSession, Vector newOrigin, boolean noAir, boolean entities) throws MaxChangedBlocksException {
        this.place(editSession, newOrigin.add(getOffset()), noAir);
        if(entities) {
            pasteEntities(newOrigin);
        }
    }

    
    
    
    

    @Override
    public void place(EditSession editSession, Vector pos, boolean noAir) throws MaxChangedBlocksException {
        CuboidClipboard placeLater = new CuboidClipboard(this.getSize());
        CuboidClipboard placeFinal = new CuboidClipboard(this.getSize());
        CuboidClipboard water = new CuboidClipboard(this.getSize());
        CuboidClipboard lava = new CuboidClipboard(this.getSize());
        
        
        for (int y = 0; y < getHeight(); ++y) {
            for (int x = 0; x < getWidth(); ++x) {
                for (int z = 0; z < getLength(); ++z) {
                    final BlockVector v = new BlockVector(x, y, z);
                    final BaseBlock b = getBlock(v);
                    if (b == null) {
                        continue;
                    }

                    if (noAir && b.isAir()) {
                        continue;
                    }
                    
                    if (isWater(b)) {
                        water.setBlock(v, b);
                    } else if (isLava(b)) {
                        lava.setBlock(v, b);
                    } else if (BlockType.shouldPlaceLast(b.getId())) {
                        placeLater.setBlock(v, b);
                    } else if (BlockType.shouldPlaceFinal(b.getId())) {
                        placeFinal.setBlock(v, b);
                    } else {
                        editSession.setBlock(new Vector(x, y, z).add(pos), b);
                    }
                }
            }
            
        }
        water.place(editSession, pos, noAir);
        lava.place(editSession, pos, noAir);
        placeLater.place(editSession, pos, noAir);
        placeFinal.place(editSession, pos, noAir);
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
    
    

}
