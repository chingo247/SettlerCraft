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

package com.sc.api.structure.construction.builder.async;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockID;

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
                    if (b == null || (noAir && b.isAir())) {
                        continue;
                    }

                   
//                    if (isWater(b)) {
//                        water.setBlock(v, b);
//                    } else if (isLava(b)) {
//                        lava.setBlock(v, b);
//                    } else if (BlockType.shouldPlaceLast(b.getId())) {
//                        placeLater.setBlock(v, b);
//                    } else if (BlockType.shouldPlaceFinal(b.getId())) {
//                        placeFinal.setBlock(v, b);
//                    } else {
                        Vector p = new Vector(x, y, z).add(pos);
                        editSession.smartSetBlock(new Vector(x, y, z).add(pos), b);
//                    }
                }
            }
            
        }
//        water.place(editSession, pos, noAir);
//        lava.place(editSession, pos, noAir);
//        placeLater.place(editSession, pos, noAir);
//        placeFinal.place(editSession, pos, noAir);
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
