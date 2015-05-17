/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.structure.plan.placement;

import com.chingo247.settlercraft.structureapi.structure.plan.placement.options.PlacementOptions;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;

/**
 *
 * @author Chingo
 * @param <T>
 */
public interface BlockPlacement<T extends PlacementOptions> extends Placement<T> {
    
    public BaseBlock getBlock(Vector position);
    
    public int getBlocks();
    
}
