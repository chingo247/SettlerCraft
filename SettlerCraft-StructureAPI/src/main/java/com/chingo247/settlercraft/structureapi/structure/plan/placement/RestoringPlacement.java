/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.structure.plan.placement;

import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.DemolishingOptions;
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
    
    private AbstractBlockPlacement parent;
    private final CuboidRegion toPlace;

    public RestoringPlacement(AbstractBlockPlacement parent, CuboidRegion toPlace) {
        super(parent.getCuboidRegion().getMaximumPoint().subtract(parent.getCuboidRegion().getMinimumPoint()).add(1, 1, 1));
        this.parent = parent;
        this.toPlace = toPlace;
    }
    
    
    @Override
    public void place(EditSession editSession, Vector pos, DemolishingOptions option) {
        Iterator<Vector> traversal = new TopDownCuboidIterator(option.getCubeX(), option.getCubeY(), option.getCubeZ()).iterate(parent.getSize());
        
        while(traversal.hasNext()) {
            Vector blockPosition = traversal.next();
            if(!toPlace.contains(blockPosition.add(pos))) {
                continue;
            }
          
            BaseBlock nextBlock = parent.getBlock(blockPosition);
            
            if (nextBlock == null) {
                continue;
            }
            
            parent.doBlock(editSession, pos, blockPosition, nextBlock, option);
        }
        
    }
    
}
