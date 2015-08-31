/*
 * Copyright (C) 2015 Chingo
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
package com.chingo247.structureapi.construction.task;

import com.chingo247.structureapi.construction.StructureBlock;
import com.chingo247.structureapi.construction.backup.IWorldPartSnapshot;
import com.chingo247.structureapi.structure.plan.placement.BlockPlacement;
import com.chingo247.structureapi.structure.plan.placement.PlacementTypes;
import com.chingo247.structureapi.structure.plan.placement.iterator.CuboidIterator;
import com.chingo247.structureapi.structure.plan.placement.options.Options;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.BlockType;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 *
 * @author Chingo
 */
class BackupRestoringPlacment extends BlockPlacement<Options> {
    
    private CuboidRegion region;
    private IWorldPartSnapshot snapshot;

    BackupRestoringPlacment(IWorldPartSnapshot worldPartSnapshot, CuboidRegion region) {
        super(getSize(region).getBlockX(), getSize(region).getBlockY(), getSize(region).getBlockZ());
        this.region = region;
        this.snapshot = worldPartSnapshot;
    }

    private static Vector getSize(CuboidRegion region) {
        return region.getMaximumPoint().subtract(region.getMinimumPoint()).add(Vector.ONE);
    }

    @Override
    public void place(EditSession editSession, Vector pos, Options option) {
        Iterator<Vector> traversal = new CuboidIterator(
                option.getCubeX() <= 0 ? getSize().getBlockX() : option.getCubeX(),
                option.getCubeY() <= 0 ? getSize().getBlockY() : option.getCubeY(),
                option.getCubeZ() <= 0 ? getSize().getBlockZ() : option.getCubeZ()
        ).iterate(getSize());
        
        PriorityQueue<StructureBlock> placeLater = new PriorityQueue<>();

        int placeLaterPlaced = 0;
        int placeLaterPause = 0;

        // Cube traverse this clipboard
        
        while (traversal.hasNext()) {
            Vector v = traversal.next();
            BaseBlock clipboardBlock = getBlock(pos.add(v));

            if (clipboardBlock == null) {
                continue;
            }

            int priority = getPriority(clipboardBlock);

            if (priority == PRIORITY_FIRST) {
                doBlock(editSession, pos, v, clipboardBlock, option);
            } else {
                placeLater.add(new StructureBlock(v, clipboardBlock));
            }

            // For every X place intensive blocks
            if (placeLaterPause > 0 && clipboardBlock.getId() != 0) {
                placeLaterPause--;
            } else {

                // only place these when having a greater xz-cubevalue to avoid placing torches etc in air and break them later
                while (placeLater.peek() != null
                        && placeLater.peek().getPosition().getBlockY() < v.getBlockY()) {
                    StructureBlock plb = placeLater.poll();
                    doBlock(editSession, pos, plb.getPosition(), plb.getBlock(), option);
                    placeLaterPlaced++;
                    
                    if (plb.getPriority() == PRIORITY_LIQUID || BlockType.emitsLight(plb.getBlock().getId())) {
                        placeLaterPlaced++;
                    }

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
            doBlock(editSession, pos, plb.getPosition(), plb.getBlock(), option);
        }
        System.out.println("BackupRestoringPlacment: " + region.getMinimumPoint() + ", " + region.getMaximumPoint());
    }
    
    @Override
    protected void doBlock(EditSession editSession, Vector position, Vector blockPosition, BaseBlock block, Options option) {
        Vector p = position.add(blockPosition);
        editSession.rawSetBlock(p, block);

    }
    
    @Override
    public BaseBlock getBlock(Vector position) {
        
//        System.out.println("[BackupRestoringPlacement]: Position "  + position);
        
        return snapshot.getWorldBlockAt(position.getBlockX(), position.getBlockY(), position.getBlockZ());
    }

    @Override
    public String getTypeName() {
        return PlacementTypes.RESTORING;
    }
    
    
    
}
