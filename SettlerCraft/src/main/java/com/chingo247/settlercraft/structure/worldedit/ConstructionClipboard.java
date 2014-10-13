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

package com.chingo247.settlercraft.structure.worldedit;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.util.Comparator;

/**
 *
 * @author Chingo
 */
public class ConstructionClipboard extends StructureClipboard {

    private final Comparator<StructureBlock> constructionMode;
    
    public ConstructionClipboard(CuboidClipboard parent, Comparator<StructureBlock> comparator) {
        super(parent, comparator);
        this.constructionMode = comparator;
    }


    

    @Override
    public void doblock(EditSession session, BaseBlock b, Vector blockPos, Vector pos) {
        BaseBlock worldBlock = session.getBlock(blockPos.add(pos));
        if (worldBlock.getId() == b.getId() && worldBlock.getData() == b.getData()) {
            return;
        }
        session.rawSetBlock(blockPos.add(pos), b);
    }

}
