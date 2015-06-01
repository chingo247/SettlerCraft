/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.structure.plan.placement.options;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;

/**
 *
 * @author Chingo
 */
public interface BlockMask {
    
    /**
     * Applies the BlockMask
     * @param relativePosition The relative position
     * @param worldPosition The position in the world
     * @param block The block that will be placed
     * @return The BaseBlock that has been applied
     */
    public BaseBlock apply(Vector relativePosition, Vector worldPosition, BaseBlock block);
    
}
