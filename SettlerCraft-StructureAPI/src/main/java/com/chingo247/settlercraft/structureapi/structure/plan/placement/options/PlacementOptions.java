/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.settlercraft.structureapi.structure.plan.placement.options;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Chingo
 */
public abstract class PlacementOptions {
    
    private int cubeX;
    private int cubeY;
    private int cubeZ;
    private final List<BlockPredicate> toIgnore;
    private final List<BlockMask> masks;

    public PlacementOptions() {
        this.cubeX = 16;
        this.cubeY = 16;
        this.cubeZ = 16;
        this.toIgnore = new ArrayList<>();
        this.masks = new ArrayList<>();
    }
    
    /**
     * Adds a block predicate, when the block predicate returns true, the block will not be placed
     * during the place of the placement
     * @param blockPredicate The blockPredicate
     */
    public void addIgnore(BlockPredicate blockPredicate) {
        toIgnore.add(blockPredicate);
    }

    /**
     * Returns all the BlockPredicates that will be determine if a block should be ignored
     * @return The blockPredicates
     */
    public List<BlockPredicate> getIgnore() {
        return toIgnore;
    }
    
    public List<BlockMask> getBlockMasks() {
        return masks;
    } 
    
    public void addBlockMask(BlockMask mask) {
        this.masks.add(mask);
    }
    
    public int getCubeX() {
        return cubeX;
    }

    public void setCubeX(int cubeX) {
        this.cubeX = cubeX;
    }

    public int getCubeY() {
        return cubeY;
    }

    public void setCubeY(int cubeY) {
        this.cubeY = cubeY;
    }

    public int getCubeZ() {
        return cubeZ;
    }

    public void setCubeZ(int cubeZ) {
        this.cubeZ = cubeZ;
    }
    
    
    
   
    
}
