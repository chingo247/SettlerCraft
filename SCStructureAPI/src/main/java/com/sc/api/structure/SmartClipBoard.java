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
package com.sc.api.structure;

import com.sc.api.structure.construction.progress.ConstructionStrategyType;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.util.ArrayList;
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


    public SmartClipBoard(CuboidClipboard clipboard, List<Vector> vertices) {
        super(clipboard.getSize());
        this.vertices = new ArrayList<>();
        this.parent = clipboard;
    }
    
    /**
     * Constructor.
     *
     * @param clipboard The CuboidClipboard
     * @param strategy The strategy
     */
    public SmartClipBoard(CuboidClipboard clipboard, ConstructionStrategyType strategy) {
        this(clipboard, strategy, true);
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
        
        long start = System.currentTimeMillis();
        for (Vector v : vertices) {

//            if (sign != null && v.equals(sign)) {
//                continue;
//            }

            BaseBlock worldBlock = editSession.getBlock(v.add(pos));
            BaseBlock b = parent.getBlock(v);

//            if (b != null && !b.isAir() && worldBlock.getId() == b.getId() && worldBlock.getData() == b.getData()) {
//                System.out.println("SKIP: " + Material.getMaterial(b.getType()));
//            }

            if (b == null || (noAir && b.isAir()) || (worldBlock.getId() == b.getId() && worldBlock.getData() == b.getData())) {
                continue;
            }
            
            editSession.setBlock((v.add(pos)), b);
        }
//        if (sign != null) {
//            BaseBlock b = parent.getBlock(sign);
//            editSession.setBlock(sign.add(pos), b);
//        }

        long end = System.currentTimeMillis();
        System.out.println(vertices.size() + " vertices in " + (end - start) + "ms");

    }

}
