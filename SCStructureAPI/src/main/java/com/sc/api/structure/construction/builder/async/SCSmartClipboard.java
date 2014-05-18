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

import com.sc.api.structure.construction.builder.strategies.PlacementStrategy;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.util.List;

/**
 * Features Vertically pasting / placing a ClipBoard
 * @author Chingo
 */
public class SCSmartClipboard extends CuboidClipboard {
    
    private List<Vector> vertices;
    private final CuboidClipboard parrent;

    public SCSmartClipboard(CuboidClipboard clipboard, PlacementStrategy strategy) {
        this(clipboard, strategy, true);
    }

    public SCSmartClipboard(CuboidClipboard clipboard, PlacementStrategy strategy, boolean noAir) {
        super(clipboard.getSize());
        this.parrent = clipboard;
        this.vertices = strategy.getList(clipboard, noAir);
    }
    
    private boolean validateVertices(CuboidClipboard c ,List<Vector> vertices) {
        for(Vector v : vertices) {
            if(v.getBlockX() < 0 || v.getBlockX() > c.getWidth()
                    || v.getBlockY() < 0 || v.getBlockY() > c.getHeight()
                    || v.getBlockZ() < 0 || v.getBlockZ() > c.getLength()) {
                return false;
            }
        }
        return true;
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
        
        for(Vector v : vertices) {
            BaseBlock worldBlock = editSession.getWorld().getBlock(pos.add(v));
            BaseBlock b = parrent.getBlock(v);
            if (b == null || (noAir && b.isAir()) || (worldBlock.getId() == b.getId() && worldBlock.getData() == b.getData())) {
                continue;
            }
            
            editSession.setBlock(pos.add(v), b);
        }
       
    }
    
    
    

}
