/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.structure.plan.placement.demolish;

import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.DemolishingOptions;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.BlockPlacement;
import com.chingo247.settlercraft.structureapi.structure.plan.placement.iterator.TopDownCuboidIterator;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import java.util.Iterator;

/**
 *
 * @author Chingo
 */
public class RestoringPlacement extends DemolishingPlacement {
    
    private BlockPlacement parent;
    private final CuboidRegion toRestore;

    public RestoringPlacement(BlockPlacement parent, CuboidRegion toRestore) {
        super(parent.getCuboidRegion().getMaximumPoint());
        this.toRestore = toRestore;
    }
    
    
    @Override
    public void place(EditSession session, Vector pos, DemolishingOptions option) {
        Iterator<Vector> iterator = new TopDownCuboidIterator(option.getCubeX(), option.getCubeY(), option.getCubeZ()).iterate(toRestore.getMaximumPoint());
       
        while(iterator.hasNext()) {
            Vector relativePosition = iterator.next();
            relativePosition = relativePosition.add(toRestore.getMinimumPoint());
            
            Vector worldPosition = relativePosition.add(pos);
            BaseBlock nextBlock = parent.getBlock(relativePosition);
            
            if (nextBlock == null) {
                continue;
            }
            
            session.rawSetBlock(worldPosition, nextBlock);
        }
        
    }
    
}
